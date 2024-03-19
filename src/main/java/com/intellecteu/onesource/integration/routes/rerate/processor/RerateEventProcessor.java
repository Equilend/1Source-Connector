package com.intellecteu.onesource.integration.routes.rerate.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RERATE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_RERATE_APPROVED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_RERATE_APPLIED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.APPLIED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.APPROVED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_APPLIED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_INTEGRATION_TOOLKIT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.RerateService;
import com.intellecteu.onesource.integration.services.RerateTradeService;
import com.intellecteu.onesource.integration.services.TradeEventService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Component
@Slf4j
@RequiredArgsConstructor
public class RerateEventProcessor {

    private final OneSourceService oneSourceService;
    private final TradeEventService tradeEventService;
    private final RerateService rerateService;
    private final RerateTradeService rerateTradeService;
    private final CloudEventRecordService cloudEventRecordService;

    public TradeEvent saveEvent(TradeEvent event) {
        return tradeEventService.saveTradeEvent(event);
    }

    public TradeEvent updateEventProcessingStatus(TradeEvent event, ProcessingStatus status) {
        event.setProcessingStatus(status);
        return event;
    }

    public TradeEvent processRerateProposedEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/rerates/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            Rerate rerate = oneSourceService.retrieveRerate(resourceUri);
            rerate.setCreateUpdateDatetime(LocalDateTime.now());
            rerate.setLastUpdateDatetime(LocalDateTime.now());
            rerate.setProcessingStatus(ProcessingStatus.PROPOSED);
            rerateService.saveRerate(rerate);
        } catch (HttpStatusCodeException e) {
            log.debug("Rerate {} was not found. Details: {} ", resourceUri, e.getMessage());
            if (Set.of(UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR).contains(e.getStatusCode())) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(RERATE);
                var recordRequest = eventBuilder.buildExceptionRequest(e, GET_RERATE_PROPOSAL, resourceUri);
                cloudEventRecordService.record(recordRequest);
            }
        }
        return event;
    }

    public TradeEvent processReratePendingEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/rerates/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            Rerate rerateUpdate = oneSourceService.retrieveRerate(resourceUri);
            Rerate rerate = rerateService.getByRerateId(rerateUpdate.getRerateId());
            rerate = rerateService.mergeRerate(rerate, rerateUpdate);
            rerate.setLastUpdateDatetime(LocalDateTime.now());
            rerate.setProcessingStatus(APPROVED);
            rerateService.saveRerate(rerate);

            RerateTrade rerateTrade = rerateTradeService.getByTradeId(rerate.getMatchingSpireTradeId());
            rerateTrade.setProcessingStatus(PROPOSAL_APPROVED);
            rerateTrade.setLastUpdateDatetime(LocalDateTime.now());
            rerateTradeService.save(rerateTrade);
            recordRerateApprovedCloudEvent(rerate);
        } catch (HttpStatusCodeException e) {
            log.debug("Rerate {} was not found. Details: {} ", resourceUri, e.getMessage());
            if (Set.of(UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR).contains(e.getStatusCode())) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(RERATE);
                var recordRequest = eventBuilder.buildExceptionRequest(e, GET_RERATE_PROPOSAL, resourceUri);
                cloudEventRecordService.record(recordRequest);
            }
        } catch (EntityNotFoundException e) {
            recordApproveRerateTechnicalException(resourceUri);
        }
        return event;
    }

    public TradeEvent processRerateAppliedEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/rerates/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            Rerate rerateUpdate = oneSourceService.retrieveRerate(resourceUri);
            Rerate rerate = rerateService.getByRerateId(rerateUpdate.getRerateId());
            rerate = rerateService.mergeRerate(rerate, rerateUpdate);
            rerate.setLastUpdateDatetime(LocalDateTime.now());
            rerate.setProcessingStatus(APPLIED);
            rerateService.saveRerate(rerate);
            recordRerateAppliedCloudEvent(rerate);
        } catch (HttpStatusCodeException e) {
            log.debug("Rerate {} was not found. Details: {} ", resourceUri, e.getMessage());
            if (Set.of(UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR).contains(e.getStatusCode())) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(RERATE);
                var recordRequest = eventBuilder.buildExceptionRequest(e, GET_RERATE_PROPOSAL, resourceUri);
                cloudEventRecordService.record(recordRequest);
            }
        } catch (EntityNotFoundException e) {
            recordApproveRerateTechnicalException(resourceUri);
        }
        return event;
    }

    private void recordRerateApprovedCloudEvent(Rerate rerate) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(RERATE);
        var recordRequest = eventBuilder.buildRequest(rerate.getRerateId(),
            RERATE_PROPOSAL_APPROVED, String.valueOf(rerate.getMatchingSpireTradeId()));
        cloudEventRecordService.record(recordRequest);
    }

    private void recordApproveRerateTechnicalException(String resourceURI) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(RERATE);
        var data = new HashMap<String, String>();
        data.put("resourceURI", resourceURI);
        var recordRequest = eventBuilder.buildRequest(GET_RERATE_APPROVED, TECHNICAL_EXCEPTION_INTEGRATION_TOOLKIT,
            data, List.of());
        cloudEventRecordService.record(recordRequest);
    }

    private void recordRerateAppliedCloudEvent(Rerate rerate) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(RERATE);
        var data = new HashMap<String, String>();
        data.put("rerateId", rerate.getRerateId());
        data.put("tradeId", String.valueOf(rerate.getMatchingSpireTradeId()));
        data.put("contractId", rerate.getContractId());
        var recordRequest = eventBuilder.buildRequest(PROCESS_RERATE_APPLIED, RERATE_PROPOSAL_APPLIED, data, List.of());
        cloudEventRecordService.record(recordRequest);
    }

}
