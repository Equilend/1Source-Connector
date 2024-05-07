package com.intellecteu.onesource.integration.routes.delegate_flow.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_CANCELLATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_DECLINED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_LOAN_CONTRACT_PENDING_CANCELLATION;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_ISSUE_INTEGRATION_TOOLKIT;

import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.TradeEventService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessor {

    private final TradeEventService tradeEventService;
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
        return saveEvent(event);
    }

    public void recordContractDeclineIssue(TradeEvent event) {
        String resourceUri = event.getResourceUri();
        cloudEventRecordService.getToolkitCloudEventId(resourceUri,
                GET_LOAN_CONTRACT_DECLINED, TECHNICAL_ISSUE_INTEGRATION_TOOLKIT)
            .ifPresentOrElse(
                cloudEventRecordService::updateTime,
                () -> recordToolkitTechnicalEvent(CONTRACT_INITIATION, resourceUri, GET_LOAN_CONTRACT_DECLINED));
    }

    public void recordCancelPendingIssue(TradeEvent event) {
        String resourceUri = event.getResourceUri();
        cloudEventRecordService.getToolkitCloudEventId(resourceUri,
                PROCESS_LOAN_CONTRACT_PENDING_CANCELLATION, TECHNICAL_ISSUE_INTEGRATION_TOOLKIT)
            .ifPresentOrElse(
                cloudEventRecordService::updateTime,
                () -> recordToolkitTechnicalEvent(CONTRACT_CANCELLATION, resourceUri,
                    PROCESS_LOAN_CONTRACT_PENDING_CANCELLATION));
    }

    public void recordContractCancelIssue(TradeEvent event) {
        String resourceUri = event.getResourceUri();
        cloudEventRecordService.getToolkitCloudEventId(resourceUri,
                GET_LOAN_CONTRACT_CANCELED, TECHNICAL_ISSUE_INTEGRATION_TOOLKIT)
            .ifPresentOrElse(
                cloudEventRecordService::updateTime,
                () -> recordToolkitTechnicalEvent(CONTRACT_INITIATION, resourceUri, GET_LOAN_CONTRACT_CANCELED));
    }

    private void recordToolkitTechnicalEvent(IntegrationProcess process, String record,
        IntegrationSubProcess subProcess) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(process);
        var recordRequest = eventBuilder.buildToolkitIssueRequest(record, subProcess);
        cloudEventRecordService.record(recordRequest);
    }
}