package com.intellecteu.onesource.integration.routes.rerate.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RERATE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_RERATE_APPROVED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_RERATE_APPLIED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_RERATE_DECLINED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.APPLIED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.APPROVED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DECLINED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_APPLIED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_DECLINED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_INTEGRATION_TOOLKIT;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.HTTP_STATUS_TEXT;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.RERATE_ID;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.RESOURCE_URI;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.TRADE_ID;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.RerateService;
import com.intellecteu.onesource.integration.services.RerateTradeService;
import com.intellecteu.onesource.integration.services.TradeEventService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final RerateCloudEventBuilder eventBuilder;

    @Autowired
    public RerateEventProcessor(OneSourceService oneSourceService, TradeEventService tradeEventService,
        RerateService rerateService, RerateTradeService rerateTradeService,
        CloudEventRecordService cloudEventRecordService) {
        this.oneSourceService = oneSourceService;
        this.tradeEventService = tradeEventService;
        this.rerateService = rerateService;
        this.rerateTradeService = rerateTradeService;
        this.cloudEventRecordService = cloudEventRecordService;
        this.eventBuilder = (RerateCloudEventBuilder) cloudEventRecordService.getFactory().eventBuilder(RERATE);
    }

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
                recordHttpExceptionCloudEvent(GET_RERATE_PROPOSAL, TECHNICAL_EXCEPTION_1SOURCE, resourceUri, e);
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
                recordHttpExceptionCloudEvent(GET_RERATE_PROPOSAL, TECHNICAL_EXCEPTION_1SOURCE, resourceUri, e);
            }
        } catch (EntityNotFoundException e) {
            log.debug("Rerate Entity with id {} was not found. Details: {} ", getRerateId(resourceUri), e.getMessage());
            recordRerateEntityNotFoundTechnicalException(GET_RERATE_APPROVED, TECHNICAL_EXCEPTION_INTEGRATION_TOOLKIT,
                resourceUri);
        }
        return event;
    }

    public TradeEvent processRerateAppliedEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/rerates/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            String rerateId = getRerateId(resourceUri);
            Rerate rerate = rerateService.getByRerateId(rerateId);
            rerate.setLastUpdateDatetime(LocalDateTime.now());
            rerate.setProcessingStatus(APPLIED);
            rerateService.saveRerate(rerate);
            recordRerateAppliedCloudEvent(rerate);
        } catch (EntityNotFoundException e) {
            log.debug("Rerate Entity with id {} was not found. Details: {} ", getRerateId(resourceUri), e.getMessage());
            recordRerateEntityNotFoundTechnicalException(PROCESS_RERATE_APPLIED,
                TECHNICAL_EXCEPTION_INTEGRATION_TOOLKIT, resourceUri);
        }
        return event;
    }

    public TradeEvent processRerateDeclinedEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/rerates/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            String rerateId = getRerateId(resourceUri);
            Rerate rerate = rerateService.getByRerateId(rerateId);
            rerate.setLastUpdateDatetime(LocalDateTime.now());
            rerate.setProcessingStatus(DECLINED);
            rerateService.saveRerate(rerate);
            if (rerate.getMatchingSpireTradeId() != null) {
                RerateTrade rerateTrade = rerateTradeService.getByTradeId(rerate.getMatchingSpireTradeId());
                rerateTrade.setLastUpdateDatetime(LocalDateTime.now());
                rerateTrade.setMatchingRerateId(null);
                rerateTradeService.save(rerateTrade);
            }
            recordRerateDeclinedCloudEvent(rerate);
        } catch (EntityNotFoundException e) {
            log.debug("Rerate Entity with id {} was not found. Details: {} ", getRerateId(resourceUri), e.getMessage());
            recordRerateEntityNotFoundTechnicalException(PROCESS_RERATE_DECLINED,
                TECHNICAL_EXCEPTION_INTEGRATION_TOOLKIT, resourceUri);
        }
        return event;
    }

    private String getRerateId(String resourceUri) {
        if (resourceUri.endsWith("/")) {
            resourceUri = resourceUri.substring(0, resourceUri.length() - 1);
        }
        return resourceUri.substring(resourceUri.lastIndexOf("/") + 1);
    }

    private void recordRerateEntityNotFoundTechnicalException(IntegrationSubProcess subProcess, RecordType recordType,
        String resourceURI) {
        Map<String, String> data = Map.of("resourceURI", resourceURI);
        var recordRequest = eventBuilder.buildRequest(subProcess, recordType, data, List.of());
        cloudEventRecordService.record(recordRequest);
    }

    private void recordHttpExceptionCloudEvent(IntegrationSubProcess subProcess, RecordType recordType,
        String resourceUri, HttpStatusCodeException e) {
        Map<String, String> data = new HashMap<>();
        data.put(HTTP_STATUS_TEXT, e.getStatusText());
        data.put(RESOURCE_URI, resourceUri);
        var recordRequest = eventBuilder.buildRequest(subProcess, recordType,
            data, List.of());
        cloudEventRecordService.record(recordRequest);
    }

    private void recordRerateApprovedCloudEvent(Rerate rerate) {
        Map<String, String> data = new HashMap<>();
        data.put(RERATE_ID, rerate.getRerateId());
        data.put(TRADE_ID, String.valueOf(rerate.getMatchingSpireTradeId()));
        var recordRequest = eventBuilder.buildRequest(GET_RERATE_APPROVED, RERATE_PROPOSAL_APPROVED,
            data, List.of());
        cloudEventRecordService.record(recordRequest);
    }

    private void recordRerateAppliedCloudEvent(Rerate rerate) {
        var data = new HashMap<String, String>();
        data.put(RERATE_ID, rerate.getRerateId());
        data.put(TRADE_ID, String.valueOf(rerate.getMatchingSpireTradeId()));
        data.put("contractId", rerate.getContractId());
        var recordRequest = eventBuilder.buildRequest(PROCESS_RERATE_APPLIED, RERATE_PROPOSAL_APPLIED, data, List.of());
        cloudEventRecordService.record(recordRequest);
    }

    private void recordRerateDeclinedCloudEvent(Rerate rerate) {
        var data = new HashMap<String, String>();
        data.put(RERATE_ID, rerate.getRerateId());
        var recordRequest = eventBuilder.buildRequest(PROCESS_RERATE_DECLINED, RERATE_PROPOSAL_DECLINED, data,
            List.of());
        cloudEventRecordService.record(recordRequest);
    }

}
