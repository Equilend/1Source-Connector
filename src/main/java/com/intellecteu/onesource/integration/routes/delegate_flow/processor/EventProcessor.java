package com.intellecteu.onesource.integration.routes.delegate_flow.processor;

import static com.intellecteu.onesource.integration.model.enums.FlowStatus.TRADE_DATA_RECEIVED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_DECLINED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.NEW;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TRADE_AGREEMENT_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TRADE_AGREEMENT_CREATED;
import static com.intellecteu.onesource.integration.model.onesource.ContractStatus.PROPOSED;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.isLender;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.AgreementService;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.TradeEventService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessor {

    private final TradeEventService tradeEventService;
    private final OneSourceService oneSourceService;
    private final BackOfficeService lenderBackOfficeService;
    private final AgreementService agreementService;
    private final ContractService contractService;
    private final PositionService positionService;
    private final CloudEventRecordService cloudEventRecordService;

    public TradeEvent saveEvent(TradeEvent event) {
        return tradeEventService.saveTradeEvent(event);
    }

    /**
     * Update event status and persist event
     *
     * @param event TradeEvent
     * @param status ProcessingStatus
     * @return persisted TradeEvent model
     */
    public TradeEvent updateEventStatus(TradeEvent event, ProcessingStatus status) {
        event.setProcessingStatus(status);
        return tradeEventService.saveTradeEvent(event);
    }

    public void processTradeEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/agreements/93f834ff-66b5-4195-892b-8f316ed77010
        String eventUri = event.getResourceUri();
        Optional<Agreement> agreementOptional = oneSourceService.retrieveTradeAgreement(eventUri, event.getEventType());
        agreementOptional
            .map(a -> enrichAgreement(a, event))
            .map(agreementService::saveAgreement)
            .ifPresent(agreement -> recordCloudEvent(eventUri));
    }

    @Deprecated(since = "0.0.5-SNAPSHOT", forRemoval = true)
    @Transactional
    public void processContractEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/contracts/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
//            oneSourceService.retrieveContractDetails(resourceUri)
//                .ifPresent(contract -> {
//                    Contract enrichedContract = enrichContract(contract, event);
//                    contractService.save(enrichedContract);
//                });
        } catch (HttpStatusCodeException e) {
            log.debug("Contract {} was not found. Details: {} ", resourceUri, e.getMessage());
            if (Set.of(UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR).contains(e.getStatusCode())) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                var recordRequest = eventBuilder.buildExceptionRequest(resourceUri,
                    e, GET_LOAN_CONTRACT_PROPOSAL, event.getEventId());
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
//            position.setProcessingStatus(ProcessingStatus.TRADE_CANCELED);
            positionService.savePosition(position);
            recordCloudEvent(agreementId, position, TRADE_AGREEMENT_CANCELED);
        });
    }

    public void cancelContract() {
        log.debug(">>>>> Starting the Cancel contract process.");
        List<Contract> proposedContracts = contractService.findAllByContractStatus(PROPOSED);
        log.debug("Retrieved {} candidatesToCancel in Proposed status.", proposedContracts.size());
        for (Contract contract : proposedContracts) {
            log.debug("Requesting Spire position!");
            //TODO Change logic -> position listener update positions and here we using repository instead of API
            final String venueRefId = contract.getTrade().getVenues().get(0).getVenueRefKey();
            List<Position> positions = lenderBackOfficeService.getPositionByVenueRefId(venueRefId);
            if (!positions.isEmpty()) {
                Position position = positions.get(0);
                if (isLender(position) && position.getPositionStatus() != null
                    && PositionService.CANCEL_POSITION_STATUSES.contains(position.getPositionStatus().getStatus())) {
                    log.debug("Executing cancellation for contract with id {} and position with id {}",
                        contract.getContractId(), position.getPositionId());
                    oneSourceService.cancelContract(contract, String.valueOf(position.getPositionId()));
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
//        contract.setEventType(event.getEventType());
        contract.setProcessingStatus(NEW);
//        contract.setFlowStatus(TRADE_DATA_RECEIVED);
//        contract.setLastEvent(event);
        return contract;
    }

    public void recordContractDeclineIssue(TradeEvent event) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildToolkitIssueRequest(event.getResourceUri(), GET_LOAN_CONTRACT_DECLINED);
        cloudEventRecordService.record(recordRequest);
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
            recordType, String.valueOf(position.getPositionId()));
        cloudEventRecordService.record(recordRequest);
    }
}