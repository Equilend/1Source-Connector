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
import com.intellecteu.onesource.integration.constant.PositionConstant.PositionStatus;
import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.TradeEventDto;
import com.intellecteu.onesource.integration.dto.spire.AndOr;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.EventType;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.Timestamp;
import com.intellecteu.onesource.integration.model.TradeEvent;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.TimestampRepository;
import com.intellecteu.onesource.integration.repository.TradeEventRepository;
import com.intellecteu.onesource.integration.services.AgreementService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.PositionService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessor {

    private final TradeEventRepository tradeEventRepository;
    private final AgreementService agreementService;
    private final ContractService contractService;
    private final PositionService positionService;
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
        List<TradeEventDto> newEvents = findNewEvents(events);
        newEvents.forEach(e -> {
            e.setProcessingStatus(CREATED);
            saveEvent(e);
        }); // make batch insert later
        log.debug("<<<<< Retrieved {} new events!", newEvents.size());
    }

    @Transactional // temp fix for detached trade event persistence issue
    public void processEvents() {
        log.debug(">>>>> Process event data!");
        List<TradeEvent> events = tradeEventRepository.findAllByProcessingStatus(CREATED);
        timeStamp = findMaxDateTimeOfEvents(events);
        storeTimestamp(timeStamp);
        log.debug("The latest timestamp: {}", timeStamp);
        processTradeEvent(events);
        log.debug("<<<<< Processed {} events", events.size());
    }

    public void cancelContract() {
        log.debug(">>>>> Starting the Cancel contract process.");
        Map<Contract, String> candidatesForCancelToPositionId = new HashMap<>();
        List<Contract> proposedContracts = contractService.findAllByContractStatus(PROPOSED);
        log.debug("Retrieved {} candidatesToCancel in Proposed status.", proposedContracts.size());
        for (Contract contract : proposedContracts) {
            log.debug("Requesting Spire position!");
            final String venueRefId = contract.getTrade().getVenue().getVenueRefKey();
            ResponseEntity<JsonNode> response = spireService.requestPosition(
                createGetPositionNQuery(null, AndOr.AND, true,
                    createListOfTuplesGetPosition("customValue2", "EQUALS", venueRefId, null)));
            var positionType = extractPositionType(response);
            var positionId = extractPositionId(response);
            if (positionId != null && isToCancelCandidate(contract, positionType, response)) {
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
            var totalRows = response.getBody().at("/data/totalRows").asText();
            if (!"0".equals(totalRows)) {
                JsonNode jsonNode = response.getBody().get("data").get("beans").get(0);
                JsonNode positionTypeDTO = jsonNode.get("positiontypeDTO");
                if (positionTypeDTO != null && positionTypeDTO.get("positionType") != null) {
                    return positionTypeDTO.get("positionType").toString().trim();
                }
            }
        }
        return "";
    }

    private String extractPositionId(ResponseEntity<JsonNode> response) {
        if (response.getBody() != null && response.getBody().get("data") != null
            && response.getBody().get("data").get("beans") != null
            && response.getBody().get("data").get("beans").get(0) != null) {
            var totalRows = response.getBody().at("/data/totalRows").asText();
            if (!"0".equals(totalRows)) {
                JsonNode jsonNode = response.getBody().get("data").get("beans").get(0);
                return jsonNode.get("positionId").toString().replace("\"", "");
            }
        }
        return null;
    }

    private boolean isToCancelCandidate(Contract contract, ResponseEntity<JsonNode> response) {
        if (response.getBody() != null && response.getBody().get("data") != null
            && response.getBody().get("data").get("beans") != null
            && response.getBody().get("data").get("beans").get(0) != null) {
            var totalRows = response.getBody().at("/data/totalRows").asText();
            if (!"0".equals(totalRows)) {
                JsonNode jsonNode = response.getBody().get("data").get("beans").get(0);
                JsonNode statusDTO = jsonNode.get("statusDTO");
                String status = eventMapper.getIfExist(statusDTO, "status");
                if (status.equalsIgnoreCase(PositionStatus.CANCEL) || status.equalsIgnoreCase(PositionStatus.FAILED)) {
                    log.debug("Position has status {}! Submitting cancellation for contract: {}", status,
                        contract.getContractId());
                    return true;
                }
            }
        }
        return false;
    }

    private void processTradeEvent(List<TradeEvent> events) {
        for (TradeEvent event : events) {
            if (event.getEventType().equals(TRADE_AGREED)) {
                processTradeEvent(event);
                updateEventStatus(event, PROCESSED);
            } else if (event.getEventType().equals(EventType.TRADE_CANCELED)) {
                processTradeCanceledEvent(event);
                updateEventStatus(event, PROCESSED);
            } else if (Set.of(CONTRACT_OPENED, CONTRACT_PENDING, CONTRACT_DECLINED,
                CONTRACT_PROPOSED, CONTRACT_CANCELED).contains(event.getEventType())) {
                processContractEvent(event);
                updateEventStatus(event, PROCESSED);
            }
        }
    }

    private void processTradeEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/agreements/93f834ff-66b5-4195-892b-8f316ed77010
        String eventUri = event.getResourceUri();
        oneSourceService.findTradeAgreement(eventUri, event.getEventType())
            .ifPresent(agreementDto -> {
                configureAndSaveAgreement(event, agreementDto);
                recordCloudEvent(eventUri);
            });
    }

    private void recordCloudEvent(String record) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(record, TRADE_AGREEMENT_CREATED);
        cloudEventRecordService.record(recordRequest);
    }

    private void configureAndSaveAgreement(TradeEvent event, AgreementDto agreementDto) {
        agreementDto.getTrade().setEventId(event.getEventId());
        agreementDto.getTrade().setResourceUri(event.getResourceUri());
        agreementDto.setEventType(event.getEventType());
        agreementDto.setFlowStatus(TRADE_DATA_RECEIVED);
        agreementDto.setProcessingStatus(CREATED);
        agreementService.saveAgreement(eventMapper.toAgreementEntity(agreementDto));
    }

    private void processTradeCanceledEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/agreements/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        String agreementId = resourceUri.substring(resourceUri.lastIndexOf('/') + 1);
        Optional<Agreement> agreementOptional = agreementService.findByAgreementId(agreementId);
        agreementOptional.ifPresent(agreement -> {
            agreement.setEventType(event.getEventType());
            agreement.setProcessingStatus(CANCELED);
            agreementService.saveAgreement(agreement);
        });

        List<Position> positions = positionService.getByMatchingTradeAgreementId(agreementId);
        positions.forEach(position -> {
            position.setProcessingStatus(ProcessingStatus.TRADE_CANCELED);
            positionService.savePosition(position);
            recordCloudEvent(agreementId, position, TRADE_AGREEMENT_CANCELED);
        });
    }

    private void processContractEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/contracts/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            oneSourceService.findContract(resourceUri)
                .ifPresent(contract -> processContract(contract, event));
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

    private void processContract(Contract contract, TradeEvent event) {
        contract.getTrade().setEventId(event.getEventId());
        contract.getTrade().setResourceUri(event.getResourceUri());
        contract.setEventType(event.getEventType());
        contract.setProcessingStatus(NEW);
        contract.setFlowStatus(TRADE_DATA_RECEIVED);
        contract.setLastEvent(event);
        storeContract(contract);
    }

    private void updateEventStatus(TradeEvent event, ProcessingStatus status) {
        event.setProcessingStatus(status);
        tradeEventRepository.save(event);
    }

    private Contract storeContract(Contract contract) {
        final Contract savedContract = contractService.save(contract);
        log.debug("Contract: {} with processingStatus: {} was saved!",
            contract.getContractId(), contract.getProcessingStatus());
        return savedContract;
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

    void recordCloudEvent(String id, Position position, RecordType recordType) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(id,
            recordType, position.getPositionId());
        cloudEventRecordService.record(recordRequest);
    }
}