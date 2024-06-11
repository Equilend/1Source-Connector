package com.intellecteu.onesource.integration.routes.rerate.processor;

import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.APPROVAL_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CANCEL_PENDING;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DECLINE_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.MATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TO_VALIDATE;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.VALIDATED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
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
import com.intellecteu.onesource.integration.model.backoffice.ReturnTrade;
import com.intellecteu.onesource.integration.model.backoffice.TradeOut;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.integrationtoolkit.CorrectionInstruction;
import com.intellecteu.onesource.integration.model.integrationtoolkit.DeclineInstruction;
import com.intellecteu.onesource.integration.model.onesource.FixedRate;
import com.intellecteu.onesource.integration.model.onesource.Rate;
import com.intellecteu.onesource.integration.model.onesource.RebateRate;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.CorrectionInstructionService;
import com.intellecteu.onesource.integration.services.DeclineInstructionService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.RerateService;
import com.intellecteu.onesource.integration.services.RerateTradeService;
import com.intellecteu.onesource.integration.services.ReturnTradeService;
import com.intellecteu.onesource.integration.services.reconciliation.RerateReconcileService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventFactory;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

class RerateProcessorTest {

    @Mock
    private BackOfficeService backOfficeService;
    @Mock
    private BackOfficeService borrowerBackOfficeService;
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
    private CorrectionInstructionService correctionInstructionService;
    @Mock
    private ReturnTradeService returnTradeService;
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
        rerateProcessor = new RerateProcessor(backOfficeService, oneSourceService,
            rerateTradeService, rerateService, rerateReconcileService, declineInstructionService,
            correctionInstructionService, returnTradeService, cloudEventRecordService);
    }

    @Test
    void fetchNewRerateTrades_BackOfficeResponseWithModel_StoredEntity() {
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(1l);
        List<RerateTrade> lenderRerateTradeList = List.of(rerateTrade);
        doReturn(lenderRerateTradeList).when(backOfficeService).getNewBackOfficeRerateTradeEvents(any());

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
        doThrow(new HttpClientErrorException(HttpStatusCode.valueOf(500))).when(oneSourceService).instructRerate(any());

        assertThrows(HttpClientErrorException.class, () -> rerateProcessor.instructRerateTrade(rerateTrade));

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
    @Disabled(value = "should be reworked according to new changes")
    void match1SourceRerateWithBackOfficeRerate_matchedCreatedRerateTrade_filledMatchingSpireTradeIdAndTO_VALIDATE() {
        Rerate rerate = new Rerate();
        rerate.setRerateId("rerateId");
        rerate.setRerate(Rate.builder()
            .rebate(RebateRate.builder().fixed(FixedRate.builder().effectiveDate(LocalDate.now()).build()).build())
            .build());
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(1L);
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
        doThrow(new HttpClientErrorException(HttpStatusCode.valueOf(500))).when(oneSourceService)
            .approveRerate(any(), any());

        assertThrows(HttpClientErrorException.class, () -> rerateProcessor.approveRerate(rerate));

        verify(cloudEventRecordService, times(1)).record(any());
    }

    @Test
    void isReturnTradePostponed_ExistOlderReturnTrade_True() {
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(2L);
        ReturnTrade olderReturnTrade = new ReturnTrade();
        olderReturnTrade.setTradeId(1L);
        TradeOut tradeOut = new TradeOut();
        tradeOut.setTradeType("Return Loan");
        olderReturnTrade.setTradeOut(tradeOut);
        doReturn(List.of(olderReturnTrade)).when(returnTradeService).findReturnTrade(any(), any());

        boolean result = rerateProcessor.isRerateTradePostponed(rerateTrade);

        assertTrue(result);
    }

    @Test
    void isReturnTradePostponed_ExistOlderRerateTrade_True() {
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(2L);
        RerateTrade olderRerateTrade = new RerateTrade();
        olderRerateTrade.setTradeId(1L);
        TradeOut tradeOut = new TradeOut();
        tradeOut.setTradeType("Rerate Borrow");
        olderRerateTrade.setTradeOut(tradeOut);
        doReturn(List.of(olderRerateTrade)).when(rerateTradeService).findReturnTrade(any(), any());
        doReturn(List.of()).when(returnTradeService).findReturnTrade(any(), any());

        boolean result = rerateProcessor.isRerateTradePostponed(rerateTrade);

        assertTrue(result);
    }

    @Test
    void isReturnTradePostponed_NotExistOlderRerateTrade_False() {
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(2L);
        doReturn(List.of()).when(rerateTradeService).findReturnTrade(any(), any());
        doReturn(List.of()).when(returnTradeService).findReturnTrade(any(), any());

        boolean result = rerateProcessor.isRerateTradePostponed(rerateTrade);

        assertFalse(result);
    }

    @Test
    void confirmRerateTrade_OkResponse_FUTUREStatus() {
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(1l);
        rerateTrade.setTradeOut(new TradeOut());

        RerateTrade result = rerateProcessor.confirmRerateTrade(rerateTrade);

        assertEquals("FUTURE", result.getTradeOut().getStatus());
    }

    @Test
    void confirmRerateTrade_NotOkResponse_RecordTechnicalExceptionEvent() {
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(1l);
        doThrow(new HttpClientErrorException(HttpStatusCode.valueOf(500))).when(backOfficeService)
            .confirmBackOfficeRerateTrade(any());

        assertThrows(HttpClientErrorException.class, () -> rerateProcessor.confirmRerateTrade(rerateTrade));

        verify(cloudEventRecordService, times(1)).record(any());
    }

    @Test
    void declineRerate_OkResponse_DECLINESUBMITTEDStatus() {
        DeclineInstruction declineInstruction = new DeclineInstruction();
        Rerate rerate = new Rerate();
        rerate.setRerateId("id");
        rerate.setProcessingStatus(DISCREPANCIES);
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

    @Test
    void amendRerateTrade_existRerateTrade_REPLACEDStatus() {
        long oldTradeId = 1L;
        long amendedTradeId = 2L;
        CorrectionInstruction correctionInstruction = new CorrectionInstruction();
        correctionInstruction.setOldTradeId(oldTradeId);
        correctionInstruction.setAmendedTradeId(amendedTradeId);
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(oldTradeId);
        rerateTrade.setProcessingStatus(ProcessingStatus.WAITING_PROPOSAL);
        RerateTrade newRerateTrade = new RerateTrade();
        newRerateTrade.setTradeId(amendedTradeId);
        doReturn(rerateTrade).when(rerateTradeService).getByTradeId(oldTradeId);
        doReturn(newRerateTrade).when(rerateTradeService).getByTradeId(amendedTradeId);

        rerateProcessor.amendRerateTrade(correctionInstruction);

        assertEquals(ProcessingStatus.REPLACED, rerateTrade.getProcessingStatus());
    }

    @Test
    void cancelRerateTrade_existRerateTrade_CANCELEDDStatus() {
        long oldTradeId = 1l;
        long amendedTradeId = 2l;
        CorrectionInstruction correctionInstruction = new CorrectionInstruction();
        correctionInstruction.setOldTradeId(oldTradeId);
        correctionInstruction.setAmendedTradeId(amendedTradeId);
        RerateTrade oldRerateTrade = new RerateTrade();
        oldRerateTrade.setTradeId(oldTradeId);
        oldRerateTrade.setProcessingStatus(ProcessingStatus.WAITING_PROPOSAL);
        doReturn(oldRerateTrade).when(rerateTradeService).getByTradeId(oldTradeId);
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(amendedTradeId);
        rerateTrade.setProcessingStatus(ProcessingStatus.WAITING_PROPOSAL);
        doReturn(rerateTrade).when(rerateTradeService).getByTradeId(amendedTradeId);
        Rerate rerate = new Rerate();
        rerate.setRerateId("id");
        rerate.setProcessingStatus(CANCEL_PENDING);
        doReturn(rerate).when(rerateService).getByRerateId(any());
        doReturn(rerate).when(rerateService).saveRerate(any());

        CorrectionInstruction result = rerateProcessor.cancelRerateTrade(correctionInstruction);

        assertEquals(ProcessingStatus.CANCELED, rerateTrade.getProcessingStatus());
    }
}