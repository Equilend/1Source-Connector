package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_NEW_POSITIONS_PENDING_CONFIRMATION;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.services.client.spire.PositionSpireApiClient;
import com.intellecteu.onesource.integration.services.record.CloudEventFactoryImpl;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.record.ContractInitiationCloudEventBuilder;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
class BackOfficeServiceTest {

    @Mock
    private PositionSpireApiClient positionSpireApiClient;
    @Mock
    private SpireMapper spireMapper;
    @Mock
    private CloudEventRecordService cloudEventRecordService;
    @InjectMocks
    private BackOfficeService service;

    @Test
    @DisplayName("Record Unauthorized response in a cloud event.")
    void testExceptionCloudEventCapturing_whenApiResponseIsUnauthorized() {
        var expectedExceptionToCapture = new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        var recordFactory = new CloudEventFactoryImpl(
            Map.of(CONTRACT_INITIATION, new ContractInitiationCloudEventBuilder()));
        var eventBuilder = recordFactory.eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildExceptionRequest(
            expectedExceptionToCapture, GET_NEW_POSITIONS_PENDING_CONFIRMATION);

        var argumentCaptor = ArgumentCaptor.forClass(CloudEventBuildRequest.class);

        when(positionSpireApiClient.getPositions(any()))
            .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));
        when(cloudEventRecordService.getFactory()).thenReturn(recordFactory);
        doNothing().when(cloudEventRecordService).record(argumentCaptor.capture());

        service.getNewSpirePositions(Optional.empty());

        assertTrue(cloudEventsAreEqual(recordRequest, argumentCaptor.getValue()));
    }

    @Test
    @DisplayName("Record Forbidden response in a cloud event.")
    void testExceptionCloudEventCapturing_whenApiResponseIsForbidden() {
        var expectedExceptionToCapture = new HttpClientErrorException(HttpStatus.FORBIDDEN);
        var recordFactory = new CloudEventFactoryImpl(
            Map.of(CONTRACT_INITIATION, new ContractInitiationCloudEventBuilder()));
        var eventBuilder = recordFactory.eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildExceptionRequest(
            expectedExceptionToCapture, GET_NEW_POSITIONS_PENDING_CONFIRMATION);

        var argumentCaptor = ArgumentCaptor.forClass(CloudEventBuildRequest.class);

        when(positionSpireApiClient.getPositions(any()))
            .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));
        when(cloudEventRecordService.getFactory()).thenReturn(recordFactory);
        doNothing().when(cloudEventRecordService).record(argumentCaptor.capture());

        service.getNewSpirePositions(Optional.empty());

        assertTrue(cloudEventsAreEqual(recordRequest, argumentCaptor.getValue()));
    }

    private boolean cloudEventsAreEqual(CloudEventBuildRequest recordRequest, CloudEventBuildRequest capturedRequest) {
        return recordRequest.getRecordType().equals(capturedRequest.getRecordType())
            && recordRequest.getSubject().split("-")[0].equals(capturedRequest.getSubject().split("-")[0])
            && recordRequest.getRelatedProcess().equals(capturedRequest.getRelatedProcess())
            && recordRequest.getRelatedSubProcess().equals(capturedRequest.getRelatedSubProcess())
            && recordRequest.getData().equals(capturedRequest.getData());
    }

}