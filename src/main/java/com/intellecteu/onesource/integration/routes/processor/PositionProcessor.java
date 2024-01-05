package com.intellecteu.onesource.integration.routes.processor;

import static com.intellecteu.onesource.integration.model.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.RECONCILED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SI_FETCHED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TRADE_DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TRADE_RECONCILED;

import com.intellecteu.onesource.integration.dto.ContractProposalDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.Settlement;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.services.AgreementService;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.SettlementService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionProcessor {

    private final PositionService positionService;
    private final BackOfficeService backOfficeService;
    private final AgreementService agreementService;
    private final ContractService contractService;
    private final OneSourceService oneSourceService;
    private final SettlementService settlementService;
    private final SpireMapper spireMapper;
    private final EventMapper eventMapper;


    public Position savePosition(Position position) {
        return positionService.savePosition(position);
    }

    public Position updateProcessingStatus(Position position, ProcessingStatus processingStatus) {
        position.setProcessingStatus(processingStatus);
        return position;
    }

    public void fetchNewPositions() {
        List<Position> newSpirePositions = backOfficeService.getNewSpirePositions(positionService.getMaxPositionId());
        newSpirePositions.forEach(position -> {
            position.setVenueRefId(position.getCustomValue2());
            position.setProcessingStatus(CREATED);
        });
        positionService.savePositions(newSpirePositions);
    }

    public Position matchTradeAgreement(Position position) {
        Optional<Agreement> agreementOptional = agreementService.findByVenueRefId(position.getVenueRefId());
        if (agreementOptional.isPresent()) {
            Agreement agreement = agreementOptional.get();
            position.setMatching1SourceTradeAgreementId(agreement.getAgreementId());
            agreementService.markAgreementAsMatched(agreement, position.getPositionId());
        }
        return position;
    }

    public Position matchContractProposal(Position position) {
        Optional<Contract> contractOpt = contractService.findByVenueRefId(position.getVenueRefId());
        contractOpt.ifPresent(contract -> {
            position.setMatching1SourceLoanContractId(contract.getContractId());
            contractService.markContractAsMatched(contract, position.getPositionId());
        });
        return position;
    }

    public Position fetchSettlementInstruction(Position position) {
        Integer accountId = position.getExposure() != null ? position.getExposure().getDepoId() : null;
        String positionType = position.getPositionType() != null ? position.getPositionType().getPositionType() : null;
        Optional<Settlement> settlementInstructionOpt = settlementService.getSettlementInstruction(
            position.getPositionId(), accountId, position.getSecurityId(), position.getPositionTypeId(), positionType,
            position.getCurrencyId(), position.getVenueRefId());
        if (settlementInstructionOpt.isPresent()) {
            Settlement settlement = settlementInstructionOpt.get();
            settlementService.persistSettlement(settlement);
            position.setApplicableInstructionId(settlement.getInstructionId());
        } else {
            //TODO WHAT we should do if Settlement instruction was not found?
        }
        return position;
    }

    public ProcessingStatus reconcileWithAgreement(Position position) {
        if (position.getMatching1SourceTradeAgreementId() != null) {
            Optional<Agreement> agreementOptional = agreementService.findByAgreementId(
                position.getMatching1SourceTradeAgreementId());
            if (agreementOptional.isPresent()) {
                Agreement agreement = agreementOptional.get();
                agreement = agreementService.reconcile(agreement, position);
                log.debug("Agreement {} changed processing status to {}", agreement.getAgreementId(),
                    agreement.getProcessingStatus());
                agreementService.saveAgreement(agreement);
                if (agreement.getProcessingStatus().equals(RECONCILED)) {
                    return TRADE_RECONCILED;
                } else if (agreement.getProcessingStatus().equals(DISCREPANCIES)) {
                    return TRADE_DISCREPANCIES;
                }
            }
        }
        return SI_FETCHED;
    }

    public Position instructLoanContractProposal(Position position) {
        if ((position.getMatching1SourceTradeAgreementId() != null
            && position.getProcessingStatus() == TRADE_RECONCILED)
            || (position.getMatching1SourceTradeAgreementId() == null
            && position.getProcessingStatus() == SI_FETCHED)) {
            List<Settlement> settlementList = settlementService.getSettlementByInstructionId(
                position.getApplicableInstructionId());
            PositionDto positionDto = spireMapper.toPositionDto(position);
            List<SettlementDto> settlementDtos = settlementList.stream().map(eventMapper::toSettlementDto).collect(
                Collectors.toList());
            ContractProposalDto contractProposalDto = buildLoanContractProposal(settlementDtos,
                spireMapper.buildTradeAgreementDto(positionDto));
            oneSourceService.createContract(null, contractProposalDto, positionDto);
            log.debug("Loan contract proposal was created for position id: {}", positionDto.getPositionId());
        }
        return position;
    }

    private ContractProposalDto buildLoanContractProposal(List<SettlementDto> settlementDtos,
        TradeAgreementDto tradeAgreementDto) {
        return ContractProposalDto.builder()
            .settlement(settlementDtos)
            .trade(tradeAgreementDto)
            .build();
    }
}
