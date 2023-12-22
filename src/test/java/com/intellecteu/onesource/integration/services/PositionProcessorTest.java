package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.DtoTestFactory.getPositionAsJson;
import static com.intellecteu.onesource.integration.TestConfig.createTestObjectMapper;
import static com.intellecteu.onesource.integration.constant.PositionConstant.BORROWER_POSITION_TYPE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.LENDER_POSITION_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellecteu.onesource.integration.DtoTestFactory;
import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.routes.processor.PositionProcessor;
import com.intellecteu.onesource.integration.services.record.CloudEventFactory;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.record.ContractInitiationCloudEventBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PositionProcessorTest {

    @Mock
    private PositionService positionService;
    @Mock
    private AgreementRepository agreementRepository;
    @Mock
    private ContractRepository contractRepository;
    private SpireMapper spireMapper;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private PositionRepository positionRepository;
    @Mock
    private SpireService spireService;
    @Mock
    private OneSourceService oneSourceService;
    @Mock
    private CloudEventRecordService cloudEventRecordService;
    @Mock
    private SettlementService settlementService;

    @Mock
    private ReconcileService<AgreementDto, PositionDto> reconcileService;

    private PositionProcessor positionProcessor;

    @BeforeEach
    void setUp() {
        openMocks(this);
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        spireMapper = new SpireMapper(objectMapper);
        positionProcessor = new PositionProcessor(positionService, agreementRepository, contractRepository,
            spireMapper, eventMapper, positionRepository, oneSourceService, settlementService,
            cloudEventRecordService, reconcileService);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Test contract initiation flow for the Borrower")
    void test_contractInitiationFlow_whenBorrower() throws Exception {
        var testEventMapper = new EventMapper(createTestObjectMapper());
        var position = ModelTestFactory.buildPosition();
        var positionDto = DtoTestFactory.buildPositionDto();
        position.getPositionType().setPositionType(BORROWER_POSITION_TYPE);
        position.setMatching1SourceTradeAgreementId("testAgreementId");
        positionDto.getPositionTypeDto().setPositionType(BORROWER_POSITION_TYPE);
        positionDto.setMatching1SourceTradeAgreementId("testAgreementId");
        var agreement = ModelTestFactory.buildAgreement();
        var agreementDto = DtoTestFactory.buildAgreementDto();
        var contractDto = DtoTestFactory.buildContractDto();
        var contract = testEventMapper.toContractEntity(contractDto);
        contractDto.setProcessingStatus(ProcessingStatus.RECONCILED);
        final JsonNode jsonNode = testEventMapper.getObjectMapper().readTree(getPositionAsJson());
        var eventFactoryMock = Mockito.mock(CloudEventFactory.class);
        var settlementDto = DtoTestFactory.buildSettlementDto();

        when(positionRepository.findAllByProcessingStatus(ProcessingStatus.NEW)).thenReturn(List.of(position));
        when(agreementRepository.findByVenueRefId(any())).thenReturn(List.of());
        when(agreementRepository.findByAgreementId(any())).thenReturn(List.of(agreement));
        when(contractRepository.findByVenueRefId(any())).thenReturn(List.of(contract));
        when(contractRepository.save(any())).thenReturn(contract);
        when(positionRepository.save(any())).thenReturn(position);
        when(settlementService.getSettlementInstruction(any())).thenReturn(List.of(settlementDto));
        when(settlementService.persistSettlement(any())).thenReturn(settlementDto);
        when(eventMapper.toContractDto(any())).thenReturn(contractDto);
        when(eventMapper.toAgreementDto(any())).thenReturn(agreementDto);
        when(cloudEventRecordService.getFactory()).thenReturn(eventFactoryMock);
        when(eventFactoryMock.eventBuilder(any())).thenReturn(new ContractInitiationCloudEventBuilder());
        doNothing().when(reconcileService).reconcile(any(), any());

        positionProcessor.startContractInitiation();

        verify(positionRepository).findAllByProcessingStatus(ProcessingStatus.NEW);
        verify(settlementService).getSettlementInstruction(any());
        verify(settlementService).persistSettlement(any());
        verify(positionRepository, times(3)).save(any());
        verify(agreementRepository).findByVenueRefId(any());
        verify(contractRepository).findByVenueRefId(any());
        verify(oneSourceService, never()).createContract(any(), any(), any());
        verify(reconcileService).reconcile(any(AgreementDto.class), any(PositionDto.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Test contract initiation flow for the Lender")
    void test_contractInitiationFlow_whenLender() throws Exception {
        var testEventMapper = new EventMapper(createTestObjectMapper());
        var position = ModelTestFactory.buildPosition();
        var positionDto = DtoTestFactory.buildPositionDto();
        position.getPositionType().setPositionType(LENDER_POSITION_TYPE);
        positionDto.getPositionTypeDto().setPositionType(LENDER_POSITION_TYPE);
        var contractDto = DtoTestFactory.buildContractDto();
        var contract = testEventMapper.toContractEntity(contractDto);
        var agreement = ModelTestFactory.buildAgreement();
        var agreementDto = DtoTestFactory.buildAgreementDto();
        contractDto.setProcessingStatus(ProcessingStatus.RECONCILED);
        var eventFactoryMock = Mockito.mock(CloudEventFactory.class);
        var settlementDto = DtoTestFactory.buildSettlementDto();

        when(positionRepository.findAllByProcessingStatus(ProcessingStatus.NEW)).thenReturn(List.of(position));
        when(agreementRepository.findByVenueRefId(any())).thenReturn(List.of());
        when(agreementRepository.findByAgreementId(any())).thenReturn(List.of(agreement));
        when(contractRepository.findByVenueRefId(any())).thenReturn(List.of(contract));
        when(contractRepository.save(any())).thenReturn(contract);
        when(positionRepository.save(any())).thenReturn(position);
        when(settlementService.getSettlementInstruction(any())).thenReturn(List.of(settlementDto));
        when(settlementService.persistSettlement(any())).thenReturn(settlementDto);
        when(eventMapper.toContractDto(any())).thenReturn(contractDto);
        when(eventMapper.toAgreementDto(any())).thenReturn(agreementDto);
        when(cloudEventRecordService.getFactory()).thenReturn(eventFactoryMock);
        when(eventFactoryMock.eventBuilder(any())).thenReturn(new ContractInitiationCloudEventBuilder());
        doNothing().when(oneSourceService).createContract(any(), any(), any());

        positionProcessor.startContractInitiation();

        verify(positionRepository).findAllByProcessingStatus(ProcessingStatus.NEW);
        verify(settlementService).getSettlementInstruction(any());
        verify(settlementService).persistSettlement(any());
        verify(positionRepository, times(3)).save(any());
        verify(agreementRepository).findByVenueRefId(any());
        verify(contractRepository).findByVenueRefId(any());
        verify(oneSourceService).createContract(any(), any(), any());
        verify(reconcileService).reconcile(any(AgreementDto.class), any(PositionDto.class));
    }
}