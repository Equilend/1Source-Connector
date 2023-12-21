package com.intellecteu.onesource.integration.services.processor.strategy.contract;

import static com.intellecteu.onesource.integration.constant.PositionConstant.BORROWER_POSITION_TYPE;
import static lombok.AccessLevel.PRIVATE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intellecteu.onesource.integration.DtoTestFactory;
import com.intellecteu.onesource.integration.TestConfig;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.dto.spire.PositionTypeDto;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.ContractStatus;
import com.intellecteu.onesource.integration.model.EventType;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.SettlementTempRepository;
import com.intellecteu.onesource.integration.routes.processor.strategy.contract.ContractDataReceived;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.SpireService;
import com.intellecteu.onesource.integration.services.record.CloudEventFactory;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.record.ContractInitiationCloudEventBuilder;
import java.util.List;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = PRIVATE)
class ContractDataReceivedTest {

    @Mock
    ContractRepository contractRepository;
    @Mock
    PositionRepository positionRepository;
    @Mock
    SettlementTempRepository settlementTempRepository;
    @Mock
    SettlementService settlementService;
    @Mock
    SpireService spireService;
    @Mock
    CloudEventRecordService cloudEventRecordService;
    @Mock
    ReconcileService<ContractDto, PositionDto> reconcileService;
    @Mock
    AgreementRepository agreementRepository;
    @Mock
    OneSourceService oneSourceService;
    EventMapper eventMapper;
    SpireMapper spireMapper;
    ContractDataReceived service;
    ContractDto contractDto;
    PositionDto positionDto;

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should update the counterparty's settlement instruction")
    void testUpdateCounterpartySettlementInstruction_shouldSuccess() {
        positionDto.setPositionTypeDto(new PositionTypeDto(BORROWER_POSITION_TYPE));
        Position position = spireMapper.toPosition(positionDto);
        contractDto.setEventType(EventType.CONTRACT_PENDING);
        contractDto.setContractStatus(ContractStatus.APPROVED);
        contractDto.getSettlement().get(0).setPartyRole(PartyRole.LENDER);
        SettlementDto settlementDto = DtoTestFactory.buildSettlementDto();
        settlementDto.setPartyRole(PartyRole.LENDER);
        var settlementResponse = new ResponseEntity<>(settlementDto, HttpStatus.OK);
        var eventFactoryMock = Mockito.mock(CloudEventFactory.class);

        when(positionRepository.findByVenueRefId(any())).thenReturn(List.of(position));
        when(positionRepository.save(any())).thenReturn(null);
        when(contractRepository.save(any())).thenReturn(null);
        when(settlementService.retrieveSettlementDetails(any(), eq(PartyRole.LENDER), any())).thenReturn(
            settlementResponse);
        when(cloudEventRecordService.getFactory()).thenReturn(eventFactoryMock);
        when(eventFactoryMock.eventBuilder(any())).thenReturn(new ContractInitiationCloudEventBuilder());
        doNothing().when(cloudEventRecordService).record(any());

        service.process(contractDto);

        verify(positionRepository).findByVenueRefId(any());
        verify(positionRepository).save(any());
        verify(contractRepository, times(2)).save(any());
        verify(settlementService).retrieveSettlementDetails(any(), any(), any());
        verify(settlementService).updateSpireInstruction(any(), any(), any());
        verify(spireService).updatePosition(any(), any());
        verify(cloudEventRecordService).record(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should update the counterparty's settlement instruction")
    void testUpdateCounterpartySettlementInstruction_shouldCaptureResponseException() {
        positionDto.setPositionTypeDto(new PositionTypeDto(BORROWER_POSITION_TYPE));
        Position position = spireMapper.toPosition(positionDto);
        contractDto.setEventType(EventType.CONTRACT_PENDING);
        contractDto.setContractStatus(ContractStatus.APPROVED);
        contractDto.getSettlement().get(0).setPartyRole(PartyRole.LENDER);
        contractDto.setMatchingSpirePositionId(positionDto.getPositionId());
        SettlementDto settlementDto = DtoTestFactory.buildSettlementDto();
        settlementDto.setPartyRole(PartyRole.LENDER);
        var eventFactoryMock = Mockito.mock(CloudEventFactory.class);

        when(positionRepository.findByVenueRefId(any())).thenReturn(List.of(position));
        when(positionRepository.save(any())).thenReturn(null);
        when(contractRepository.save(any())).thenReturn(null);
        when(settlementService.retrieveSettlementDetails(any(), eq(PartyRole.LENDER), any())).thenThrow(
            new HttpClientErrorException(HttpStatus.FORBIDDEN));
        when(cloudEventRecordService.getFactory()).thenReturn(eventFactoryMock);
        when(eventFactoryMock.eventBuilder(any())).thenReturn(new ContractInitiationCloudEventBuilder());
        doNothing().when(cloudEventRecordService).record(any());

        service.process(contractDto);

        verify(positionRepository).findByVenueRefId(any());
        verify(positionRepository).save(any());
        verify(contractRepository, times(2)).save(any());
        verify(settlementService).retrieveSettlementDetails(any(), any(), any());
        verify(settlementService, never()).updateSpireInstruction(any(), any(), any());
        verify(spireService, never()).updateInstruction(any(), any(), any());
        verify(spireService).updatePosition(any(), any());
        verify(cloudEventRecordService, times(2)).record(any());
    }

    @BeforeEach
    void setUp() {
        contractDto = DtoTestFactory.buildContractDto();
        positionDto = DtoTestFactory.buildPositionDtoFromTradeAgreement(contractDto.getTrade());
        eventMapper = new EventMapper(TestConfig.createTestObjectMapper());
        spireMapper = new SpireMapper(TestConfig.createTestObjectMapper());
        service = new ContractDataReceived(contractRepository, positionRepository,
            settlementTempRepository, settlementService,
            spireService, cloudEventRecordService,
            reconcileService, eventMapper, spireMapper,
            agreementRepository, oneSourceService);
    }
}