package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_NEW_POSITIONS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_UPDATED_POSITIONS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.*;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createGetPositionNQuery;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createListOfTuplesGetPositionWithoutTA;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createListOfTuplesUpdatedPositions;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.dto.ContractProposalDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.dto.spire.AndOr;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.PositionMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

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
    private final static String LENDER_POSITION_TYPE = "LOAN CASH";
    private final static String BORROWER_POSITION_TYPE = "BORROW CASH";

    @Override
    public void createLoanContractWithoutTA() {
        List<Position> storedPositions = positionRepository.findAll();
        storedPositions.stream()
            .max(Comparator.comparingInt(p -> Integer.parseInt(p.getPositionId())))
            .ifPresent(this::requestAndProcessPosition);
    }

    private void requestAndProcessPosition(Position position) {
        log.debug("Sending POST request for position id: {}", position.getPositionId());
        try {
            ResponseEntity<JsonNode> response = spireService.requestPosition(
                createGetPositionNQuery(null, AndOr.AND, null,
                    createListOfTuplesGetPositionWithoutTA(position.getPositionId())));
            List<Position> positions = new ArrayList<>();
            processPositionsResponse(response, positions);
            positions.forEach(this::processPosition);
        } catch (HttpStatusCodeException e) {
            log.warn("SPIRE error response for {} subprocess. Details: {}",
                GET_NEW_POSITIONS_PENDING_CONFIRMATION, e.getStatusCode());
            if (Set.of(UNAUTHORIZED, FORBIDDEN).contains(e.getStatusCode())) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                final CloudEventBuildRequest recordRequest = eventBuilder.buildExceptionRequest(e,
                    IntegrationSubProcess.GET_NEW_POSITIONS_PENDING_CONFIRMATION);
                cloudEventRecordService.record(recordRequest);
            }
        }
    }

    @Override
    public void processUpdatedPositions() {
        List<Position> positions = positionRepository.findAllNotCanceledAndSettled();
        findLastUpdatedDateTime(positions)
            .ifPresent(lastUpdated -> update(lastUpdated, positions));
    }

    private void update(LocalDateTime lastUpdatedDateTime, List<Position> positions) {
        String ids = positions.stream()
            .map(Position::getPositionId)
            .collect(Collectors.joining(", "));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        String dateTime = lastUpdatedDateTime.format(formatter);
        try {
            ResponseEntity<JsonNode> response = spireService.requestPosition(
                createGetPositionNQuery(null, AndOr.AND, null,
                    createListOfTuplesUpdatedPositions(dateTime, ids)));
            List<Position> retrievedPositions = new ArrayList<>();
            processPositionsResponse(response, retrievedPositions);
            retrievedPositions.forEach(this::processUpdatedPosition);
        } catch (HttpStatusCodeException e) {
            log.warn("SPIRE error response for {} subprocess. Details: {}",
                GET_UPDATED_POSITIONS_PENDING_CONFIRMATION, e.getStatusCode());
            if (Set.of(UNAUTHORIZED, FORBIDDEN).contains(e.getStatusCode())) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                final CloudEventBuildRequest recordRequest = eventBuilder.buildExceptionRequest(e,
                    IntegrationSubProcess.GET_UPDATED_POSITIONS_PENDING_CONFIRMATION);
                cloudEventRecordService.record(recordRequest);
            }
        }
    }

    public void getSettlementDetailsWithoutTA(Position position) {
        String positionType = null;
        if (position.getPositionType() != null) {
            positionType = position.getPositionType().getPositionType();
        }
        PartyRole partyRole = null;
        if (positionType != null && positionType.contains(LENDER_POSITION_TYPE)) {
            partyRole = PartyRole.LENDER;
        } else if (positionType != null && positionType.contains(BORROWER_POSITION_TYPE)) {
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
            createContractInitiationCloudEvent(agreement.getAgreementId(),
                TRADE_AGREEMENT_MATCHED_POSITION, agreement.getMatchingSpirePositionId());

            agreementRepository.save(agreement);
        }

        List<Contract> contracts = contractRepository.findByVenueRefId(position.getCustomValue2());
        if (!contracts.isEmpty()) {
            Contract contract = contracts.get(0);
            position.setMatching1SourceLoanContractId(contract.getContractId());
            contract.setMatchingSpirePositionId(position.getPositionId());
            contract.setLastUpdateDatetime(LocalDateTime.now());
            contract.setProcessingStatus(MATCHED_POSITION);
            createContractInitiationCloudEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION,
                contract.getMatchingSpirePositionId());

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
        position.setProcessingStatus(ProcessingStatus.CREATED);
        position.setLastUpdateDateTime(LocalDateTime.now());
        positionRepository.save(position);
    }

    private void createContractInitiationCloudEvent(String recordData, RecordType recordType, String relatedData) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(recordData, recordType, relatedData);
        cloudEventRecordService.record(recordRequest);
    }

    private Optional<LocalDateTime> findLastUpdatedDateTime(List<Position> positions) {
        LocalDateTime localDateTime = null;
        if (positions != null && !positions.isEmpty()) {
            localDateTime = positions.stream()
                .map(Position::getLastUpdateDateTime)
                .max(LocalDateTime::compareTo)
                .get();
        }
        return Optional.ofNullable(localDateTime);
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
        agreementRepository.findByVenueRefId(position.getCustomValue2()).stream()
            .findFirst()
            .ifPresent(this::processAgreementMatchedCanceledPosition);

        contractRepository.findByVenueRefId(position.getCustomValue2()).stream()
            .findFirst()
            .ifPresent(this::processContractMatchedCanceledPosition);
    }

    private void processAgreementMatchedCanceledPosition(Agreement agreement) {
        agreement.setLastUpdateDatetime(LocalDateTime.now());
        agreement.setProcessingStatus(MATCHED_CANCELED_POSITION);
        createContractInitiationCloudEvent(agreement.getAgreementId(),
            TRADE_AGREEMENT_MATCHED_CANCELED_POSITION, agreement.getMatchingSpirePositionId());
        agreementRepository.save(agreement);
    }

    private void processContractMatchedCanceledPosition(Contract contract) {
        contract.setLastUpdateDatetime(LocalDateTime.now());
        contract.setProcessingStatus(MATCHED_CANCELED_POSITION);
        createContractInitiationCloudEvent(contract.getContractId(),
            LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION, contract.getMatchingSpirePositionId());
        contractRepository.save(contract);
    }
}
