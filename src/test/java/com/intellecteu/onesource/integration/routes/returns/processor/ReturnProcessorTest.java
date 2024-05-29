package com.intellecteu.onesource.integration.routes.returns.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.MATCH_RETURN;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_RETURN;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CONFIRMED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TO_VALIDATE;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_MATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_PENDING_ACKNOWLEDGEMENT;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_TRADE_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import com.intellecteu.onesource.integration.model.backoffice.ReturnTrade;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.Return;
import com.intellecteu.onesource.integration.model.onesource.Venue;
import com.intellecteu.onesource.integration.model.onesource.VenueParty;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.ReturnService;
import com.intellecteu.onesource.integration.services.ReturnTradeService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventFactory;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

class ReturnProcessorTest {

    private ReturnProcessor returnProcessor;
    @Mock
    private BackOfficeService backOfficeService;
    @Mock
    private OneSourceService oneSourceService;
    @Mock
    private ReturnTradeService returnTradeService;
    @Mock
    private ReturnService returnService;
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
        returnProcessor = new ReturnProcessor(backOfficeService, oneSourceService, returnTradeService, returnService,
            cloudEventRecordService);
    }

    @Test
    void fetchNewReturnTrades_BackOfficeResponseWithModel_StoredEntity() {
        ReturnTrade returnTrade = new ReturnTrade();
        returnTrade.setTradeId(1l);
        doReturn(List.of(returnTrade)).when(backOfficeService).retrieveReturnTrades(any());

        List<ReturnTrade> result = returnProcessor.fetchNewReturnTrades();

        assertTrue(result.contains(returnTrade));
    }

    @Test
    void postReturnTrade_OneSourceSuccessResponse_RecordCloudEvent() {
        ReturnTrade returnTrade = new ReturnTrade();
        returnTrade.setTradeId(1l);
        doNothing().when(oneSourceService).postReturnTrade(any());

        returnProcessor.postReturnTrade(returnTrade);

        verify(cloudEventRecordService, times(1)).record(any());
        verify(eventBuilder, times(1)).buildRequest(eq(POST_RETURN), eq(RETURN_TRADE_SUBMITTED),
            any(), any());
    }

    @Test
    void postReturnTrade_OneSourceHttpStatusCodeException_RecordCloudEvent() {
        ReturnTrade returnTrade = new ReturnTrade();
        returnTrade.setTradeId(1l);
        doThrow(new HttpClientErrorException(HttpStatusCode.valueOf(400))).when(oneSourceService)
            .postReturnTrade(any());

        returnProcessor.postReturnTrade(returnTrade);

        verify(cloudEventRecordService, times(1)).record(any());
        verify(eventBuilder, times(1)).buildRequest(eq(POST_RETURN), eq(TECHNICAL_EXCEPTION_1SOURCE),
            any(), any());
    }

    @Test
    void matchingReturn_MatchedReturnTrade_BorrowerMatchingWithCONFIRMEDStatusAndRecordCloudEvent() {
        Return oneSourceReturn = new Return();
        oneSourceReturn.setContractId("testContractId");
        oneSourceReturn.setProcessingStatus(CREATED);
        Venue venue = new Venue();
        String tradeIdStr = "1";
        venue.setVenueRefKey(tradeIdStr);
        venue.setVenueParties(Set.of(new VenueParty(1L, PartyRole.BORROWER, tradeIdStr)));
        oneSourceReturn.setExecutionVenue(venue);
        ReturnTrade returnTrade = new ReturnTrade();
        returnTrade.setTradeId(1l);
        doReturn(Optional.of(returnTrade)).when(returnTradeService)
            .findUnmatchedReturnTrade(any(), any());

        Return result = returnProcessor.matchingReturn(oneSourceReturn);

        assertEquals(CONFIRMED, result.getProcessingStatus());
        verify(cloudEventRecordService, times(1)).record(any());
        verify(eventBuilder, times(1)).buildRequest(eq(MATCH_RETURN), eq(RETURN_PENDING_ACKNOWLEDGEMENT),
            any(), any());
    }

    @Test
    void matchingReturn_MatchedReturnTrade_LenderMatchingWithTOVALIDATEStatusAndRecordCloudEvent() {
        Return oneSourceReturn = new Return();
        oneSourceReturn.setContractId("testContractId");
        oneSourceReturn.setProcessingStatus(CREATED);
        Venue venue = new Venue();
        String tradeIdStr = "1";
        venue.setVenueRefKey(tradeIdStr);
        venue.setVenueParties(Set.of(new VenueParty(1L, PartyRole.BORROWER, tradeIdStr)));
        oneSourceReturn.setExecutionVenue(venue);
        doReturn(Optional.empty()).when(returnTradeService)
            .findUnmatchedReturnTrade(any(), any());
        ReturnTrade returnTrade = new ReturnTrade();
        returnTrade.setTradeId(1l);
        doReturn(Optional.of(returnTrade)).when(returnTradeService)
            .findUnmatchedReturnTrade(any(), any(), any(), any());

        Return result = returnProcessor.matchingReturn(oneSourceReturn);

        assertEquals(TO_VALIDATE, result.getProcessingStatus());
        verify(cloudEventRecordService, times(1)).record(any());
        verify(eventBuilder, times(1)).buildRequest(eq(MATCH_RETURN), eq(RETURN_MATCHED),
            any(), any());
    }

    @Test
    void matchingReturn_UnMatchedReturnTrade_UNMATCHEDStatusAndRecordCloudEvent() {
        Return oneSourceReturn = new Return();
        oneSourceReturn.setContractId("testContractId");
        oneSourceReturn.setProcessingStatus(CREATED);
        Venue venue = new Venue();
        String tradeIdStr = "1";
        venue.setVenueRefKey(tradeIdStr);
        venue.setVenueParties(Set.of(new VenueParty(1L, PartyRole.LENDER, tradeIdStr)));
        oneSourceReturn.setExecutionVenue(venue);
        doReturn(Optional.empty()).when(returnTradeService).findUnmatchedReturnTrade(any(), any(), any(), any());

        Return result = returnProcessor.matchingReturn(oneSourceReturn);

        assertEquals(UNMATCHED, result.getProcessingStatus());
        verify(cloudEventRecordService, times(1)).record(any());
        verify(eventBuilder, times(1)).buildRequest(eq(MATCH_RETURN), eq(RETURN_UNMATCHED),
            any(), any());
    }
}