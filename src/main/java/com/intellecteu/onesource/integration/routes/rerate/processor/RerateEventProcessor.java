package com.intellecteu.onesource.integration.routes.rerate.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RERATE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.*;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.*;
import static com.intellecteu.onesource.integration.model.enums.RecordType.*;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.CONTRACT_ID;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.HTTP_STATUS_TEXT;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.POSITION_ID;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.RERATE_ID;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.RESOURCE_URI;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.TRADE_ID;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwExceptionForRedeliveryPolicy;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.toStringNullSafe;

import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.model.onesource.RerateStatus;
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
            recordHttpExceptionCloudEvent(GET_RERATE_PROPOSAL, TECHNICAL_EXCEPTION_1SOURCE, resourceUri, e);
            throwExceptionForRedeliveryPolicy(e);
        }
        return event;
    }

    public TradeEvent processReratePendingEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/contracts/a3836adb-3c98-43ab-b6c7-308f7fbac399/rerates/cc010eaf-cba4-4756-a897-da340e70ab98
        String resourceUri = event.getResourceUri();
        try {
            String rerateId = getRerateId(resourceUri);
            Rerate rerate = rerateService.getByRerateId(rerateId);
            if(!APPLIED.equals(rerate.getProcessingStatus())) {
                rerate.setLastUpdateDatetime(LocalDateTime.now());
                rerate.setRerateStatus(RerateStatus.PENDING);
                rerate.setProcessingStatus(APPROVED);
                rerateService.saveRerate(rerate);
            }

            RerateTrade rerateTrade = rerateTradeService.getByTradeId(rerate.getMatchingSpireTradeId());
            rerateTrade.setProcessingStatus(PROPOSAL_APPROVED);
            rerateTrade.setLastUpdateDatetime(LocalDateTime.now());
            rerateTradeService.save(rerateTrade);
            recordRerateApprovedCloudEvent(rerate);
        } catch (HttpStatusCodeException e) {
            log.debug("Rerate {} was not found. Details: {} ", resourceUri, e.getMessage());
            recordHttpExceptionCloudEvent(GET_RERATE_PROPOSAL, TECHNICAL_EXCEPTION_1SOURCE, resourceUri, e);
        } catch (EntityNotFoundException e) {
            log.debug("Rerate Entity with id {} was not found. Details: {} ", getRerateId(resourceUri), e.getMessage());
            recordRerateEntityNotFoundTechnicalException(GET_RERATE_APPROVED, TECHNICAL_ISSUE_INTEGRATION_TOOLKIT,
                resourceUri);
        }
        return event;
    }

    public TradeEvent processRerateAppliedEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/contracts/a3836adb-3c98-43ab-b6c7-308f7fbac399
        String resourceUri = event.getResourceUri();
        try {
            String contractId = getContractId(resourceUri);
            Rerate rerate = rerateService.findRerateByContractIdAndProcessingStatuses(contractId, List.of(APPROVED, VALIDATED, MATCHED, APPROVAL_SUBMITTED));
            rerate.setLastUpdateDatetime(LocalDateTime.now());
            rerate.setRerateStatus(RerateStatus.APPLIED);
            rerate.setProcessingStatus(APPLIED);
            rerateService.saveRerate(rerate);
            recordRerateAppliedCloudEvent(rerate);
        } catch (EntityNotFoundException e) {
            log.debug("Rerate Entity with contract id {} was not found. Details: {} ", getContractId(resourceUri), e.getMessage());
            recordRerateEntityNotFoundTechnicalException(PROCESS_RERATE_APPLIED,
                TECHNICAL_ISSUE_INTEGRATION_TOOLKIT, resourceUri);
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
                TECHNICAL_ISSUE_INTEGRATION_TOOLKIT, resourceUri);
        }
        return event;
    }

    public TradeEvent processRerateCanceledEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/rerates/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            String rerateId = getRerateId(resourceUri);
            Rerate rerate = rerateService.getByRerateId(rerateId);
            if (!APPROVED.equals(rerate.getProcessingStatus())) {
                if (rerate.getMatchingSpireTradeId() != null) {
                    RerateTrade rerateTrade = rerateTradeService.getByTradeId(rerate.getMatchingSpireTradeId());
                    rerateTrade.setLastUpdateDatetime(LocalDateTime.now());
                    rerateTrade.setMatchingRerateId(null);
                    rerateTradeService.save(rerateTrade);
                }
                recordRerateProposalCanceledCloudEvent(rerate);
            }else{
                recordRerateCanceledCloudEvent(rerate);
            }
            rerate.setLastUpdateDatetime(LocalDateTime.now());
            rerate.setProcessingStatus(CANCELED);
            rerateService.saveRerate(rerate);
        } catch (EntityNotFoundException e) {
            log.debug("Rerate Entity with id {} was not found. Details: {} ", getRerateId(resourceUri), e.getMessage());
            recordRerateEntityNotFoundTechnicalException(PROCESS_RERATE_PROPOSAL_CANCELED,
                TECHNICAL_ISSUE_INTEGRATION_TOOLKIT, resourceUri);
        }
        return event;
    }

    public TradeEvent processReratePendingCancelEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/rerates/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            String rerateId = getRerateId(resourceUri);
            Rerate rerate = rerateService.getByRerateId(rerateId);
            rerate.setLastUpdateDatetime(LocalDateTime.now());
            rerate.setProcessingStatus(CANCEL_PENDING);
            rerateService.saveRerate(rerate);
            recordRerateCancelPendingCloudEvent(rerate);
        } catch (EntityNotFoundException e) {
            log.debug("Rerate Entity with id {} was not found. Details: {} ", getRerateId(resourceUri), e.getMessage());
            recordRerateEntityNotFoundTechnicalException(PROCESS_RERATE_CANCEL_PENDING,
                TECHNICAL_ISSUE_INTEGRATION_TOOLKIT, resourceUri);
        }
        return event;
    }

    private String getRerateId(String resourceUri) {
        if (resourceUri.endsWith("/")) {
            resourceUri = resourceUri.substring(0, resourceUri.length() - 1);
        }
        return resourceUri.substring(resourceUri.lastIndexOf("/") + 1);
    }

    private String getContractId(String resourceUri) {
        //TODO we should change this implementation to regex because we don't control format of received uri
        if (resourceUri.endsWith("/")) {
            resourceUri = resourceUri.substring(0, resourceUri.length() - 1);
        }
        return resourceUri.substring(resourceUri.lastIndexOf("/") + 1);
    }

    private void recordRerateEntityNotFoundTechnicalException(IntegrationSubProcess subProcess, RecordType recordType,
        String resourceURI) {
        Map<String, String> data = Map.of(RESOURCE_URI, resourceURI);
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
        data.put(TRADE_ID, toStringNullSafe(rerate.getMatchingSpireTradeId()));
        data.put(POSITION_ID, toStringNullSafe(rerate.getRelatedSpirePositionId()));
        data.put(CONTRACT_ID, rerate.getContractId());
        var recordRequest = eventBuilder.buildRequest(GET_RERATE_APPROVED, RERATE_PROPOSAL_APPROVED,
            data, List.of());
        cloudEventRecordService.record(recordRequest);
    }

    private void recordRerateAppliedCloudEvent(Rerate rerate) {
        var data = new HashMap<String, String>();
        data.put(RERATE_ID, rerate.getRerateId());
        data.put(POSITION_ID, toStringNullSafe(rerate.getRelatedSpirePositionId()));
        data.put(TRADE_ID, toStringNullSafe(rerate.getMatchingSpireTradeId()));
        data.put(CONTRACT_ID, rerate.getContractId());
        var recordRequest = eventBuilder.buildRequest(PROCESS_RERATE_APPLIED, RERATE_APPLIED, data, List.of());
        cloudEventRecordService.record(recordRequest);
    }

    private void recordRerateDeclinedCloudEvent(Rerate rerate) {
        var data = new HashMap<String, String>();
        data.put(RERATE_ID, rerate.getRerateId());
        data.put(POSITION_ID, toStringNullSafe(rerate.getRelatedSpirePositionId()));
        data.put(CONTRACT_ID, rerate.getContractId());
        var recordRequest = eventBuilder.buildRequest(PROCESS_RERATE_DECLINED, RERATE_PROPOSAL_DECLINED, data,
            List.of());
        cloudEventRecordService.record(recordRequest);
    }

    private void recordRerateProposalCanceledCloudEvent(Rerate rerate) {
        var data = new HashMap<String, String>();
        data.put(RERATE_ID, rerate.getRerateId());
        data.put(POSITION_ID, toStringNullSafe(rerate.getRelatedSpirePositionId()));
        data.put(CONTRACT_ID, rerate.getContractId());
        var recordRequest = eventBuilder.buildRequest(PROCESS_RERATE_PROPOSAL_CANCELED, RERATE_PROPOSAL_CANCELED, data,
            List.of());
        cloudEventRecordService.record(recordRequest);
    }

    private void recordRerateCanceledCloudEvent(Rerate rerate) {
        var data = new HashMap<String, String>();
        data.put(RERATE_ID, rerate.getRerateId());
        data.put(POSITION_ID, toStringNullSafe(rerate.getRelatedSpirePositionId()));
        data.put(CONTRACT_ID, rerate.getContractId());
        var recordRequest = eventBuilder.buildRequest(PROCESS_RERATE_CANCELED, RERATE_CANCELED, data,
            List.of());
        cloudEventRecordService.record(recordRequest);
    }

    private void recordRerateCancelPendingCloudEvent(Rerate rerate) {
        var data = new HashMap<String, String>();
        data.put(RERATE_ID, rerate.getRerateId());
        data.put(POSITION_ID, toStringNullSafe(rerate.getRelatedSpirePositionId()));
        data.put(CONTRACT_ID, rerate.getContractId());
        var recordRequest = eventBuilder.buildRequest(PROCESS_RERATE_CANCEL_PENDING, RERATE_CANCEL_PENDING_CONFIRMATION, data,
            List.of());
        cloudEventRecordService.record(recordRequest);
    }
}
