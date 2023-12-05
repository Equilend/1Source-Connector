package com.intellecteu.onesource.integration.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.DtoTestFactory;
import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.PositionMapper;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.Settlement;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.SettlementRepository;
import com.intellecteu.onesource.integration.services.record.CloudEventFactory;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.record.ContractInitiationCloudEventBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.intellecteu.onesource.integration.DtoTestFactory.getPositionAsJson;
import static com.intellecteu.onesource.integration.TestConfig.createTestObjectMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PositionApiServiceTest {

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

  @InjectMocks
  private PositionApiService positionApiService;

  @BeforeEach
  void setUp() {}

  @Test
  @SuppressWarnings("unchecked")
  @DisplayName("Execute approve contract for Borrower reconciled contract")
  void test_shouldExecuteApproveContract_whenReconciledAndBorrower() throws JsonProcessingException {
    var testEventMapper = new EventMapper(createTestObjectMapper());
    var position = ModelTestFactory.buildPosition();
    var positionDto = DtoTestFactory.buildPositionDto();
    position.getPositionType().setPositionType("BORROW CASH");
    var contractDto = DtoTestFactory.buildContractDto();
    var contract = testEventMapper.toContractEntity(contractDto);
    contractDto.setProcessingStatus(ProcessingStatus.RECONCILED);
    final JsonNode jsonNode = testEventMapper.getObjectMapper().readTree(getPositionAsJson());
    var eventFactoryMock = Mockito.mock(CloudEventFactory.class);
    var settlementDto = DtoTestFactory.buildSettlementDto();


    when(positionRepository.findAll()).thenReturn(List.of(position));
    when(positionMapper.toPosition(any(JsonNode.class))).thenReturn(position);
    when(cloudEventRecordService.getFactory()).thenReturn(eventFactoryMock);
    when(eventFactoryMock.eventBuilder(any())).thenReturn(new ContractInitiationCloudEventBuilder());
    when(spireService.requestPosition(any())).thenReturn(ResponseEntity.ok(jsonNode));
    when(agreementRepository.findByVenueRefId(any())).thenReturn(List.of());
    when(contractRepository.findByVenueRefId(any())).thenReturn(List.of(contract));
    when(contractRepository.save(any())).thenReturn(contract);
    when(positionRepository.save(any())).thenReturn(position);
    when(settlementRepository.save(any())).thenReturn(null);
    when(eventMapper.toContractDto(any())).thenReturn(contractDto);
    when(eventMapper.toSettlementEntity(any())).thenReturn(new Settlement());
    when(spireService.retrieveSettlementDetails(any(), any(), any(), any())).thenReturn(List.of(settlementDto));
    when(positionMapper.toPositionDto(any())).thenReturn(positionDto);
    doNothing().when(cloudEventRecordService).record(any());
    doNothing().when(oneSourceService).approveContract(any(ContractDto.class), any(SettlementDto.class));

    positionApiService.createLoanContractWithoutTA();

    verify(positionRepository).findAll();
    verify(spireService).requestPosition(any());
    verify(agreementRepository).findByVenueRefId(any());
    verify(contractRepository).findByVenueRefId(any());
    verify(oneSourceService).approveContract(any(ContractDto.class), any(SettlementDto.class));
  }
}