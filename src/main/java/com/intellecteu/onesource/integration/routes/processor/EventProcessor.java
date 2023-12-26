package com.intellecteu.onesource.integration.routes.processor;

import static com.intellecteu.onesource.integration.enums.FlowStatus.TRADE_DATA_RECEIVED;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_CANCELED;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_CREATED;
import static com.intellecteu.onesource.integration.exception.LoanContractCancelException.CANCEL_EXCEPTION_MESSAGE;
import static com.intellecteu.onesource.integration.model.ContractStatus.PROPOSED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_CANCELED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_DECLINED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_OPENED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_PENDING;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_PROPOSED;
import static com.intellecteu.onesource.integration.model.EventType.TRADE_AGREED;
import static com.intellecteu.onesource.integration.model.EventType.TRADE_CANCELED;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.NEW;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.PROCESSED;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createGetPositionNQuery;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createListOfTuplesGetPosition;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.TradeEventDto;
import com.intellecteu.onesource.integration.dto.spire.AndOr;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.Timestamp;
import com.intellecteu.onesource.integration.model.TradeEvent;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.TimestampRepository;
import com.intellecteu.onesource.integration.repository.TradeEventRepository;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.SpireService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessor {

    private final TradeEventRepository tradeEventRepository;
    private final AgreementRepository agreementRepository;
    private final ContractRepository contractRepository;
    private final PositionRepository positionRepository;
    private final TimestampRepository timestampRepository;
    private final EventMapper eventMapper;
    private final SpireService spireService;
    private final OneSourceService oneSourceService;
    private final CloudEventRecordService cloudEventRecordService;

    @Value("${camel.timestamp}")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime timeStamp;

    public void consumeEvents() {
        log.debug(">>>>> Pulling events!");
        Optional<Timestamp> maxTimestamp = timestampRepository.findFirstByOrderByTimestampDesc();
        maxTimestamp.ifPresent(timestamp -> timeStamp = timestamp.getTimestamp());
        log.debug("Timestamp: " + timeStamp);
        List<TradeEventDto> events = oneSourceService.retrieveEvents(timeStamp);
        events.forEach(i -> log.debug("Event Id: {}, Type: {}, Uri: {}, Event Datetime {}",
            i.getEventId(), i.getEventType(), i.getResourceUri(), i.getEventDatetime()));
        List<TradeEventDto> newEvents = findNewEvents(events);
        newEvents.forEach(this::saveEvent); //make batch insert later
        log.debug("<<<<< Retrieved {} new events!", newEvents.size());
    }

    public void processEvents() {
        log.debug(">>>>> Process event data!");
        List<TradeEvent> events = tradeEventRepository.findAllByProcessingStatus(CREATED);
        timeStamp = findMaxDateTimeOfEvents(events);
        storeTimestamp(timeStamp);
        log.debug("The latest timestamp: {}", timeStamp);
        processData(events);
        log.debug("<<<<< Processed {} events", events.size());
    }

    public void cancelContract() {
        log.debug(">>>>> Starting the Cancel contract process.");
        Map<Contract, String> candidatesForCancelToPositionId = new HashMap<>();
        List<Contract> proposedContracts = contractRepository.findAllByContractStatus(PROPOSED);
        log.debug("Retrieved {} candidatesToCancel in Proposed status.", proposedContracts.size());
        for (Contract contract : proposedContracts) {
            log.debug("Requesting Spire position!");
            final String venueRefId = contract.getTrade().getVenue().getVenueRefKey();
            ResponseEntity<JsonNode> response = spireService.requestPosition(
                createGetPositionNQuery(null, AndOr.AND, true,
                    createListOfTuplesGetPosition("customValue2", "EQUALS", venueRefId, null)));
            var positionType = extractPositionType(response);
            var positionId = extractPositionId(response);
            if (isToCancelCandidate(contract, positionType, response)) {
                candidatesForCancelToPositionId.put(contract, positionId);
            }
        }
        log.debug("Executing cancellation for {} contracts", candidatesForCancelToPositionId.size());
        for (var candidateWithPositionId : candidatesForCancelToPositionId.entrySet()) {
            oneSourceService.cancelContract(candidateWithPositionId.getKey(), candidateWithPositionId.getValue());
        }
        log.debug("<<<<< Finishing Cancel contract process.");
    }

    private void saveEvent(TradeEventDto tradeEventDto) {
        TradeEvent eventEntity = eventMapper.toEventEntity(tradeEventDto);
        eventEntity.setProcessingStatus(CREATED);
        tradeEventRepository.save(eventEntity);
    }

    private List<TradeEventDto> findNewEvents(List<TradeEventDto> events) {
        return events.stream()
            .filter(p -> p.getEventDatetime().isAfter(timeStamp))
            .peek(i -> log.debug("New event Id: {}, Type: {}, Uri: {}, Event Datetime {}",
                i.getEventId(), i.getEventType(), i.getResourceUri(), i.getEventDatetime()))
            .toList();
    }

    private boolean isToCancelCandidate(Contract contract, String positionType, ResponseEntity<JsonNode> response) {
        Optional<PartyRole> partyRole = extractPartyRole(positionType);
        if (partyRole.isEmpty()) {
            log.debug(String.format(CANCEL_EXCEPTION_MESSAGE, contract.getContractId(), positionType));
            contract.setProcessingStatus(ProcessingStatus.ONESOURCE_ISSUE);
        }
        return partyRole
            .filter(role -> role == LENDER)
            .map(party -> isToCancelCandidate(contract, response))
            .orElse(false);
    }

    private String extractPositionType(ResponseEntity<JsonNode> response) {
        if (response.getBody() != null && response.getBody().get("data") != null
            && response.getBody().get("data").get("beans") != null
            && response.getBody().get("data").get("beans").get(0) != null) {
            JsonNode jsonNode = response.getBody().get("data").get("beans").get(0);
            JsonNode positionTypeDTO = jsonNode.get("positiontypeDTO");
            if (positionTypeDTO != null && positionTypeDTO.get("positionType") != null) {
                return positionTypeDTO.get("positionType").toString().trim();
            }
        }
        return "";
    }

    private String extractPositionId(ResponseEntity<JsonNode> response) {
        String positionId = "";
        if (response.getBody() != null && response.getBody().get("data") != null
            && response.getBody().get("data").get("beans") != null
            && response.getBody().get("data").get("beans").get(0) != null) {
            JsonNode jsonNode = response.getBody().get("data").get("beans").get(0);
            positionId = jsonNode.get("positionId").toString().replace("\"", "");
        }
        return positionId;
    }

    private boolean isToCancelCandidate(Contract contract, ResponseEntity<JsonNode> response) {
        if (response.getBody() != null && response.getBody().get("data") != null
            && response.getBody().get("data").get("beans") != null
            && response.getBody().get("data").get("beans").get(0) != null) {
            JsonNode jsonNode = response.getBody().get("data").get("beans").get(0);
            JsonNode statusDTO = jsonNode.get("statusDTO");
            String status = eventMapper.getIfExist(statusDTO, "status");
            if (status.equalsIgnoreCase("CANCELED") || status.equalsIgnoreCase("FAILED")) {
                log.debug("Position has status {}! Submitting cancellation for contract: {}", status,
                    contract.getContractId());
                return true;
            }
        }
        return false;
    }

    private void processData(List<TradeEvent> events) {
        for (TradeEvent event : events) {
            if (event.getEventType().equals(TRADE_AGREED)) {
                processTradeEvent(event);
            } else if (event.getEventType().equals(TRADE_CANCELED)) {
                processTradeCanceledEvent(event);
            } else if (Set.of(CONTRACT_OPENED, CONTRACT_PENDING, CONTRACT_DECLINED,
                CONTRACT_PROPOSED, CONTRACT_CANCELED).contains(event.getEventType())) {
                processContractEvent(event);
            }
        }
    }

    private void processTradeEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/agreements/93f834ff-66b5-4195-892b-8f316ed77010
        String eventUri = event.getResourceUri();
        AgreementDto agreementDto = oneSourceService.findTradeAgreement(eventUri, event.getEventType());
        if (agreementDto != null) {
            storeAgreement(agreementDto, event);
            event.setProcessingStatus(PROCESSED);
            final TradeEvent savedTradeEvent = tradeEventRepository.save(event);
            var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
            var recordRequest = eventBuilder.buildRequest(TRADE_AGREEMENT_CREATED,
                savedTradeEvent.getResourceUri());
            cloudEventRecordService.record(recordRequest);
        }
    }

    private void processTradeCanceledEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/agreements/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        String agreementId = resourceUri.substring(resourceUri.lastIndexOf('/') + 1);
        Agreement agreement = null;
        Position position = null;
        List<Agreement> agreements = agreementRepository.findByAgreementId(agreementId);
        if (!agreements.isEmpty()) {
            agreement = agreements.get(0);
            agreement.setLastUpdateDatetime(LocalDateTime.now());
            agreement.setProcessingStatus(CANCELED);
            agreementRepository.save(agreement);
        }

        List<Position> positions = positionRepository.findByMatching1SourceTradeAgreementId(
            agreementId);
        if (!positions.isEmpty()) {
            position = positions.get(0);
            position.setProcessingStatus(ProcessingStatus.TRADE_CANCELED);
            position.setLastUpdateDateTime(LocalDateTime.now());
            positionRepository.save(position);
        }
        event.setProcessingStatus(PROCESSED);
        tradeEventRepository.save(event);
        if (agreement != null && position != null) {
            createContractInitiationCloudEvent(agreement.getAgreementId(), position,
                TRADE_AGREEMENT_CANCELED);
        }
    }

    private void processContractEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/contracts/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            oneSourceService.findContract(resourceUri)
                .ifPresent(contractDto -> processContract(contractDto, event));
        } catch (HttpStatusCodeException e) {
            log.debug("Contract {} was not found. Details: {} ", resourceUri, e.getMessage());
            if (Set.of(UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR).contains(e.getStatusCode())) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                var recordRequest = eventBuilder.buildExceptionRequest(resourceUri,
                    e, GET_LOAN_CONTRACT_PROPOSAL, String.valueOf(event.getEventId()));
                cloudEventRecordService.record(recordRequest);
            }
        }
    }

    private void processContract(ContractDto contractDto, TradeEvent event) {
        contractDto.getTrade().setEventId(event.getEventId());
        contractDto.getTrade().setResourceUri(event.getResourceUri());
        contractDto.setEventType(event.getEventType());
        contractDto.setProcessingStatus(NEW);
        contractDto.setFlowStatus(TRADE_DATA_RECEIVED);
        storeContract(contractDto);
        event.setProcessingStatus(PROCESSED);
        tradeEventRepository.save(event);
    }

    private Agreement storeAgreement(AgreementDto agreementDto, TradeEvent event) {
        agreementDto.getTrade().setEventId(event.getEventId());
        agreementDto.getTrade().setResourceUri(event.getResourceUri());
        agreementDto.setEventType(event.getEventType());
        agreementDto.setEventType(event.getEventType());
        agreementDto.setFlowStatus(TRADE_DATA_RECEIVED);
        Agreement agreementEntity = eventMapper.toAgreementEntity(agreementDto);
            agreementEntity.setLastUpdateDatetime(LocalDateTime.now());
            agreementEntity.setProcessingStatus(CREATED);
        return agreementRepository.save(agreementEntity);
    }

    private Contract storeContract(ContractDto contractDto) {
        Contract contractEntity = eventMapper.toContractEntity(contractDto);
        return contractRepository.save(contractEntity);
    }

    private void storeTimestamp(LocalDateTime timestamp) {
        timestampRepository.save(eventMapper.toTimestampEntity(timestamp));
    }

    private LocalDateTime findMaxDateTimeOfEvents(List<TradeEvent> events) {
        LocalDateTime localDateTime = timeStamp;
        if (events != null && !events.isEmpty()) {
            localDateTime = events.stream()
                .map(TradeEvent::getEventDatetime)
                .max(LocalDateTime::compareTo)
                .get();
        }
        return localDateTime;
    }

    void createContractInitiationCloudEvent(String id, Position position, RecordType recordType) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(id,
            recordType, position.getPositionId());
        cloudEventRecordService.record(recordRequest);
    }
}