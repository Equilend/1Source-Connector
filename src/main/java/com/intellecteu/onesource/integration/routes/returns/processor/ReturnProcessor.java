package com.intellecteu.onesource.integration.routes.returns.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RETURN;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_NEW_RETURN_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_RETURN;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_TRADE_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_SPIRE;
import static com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder.CONTRACT_ID;
import static com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder.HTTP_STATUS_TEXT;
import static com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder.POSITION_ID;
import static com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder.TRADE_ID;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwExceptionForRedeliveryPolicy;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.toStringNullSafe;

import com.intellecteu.onesource.integration.model.backoffice.ReturnTrade;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.ReturnTradeService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Component
@Slf4j
public class ReturnProcessor {

    private final BackOfficeService backOfficeService;
    private final OneSourceService oneSourceService;
    private final ReturnTradeService returnTradeService;
    private final CloudEventRecordService cloudEventRecordService;
    private final ReturnCloudEventBuilder eventBuilder;

    @Autowired
    public ReturnProcessor(BackOfficeService backOfficeService, OneSourceService oneSourceService,
        ReturnTradeService returnTradeService,
        CloudEventRecordService cloudEventRecordService) {
        this.backOfficeService = backOfficeService;
        this.oneSourceService = oneSourceService;
        this.returnTradeService = returnTradeService;
        this.cloudEventRecordService = cloudEventRecordService;
        this.eventBuilder = (ReturnCloudEventBuilder) cloudEventRecordService.getFactory().eventBuilder(RETURN);
    }

    public ReturnTrade saveReturnTrade(ReturnTrade returnTrade) {
        returnTrade.setLastUpdateDatetime(LocalDateTime.now());
        return returnTradeService.save(returnTrade);
    }

    public ReturnTrade saveReturnTradeWithProcessingStatus(ReturnTrade returnTrade, ProcessingStatus processingStatus) {
        returnTrade.setProcessingStatus(processingStatus);
        return saveReturnTrade(returnTrade);
    }

    public ReturnTrade updateReturnTradeCreationDatetime(ReturnTrade returnTrade) {
        returnTrade.setCreationDatetime(LocalDateTime.now());
        return returnTrade;
    }

    public List<ReturnTrade> fetchNewReturnTrades() {
        Optional<Long> lastTradeId = returnTradeService.getMaxTradeId();
        List<ReturnTrade> rerateTradeList = new ArrayList<>();
        try {
            rerateTradeList = backOfficeService.retrieveReturnTrades(lastTradeId);
        } catch (HttpStatusCodeException exception) {
            recordHttpExceptionCloudEvent(GET_NEW_RETURN_PENDING_CONFIRMATION, TECHNICAL_EXCEPTION_SPIRE, exception,
                null, null);
        }
        return rerateTradeList;
    }

    public ReturnTrade postReturnTrade(ReturnTrade returnTrade) {
        try {
            oneSourceService.postReturnTrade(returnTrade);
            recordCloudEvent(POST_RETURN, RETURN_TRADE_SUBMITTED, returnTrade.getTradeId(),
                returnTrade.getRelatedPositionId(), returnTrade.getRelatedContractId(), List.of());
        } catch (HttpStatusCodeException exception) {
            recordHttpExceptionCloudEvent(POST_RETURN, TECHNICAL_EXCEPTION_1SOURCE, exception,
                returnTrade.getTradeId(), null);
            throwExceptionForRedeliveryPolicy(exception);
        }
        return returnTrade;
    }

    private void recordCloudEvent(IntegrationSubProcess subProcess, RecordType recordType, Long tradeId,
        Long positionId, String contractId, List<FieldImpacted> fieldImpacteds) {
        Map<String, String> data = new HashMap<>();
        if (tradeId != null) {
            data.put(TRADE_ID, toStringNullSafe(tradeId));
        }
        if (positionId != null) {
            data.put(POSITION_ID, toStringNullSafe(positionId));
        }
        if (contractId != null) {
            data.put(CONTRACT_ID, contractId);
        }
        //var recordRequest = eventBuilder.buildRequest(subProcess, recordType, data, List.of());
        //cloudEventRecordService.record(recordRequest);
        recordOrUpdateCloudEvent(subProcess, recordType, tradeId, data, fieldImpacteds);
    }

    private void recordOrUpdateCloudEvent(IntegrationSubProcess subProcess, RecordType recordType, Long tradeId,
        Map<String, String> data, List<FieldImpacted> fieldImpacteds) {
        String persistedCloudEventId = null;
        if (tradeId != null) {
            persistedCloudEventId = cloudEventRecordService // temporary hardcoded until related object will be captured
                .getToolkitCloudEventIdForRerateWorkaround("Trade - " + tradeId, subProcess, recordType)
                .orElse(null);
        }
        if (persistedCloudEventId == null) {
            var recordRequest = eventBuilder.buildRequest(subProcess, recordType,
                data, fieldImpacteds);
            cloudEventRecordService.record(recordRequest);
        } else {
            cloudEventRecordService.updateTime(persistedCloudEventId);
        }
    }

    private void recordHttpExceptionCloudEvent(IntegrationSubProcess subProcess, RecordType recordType,
        HttpStatusCodeException e, Long tradeId, Long positionId) {
        Map<String, String> data = new HashMap<>();
        data.put(HTTP_STATUS_TEXT, e.getStatusText());
        data.put(TRADE_ID, toStringNullSafe(tradeId));
        data.put(POSITION_ID, toStringNullSafe(positionId));
        var recordRequest = eventBuilder.buildRequest(subProcess, recordType, data, List.of());
        cloudEventRecordService.record(recordRequest);
    }

}
