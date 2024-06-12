package com.intellecteu.onesource.integration.routes.returns.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.CAPTURE_RETURN_ACKNOWLEDGEMENT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_RETURN_ACKNOWLEDGEMENT_DETAILS;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_RETURN_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_RETURN_SETTLED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.NEGATIVELY_ACKNOWLEDGED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.POSITIVELY_ACKNOWLEDGED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.SETTLED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_NEGATIVELY_ACKNOWLEDGED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_POSITIVELY_ACKNOWLEDGED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_SETTLED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import com.intellecteu.onesource.integration.model.onesource.AcknowledgementType;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.model.onesource.Return;
import com.intellecteu.onesource.integration.model.onesource.ReturnStatus;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.ReturnService;
import com.intellecteu.onesource.integration.services.ReturnTradeService;
import com.intellecteu.onesource.integration.services.TradeEventService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventFactory;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder;
import org.junit.jupiter.api.Assertions;
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
    private ReturnTradeService returnTradeService;
    @Mock
    private CloudEventRecordService cloudEventRecordService;
    @Mock
    private ReturnCloudEventBuilder eventBuilder;

    @BeforeEach
    void setUp() {
        openMocks(this);
        CloudEventFactory cloudEventFactory = mock(CloudEventFactory.class);
        doReturn(eventBuilder).when(cloudEventFactory).eventBuilder(any());
        doReturn(cloudEventFactory).when(cloudEventRecordService).getFactory();
        returnEventProcessor = new ReturnEventProcessor(returnService, tradeEventService, returnTradeService,
            oneSourceService, cloudEventRecordService);
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

    @Test
    void processReturnAcknowledgedEvent_POSITIVEReturnAcknowledgedEvent_ProcessingStatusPOSITIVELYACKNOWLEDGEDS() {
        TradeEvent tradeEvent = new TradeEvent();
        tradeEvent.setEventType(EventType.RETURN_ACKNOWLEDGED);
        tradeEvent.setResourceUri("/v1/ledger/returns/93f834ff-66b5-4195-892b-8f316ed77006");
        Return oneSourceReturnUpdate = new Return();
        oneSourceReturnUpdate.setAcknowledgementType(AcknowledgementType.POSITIVE);
        doReturn(oneSourceReturnUpdate).when(oneSourceService).retrieveReturn(any());
        Return oneSourceReturn = new Return();
        doReturn(oneSourceReturn).when(returnService).getByReturnId(any());
        doCallRealMethod().when(returnService).merge(any(), any());

        TradeEvent result = returnEventProcessor.processReturnAcknowledgedEvent(tradeEvent);

        assertEquals(POSITIVELY_ACKNOWLEDGED, oneSourceReturn.getProcessingStatus());
        verify(cloudEventRecordService, times(1)).record(any());
        verify(eventBuilder, times(1)).buildRequest(eq(CAPTURE_RETURN_ACKNOWLEDGEMENT),
            eq(RETURN_POSITIVELY_ACKNOWLEDGED),
            any(), any());
    }

    @Test
    void processReturnAcknowledgedEvent_NEGATIVEAcknowledgedEvent_ProcessingStatusNEGATIVELYACKNOWLEDGED() {
        TradeEvent tradeEvent = new TradeEvent();
        tradeEvent.setEventType(EventType.RETURN_ACKNOWLEDGED);
        tradeEvent.setResourceUri("/v1/ledger/returns/93f834ff-66b5-4195-892b-8f316ed77006");
        Return oneSourceReturnUpdate = new Return();
        oneSourceReturnUpdate.setAcknowledgementType(AcknowledgementType.NEGATIVE);
        doReturn(oneSourceReturnUpdate).when(oneSourceService).retrieveReturn(any());
        Return oneSourceReturn = new Return();
        doReturn(oneSourceReturn).when(returnService).getByReturnId(any());
        doCallRealMethod().when(returnService).merge(any(), any());

        TradeEvent result = returnEventProcessor.processReturnAcknowledgedEvent(tradeEvent);

        assertEquals(NEGATIVELY_ACKNOWLEDGED, oneSourceReturn.getProcessingStatus());
        verify(cloudEventRecordService, times(1)).record(any());
        verify(eventBuilder, times(1)).buildRequest(eq(CAPTURE_RETURN_ACKNOWLEDGEMENT),
            eq(RETURN_NEGATIVELY_ACKNOWLEDGED),
            any(), any());
    }

    @Test
    void processReturnAcknowledgedEvent_ThrownHttpClientErrorException_SavedCloudEvent() {
        TradeEvent tradeEvent = new TradeEvent();
        tradeEvent.setEventType(EventType.RETURN_ACKNOWLEDGED);
        tradeEvent.setResourceUri("/v1/ledger/returns/93f834ff-66b5-4195-892b-8f316ed77006");
        Return oneSourceReturnUpdate = new Return();
        doThrow(new HttpClientErrorException(HttpStatusCode.valueOf(400))).when(oneSourceService).retrieveReturn(any());

        TradeEvent result = returnEventProcessor.processReturnAcknowledgedEvent(tradeEvent);

        verify(cloudEventRecordService, times(1)).record(any());
        verify(eventBuilder, times(1)).buildRequest(eq(GET_RETURN_ACKNOWLEDGEMENT_DETAILS),
            eq(TECHNICAL_EXCEPTION_1SOURCE),
            any(), any());
    }

    @Test
    void processReturnSettledEvent_SETTLEDEvent_ProcessingStatusSETTLED() {
        TradeEvent tradeEvent = new TradeEvent();
        tradeEvent.setEventType(EventType.RETURN_SETTLED);
        tradeEvent.setResourceUri("/v1/ledger/returns/93f834ff-66b5-4195-892b-8f316ed77006");
        Return oneSourceReturn = new Return();
        oneSourceReturn.setProcessingStatus(CREATED);
        doReturn(oneSourceReturn).when(returnService).getByReturnId(any());

        TradeEvent result = returnEventProcessor.processReturnSettledEvent(tradeEvent);

        Assertions.assertEquals(ReturnStatus.SETTLED, oneSourceReturn.getReturnStatus());
        Assertions.assertEquals(SETTLED, oneSourceReturn.getProcessingStatus());
        verify(cloudEventRecordService, times(1)).record(any());
        verify(eventBuilder, times(1)).buildRequest(eq(PROCESS_RETURN_SETTLED), eq(RETURN_SETTLED),
            any(), any());
    }

    @Test
    void processReturnCanceledEvent_CANCELEDEvent_ProcessingStatusCANCELED() {
        TradeEvent tradeEvent = new TradeEvent();
        tradeEvent.setEventType(EventType.RETURN_CANCELED);
        tradeEvent.setResourceUri("/v1/ledger/returns/93f834ff-66b5-4195-892b-8f316ed77006");
        Return oneSourceReturn = new Return();
        doReturn(oneSourceReturn).when(returnService).getByReturnId("93f834ff-66b5-4195-892b-8f316ed77006");

        returnEventProcessor.processReturnCancellationEvent(tradeEvent);

        Assertions.assertEquals(ReturnStatus.CANCELED, oneSourceReturn.getReturnStatus());
        Assertions.assertEquals(CANCELED, oneSourceReturn.getProcessingStatus());
        verify(cloudEventRecordService, times(1)).record(any());
        verify(eventBuilder, times(1)).buildRequest(eq(PROCESS_RETURN_CANCELED),
            eq(RETURN_CANCELED), any(), any());
    }
}