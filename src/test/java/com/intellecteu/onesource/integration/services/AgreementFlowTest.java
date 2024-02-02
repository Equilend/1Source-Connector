package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.DtoTestFactory.buildAgreementDto;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_SETTLEMENT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.GENERIC;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.MAINTAIN_1SOURCE_PARTICIPANTS_LIST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellecteu.onesource.integration.TestConfig;
import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ExceptionMessageDto;
import com.intellecteu.onesource.integration.dto.record.IntegrationCloudEvent;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapperImpl;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.SettlementUpdateRepository;
import com.intellecteu.onesource.integration.repository.TradeEventRepository;
import com.intellecteu.onesource.integration.routes.contract_initiation_without_trade.processor.strategy.agreement.AgreementDataReceived;
import com.intellecteu.onesource.integration.services.client.onesource.OneSourceApiClientImpl;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventFactory;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventFactoryImpl;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.GenericRecordCloudEventBuilder;
import com.intellecteu.onesource.integration.services.systemevent.IntegrationCloudEventBuilder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
@Disabled // todo temporary disabled until Flow I requirements will be updated
public class AgreementFlowTest {

    private static final String ENDPOINT_FIELD_INJECT = "onesourceBaseEndpoint";
    private static final String LENDER_ENDPOINT_FIELD_INJECT = "lenderSpireEndpoint";
    private static final String BORROWER_ENDPOINT_FIELD_INJECT = "borrowerSpireEndpoint";
    private static final String VERSION_FIELD_INJECT = "version";
    private static final String TEST_ENDPOINT = "http://localhost:8080";
    private static final String TEST_API_VERSION = "/v1";
    private static final String TEST_CREATE_CONTRACT_ENDPOINT = "/ledger/contracts";
    private static final String TEST_GET_INSTRUCTION_ENDPOINT = "/rds/static/instruction";
    private static final String TEST_GET_POSITION_ENDPOINT = "/trades/search/position/query";

    @Mock
    private SettlementUpdateRepository settlementUpdateRepository;
    @Mock
    private PositionService positionService;
    @Mock
    private PositionRepository positionRepository;
    @Mock
    private ContractRepository contractRepository;
    @Mock
    private AgreementRepository agreementRepository;
    @Mock
    private AgreementService agreementService;
    @Mock
    private RestTemplate restTemplate;
    private EventMapper eventMapper;
    private SpireMapper spireMapper;
    private ObjectMapper objectMapper;
    private OneSourceApiClientImpl oneSourceService;
    @Mock
    private SettlementService settlementService;
    @Mock
    private BackOfficeService lenderBackOfficeService;
    @Mock
    private BackOfficeService borrowerBackOfficeService;
    @Mock
    private ReconcileService<AgreementDto, PositionDto> reconcileService;
    private AgreementDataReceived agreementDataReceived;

    @Mock
    private CloudEventRecordService cloudEventRecordService;
    @Mock
    private TradeEventRepository eventRepository;
    private CloudEventFactory<IntegrationCloudEvent> recordFactory;
    private OneSourceMapper oneSourceMapper = new OneSourceMapperImpl();

    @BeforeEach
    void setUp() {
        objectMapper = TestConfig.createTestObjectMapper();
        eventMapper = new EventMapper(objectMapper);
        spireMapper = new SpireMapper(objectMapper);
        var builderMap = new HashMap<IntegrationProcess, IntegrationCloudEventBuilder>();
        builderMap.put(GENERIC, new GenericRecordCloudEventBuilder());
        builderMap.put(CONTRACT_INITIATION, new GenericRecordCloudEventBuilder());
        builderMap.put(MAINTAIN_1SOURCE_PARTICIPANTS_LIST, new GenericRecordCloudEventBuilder());
        builderMap.put(CONTRACT_SETTLEMENT, new GenericRecordCloudEventBuilder());
        recordFactory = new CloudEventFactoryImpl(builderMap);
        oneSourceService = new OneSourceApiClientImpl(contractRepository, cloudEventRecordService, restTemplate,
            settlementUpdateRepository, eventMapper, eventRepository, oneSourceMapper);
        agreementDataReceived = new AgreementDataReceived(oneSourceService, settlementService, reconcileService,
            agreementService, positionService, eventMapper, spireMapper, cloudEventRecordService,
            lenderBackOfficeService, borrowerBackOfficeService);
        ReflectionTestUtils.setField(oneSourceService, ENDPOINT_FIELD_INJECT, TEST_ENDPOINT);
        ReflectionTestUtils.setField(oneSourceService, VERSION_FIELD_INJECT, TEST_API_VERSION);
    }

