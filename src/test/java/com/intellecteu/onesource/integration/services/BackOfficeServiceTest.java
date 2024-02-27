package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_NEW_POSITIONS_PENDING_CONFIRMATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapperImpl;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.services.client.spire.InstructionSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.PositionSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.TradeSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.dto.NQueryResponseTradeOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.PositionOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponseNQueryResponseTradeOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.TradeOutDTO;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventFactoryImpl;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.ContractInitiationCloudEventBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
class BackOfficeServiceTest {

    @Mock
    private PositionSpireApiClient positionSpireApiClient;

    @Mock
    private TradeSpireApiClient tradeSpireApiClient;

    @Mock
    private InstructionSpireApiClient instructionClient;

    @Mock
    private SpireMapper spireMapper;

    @Mock
    private CloudEventRecordService cloudEventRecordService;

    BackOfficeMapperImpl rerateTradeMapper = new BackOfficeMapperImpl();

    private BackOfficeService service;

    @BeforeEach
    void setUp() {
        openMocks(this);
        service = new BackOfficeService(positionSpireApiClient, tradeSpireApiClient, instructionClient,
            spireMapper, rerateTradeMapper, cloudEventRecordService);
    }

    @Test
    @DisplayName("Record Unauthorized response in a cloud event.")
    void testExceptionCloudEventCapturing_whenApiResponseIsUnauthorized() {
        var expectedExceptionToCapture = new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        var recordFactory = new CloudEventFactoryImpl(
            Map.of(CONTRACT_INITIATION,
                new ContractInitiationCloudEventBuilder("specVersion", "http://localhost:8000")));
        var eventBuilder = recordFactory.eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildExceptionRequest(
            expectedExceptionToCapture, GET_NEW_POSITIONS_PENDING_CONFIRMATION);

        var argumentCaptor = ArgumentCaptor.forClass(CloudEventBuildRequest.class);

        when(positionSpireApiClient.getPositions(any()))
            .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));
        when(cloudEventRecordService.getFactory()).thenReturn(recordFactory);
        doNothing().when(cloudEventRecordService).record(argumentCaptor.capture());

        service.getNewSpirePositionsObsolete(Optional.empty());

        assertTrue(cloudEventsAreEqual(recordRequest, argumentCaptor.getValue()));
    }

    @Test
    @DisplayName("Record Forbidden response in a cloud event.")
    void testExceptionCloudEventCapturing_whenApiResponseIsForbidden() {
        var expectedExceptionToCapture = new HttpClientErrorException(HttpStatus.FORBIDDEN);
        var recordFactory = new CloudEventFactoryImpl(
            Map.of(CONTRACT_INITIATION,
                new ContractInitiationCloudEventBuilder("specVersion", "http://localhost:8000")));
        var eventBuilder = recordFactory.eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildExceptionRequest(
            expectedExceptionToCapture, GET_NEW_POSITIONS_PENDING_CONFIRMATION);

        var argumentCaptor = ArgumentCaptor.forClass(CloudEventBuildRequest.class);

        when(positionSpireApiClient.getPositions(any()))
            .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));
        when(cloudEventRecordService.getFactory()).thenReturn(recordFactory);
        doNothing().when(cloudEventRecordService).record(argumentCaptor.capture());

        service.getNewSpirePositionsObsolete(Optional.empty());

        assertTrue(cloudEventsAreEqual(recordRequest, argumentCaptor.getValue()));
    }

    @Test
    void getNewBackOfficeTradeEvents_OKApiClientResponseWithObject_ListOfRerateTrades() {
        SResponseNQueryResponseTradeOutDTO responseNQueryResponseTradeOutDTO = new SResponseNQueryResponseTradeOutDTO();
        NQueryResponseTradeOutDTO data = new NQueryResponseTradeOutDTO();
        PositionOutDTO positionOutDTO = new PositionOutDTO();
        positionOutDTO.setPositionId(1L);
        TradeOutDTO tradeOutDTO = new TradeOutDTO();
        Long tradeId = 1L;
        tradeOutDTO.setTradeId(tradeId);
        tradeOutDTO.setPositionOutDTO(positionOutDTO);
        List<TradeOutDTO> beans = List.of(tradeOutDTO);
        data.setBeans(beans);
        data.setTotalRows(1);
        responseNQueryResponseTradeOutDTO.setData(data);
        ResponseEntity response = ResponseEntity.ok(responseNQueryResponseTradeOutDTO);
        doReturn(response).when(tradeSpireApiClient).getTrades(any());

        List<RerateTrade> newBackOfficeTradeList = service.getNewBackOfficeRerateTradeEvents(Optional.empty());

        assertTrue(!newBackOfficeTradeList.isEmpty());
        assertEquals(tradeId, newBackOfficeTradeList.get(0).getTradeId());
    }

    private boolean cloudEventsAreEqual(CloudEventBuildRequest recordRequest, CloudEventBuildRequest capturedRequest) {
        return recordRequest.getRecordType().equals(capturedRequest.getRecordType())
            && recordRequest.getSubject().split("-")[0].equals(capturedRequest.getSubject().split("-")[0])
            && recordRequest.getRelatedProcess().equals(capturedRequest.getRelatedProcess())
            && recordRequest.getRelatedSubProcess().equals(capturedRequest.getRelatedSubProcess())
            && recordRequest.getData().equals(capturedRequest.getData());
    }
}