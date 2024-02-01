package com.intellecteu.onesource.integration.routes.processor;

import static com.intellecteu.onesource.integration.enums.FlowStatus.TRADE_DATA_RECEIVED;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.RERATE;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_CANCELED;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_CREATED;
import static com.intellecteu.onesource.integration.model.ContractStatus.PROPOSED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.NEW;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.isLender;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.Rerate;
import com.intellecteu.onesource.integration.model.TradeEvent;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.services.AgreementService;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.OneSourceServiceTrueService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.RerateService;
import com.intellecteu.onesource.integration.services.TradeEventService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessor {

    private final TradeEventService tradeEventService;
    private final OneSourceServiceTrueService oneSourceService;
    private final BackOfficeService lenderBackOfficeService;
    private final AgreementService agreementService;
    private final ContractService contractService;
    private final PositionService positionService;
    private final RerateService rerateService;
    private final CloudEventRecordService cloudEventRecordService;

    public TradeEvent saveEvent(TradeEvent event) {
        return tradeEventService.saveTradeEvent(event);
    }

    public TradeEvent updateEventStatus(TradeEvent event, ProcessingStatus status) {
        event.setProcessingStatus(status);
        return event;
    }

    public void consumeEvents() {
        log.debug(">>>>> Pulling events!");
        LocalDateTime lastEventDatetime = tradeEventService.getLastEventDatetime();
        log.debug("Timestamp: " + lastEventDatetime);
        List<TradeEvent> newEvents = oneSourceService.retrieveEvents(lastEventDatetime);
        newEvents.forEach(event -> event.setProcessingStatus(CREATED));
        tradeEventService.saveTradeEvents(newEvents);
        tradeEventService.updateLastEventDatetime(newEvents);
        log.debug("<<<<< Retrieved {} new events!", newEvents.size());
    }

    public void processTradeEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/agreements/93f834ff-66b5-4195-892b-8f316ed77010
        String eventUri = event.getResourceUri();
        Optional<Agreement> agreementOptional = oneSourceService.retrieveTradeAgreement(eventUri, event.getEventType());
        agreementOptional
            .ifPresent(agreement -> {
                agreement = enrichAgreement(agreement, event);
                agreementService.saveAgreement(agreement);
                recordCloudEvent(eventUri);
            });
    }

    public void processContractEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/contracts/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            oneSourceService.retrieveContract(resourceUri)
                .ifPresent(contract -> {
                    contract = enrichContract(contract, event);
                    contractService.save(contract);
                });
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

    public void processTradeCanceledEvent(TradeEvent event) {
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

    public void processRerateEvent(TradeEvent event){
        // expected format for resourceUri: /v1/ledger/rerates/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            oneSourceService.retrieveRerate(resourceUri)
                .ifPresent(rerate -> {
                    rerate.setProcessingStatus(CREATED);
                    rerateService.saveRerate(rerate);
                });
        } catch (HttpStatusCodeException e) {
            log.debug("Rerate {} was not found. Details: {} ", resourceUri, e.getMessage());
            if (Set.of(UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR).contains(e.getStatusCode())) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(RERATE);
                var recordRequest = eventBuilder.buildExceptionRequest(e, GET_RERATE_PROPOSAL, resourceUri);
                cloudEventRecordService.record(recordRequest);
            }
        }
    }

    public void cancelContract() {
        log.debug(">>>>> Starting the Cancel contract process.");
        List<Contract> proposedContracts = contractService.findAllByContractStatus(PROPOSED);
        log.debug("Retrieved {} candidatesToCancel in Proposed status.", proposedContracts.size());
        for (Contract contract : proposedContracts) {
            log.debug("Requesting Spire position!");
            //TODO Change logic -> position listener update positions and here we using repository instead of API
            final String venueRefId = contract.getTrade().getVenue().getVenueRefKey();
            List<Position> positions = lenderBackOfficeService.getPositionByVenueRefId(venueRefId);
            if (!positions.isEmpty()) {
                Position position = positions.get(0);
                if (isLender(position) && position.getPositionStatus() != null
                    && PositionService.CANCEL_POSITION_STATUSES.contains(position.getPositionStatus().getStatus())) {
                    log.debug("Executing cancellation for contract with id {} and position with id {}",
                        contract.getContractId(), position.getPositionId());
                    oneSourceService.cancelContract(contract, position.getPositionId());
                }
            }
        }
        log.debug("<<<<< Finishing Cancel contract process.");
    }

    private Agreement enrichAgreement(Agreement agreement, TradeEvent event) {
        agreement.getTrade().setEventId(event.getEventId());
        agreement.getTrade().setResourceUri(event.getResourceUri());
        agreement.setEventType(event.getEventType());
        agreement.setFlowStatus(TRADE_DATA_RECEIVED);
        agreement.setProcessingStatus(CREATED);
        return agreement;
    }

    private Contract enrichContract(Contract contract, TradeEvent event) {
        contract.getTrade().setEventId(event.getEventId());
        contract.getTrade().setResourceUri(event.getResourceUri());
        contract.setEventType(event.getEventType());
        contract.setProcessingStatus(NEW);
        contract.setFlowStatus(TRADE_DATA_RECEIVED);
        contract.setLastEvent(event);
        return contract;
    }

    private void recordCloudEvent(String record) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(record, TRADE_AGREEMENT_CREATED);
        cloudEventRecordService.record(recordRequest);
    }

    void recordCloudEvent(String id, Position position, RecordType recordType) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(id,
            recordType, position.getPositionId());
        cloudEventRecordService.record(recordRequest);
    }
}