    @Test
    @DisplayName("Contract proposal successfully created with 201 response code.")
    void test_agreementFlow_shouldCreateContractProposal_success() throws JsonProcessingException, ReconcileException {
        var agreement = buildAgreementDto();
        var agreementEntity = eventMapper.toAgreementEntity(agreement);
        agreement.getTrade().setTradeDate(LocalDateTime.parse("2023-11-14T16:52:06.060844").toLocalDate());
        agreement.getTrade().setSettlementDate(LocalDateTime.parse("2023-11-14T16:52:06.061189").toLocalDate());

        String positionEntityResponse = """
            {"id":1,"positionId":"testSpirePositionId","customValue2":"testVenueRefId","rate":10.2,"quantity":2.0,"tradeDate":"2023-11-14T16:52:06.060844","settleDate":"2023-11-14T16:52:06.061189","deliverFree":false,"amount":400.32,"price":100.0,"contractValue":4.52,"positionTypeId":null,"currencyId":null,"securityId":null,"accountLei":"lender-lei","cpLei":"borrower-lei","collateralType":"CASH","cpHaircut":2.02,"cpMarkRoundTo":2,"depoId":null,"securityDetailDTO":{"ticker":"testTicker","cusip":"testCusip","isin":"testIsin","sedol":"testSedol","quickCode":"testQuick","bloombergId":"testFigi"},"currencyDTO":{"currencyName":"EUR"},"loanBorrowDTO":{"taxWithholdingRate":2.0},"collateralTypeDTO":{"collateralType":"CASH"},"exposureDTO":{"cpHaircut":2.02,"cpMarkRoundTo":2,"depoId":null},"positiontypeDTO":{"positionType":"CASH LOAN"},"accountDTO":{"dtc":null,"lei":"lender-lei"},"counterPartyDTO":{"dtc":null,"lei":"borrower-lei"}, "statusDTO":{"status":"CREATED"}}
            """;

        JsonNode positionEntityNode = objectMapper.readTree(positionEntityResponse);
        var positionEntity = spireMapper.toPosition(positionEntityNode);
        positionEntity.getCurrency().setCurrencyKy("USD");
        var positionDto = spireMapper.toPositionDto(positionEntity);

//        when(spireService.getTradePosition(any(AgreementDto.class))).thenReturn(positionDto);
        when(agreementService.saveAgreement(any())).thenReturn(agreementEntity);
        when(positionService.findByVenueRefId(any())).thenReturn(Optional.of(positionEntity));
        when(positionService.savePosition(any())).thenReturn(positionEntity);
        doNothing().when(reconcileService).reconcile(any(AgreementDto.class), any(PositionDto.class));
        doNothing().when(cloudEventRecordService).record(any());
        when(cloudEventRecordService.getFactory()).thenReturn(recordFactory);

        agreementDataReceived.process(agreement);

        verify(positionService).findByVenueRefId(any());
        verify(cloudEventRecordService, times(2)).record(any());
        verify(positionService, times(2)).savePosition(any());
        verify(agreementService, times(3)).saveAgreement(any());
        verify(reconcileService).reconcile(any(AgreementDto.class), any(PositionDto.class));
    }

    @Test
    @DisplayName("Contract proposal was not created due reconcile error")
    void test_agreementFlow_shouldNotCreateContractProposal_reconcile_failed() throws Exception {
        var agreement = buildAgreementDto();
        String positionEntityResponse = """
            {"id":1,"positionId":"testSpirePositionId","customValue2":"testVenueRefId","rate":10.2,"quantity":2.0,"tradeDate":"2023-11-14T16:52:06.060844","settleDate":"2023-11-14T16:52:06.061189","deliverFree":false,"amount":400.32,"price":100.0,"contractValue":4.52,"positionTypeId":null,"currencyId":null,"securityId":null,"accountLei":"lender-lei","cpLei":"borrower-lei","collateralType":"CASH","cpHaircut":2.02,"cpMarkRoundTo":2,"depoId":null,"securityDetailDTO":{"ticker":"testTicker","cusip":"testCusip","isin":"testIsin","sedol":"testSedol","quickCode":"testQuick","bloombergId":"testFigi"},"currencyDTO":{"currencyName":"EUR"},"loanBorrowDTO":{"taxWithholdingRate":2.0},"collateralTypeDTO":{"collateralType":"CASH"},"exposureDTO":{"cpHaircut":2.02,"cpMarkRoundTo":2,"depoId":null},"positiontypeDTO":{"positionType":"CASH LOAN"},"accountDTO":{"dtc":null,"lei":"lender-lei"},"counterPartyDTO":{"dtc":null,"lei":"borrower-lei"}, "statusDTO":{"status":"CREATED"}}
            """;

        var createContractUrl = TEST_ENDPOINT + TEST_API_VERSION + TEST_CREATE_CONTRACT_ENDPOINT;
        var getInstructionUrl = TEST_ENDPOINT + TEST_GET_INSTRUCTION_ENDPOINT;

        JsonNode positionEntityNode = objectMapper.readTree(positionEntityResponse);
        var positionEntity = spireMapper.toPosition(positionEntityNode);
        var positionDto = spireMapper.toPositionDto(positionEntity);

//        when(spireService.getTradePosition(any(AgreementDto.class))).thenReturn(positionDto);
        when(positionService.savePosition(any())).thenReturn(positionEntity);
        var exceptionMsgDto = new ExceptionMessageDto("testValue", "testMsg");
        final ReconcileException reconcileException = new ReconcileException("exception", List.of(exceptionMsgDto));
        doThrow(reconcileException).when(reconcileService)
            .reconcile(any(AgreementDto.class), any(PositionDto.class));
        doNothing().when(cloudEventRecordService).record(any());
        when(cloudEventRecordService.getFactory()).thenReturn(recordFactory);

        agreementDataReceived.process(agreement);

        verify(restTemplate, never()).postForEntity(eq(getInstructionUrl), any(), eq(JsonNode.class));
        verify(restTemplate, never()).exchange(eq(createContractUrl), eq(POST), any(), eq(JsonNode.class));
    }
}
