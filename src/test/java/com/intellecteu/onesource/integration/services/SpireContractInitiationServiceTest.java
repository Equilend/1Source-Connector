package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.DtoTestFactory.getPositionAsJson;
import static com.intellecteu.onesource.integration.TestConfig.createTestObjectMapper;
import static com.intellecteu.onesource.integration.constant.PositionConstant.BORROWER_POSITION_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.DtoTestFactory;
import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.PositionMapper;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.SettlementRepository;
import com.intellecteu.onesource.integration.services.record.CloudEventFactory;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.record.ContractInitiationCloudEventBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class SpireContractInitiationServiceTest {

    @Mock
    private AgreementRepository agreementRepository;
    @Mock
    private ContractRepository contractRepository;
    @Mock
    private PositionMapper positionMapper;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private PositionRepository positionRepository;
    @Mock
    private SettlementRepository settlementRepository;
    @Mock
    private SpireService spireService;
    @Mock
    private OneSourceService oneSourceService;
    @Mock
    private CloudEventRecordService cloudEventRecordService;
    @Mock
    private SettlementService settlementService;

    @InjectMocks
    private SpireContractInitiationService positionApiService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Execute approve contract for Borrower reconciled contract")
    void test_shouldExecuteApproveContract_whenReconciledAndBorrower() throws JsonProcessingException {
        var testEventMapper = new EventMapper(createTestObjectMapper());
        var position = ModelTestFactory.buildPosition();
        var positionDto = DtoTestFactory.buildPositionDto();
        position.getPositionType().setPositionType(BORROWER_POSITION_TYPE);
        var contractDto = DtoTestFactory.buildContractDto();
        var contract = testEventMapper.toContractEntity(contractDto);
        contractDto.setProcessingStatus(ProcessingStatus.RECONCILED);
        final JsonNode jsonNode = testEventMapper.getObjectMapper().readTree(getPositionAsJson());
        var eventFactoryMock = Mockito.mock(CloudEventFactory.class);
        var settlementDto = DtoTestFactory.buildSettlementDto();

        when(positionRepository.findAll()).thenReturn(List.of(position));
        when(spireService.requestPosition(any())).thenReturn(ResponseEntity.ok(jsonNode));
        when(positionMapper.jsonToPositionDto(any(JsonNode.class))).thenReturn(positionDto);
        when(positionMapper.toPosition(any(PositionDto.class))).thenReturn(position);
        when(agreementRepository.findByVenueRefId(any())).thenReturn(List.of());
        when(contractRepository.findByVenueRefId(any())).thenReturn(List.of(contract));
        when(positionRepository.save(any())).thenReturn(position);
        when(settlementService.getSettlementInstruction(any())).thenReturn(List.of(settlementDto));
        when(settlementService.persistSettlement(any())).thenReturn(settlementDto);
        when(eventMapper.toContractDto(any())).thenReturn(contractDto);
        when(cloudEventRecordService.getFactory()).thenReturn(eventFactoryMock);
        when(eventFactoryMock.eventBuilder(any())).thenReturn(new ContractInitiationCloudEventBuilder());
        when(contractRepository.save(any())).thenReturn(contract);
        doNothing().when(oneSourceService).createContract(any(), any(), any());

        positionApiService.startContractInitiation();

        verify(positionRepository).findAll();
        verify(spireService).requestPosition(any());
        verify(positionMapper).jsonToPositionDto(any(JsonNode.class));
        verify(settlementService).getSettlementInstruction(any());
        verify(settlementService).persistSettlement(any());
        verify(positionMapper, times(2)).toPosition(any(PositionDto.class));
        verify(positionRepository, times(2)).save(any());
        verify(agreementRepository).findByVenueRefId(any());
        verify(contractRepository).findByVenueRefId(any());
        verify(oneSourceService).createContract(any(), any(), any());
    }
}