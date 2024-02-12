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
import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.TestConfig;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.dto.spire.PositionTypeDto;
import com.intellecteu.onesource.integration.exception.InstructionRetrievementException;
import com.intellecteu.onesource.integration.mapper.ContractMapper;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ContractStatus;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.SettlementTempRepository;
import com.intellecteu.onesource.integration.routes.contract_initiation_without_trade.processor.strategy.contract.ContractDataReceived;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.client.onesource.OneSourceApiClient;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventFactory;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.ContractInitiationCloudEventBuilder;
import java.util.Optional;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = PRIVATE)
class ContractDataReceivedTest {

    @Mock
    ContractService contractService;
    @Mock
    PositionService positionService;
    @Mock
    SettlementTempRepository settlementTempRepository;
    @Mock
    SettlementService settlementService;
    @Mock
    CloudEventRecordService cloudEventRecordService;
    @Mock
    ReconcileService<Contract, PositionDto> reconcileService;
    @Mock
    AgreementRepository agreementRepository;
    @Mock
    OneSourceApiClient oneSourceApiClient;
    @Mock
    BackOfficeService borrowerBackOfficeService;
    @Mock
    BackOfficeService lenderBackOfficeService;
    EventMapper eventMapper;
    SpireMapper spireMapper;
    ContractDataReceived service;
    Contract contract;
    PositionDto positionDto;

    final ContractMapper contractMapper = Mappers.getMapper(ContractMapper.class);

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should update the counterparty's settlement instruction")
    void testUpdateCounterpartySettlementInstruction_shouldSuccess() {
        positionDto.setPositionTypeDto(new PositionTypeDto(BORROWER_POSITION_TYPE));
        Position position = spireMapper.toPosition(positionDto);
        contract.setEventType(EventType.CONTRACT_PENDING);
        contract.setContractStatus(ContractStatus.APPROVED);
        contract.getSettlement().get(0).setPartyRole(PartyRole.LENDER);
        Settlement settlement = DtoTestFactory.buildSettlement();
        settlement.setPartyRole(PartyRole.LENDER);
        var settlementResponse = Optional.of(settlement);
        var eventFactoryMock = Mockito.mock(CloudEventFactory.class);

        when(positionService.findByVenueRefId(any())).thenReturn(Optional.of(position));
        when(positionService.savePosition(any())).thenReturn(null);
        when(contractService.save(any())).thenReturn(null);
        when(lenderBackOfficeService.retrieveSettlementInstruction(any(), any(), any())).thenReturn(settlementResponse);
        when(cloudEventRecordService.getFactory()).thenReturn(eventFactoryMock);
        when(eventFactoryMock.eventBuilder(any())).thenReturn(new ContractInitiationCloudEventBuilder());
        doNothing().when(cloudEventRecordService).record(any());

        service.process(contract);

        verify(positionService).findByVenueRefId(any());
        verify(positionService).savePosition(any());
        verify(contractService, times(2)).save(any());
        verify(lenderBackOfficeService).retrieveSettlementInstruction(any(), any(), any());
        verify(lenderBackOfficeService).updateSettlementInstruction(any());
        verify(borrowerBackOfficeService).update1SourceLoanContractIdentifier(any());
        verify(cloudEventRecordService).record(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should capture an exception on counterparty's settlement instruction update")
    void testUpdateCounterpartySettlementInstruction_shouldCaptureResponseException() {
        positionDto.setPositionTypeDto(new PositionTypeDto(BORROWER_POSITION_TYPE));
        Position position = spireMapper.toPosition(positionDto);
        contract.setEventType(EventType.CONTRACT_PENDING);
        contract.setContractStatus(ContractStatus.APPROVED);
        contract.getSettlement().get(0).setPartyRole(PartyRole.LENDER);
        contract.setMatchingSpirePositionId(positionDto.getPositionId());
        SettlementDto settlementDto = DtoTestFactory.buildSettlementDto();
        settlementDto.setPartyRole(PartyRole.LENDER);
        var eventFactoryMock = Mockito.mock(CloudEventFactory.class);

        when(positionService.findByVenueRefId(any())).thenReturn(Optional.of(position));
        when(positionService.savePosition(any())).thenReturn(null);
        when(contractService.save(any())).thenReturn(null);
        when(lenderBackOfficeService.retrieveSettlementInstruction(any(), eq(PartyRole.LENDER), any())).thenThrow(
            new InstructionRetrievementException(new HttpClientErrorException(HttpStatus.FORBIDDEN)));
        when(cloudEventRecordService.getFactory()).thenReturn(eventFactoryMock);
        when(eventFactoryMock.eventBuilder(any())).thenReturn(new ContractInitiationCloudEventBuilder());
        doNothing().when(cloudEventRecordService).record(any());

        service.process(contract);

        verify(positionService).findByVenueRefId(any());
        verify(positionService).savePosition(any());
        verify(contractService, times(2)).save(any());
        verify(lenderBackOfficeService).retrieveSettlementInstruction(any(), any(), any());
        verify(lenderBackOfficeService, never()).updateSettlementInstruction(any());
        verify(lenderBackOfficeService, never()).updateSettlementInstruction(any());
        verify(borrowerBackOfficeService).update1SourceLoanContractIdentifier(any());
        verify(cloudEventRecordService, times(2)).record(any());
    }

    @BeforeEach
    void setUp() {
        contract = ModelTestFactory.buildContract();
        positionDto = DtoTestFactory.buildPositionDtoFromTradeAgreement(contract.getTrade());
        eventMapper = new EventMapper(TestConfig.createTestObjectMapper());
        spireMapper = new SpireMapper(TestConfig.createTestObjectMapper());
        service = new ContractDataReceived(contractService, positionService,
            settlementTempRepository, settlementService,
            borrowerBackOfficeService, lenderBackOfficeService, cloudEventRecordService,
            reconcileService, eventMapper, spireMapper,
            agreementRepository, oneSourceApiClient, contractMapper);
    }
}