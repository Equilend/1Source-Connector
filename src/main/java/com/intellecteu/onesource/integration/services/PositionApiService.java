package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.*;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createGetPositionNQuery;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createListOfTuplesGetPositionWithoutTA;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createListOfTuplesUpdatedPositions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.dto.ContractProposalDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.spire.AndOr;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.PositionMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.SettlementRepository;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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
    private final EventMapper eventMapper;
    private final PositionRepository positionRepository;
    private final SettlementRepository settlementRepository;
    private final SpireService spireService;
    private final OneSourceService oneSourceService;
    private final CloudEventRecordService cloudEventRecordService;
    private final static String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

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

    @Override
    public void processUpdatedPositions() {
        List<Position> positions = positionRepository.findAllNotCanceledAndSettled();
        LocalDateTime lastUpdatedDateTime = findLastUpdatedDateTime(positions);
        List<String> positionIds = positions.stream().map(Position::getPositionId).toList();
        String ids = positionIds.stream().collect(Collectors.joining(", "));

        if (lastUpdatedDateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
            String dateTime = lastUpdatedDateTime.format(formatter);

            ResponseEntity<JsonNode> response = spireService.requestPosition(
                createGetPositionNQuery(null, AndOr.AND, null,
                    createListOfTuplesUpdatedPositions(dateTime, ids)));
            List<Position> retrievedPositions = new ArrayList<>();

            processPositionsResponse(response, retrievedPositions);

            retrievedPositions.forEach(this::processUpdatedPosition);
        }
    }

    public void getSettlementDetailsWithoutTA(Position position) {
        String positionType = null;
        if (position.getPositionType() != null) {
            positionType = position.getPositionType().getPositionType();
        }
        PartyRole partyRole = null;
        if (positionType != null && positionType.contains("LOAN")) {
            partyRole = PartyRole.LENDER;
        } else if (positionType != null && positionType.contains("BORROW")) {
            partyRole = PartyRole.BORROWER;
        }
        if (partyRole != null) {
            log.debug("Retrieving Settlement Instruction by position from Spire as a {}", partyRole);
            List<SettlementDto> settlementDtos = spireService.retrieveSettlementDetails(
                positionMapper.toPositionDto(position),
                position.getCustomValue2(), null, partyRole);
            SettlementDto settlement = settlementDtos.get(0);
            settlementRepository.save(eventMapper.toSettlementEntity(settlement));

            position.setApplicableInstructionId(settlement.getInstructionId());
            position.setLastUpdateDateTime(LocalDateTime.now());
            position.setProcessingStatus(SI_FETCHED);
            positionRepository.save(position);
        }
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
                    Position position;
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
        getSettlementDetailsWithoutTA(position);
    }

    private void processUpdatedPosition(Position position) {
        updatePosition(position);
        savePosition(position);
        getSettlementDetailsWithoutTA(position);
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

    private LocalDateTime findLastUpdatedDateTime(List<Position> positions) {
        LocalDateTime localDateTime = null;
        if (positions != null && !positions.isEmpty()) {
            localDateTime = positions.stream()
                .map(Position::getLastUpdateDateTime)
                .max(LocalDateTime::compareTo)
                .get();
        }
        return localDateTime;
    }

    private void updatePosition(Position position) {
        if (position != null && position.getPositionStatus() != null
            && position.getPositionStatus().getStatus() != null) {
            String status = position.getPositionStatus().getStatus();
            if (status.equals("FUTURE")) {
                position.setProcessingStatus(UPDATED);
            } else if (List.of("CANCEL", "FAILED").contains(status)) {
                position.setProcessingStatus(CANCELED);
                matchingCanceledPosition(position);
            } else if (status.equals("OPEN")) {
                position.setProcessingStatus(SETTLED);
            }
            position.setLastUpdateDateTime(LocalDateTime.now());
        }
    }

    private void matchingCanceledPosition(Position position) {
        List<Agreement> agreements = agreementRepository.findByVenueRefId(position.getCustomValue2());
        if (!agreements.isEmpty()) {
            Agreement agreement = agreements.get(0);
            agreement.setLastUpdateDatetime(LocalDateTime.now());
            agreement.setProcessingStatus(MATCHED_CANCELED_POSITION);
            createContractInitiationCloudEvent(agreement.getAgreementId(), position,
                TRADE_AGREEMENT_MATCHED_CANCELED_POSITION);

            agreementRepository.save(agreement);
        }

        List<Contract> contracts = contractRepository.findByVenueRefId(position.getCustomValue2());
        if (!contracts.isEmpty()) {
            Contract contract = contracts.get(0);
            contract.setLastUpdateDatetime(LocalDateTime.now());
            contract.setProcessingStatus(MATCHED_CANCELED_POSITION);
            createContractInitiationCloudEvent(contract.getContractId(), position,
                LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION);

            contractRepository.save(contract);
        }
    }
}