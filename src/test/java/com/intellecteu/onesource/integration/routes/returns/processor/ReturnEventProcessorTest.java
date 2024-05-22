package com.intellecteu.onesource.integration.routes.returns.processor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.model.onesource.Return;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.ReturnService;
import com.intellecteu.onesource.integration.services.TradeEventService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventFactoryImpl;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

class ReturnEventProcessorTest {

    private ReturnEventProcessor returnEventProcessor;
    @Mock
    private ReturnService returnService;
    @Mock
    private TradeEventService tradeEventService;
    @Mock
    private OneSourceService oneSourceService;
    @Mock
    private CloudEventRecordService cloudEventRecordService;

    @BeforeEach
    void setUp() {
        openMocks(this);
        doReturn(new CloudEventFactoryImpl(
            Map.of(IntegrationProcess.RETURN, new ReturnCloudEventBuilder("1", "http://integration.toolkit")))).when(
            cloudEventRecordService).getFactory();
        returnEventProcessor = new ReturnEventProcessor(returnService, tradeEventService, oneSourceService,
            cloudEventRecordService);
    }

    @Test
    void processReturnPendingEvent_ReturnPendingEvent_savedReturn() {
        TradeEvent tradeEvent = new TradeEvent();
        tradeEvent.setEventType(EventType.RETURN_PENDING);
        tradeEvent.setResourceUri("/v1/ledger/returns/93f834ff-66b5-4195-892b-8f316ed77006");
        Return oneSourceReturn = new Return();
        doReturn(oneSourceReturn).when(oneSourceService).retrieveReturn(any());

        TradeEvent result = returnEventProcessor.processReturnPendingEvent(tradeEvent);

        verify(returnService, times(1)).saveReturn(any());
    }

    @Test
    void processReturnPendingEvent_thrownHttpClientErrorException_savedCloudEvent() {
        TradeEvent tradeEvent = new TradeEvent();
        tradeEvent.setEventType(EventType.RETURN_PENDING);
        tradeEvent.setResourceUri("/v1/ledger/returns/93f834ff-66b5-4195-892b-8f316ed77006");
        Return oneSourceReturn = new Return();
        doThrow(new HttpClientErrorException(HttpStatusCode.valueOf(400))).when(oneSourceService).retrieveReturn(any());

        TradeEvent result = returnEventProcessor.processReturnPendingEvent(tradeEvent);

        verify(cloudEventRecordService, times(1)).record(any());
    }
}