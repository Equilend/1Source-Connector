package com.intellecteu.onesource.integration.routes.rerate.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.openMocks;

import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.backoffice.TradeOut;
import com.intellecteu.onesource.integration.model.onesource.FixedRate;
import com.intellecteu.onesource.integration.model.onesource.Rate;
import com.intellecteu.onesource.integration.model.onesource.RebateRate;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.RerateService;
import com.intellecteu.onesource.integration.services.RerateTradeService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventFactory;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.IntegrationCloudEventBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class RerateProcessorTest {

    @Mock
    private BackOfficeService lenderBackOfficeService;
    @Mock
    private BackOfficeService borrowerBackOfficeService;
    @Mock
    private ContractService contractService;
    @Mock
    private RerateTradeService rerateTradeService;
    @Mock
    private RerateService rerateService;
    @Mock
    private CloudEventRecordService cloudEventRecordService;

    RerateProcessor rerateProcessor;

    @BeforeEach
    void setUp() {
        openMocks(this);
        CloudEventFactory cloudEventFactory = mock(CloudEventFactory.class);
        IntegrationCloudEventBuilder cloudEventBuildRequest = mock(IntegrationCloudEventBuilder.class);
        doReturn(cloudEventBuildRequest).when(cloudEventFactory).eventBuilder(any());
        doReturn(cloudEventFactory).when(cloudEventRecordService).getFactory();
        rerateProcessor = new RerateProcessor(lenderBackOfficeService, borrowerBackOfficeService, contractService,
            rerateTradeService, rerateService, cloudEventRecordService);
    }

    @Test
    void fetchNewRerateTrades_BackOfficeResponseWithModel_StoredEntity() {
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(1l);
        List<RerateTrade> lenderRerateTradeList = List.of(rerateTrade);
        doReturn(lenderRerateTradeList).when(lenderBackOfficeService).getNewBackOfficeRerateTradeEvents(any());

        List<RerateTrade> rerateTradeList = rerateProcessor.fetchNewRerateTrades();

        assertTrue(rerateTradeList.contains(rerateTrade));
    }

    @Test
    void matchRerate_matched1SourceRerate_filledMatchingRerateId() {
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(1l);
        TradeOut tradeOut = new TradeOut();
        tradeOut.setAccrualDate(LocalDateTime.now());
        rerateTrade.setTradeOut(tradeOut);
        Rerate rerate = new Rerate();
        rerate.setRerateId("rerateId");
        doReturn(Optional.of(rerate)).when(rerateService).findRerate(any(), any(), any());

        RerateTrade result = rerateProcessor.matchRerate(rerateTrade);

        assertEquals(rerate.getRerateId(), result.getMatchingRerateId());
    }

    @Test
    void matchRerateTrade() {
        Rerate rerate = new Rerate();
        rerate.setRerateId("rerateId");
        rerate.setRerate(Rate.builder()
            .rebate(RebateRate.builder().fixed(FixedRate.builder().effectiveDate(LocalDate.now()).build()).build())
            .build());
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(1l);
        doReturn(Optional.of(rerateTrade)).when(rerateTradeService)
            .findRerateTradeByContractIdAndSettleDate(any(), any());

        Rerate result = rerateProcessor.matchRerateTrade(rerate);

        assertEquals(result.getMatchingSpireTradeId(), rerateTrade.getTradeId());
    }
}