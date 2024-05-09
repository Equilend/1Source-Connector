package com.intellecteu.onesource.integration.routes.returns.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RETURN;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_NEW_RETURN_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_SPIRE;
import static com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder.HTTP_STATUS_TEXT;
import static com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder.POSITION_ID;
import static com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder.RETURN_ID;
import static com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder.TRADE_ID;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.toStringNullSafe;

import com.intellecteu.onesource.integration.model.backoffice.ReturnTrade;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.services.BackOfficeService;
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
    private final ReturnTradeService returnTradeService;
    private final CloudEventRecordService cloudEventRecordService;
    private final ReturnCloudEventBuilder eventBuilder;

    @Autowired
    public ReturnProcessor(BackOfficeService backOfficeService, ReturnTradeService returnTradeService,
        CloudEventRecordService cloudEventRecordService) {
        this.backOfficeService = backOfficeService;
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
                null, null, null);
        }
        return rerateTradeList;
    }

    private void recordHttpExceptionCloudEvent(IntegrationSubProcess subProcess, RecordType recordType,
        HttpStatusCodeException e, String returnId, Long tradeId, Long positionId) {
        Map<String, String> data = new HashMap<>();
        data.put(HTTP_STATUS_TEXT, e.getStatusText());
        data.put(RETURN_ID, returnId);
        data.put(TRADE_ID, toStringNullSafe(tradeId));
        data.put(POSITION_ID, toStringNullSafe(positionId));
        var recordRequest = eventBuilder.buildRequest(subProcess, recordType, data, List.of());
        cloudEventRecordService.record(recordRequest);
    }

}
