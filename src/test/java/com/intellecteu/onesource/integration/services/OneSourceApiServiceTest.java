package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.GENERIC;
import static com.intellecteu.onesource.integration.model.onesource.RoundingMode.ALWAYSUP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapperImpl;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.IntegrationCloudEvent;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.ContractProposal;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.onesource.TradeAgreement;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.SettlementUpdateRepository;
import com.intellecteu.onesource.integration.repository.TradeEventRepository;
import com.intellecteu.onesource.integration.services.client.onesource.ContractsApi;
import com.intellecteu.onesource.integration.services.client.onesource.OneSourceApiClientImpl;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventFactory;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventFactoryImpl;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.ContractInitiationCloudEventBuilder;
import com.intellecteu.onesource.integration.services.systemevent.GenericRecordCloudEventBuilder;
import com.intellecteu.onesource.integration.services.systemevent.IntegrationCloudEventBuilder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
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
    private ContractsApi contractsApi;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private CloudEventRecordService recordService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private TradeEventRepository eventRepository;

    private CloudEventFactory<IntegrationCloudEvent> eventFactory;

    private OneSourceApiClientImpl service;

    private OneSourceMapper oneSourceMapper = new OneSourceMapperImpl();

    @BeforeEach
    void setUp() {
        service = new OneSourceApiClientImpl(contractsApi, contractRepository, recordService, restTemplate,
            settlementUpdateRepository, eventRepository, oneSourceMapper);
        ReflectionTestUtils.setField(service, ENDPOINT_FIELD_INJECT, TEST_ENDPOINT);
        ReflectionTestUtils.setField(service, VERSION_FIELD_INJECT, TEST_API_VERSION);
        var builderMap = new HashMap<IntegrationProcess, IntegrationCloudEventBuilder>();
        builderMap.put(GENERIC, new GenericRecordCloudEventBuilder(VERSION_FIELD_INJECT, TEST_ENDPOINT));
        builderMap.put(CONTRACT_INITIATION,
            new ContractInitiationCloudEventBuilder(VERSION_FIELD_INJECT, TEST_ENDPOINT));
        eventFactory = new CloudEventFactoryImpl(builderMap);
    }

    @Test
    @Disabled(value = "Staging env sends 404 on encoded timestamp. Temporary disabled.")
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

    ContractProposal buildContract(Agreement agreement, Position position,
        List<Settlement> settlements) {
        TradeAgreement trade = agreement.getTrade();
        trade.getCollateral().setRoundingRule(position.getCpMarkRoundTo());
        trade.getCollateral().setRoundingMode(ALWAYSUP);
        return ContractProposal.builder()
            .trade(trade)
            .settlementList(settlements)
            .build();
    }

}