package com.intellecteu.onesource.integration.routes.returns.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RETURN;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_RETURN;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder.HTTP_STATUS_TEXT;
import static com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder.RESOURCE_URI;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwExceptionForRedeliveryPolicy;

import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.onesource.Return;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.ReturnService;
import com.intellecteu.onesource.integration.services.TradeEventService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Component
@Slf4j
public class ReturnEventProcessor {

    private final ReturnService returnService;
    private final TradeEventService tradeEventService;
    private final OneSourceService oneSourceService;
    private final CloudEventRecordService cloudEventRecordService;
    private final ReturnCloudEventBuilder eventBuilder;

    @Autowired
    public ReturnEventProcessor(ReturnService returnService, TradeEventService tradeEventService,
        OneSourceService oneSourceService, CloudEventRecordService cloudEventRecordService) {
        this.returnService = returnService;
        this.tradeEventService = tradeEventService;
        this.oneSourceService = oneSourceService;
        this.cloudEventRecordService = cloudEventRecordService;
        this.eventBuilder = (ReturnCloudEventBuilder) cloudEventRecordService.getFactory().eventBuilder(RETURN);
    }

    public TradeEvent saveEventWithProcessingStatus(TradeEvent event, ProcessingStatus status) {
        event.setProcessingStatus(status);
        return tradeEventService.saveTradeEvent(event);
    }

    public TradeEvent processReturnPendingEvent(TradeEvent event) {
        String resourceUri = event.getResourceUri();
        try {
            Return oneSourceReturn = oneSourceService.retrieveReturn(resourceUri);
            oneSourceReturn.setProcessingStatus(ProcessingStatus.CREATED);
            oneSourceReturn.setCreateUpdateDatetime(LocalDateTime.now());
            oneSourceReturn.setLastUpdateDatetime(LocalDateTime.now());
            returnService.saveReturn(oneSourceReturn);
        } catch (HttpStatusCodeException e) {
            log.debug("Rerate {} was not found. Details: {} ", resourceUri, e.getMessage());
            recordHttpExceptionCloudEvent(GET_RETURN, TECHNICAL_EXCEPTION_1SOURCE, resourceUri, e);
            throwExceptionForRedeliveryPolicy(e);
        }
        return event;
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


}
