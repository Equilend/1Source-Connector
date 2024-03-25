package com.intellecteu.onesource.integration.routes.rerate.processor;

import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.APPROVAL_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CONFIRMED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DECLINE_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.MATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TO_VALIDATE;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.VALIDATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.MATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.SENT_FOR_APPROVAL;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TO_VALIDATE;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.VALIDATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.backoffice.TradeOut;
import com.intellecteu.onesource.integration.model.integrationtoolkit.DeclineInstruction;
import com.intellecteu.onesource.integration.model.onesource.FixedRate;
import com.intellecteu.onesource.integration.model.onesource.Rate;
import com.intellecteu.onesource.integration.model.onesource.RebateRate;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.DeclineInstructionService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.RerateService;
import com.intellecteu.onesource.integration.services.RerateTradeService;
import com.intellecteu.onesource.integration.services.reconciliation.RerateReconcileService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventFactory;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.IntegrationCloudEventBuilder;
import com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

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
    private RerateReconcileService rerateReconcileService;
    @Mock
    private CloudEventRecordService cloudEventRecordService;
    @Mock
    private DeclineInstructionService declineInstructionService;
    @Mock
    private OneSourceService oneSourceService;

    RerateProcessor rerateProcessor;

    @BeforeEach
    void setUp() {
        openMocks(this);
        CloudEventFactory cloudEventFactory = mock(CloudEventFactory.class);
        RerateCloudEventBuilder cloudEventBuildRequest = mock(RerateCloudEventBuilder.class);
        doReturn(cloudEventBuildRequest).when(cloudEventFactory).eventBuilder(any());
        doReturn(cloudEventFactory).when(cloudEventRecordService).getFactory();
        rerateProcessor = new RerateProcessor(lenderBackOfficeService, borrowerBackOfficeService, oneSourceService,
            contractService, rerateTradeService, rerateService, rerateReconcileService, declineInstructionService,
            cloudEventRecordService);
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
    void instructRerateTrade_OkResponse_SubmittedStatus() {
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setProcessingStatus(CREATED);

        RerateTrade result = rerateProcessor.instructRerateTrade(rerateTrade);

        assertEquals(SUBMITTED, result.getProcessingStatus());
    }

    @Test
    void instructRerateTrade_Not400Response_RecordTechnicalExceptionEvent() {
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setProcessingStatus(CREATED);
        doThrow(new HttpClientErrorException(HttpStatusCode.valueOf(401))).when(oneSourceService).instructRerate(any());

        RerateTrade result = rerateProcessor.instructRerateTrade(rerateTrade);

        verify(cloudEventRecordService, times(1)).record(any());
    }


    @Test
    void matchWithRerate_matched1SourceRerate_filledMatchingRerateId() {
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(1l);
        TradeOut tradeOut = new TradeOut();
        tradeOut.setAccrualDate(LocalDateTime.now());
        tradeOut.setSettleDate(LocalDateTime.now());
        rerateTrade.setTradeOut(tradeOut);
        Rerate rerate = new Rerate();
        rerate.setRerateId("rerateId");
        doReturn(Optional.of(rerate)).when(rerateService).findUnmatchedRerate(any(), any());

        RerateTrade result = rerateProcessor.matchBackOfficeRerateTradeWith1SourceRerate(rerateTrade);

        assertEquals(rerate.getRerateId(), result.getMatchingRerateId());
    }

    @Test
    void match1SourceRerateWithBackOfficeRerate_matchedSubmittedRerateTrade_filledMatchingSpireTradeIdAndMATCHED() {
        Rerate rerate = new Rerate();
        rerate.setRerateId("rerateId");
        rerate.setRerate(Rate.builder()
            .rebate(RebateRate.builder().fixed(FixedRate.builder().effectiveDate(LocalDate.now()).build()).build())
            .build());
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(1l);
        rerateTrade.setProcessingStatus(SUBMITTED);
        doReturn(Optional.of(rerateTrade)).when(rerateTradeService)
            .findUnmatchedRerateTrade(any(), any());

        Rerate result = rerateProcessor.match1SourceRerateWithBackOfficeRerateTrade(rerate);

        assertEquals(rerateTrade.getTradeId(), result.getMatchingSpireTradeId());
        assertEquals(MATCHED, result.getProcessingStatus());
    }

    @Test
    void match1SourceRerateWithBackOfficeRerate_matchedCreatedRerateTrade_filledMatchingSpireTradeIdAndTO_VALIDATE() {
        Rerate rerate = new Rerate();
        rerate.setRerateId("rerateId");
        rerate.setRerate(Rate.builder()
            .rebate(RebateRate.builder().fixed(FixedRate.builder().effectiveDate(LocalDate.now()).build()).build())
            .build());
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(1l);
        rerateTrade.setProcessingStatus(CREATED);
        doReturn(Optional.of(rerateTrade)).when(rerateTradeService)
            .findUnmatchedRerateTrade(any(), any());

        Rerate result = rerateProcessor.match1SourceRerateWithBackOfficeRerateTrade(rerate);

        assertEquals(rerateTrade.getTradeId(), result.getMatchingSpireTradeId());
        assertEquals(TO_VALIDATE, result.getProcessingStatus());
    }

    @Test
    void validate_validData_VALIDATED() throws ReconcileException {
        Rerate rerate = new Rerate();
        rerate.setRerateId("rerateId");
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(1l);
        doReturn(Optional.of(rerateTrade)).when(rerateTradeService)
            .findUnmatchedRerateTrade(any(), any());
        doNothing().when(rerateReconcileService).reconcile(any(), any());

        Rerate result = rerateProcessor.validateRerate(rerate);

        assertEquals(VALIDATED, result.getProcessingStatus());
    }

    @Test
    void validate_invalidData_DISCREPANCIES() throws ReconcileException {
        Rerate rerate = new Rerate();
        rerate.setRerateId("rerateId");
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(1l);
        doReturn(Optional.of(rerateTrade)).when(rerateTradeService)
            .findUnmatchedRerateTrade(any(), any());
        doThrow(new ReconcileException(new ArrayList<>())).when(rerateReconcileService)
            .reconcile(any(), any());

        Rerate result = rerateProcessor.validateRerate(rerate);

        assertEquals(DISCREPANCIES, result.getProcessingStatus());
    }

    @Test
    void approve_OkResponse_SENTFORAPPROVALStatus() {
        Rerate rerate = new Rerate();
        rerate.setRerateId("rerateId");

        Rerate result = rerateProcessor.approveRerate(rerate);

        assertEquals(APPROVAL_SUBMITTED, result.getProcessingStatus());
    }

    @Test
    void approve_NotOkResponse_RecordTechnicalExceptionEvent() {
        Rerate rerate = new Rerate();
        rerate.setRerateId("rerateId");

        doThrow(new HttpClientErrorException(HttpStatusCode.valueOf(401))).when(oneSourceService)
            .approveRerate(any(), any());

        Rerate result = rerateProcessor.approveRerate(rerate);

        verify(cloudEventRecordService, times(1)).record(any());
    }

    @Test
    void confirmRerateTrade_OkResponse_SENTFORAPPROVALStatus() {
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(1l);

        RerateTrade result = rerateProcessor.confirmRerateTrade(rerateTrade);

        assertEquals(CONFIRMED, result.getProcessingStatus());
    }

    @Test
    void confirmRerateTrade_NotOkResponse_RecordTechnicalExceptionEvent() {
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(1l);
        doThrow(new HttpClientErrorException(HttpStatusCode.valueOf(401))).when(lenderBackOfficeService)
            .confirmBackOfficeRerateTrade(any());

        RerateTrade result = rerateProcessor.confirmRerateTrade(rerateTrade);

        verify(cloudEventRecordService, times(1)).record(any());
    }

    @Test
    void declineRerate_OkResponse_DECLINESUBMITTEDStatus() {
        DeclineInstruction declineInstruction = new DeclineInstruction();
        Rerate rerate = new Rerate();
        rerate.setRerateId("id");
        doReturn(rerate).when(rerateService).getByRerateId(any());

        DeclineInstruction result = rerateProcessor.declineRerate(declineInstruction);

        verify(oneSourceService, times(1)).declineRerate(any(), any());
        assertEquals(DECLINE_SUBMITTED, rerate.getProcessingStatus());
    }

    @Test
    void declineRerate_NotOkResponse_RecordTechnicalExceptionEvent() {
        DeclineInstruction declineInstruction = new DeclineInstruction();
        Rerate rerate = new Rerate();
        rerate.setRerateId("id");
        doReturn(rerate).when(rerateService).getByRerateId(any());
        doThrow(new HttpClientErrorException(HttpStatusCode.valueOf(401))).when(oneSourceService)
            .declineRerate(any(), any());

        DeclineInstruction result = rerateProcessor.declineRerate(declineInstruction);

        verify(cloudEventRecordService, times(1)).record(any());
    }
}