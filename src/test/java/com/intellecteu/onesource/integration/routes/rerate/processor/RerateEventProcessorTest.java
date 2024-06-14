package com.intellecteu.onesource.integration.routes.rerate.processor;

import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.APPLIED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.APPROVED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CANCEL_PENDING;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DECLINED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RERATE_APPLIED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RERATE_CANCELED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RERATE_DECLINED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RERATE_PENDING;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RERATE_PROPOSED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.RerateService;
import com.intellecteu.onesource.integration.services.RerateTradeService;
import com.intellecteu.onesource.integration.services.TradeEventService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventFactoryImpl;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class RerateEventProcessorTest {

    @Mock
    private OneSourceService oneSourceService;
    @Mock
    private TradeEventService tradeEventService;
    @Mock
    private RerateService rerateService;
    @Mock
    private RerateTradeService rerateTradeService;
    @Mock
    private CloudEventRecordService cloudEventRecordService;

    private RerateEventProcessor rerateEventProcessor;

    @BeforeEach
    void setUp() {
        openMocks(this);
        doReturn(new CloudEventFactoryImpl(
            Map.of(IntegrationProcess.RERATE, new RerateCloudEventBuilder("1", "http://integration.toolkit")))).when(
            cloudEventRecordService).getFactory();
        rerateEventProcessor = new RerateEventProcessor(oneSourceService, tradeEventService, rerateService,
            rerateTradeService, cloudEventRecordService);
    }

    @Test
    void processRerateProposedEvent_RerateProposedEvent_savedRerate() {
        TradeEvent event = new TradeEvent();
        event.setEventType(RERATE_PROPOSED);
        event.setResourceUri("/v1/ledger/rerates/93f834ff-66b5-4195-892b-8f316ed77006");
        Rerate rerate = new Rerate();
        doReturn(rerate).when(oneSourceService).retrieveRerate(any());

        rerateEventProcessor.processRerateProposedEvent(event);

        verify(rerateService, times(1)).saveRerate(any());
    }

    @Test
    void processReratePendingEvent_ReratePendingEvent_savedRerate() {
        TradeEvent event = new TradeEvent();
        event.setEventType(RERATE_PENDING);
        event.setResourceUri("/v1/ledger/rerates/93f834ff-66b5-4195-892b-8f316ed77006");
        Rerate rerateUpdate = new Rerate();
        rerateUpdate.setRerateId("1");
        doReturn(rerateUpdate).when(oneSourceService).retrieveRerate(any());
        Rerate rerate = new Rerate();
        rerate.setRerateId("1");
        rerate.setMatchingSpireTradeId(1l);
        doReturn(rerate).when(rerateService).getByRerateId(any());
        RerateTrade rerateTrade = new RerateTrade();
        doReturn(rerateTrade).when(rerateTradeService).getByTradeId(any());
        doReturn(new CloudEventFactoryImpl(
            Map.of(IntegrationProcess.RERATE, new RerateCloudEventBuilder("1", "http://integration.toolkit")))).when(
            cloudEventRecordService).getFactory();

        rerateEventProcessor.processReratePendingEvent(event);

        assertEquals(APPROVED, rerate.getProcessingStatus());
        verify(rerateService, times(1)).saveRerate(any());
        verify(rerateTradeService, times(1)).save(any(RerateTrade.class));
    }

    @Test
    void processRerateAppliedEvent_RerateAppliedEvent_savedRerate() {
        TradeEvent event = new TradeEvent();
        event.setEventType(RERATE_APPLIED);
        event.setResourceUri("/v1/ledger/rerates/93f834ff-66b5-4195-892b-8f316ed77006/");
        Rerate rerate = new Rerate();
        rerate.setRerateId("1");
        rerate.setMatchingSpireTradeId(1l);
        doReturn(rerate).when(rerateService).findRerateByContractIdAndProcessingStatuses(any(), any());
        doReturn(new CloudEventFactoryImpl(
            Map.of(IntegrationProcess.RERATE, new RerateCloudEventBuilder("1", "http://integration.toolkit")))).when(
            cloudEventRecordService).getFactory();

        rerateEventProcessor.processRerateAppliedEvent(event);

        assertEquals(APPLIED, rerate.getProcessingStatus());
        verify(rerateService, times(1)).saveRerate(any());
        verify(cloudEventRecordService, times(1)).record(any());
    }

    @Test
    void processRerateDeclinedEvent_RerateDecliedEvent_savedRerate() {
        TradeEvent event = new TradeEvent();
        event.setEventType(RERATE_DECLINED);
        event.setResourceUri("/v1/ledger/rerates/93f834ff-66b5-4195-892b-8f316ed77006");
        Rerate rerate = new Rerate();
        rerate.setRerateId("1");
        rerate.setMatchingSpireTradeId(1l);
        doReturn(rerate).when(rerateService).getByRerateId(any());
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setMatchingRerateId("1");
        doReturn(rerateTrade).when(rerateTradeService).getByTradeId(any());

        rerateEventProcessor.processRerateDeclinedEvent(event);

        assertEquals(DECLINED, rerate.getProcessingStatus());
        assertNull(rerateTrade.getMatchingRerateId());
        verify(rerateService, times(1)).saveRerate(any());
        verify(cloudEventRecordService, times(1)).record(any());
    }

    @Test
    void processRerateCanceledEvent_RerateCanceledEventAndNotApprovedRerate_savedCANCELEDRerateAndDelinkedRerateTrade() {
        TradeEvent event = new TradeEvent();
        event.setEventType(RERATE_CANCELED);
        event.setResourceUri("/v1/ledger/rerates/93f834ff-66b5-4195-892b-8f316ed77006");
        Rerate rerate = new Rerate();
        rerate.setRerateId("1");
        rerate.setMatchingSpireTradeId(1l);
        doReturn(rerate).when(rerateService).getByRerateId(any());
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setMatchingRerateId("1");
        doReturn(rerateTrade).when(rerateTradeService).getByTradeId(any());

        rerateEventProcessor.processRerateCanceledEvent(event);

        assertEquals(CANCELED, rerate.getProcessingStatus());
        assertNull(rerateTrade.getMatchingRerateId());
        verify(rerateService, times(1)).saveRerate(any());
        verify(rerateTradeService, times(1)).save(any(RerateTrade.class));
        verify(cloudEventRecordService, times(1)).record(any());
    }

    @Test
    void processRerateCanceledEvent_RerateCanceledEventAndApprovedRerate_savedCANCELEDRerate() {
        TradeEvent event = new TradeEvent();
        event.setEventType(RERATE_CANCELED);
        event.setResourceUri("/v1/ledger/rerates/93f834ff-66b5-4195-892b-8f316ed77006");
        Rerate rerate = new Rerate();
        rerate.setRerateId("1");
        rerate.setMatchingSpireTradeId(1l);
        rerate.setProcessingStatus(APPROVED);
        doReturn(rerate).when(rerateService).getByRerateId(any());

        rerateEventProcessor.processRerateCanceledEvent(event);

        assertEquals(CANCELED, rerate.getProcessingStatus());
        verify(rerateService, times(1)).saveRerate(any());
        verify(rerateTradeService, times(0)).save(any(RerateTrade.class));
        verify(cloudEventRecordService, times(1)).record(any());
    }

    @Test
    void processReratePendingCancelEvent_ReratePendingCancelEvent_savedRerate() {
        TradeEvent event = new TradeEvent();
        event.setEventType(RERATE_CANCELED);
        event.setResourceUri("/v1/ledger/rerates/93f834ff-66b5-4195-892b-8f316ed77006");
        Rerate rerate = new Rerate();
        rerate.setRerateId("1");
        rerate.setMatchingSpireTradeId(1l);
        rerate.setProcessingStatus(APPROVED);
        doReturn(rerate).when(rerateService).getByRerateId(any());

        rerateEventProcessor.processReratePendingCancelEvent(event);

        assertEquals(CANCEL_PENDING, rerate.getProcessingStatus());
        verify(rerateService, times(1)).saveRerate(any());
        verify(cloudEventRecordService, times(1)).record(any());
    }

}