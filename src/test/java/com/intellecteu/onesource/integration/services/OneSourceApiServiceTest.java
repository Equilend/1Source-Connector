package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.GENERIC;
import static com.intellecteu.onesource.integration.model.RoundingMode.ALWAYSUP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.DtoTestFactory;
import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractProposalDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.dto.record.IntegrationCloudEvent;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.SettlementUpdateRepository;
import com.intellecteu.onesource.integration.repository.TradeEventRepository;
import com.intellecteu.onesource.integration.services.record.CloudEventFactory;
import com.intellecteu.onesource.integration.services.record.CloudEventFactoryImpl;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.record.ContractInitiationCloudEventBuilder;
import com.intellecteu.onesource.integration.services.record.GenericRecordCloudEventBuilder;
import com.intellecteu.onesource.integration.services.record.IntegrationCloudEventBuilder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@ExtendWith(MockitoExtension.class)
class OneSourceApiServiceTest {

    private static final String ENDPOINT_FIELD_INJECT = "onesourceBaseEndpoint";
    private static final String VERSION_FIELD_INJECT = "version";
    private static final String TEST_EVENT_ENDPOINT = "/ledger/events";
    private static final String TEST_CREATE_CONTRACT_ENDPOINT = "/ledger/contracts";
    private static final String TEST_ENDPOINT = "http://localhost:8080";
    private static final String TEST_API_VERSION = "/v1";

    @Mock
    private SettlementUpdateRepository settlementUpdateRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private CloudEventRecordService recordService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private TradeEventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    private CloudEventFactory<IntegrationCloudEvent> eventFactory;

    private OneSourceApiService service;

    @BeforeEach
    void setUp() {
        service = new OneSourceApiService(contractRepository, recordService, restTemplate,
            settlementUpdateRepository, eventMapper, eventRepository);
        ReflectionTestUtils.setField(service, ENDPOINT_FIELD_INJECT, TEST_ENDPOINT);
        ReflectionTestUtils.setField(service, VERSION_FIELD_INJECT, TEST_API_VERSION);
        var builderMap = new HashMap<IntegrationProcess, IntegrationCloudEventBuilder>();
        builderMap.put(GENERIC, new GenericRecordCloudEventBuilder());
        builderMap.put(CONTRACT_INITIATION, new ContractInitiationCloudEventBuilder());
        eventFactory = new CloudEventFactoryImpl(builderMap);
    }

    @Test
    @DisplayName("Url builder should change colons ':' symbols to encoded '%3A' chars")
    void test_retrieveEvents_shouldEncodeColonsSymbols() {
        var localDateTime = LocalDateTime.of(2023, 10, 20, 11, 10, 7, 2);
        var expectedParam = "?since=2023-10-20T11%3A10%3A07.000000002Z";
        var expectedUrl = TEST_ENDPOINT + TEST_API_VERSION + TEST_EVENT_ENDPOINT + expectedParam;
        var response = ResponseEntity.ok(List.of());

        when(restTemplate.exchange(eq(expectedUrl), eq(GET), any(), any(ParameterizedTypeReference.class)))
            .thenReturn(response);

        service.retrieveEvents(localDateTime);

        verify(restTemplate).exchange(eq(expectedUrl), eq(GET), any(), any(ParameterizedTypeReference.class));
    }

    @Test
    @DisplayName("Execute record service when create contract request returns 401 response code.")
    void test_createContract_shouldExecuteRecordService_whenResponse401() {
        var agreement = DtoTestFactory.buildAgreementDto();
        var settlement = new SettlementDto();
        var position = DtoTestFactory.buildPositionDtoFromTradeAgreement(agreement.getTrade());

        var expectedUrl = TEST_ENDPOINT + TEST_API_VERSION + TEST_CREATE_CONTRACT_ENDPOINT;

        when(recordService.getFactory()).thenReturn(eventFactory);
        when(restTemplate.exchange(eq(expectedUrl), eq(POST), any(), eq(JsonNode.class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        service.createContract(agreement, buildContract(agreement, position, List.of(settlement)), position);

        verify(restTemplate).exchange(eq(expectedUrl), eq(POST), any(), eq(JsonNode.class));
        verify(recordService).getFactory();
        verify(recordService).record(any(CloudEventBuildRequest.class));
    }

    ContractProposalDto buildContract(AgreementDto agreement, PositionDto positionDto,
        List<SettlementDto> settlements) {
        TradeAgreementDto trade = agreement.getTrade();
        trade.getCollateral().setRoundingRule(positionDto.getCpMarkRoundTo());
        trade.getCollateral().setRoundingMode(ALWAYSUP);
        return ContractProposalDto.builder()
            .trade(trade)
            .settlement(settlements)
            .build();
    }

}