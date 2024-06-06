package com.intellecteu.onesource.integration.routes.returns.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RETURN;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.CAPTURE_RETURN_ACKNOWLEDGEMENT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_RETURN;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_RETURN_ACKNOWLEDGEMENT_DETAILS;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.NEGATIVELY_ACKNOWLEDGED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.POSITIVELY_ACKNOWLEDGED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TO_CONFIRM;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_NEGATIVELY_ACKNOWLEDGED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_POSITIVELY_ACKNOWLEDGED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder.HTTP_STATUS_TEXT;
import static com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder.RESOURCE_URI;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwExceptionForRedeliveryPolicy;

import com.intellecteu.onesource.integration.model.backoffice.ReturnTrade;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.onesource.AcknowledgementType;
import com.intellecteu.onesource.integration.model.onesource.Return;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.ReturnService;
import com.intellecteu.onesource.integration.services.ReturnTradeService;
import com.intellecteu.onesource.integration.services.TradeEventService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder;
import com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder.DataBuilder;
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
    private final ReturnTradeService returnTradeService;
    private final CloudEventRecordService cloudEventRecordService;
    private final ReturnCloudEventBuilder eventBuilder;

    @Autowired
    public ReturnEventProcessor(ReturnService returnService, TradeEventService tradeEventService,
        ReturnTradeService returnTradeService,
        OneSourceService oneSourceService, CloudEventRecordService cloudEventRecordService) {
        this.returnService = returnService;
        this.tradeEventService = tradeEventService;
        this.oneSourceService = oneSourceService;
        this.returnTradeService = returnTradeService;
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
            oneSourceReturn.setProcessingStatus(CREATED);
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

    public TradeEvent processReturnAcknowledgedEvent(TradeEvent event) {
        String resourceUri = event.getResourceUri();
        try {
            Return oneSourceReturnUpdate = oneSourceService.retrieveReturn(resourceUri);
            Return oneSourceReturn = returnService.getByReturnId(oneSourceReturnUpdate.getReturnId());
            if (AcknowledgementType.POSITIVE.equals(oneSourceReturnUpdate.getAcknowledgementType())) {
                oneSourceReturn = returnService.merge(oneSourceReturn, oneSourceReturnUpdate);
                oneSourceReturn.setProcessingStatus(POSITIVELY_ACKNOWLEDGED);
                oneSourceReturn.setLastUpdateDatetime(LocalDateTime.now());
                if (oneSourceReturn.getMatchingSpireTradeId() != null) {
                    ReturnTrade returnTrade = returnTradeService.getByTradeId(
                        oneSourceReturn.getMatchingSpireTradeId());
                    if ("Return Loan".equals(returnTrade.getTradeOut().getTradeType())) {
                        returnTrade.setProcessingStatus(TO_CONFIRM);
                        returnTrade.setLastUpdateDatetime(LocalDateTime.now());
                        returnTradeService.save(returnTrade);
                    }
                }
                recordCloudEvent(CAPTURE_RETURN_ACKNOWLEDGEMENT, RETURN_POSITIVELY_ACKNOWLEDGED,
                    new ReturnCloudEventBuilder.DataBuilder()
                        .putReturnId(oneSourceReturn.getReturnId())
                        .putTradeId(oneSourceReturn.getMatchingSpireTradeId())
                        .putPositionId(oneSourceReturn.getRelatedSpirePositionId())
                        .putContractId(oneSourceReturn.getContractId()).getData(), List.of());
            } else {
                oneSourceReturn.setAcknowledgementType(oneSourceReturnUpdate.getAcknowledgementType());
                oneSourceReturn.setDescription(oneSourceReturnUpdate.getDescription());
                oneSourceReturn.setLastUpdateDatetime(LocalDateTime.now());
                oneSourceReturn.setProcessingStatus(NEGATIVELY_ACKNOWLEDGED);
                Map<String, String> data = new DataBuilder()
                    .putReturnId(oneSourceReturn.getReturnId())
                    .putTradeId(oneSourceReturn.getMatchingSpireTradeId())
                    .putPositionId(oneSourceReturn.getRelatedSpirePositionId())
                    .putContractId(oneSourceReturn.getContractId()).getData();
                data.put("description", oneSourceReturn.getDescription());
                recordCloudEvent(CAPTURE_RETURN_ACKNOWLEDGEMENT, RETURN_NEGATIVELY_ACKNOWLEDGED,
                    data, List.of());
            }
            returnService.saveReturn(oneSourceReturnUpdate);
        } catch (HttpStatusCodeException e) {
            log.debug("Rerate {} was not found. Details: {} ", resourceUri, e.getMessage());
            recordHttpExceptionCloudEvent(GET_RETURN_ACKNOWLEDGEMENT_DETAILS, TECHNICAL_EXCEPTION_1SOURCE, resourceUri,
                e);
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

    private void recordCloudEvent(IntegrationSubProcess subProcess, RecordType recordType,
        Map<String, String> data, List<FieldImpacted> fieldImpacteds) {
        var recordRequest = eventBuilder.buildRequest(subProcess, recordType, data, fieldImpacteds);
        cloudEventRecordService.record(recordRequest);
    }
}
