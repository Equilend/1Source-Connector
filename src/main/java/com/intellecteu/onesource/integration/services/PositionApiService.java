package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.*;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createGetPositionNQuery;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createListOfTuplesGetPositionWithoutTA;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.dto.ContractProposalDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.spire.AndOr;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.mapper.PositionMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionApiService implements PositionService {

    private final AgreementRepository agreementRepository;
    private final ContractRepository contractRepository;
    private final PositionMapper positionMapper;
    private final PositionRepository positionRepository;
    private final SpireService spireService;
    private final OneSourceService oneSourceService;
    private final CloudEventRecordService cloudEventRecordService;

    @Override
    public void createLoanContractWithoutTA() {
        List<Position> storedPositions = positionRepository.findAll();
        Position position = storedPositions.stream()
            .max(Comparator.comparingInt(p -> Integer.parseInt(p.getPositionId()))).orElse(null);
        if (position != null) {
            ResponseEntity<JsonNode> response = spireService.requestPosition(
                createGetPositionNQuery(null, AndOr.AND, null,
                    createListOfTuplesGetPositionWithoutTA(position.getPositionId())));
            List<Position> positions = new ArrayList<>();

            processPositionsResponse(response, positions);

            positions.forEach(this::processPosition);
        }
    }

    public PositionDto getPositionWithoutTA() throws JsonProcessingException {
        ResponseEntity<JsonNode> response = spireService.requestPosition(
            createGetPositionNQuery(null, AndOr.AND, null, createListOfTuplesGetPositionWithoutTA("positionID")));
        List<Position> positions = new ArrayList<>();

        processPositionsResponse(response, positions);

        positions.forEach(this::processPosition);
        return null;
    }

    public List<SettlementDto> getSettlementDetailsWithoutTA(PositionDto positionDto,
        TradeAgreementDto tradeAgreementDto) {
        final PartyRole partyRole = tradeAgreementDto
            .getTransactingParties().get(0).getPartyRole();
        log.debug("Retrieving Settlement Instruction from Spire as a {}", partyRole);
        return spireService.retrieveSettlementDetails(positionDto, tradeAgreementDto, partyRole);
    }

    public void createLoanContractProposalWithoutTA(PositionDto positionDto, List<SettlementDto> settlementDtos,
        TradeAgreementDto tradeAgreementDto) {
        ContractProposalDto contractProposalDto = buildLoanContractProposal(settlementDtos, tradeAgreementDto);
        oneSourceService.createContract(null, contractProposalDto, positionDto);
    }

    private ContractProposalDto buildLoanContractProposal(List<SettlementDto> settlementDtos,
        TradeAgreementDto tradeAgreementDto) {
        return ContractProposalDto.builder()
            .settlement(settlementDtos)
            .trade(tradeAgreementDto)
            .build();
    }

    private void processPositionsResponse(ResponseEntity<JsonNode> response, List<Position> positions) {
        if (response.getBody() != null
            && response.getBody().get("data") != null
            && response.getBody().get("data").get("beans") != null
            && response.getBody().get("data").get("beans").get(0) != null) {
            JsonNode jsonNode = response.getBody().get("data").get("beans");
            if (jsonNode.isArray()) {
                for (JsonNode positionNode : jsonNode) {
                    Position position = null;
                    try {
                        position = positionMapper.toPosition(positionNode);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    positions.add(position);
                }
            }
        }
    }

    private TradeAgreementDto buildTradeAgreementDto(PositionDto positionDto) {
        return positionMapper.buildTradeAgreementDto(positionDto);
    }

    private void processPosition(Position position) {
        List<Agreement> agreements = agreementRepository.findByVenueRefId(position.getCustomValue2());
        if (!agreements.isEmpty()) {
            Agreement agreement = agreements.get(0);
            position.setMatching1SourceTradeAgreementId(agreement.getAgreementId());
            agreement.setMatchingSpirePositionId(position.getPositionId());
            agreement.setLastUpdateDatetime(LocalDateTime.now());
            agreement.setProcessingStatus(MATCHED_POSITION);
            createContractInitiationCloudEvent(agreement.getAgreementId(), position, TRADE_AGREEMENT_MATCHED_POSITION);

            agreementRepository.save(agreement);
        }

        List<Contract> contracts = contractRepository.findByVenueRefId(position.getCustomValue2());
        if (!contracts.isEmpty()) {
            Contract contract = contracts.get(0);
            position.setMatching1SourceLoanContractId(contract.getContractId());
            contract.setMatchingSpirePositionId(position.getPositionId());
            contract.setLastUpdateDatetime(LocalDateTime.now());
            contract.setProcessingStatus(MATCHED_POSITION);
            createContractInitiationCloudEvent(contract.getContractId(), position,
                LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION);

            contractRepository.save(contract);
        }

        savePosition(position);

//        TradeAgreementDto tradeAgreementDto = buildTradeAgreementDto(positionDto);
//        List<SettlementDto> settlementDtos = getSettlementDetailsWithoutTA(positionDto, tradeAgreementDto);
//        createLoanContractProposalWithoutTA(positionDto, settlementDtos, tradeAgreementDto);
    }

    private void savePosition(Position position) {
        position.setVenueRefId(position.getCustomValue2());
        position.setProcessingStatus(CREATED);
        position.setLastUpdateDateTime(LocalDateTime.now());
        positionRepository.save(position);
    }

    private void createContractInitiationCloudEvent(String id, Position position, RecordType recordType) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(id,
            recordType, position.getPositionId());
        cloudEventRecordService.record(recordRequest);
    }
}