package com.intellecteu.onesource.integration.routes.rerate.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RERATE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.APPROVED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_APPROVED;
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
import java.time.LocalDateTime;
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

    public TradeEvent updateEventStatus(TradeEvent event, ProcessingStatus status) {
        event.setProcessingStatus(status);
        return event;
    }

    public TradeEvent processRerateProposedEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/rerates/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            oneSourceService.retrieveRerate(resourceUri)
                .ifPresent(rerate -> {
                    rerate.setCreateUpdateDatetime(LocalDateTime.now());
                    rerate.setLastUpdateDatetime(LocalDateTime.now());
                    rerate.setProcessingStatus(ProcessingStatus.PROPOSED);
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
        return event;
    }

    public TradeEvent processReratePendingEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/rerates/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            oneSourceService.retrieveRerate(resourceUri)
                .ifPresent(rerateUpdate -> {
                    Rerate rerate = rerateService.getByRerateId(rerateUpdate.getRerateId());
                    rerate = rerateService.mergeRerate(rerate, rerateUpdate);
                    rerateUpdate.setLastUpdateDatetime(LocalDateTime.now());
                    rerateUpdate.setProcessingStatus(APPROVED);
                    rerateService.saveRerate(rerate);

                    RerateTrade rerateTrade = rerateTradeService.getByTradeId(rerate.getMatchingSpireTradeId());
                    rerateTrade.setProcessingStatus(PROPOSAL_APPROVED);
                    rerateTrade.setLastUpdateDatetime(LocalDateTime.now());
                    rerateTradeService.save(rerateTrade);
                    recordRerateApprovedCloudEvent(rerate);
                });
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

    private void recordRerateApprovedCloudEvent(Rerate rerate) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(RERATE);
        var recordRequest = eventBuilder.buildRequest(rerate.getRerateId(),
            RERATE_PROPOSAL_APPROVED, String.valueOf(rerate.getMatchingSpireTradeId()));
        cloudEventRecordService.record(recordRequest);
    }

}
