package com.intellecteu.onesource.integration.routes.processor;

import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.RECONCILED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SI_FETCHED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TRADE_DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TRADE_RECONCILED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.dto.ContractProposalDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.exception.InstructionRetrievementException;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.Settlement;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.services.AgreementService;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import com.intellecteu.onesource.integration.utils.IntegrationUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@Service
public class PositionProcessor {

    private final PositionService positionService;
    private final BackOfficeService borrowerBackOfficeService;
    private final BackOfficeService lenderBackOfficeService;
    private final AgreementService agreementService;
    private final ContractService contractService;
    private final OneSourceService oneSourceService;
    private final SettlementService settlementService;
    private final SpireMapper spireMapper;
    private final EventMapper eventMapper;
    private final CloudEventRecordService cloudEventRecordService;

    @Autowired
    public PositionProcessor(PositionService positionService,
        BackOfficeService borrowerBackOfficeService,
        BackOfficeService lenderBackOfficeService,
        AgreementService agreementService, ContractService contractService, OneSourceService oneSourceService,
        SettlementService settlementService, SpireMapper spireMapper, EventMapper eventMapper,
        CloudEventRecordService cloudEventRecordService) {
        this.positionService = positionService;
        this.borrowerBackOfficeService = borrowerBackOfficeService;
        this.lenderBackOfficeService = lenderBackOfficeService;
        this.agreementService = agreementService;
        this.contractService = contractService;
        this.oneSourceService = oneSourceService;
        this.settlementService = settlementService;
        this.spireMapper = spireMapper;
        this.eventMapper = eventMapper;
        this.cloudEventRecordService = cloudEventRecordService;
    }

    public Position savePosition(Position position) {
        return positionService.savePosition(position);
    }

    public Position updateProcessingStatus(Position position, ProcessingStatus processingStatus) {
        position.setProcessingStatus(processingStatus);
        return position;
    }

    public void fetchNewPositions() {
        Optional<String> maxPositionId = positionService.getMaxPositionId();
        List<Position> newSpirePositions = new ArrayList<>();
        newSpirePositions.addAll(borrowerBackOfficeService.getNewSpirePositions(maxPositionId));
        newSpirePositions.addAll(lenderBackOfficeService.getNewSpirePositions(maxPositionId));
        newSpirePositions.forEach(position -> {
            position.setVenueRefId(position.getCustomValue2());
            position.setProcessingStatus(CREATED);
        });
        positionService.savePositions(newSpirePositions);
    }

    public Position matchTradeAgreement(Position position) {
        agreementService.findByVenueRefId(position.getVenueRefId())
            .ifPresent(agreement -> {
                position.setMatching1SourceTradeAgreementId(agreement.getAgreementId());
                agreementService.markAgreementAsMatched(agreement, position.getPositionId());
            });
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
        PartyRole partyRole = IntegrationUtils.extractPartyRole(position).get();
        try {
            Optional<Settlement> settlementInstructionOpt = lenderBackOfficeService.retrieveSettlementInstruction(
                position, partyRole, position.getPositionAccount().getAccountId());
            settlementInstructionOpt.ifPresent(si -> {
                settlementService.persistSettlement(si);
                position.setApplicableInstructionId(si.getInstructionId());
            });
        } catch (InstructionRetrievementException e) {
            if (e.getCause() instanceof HttpStatusCodeException exception) {
                log.warn("SPIRE error response for request Instruction: " + exception.getStatusCode());
                if (Set.of(UNAUTHORIZED, FORBIDDEN, NOT_FOUND).contains(exception.getStatusCode())) {
                    var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                    var recordRequest = eventBuilder.buildExceptionRequest(
                        position.getMatching1SourceTradeAgreementId(), exception,
                        IntegrationSubProcess.GET_SETTLEMENT_INSTRUCTIONS, position.getPositionId());
                    cloudEventRecordService.record(recordRequest);
                }
            }
        }
        return position;
    }

    public ProcessingStatus reconcileWithAgreement(Position position) {
        if (position.getMatching1SourceTradeAgreementId() == null || position.getProcessingStatus() != SI_FETCHED) {
            return position.getProcessingStatus();
        }
        return agreementService.findByAgreementId(position.getMatching1SourceTradeAgreementId())
            .map(agreement -> agreementService.reconcile(agreement, position))
            .map(agreementService::saveAgreement)
            .map(agreement -> retrieveProcessingStatus(position, agreement))
            .orElse(position.getProcessingStatus());
    }

    private ProcessingStatus retrieveProcessingStatus(Position position, Agreement agreement) {
        if (agreement.getProcessingStatus().equals(RECONCILED)) {
            return TRADE_RECONCILED;
        }
        if (agreement.getProcessingStatus().equals(DISCREPANCIES)) {
            return TRADE_DISCREPANCIES;
        }
        return position.getProcessingStatus();
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
