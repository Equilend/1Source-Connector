package com.intellecteu.onesource.integration.routes.delegate_flow.processor;

import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_LOAN_CONTRACT;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.POSITION;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.SPIRE_TRADE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.CANCEL_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_UPDATED_POSITIONS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DECLINED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.POSITION_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.POSITION_CANCEL_SUBMITTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.TradeOut;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RelatedObject;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventFactoryImpl;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.ContractInitiationCloudEventBuilder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdatePositionProcessorTest {

    @Mock
    private BackOfficeService backOfficeService;
    @Mock
    private ContractService contractService;
    @Mock
    private OneSourceService oneSourceService;
    @Mock
    private CloudEventRecordService cloudEventRecordService;

    @InjectMocks
    private UpdatePositionProcessor service;

    @Test
    void testFetchUpdatePositions_shouldReturnTradesList() {
        final TradeOut trade = ModelTestFactory.buildSpireTrade("RERATE");

        when(backOfficeService.fetchUpdatesOnPositions(any())).thenReturn(List.of(trade));

        List<TradeOut> trades = service.fetchUpdatesOnPositions(List.of(ModelTestFactory.buildPosition()));

        assertFalse(trades.isEmpty());
    }

    @Test
    void testUpdatePositionForRerate_shouldUpdateInitialPosition_whenBothHasFixedRate() {
        final TradeOut trade = ModelTestFactory.buildSpireTrade("RERATE");
        trade.getPosition().setPositionId(25L);
        trade.getPosition().getIndex().setIndexName("Fixed Rate");

        List<Position> positionList = new ArrayList<>();
        positionList.add(ModelTestFactory.buildPosition());
        positionList.add(ModelTestFactory.buildPosition());
        Position thirdPosition = ModelTestFactory.buildPosition();
        thirdPosition.setPositionId(trade.getPosition().getPositionId());
        thirdPosition.getIndex().setIndexName("Fixed Rate");
        thirdPosition.getIndex().setSpread(5.05d);
        positionList.add(thirdPosition);

        Position expectedposition = service.updatePositionForRerateTrade(trade, positionList);

        assertEquals(expectedposition.getRate(), trade.getRateOrSpread());
        assertEquals(expectedposition.getAccrualDate(), trade.getAccrualDate());
        assertNotNull(expectedposition.getIndex().getSpread());
    }

    @Test
    void testUpdatePositionForRerate_shouldUpdateInitialPosition_whenInitialPositionHasNotFixedRate() {
        final TradeOut trade = ModelTestFactory.buildSpireTrade("RERATE");
        trade.getPosition().setPositionId(25L);
        trade.getPosition().getIndex().setIndexName("Fixed Rate");
        trade.getPosition().getIndex().setIndexId(1001);

        List<Position> positionList = new ArrayList<>();
        positionList.add(ModelTestFactory.buildPosition());
        positionList.add(ModelTestFactory.buildPosition());
        Position thirdPosition = ModelTestFactory.buildPosition();
        thirdPosition.setPositionId(trade.getPosition().getPositionId());
        thirdPosition.getIndex().setIndexName("Floating Rate");
        positionList.add(thirdPosition);

        Position expectedposition = service.updatePositionForRerateTrade(trade, positionList);

        assertEquals(expectedposition.getRate(), trade.getRateOrSpread());
        assertEquals(expectedposition.getAccrualDate(), trade.getAccrualDate());
        assertEquals(expectedposition.getIndex().getIndexId(), trade.getPosition().getIndex().getIndexId());
        assertEquals(expectedposition.getIndex().getIndexName(), trade.getPosition().getIndex().getIndexName());
        assertNull(expectedposition.getIndex().getSpread());
    }

    @Test
    void testUpdatePositionForRerate_shouldUpdateInitialPosition_whenInitialBothHasNotFixedRate() {
        final TradeOut trade = ModelTestFactory.buildSpireTrade("RERATE");
        trade.getPosition().setPositionId(25L);
        trade.getPosition().getIndex().setIndexName("Floating Rate");
        trade.getPosition().getIndex().setIndexId(1001);

        List<Position> positionList = new ArrayList<>();
        positionList.add(ModelTestFactory.buildPosition());
        positionList.add(ModelTestFactory.buildPosition());
        Position thirdPosition = ModelTestFactory.buildPosition();
        thirdPosition.setPositionId(trade.getPosition().getPositionId());
        thirdPosition.getIndex().setIndexName("Floating Rate");
        positionList.add(thirdPosition);

        Position expectedposition = service.updatePositionForRerateTrade(trade, positionList);

        assertNull(expectedposition.getRate());
        assertEquals(expectedposition.getAccrualDate(), trade.getAccrualDate());
        assertEquals(expectedposition.getIndex().getIndexId(), trade.getPosition().getIndex().getIndexId());
        assertEquals(expectedposition.getIndex().getIndexName(), trade.getPosition().getIndex().getIndexName());
        assertEquals(expectedposition.getIndex().getSpread(), trade.getRateOrSpread());
    }

    @Test
    void testUpdatePositionForRerate_shouldUpdateInitialPosition_whenInitialInitialPositionHasFixedRate() {
        final TradeOut trade = ModelTestFactory.buildSpireTrade("RERATE");
        trade.getPosition().setPositionId(25L);
        trade.getPosition().getIndex().setIndexName("Floating Rate");
        trade.getPosition().getIndex().setIndexId(1001);

        List<Position> positionList = new ArrayList<>();
        positionList.add(ModelTestFactory.buildPosition());
        positionList.add(ModelTestFactory.buildPosition());
        Position thirdPosition = ModelTestFactory.buildPosition();
        thirdPosition.setPositionId(trade.getPosition().getPositionId());
        thirdPosition.getIndex().setIndexName("Fixed Rate");
        positionList.add(thirdPosition);

        Position expectedposition = service.updatePositionForRerateTrade(trade, positionList);

        assertNull(expectedposition.getRate());
        assertEquals(expectedposition.getAccrualDate(), trade.getAccrualDate());
        assertEquals(expectedposition.getIndex().getIndexId(), trade.getPosition().getIndex().getIndexId());
        assertEquals(expectedposition.getIndex().getIndexName(), trade.getPosition().getIndex().getIndexName());
        assertEquals(expectedposition.getIndex().getSpread(), trade.getRateOrSpread());
    }

    @Test
    void testGetPositionToUpdateById_shouldRetrieveExpectedPositionById() {
        List<Position> positionList = new ArrayList<>();
        positionList.add(ModelTestFactory.buildPosition());
        positionList.add(ModelTestFactory.buildPosition());
        Position thirdPosition = ModelTestFactory.buildPosition();
        thirdPosition.setPositionId(12345678L);
        thirdPosition.setPrice(87654.0);
        thirdPosition.setQuantity(555.55);
        positionList.add(thirdPosition);

        Position expectedposition = service.getPositionToUpdateById(12345678L, positionList);

        assertEquals(thirdPosition.getPrice(), expectedposition.getPrice());
        assertEquals(thirdPosition.getQuantity(), expectedposition.getQuantity());
    }

    @Test
    void testUpdatePositionForRollTrade_shouldUpdateInitialPosition() {
        final TradeOut trade = ModelTestFactory.buildSpireTrade("ROLL LOAN");
        List<Position> positionList = new ArrayList<>();
        positionList.add(ModelTestFactory.buildPosition());
        positionList.add(ModelTestFactory.buildPosition());
        Position thirdPosition = ModelTestFactory.buildPosition();
        thirdPosition.setPositionId(trade.getPosition().getPositionId());
        positionList.add(thirdPosition);

        Position expectedposition = service.updatePositionForRollTrade(trade, positionList);

        assertEquals(thirdPosition.getTermId(), expectedposition.getTermId());
        assertEquals(thirdPosition.getEndDate().toLocalDate(), expectedposition.getEndDate().toLocalDate());
    }

    @Test
    void testUpdatePositionStatus_shouldUpdateInitialPositionStatusAndLastUpdateDateTime() {
        Position position = ModelTestFactory.buildPosition();
        position.setProcessingStatus(ProcessingStatus.CREATED);
        final LocalDateTime testLocalDateTime = LocalDateTime.of(2024, 1, 2, 12, 22, 33);
        position.setLastUpdateDateTime(testLocalDateTime);

        Position expectedposition = service.updatePositionProcessingStatus(position, ProcessingStatus.UPDATED);

        assertEquals(expectedposition.getProcessingStatus(), ProcessingStatus.UPDATED);
        assertTrue(expectedposition.getLastUpdateDateTime().isAfter(testLocalDateTime));
    }

    @Test
    void testExecuteCancelRequest_shouldSendCancelRequestIfMatchedContractIsSaved() {
        Position position = ModelTestFactory.buildPosition();
        position.setPositionId(33L);
        position.setMatching1SourceLoanContractId("1111-2222-3333-4444");

        Contract contract = ModelTestFactory.buildContract();
        contract.setContractId("1111-2222-3333-4444");

        var argumentCaptor = ArgumentCaptor.forClass(Contract.class);

        when(contractService.findContractById("1111-2222-3333-4444")).thenReturn(Optional.of(contract));
        when(oneSourceService.instructCancelLoanContract("1111-2222-3333-4444")).thenReturn(true);
        when(contractService.save(argumentCaptor.capture())).thenReturn(contract);

        service.instructProposalCancel(position);
        Contract expectedContractSaved = argumentCaptor.getValue();

        verify(oneSourceService).instructCancelLoanContract(contract.getContractId());
        verify(oneSourceService).instructCancelLoanContract(contract.getContractId());
        verify(contractService).save(any(Contract.class));

        assertEquals(ProcessingStatus.CANCEL_SUBMITTED, expectedContractSaved.getProcessingStatus());
    }

    @Test
    void testExecuteCancelRequest_shouldNotSendCancelRequestIfMatchedContractIsNotRetrieved() {
        Position position = ModelTestFactory.buildPosition();
        position.setPositionId(33L);
        position.setMatching1SourceLoanContractId("1111-2222-3333-4444");

        Contract contract = ModelTestFactory.buildContract();
        contract.setContractId("1111-2222-3333-4444");

        when(contractService.findContractById("1111-2222-3333-4444")).thenReturn(Optional.empty());

        service.instructProposalCancel(position);

        verify(oneSourceService, never()).instructCancelLoanContract(any());
    }

    @Test
    void testCancelContractForCancelLoanTrade_shouldSendCancelRequestAndCreateSystemRecord() {
        Position position = ModelTestFactory.buildPosition();
        position.setPositionId(33L);
        position.setMatching1SourceLoanContractId("1111-2222-3333-4444");

        Contract contract = ModelTestFactory.buildContract();
        contract.setContractId("1111-2222-3333-4444");
        contract.setProcessingStatus(ProcessingStatus.CREATED);

        var recordFactory = new CloudEventFactoryImpl(Map.of(CONTRACT_INITIATION,
            new ContractInitiationCloudEventBuilder("1.0", "http://localhost:8000")));

        when(contractService.findContractByIdExcludeProcessingStatus("1111-2222-3333-4444", DECLINED))
            .thenReturn(Optional.of(contract));
        when(oneSourceService.instructCancelLoanContract(contract.getContractId())).thenReturn(true);
        when(contractService.save(any(Contract.class))).thenReturn(contract);
        when(cloudEventRecordService.getFactory()).thenReturn(recordFactory);

        service.cancelContractForCancelLoanTrade(position);

        verify(oneSourceService).instructCancelLoanContract(contract.getContractId());
        verify(cloudEventRecordService).record(any(CloudEventBuildRequest.class));
    }

    @Test
    void testCancelContractForCancelLoanTrade_shouldNotSendCancelRequestAndDoRecord_whenContractProcessingStatusIsDeclined() {
        Position position = ModelTestFactory.buildPosition();
        position.setPositionId(33L);
        position.setMatching1SourceLoanContractId("1111-2222-3333-4444");

        when(contractService.findContractByIdExcludeProcessingStatus("1111-2222-3333-4444", DECLINED))
            .thenReturn(Optional.empty());

        service.cancelContractForCancelLoanTrade(position);

        verify(oneSourceService, never()).instructCancelLoanContract(any());
        verify(cloudEventRecordService, never()).record(any(CloudEventBuildRequest.class));
    }

    @Test
    void testCancelContractForCancelLoanTrade_shouldNotSendCancelRequestAndDoRecord_whenContractNotMatchedWithPosition() {
        Position position = ModelTestFactory.buildPosition();
        position.setPositionId(33L);
        position.setMatching1SourceLoanContractId("1111");

        when(contractService.findContractByIdExcludeProcessingStatus("1111", DECLINED)).thenReturn(Optional.empty());

        service.cancelContractForCancelLoanTrade(position);

        verify(oneSourceService, never()).instructCancelLoanContract(position.getMatching1SourceLoanContractId());
        verify(cloudEventRecordService, never()).record(any(CloudEventBuildRequest.class));
    }

    @Test
    void testCancelContractForCancelLoanTrade_shouldCreatePositionCancelSubmittedSystemRecord() {
        Position position = ModelTestFactory.buildPosition();
        position.setPositionId(33L);
        position.setMatching1SourceLoanContractId("1111-2222-3333-4444");

        Contract contract = ModelTestFactory.buildContract();
        contract.setContractId("1111-2222-3333-4444");
        contract.setProcessingStatus(ProcessingStatus.CREATED);
        contract.setMatchingSpirePositionId(33L);
        contract.setMatchingSpireTradeId(333L);

        var recordFactory = new CloudEventFactoryImpl(Map.of(CONTRACT_INITIATION,
            new ContractInitiationCloudEventBuilder("1.0", "http://localhost:8000")));

        var argumentCaptor = ArgumentCaptor.forClass(CloudEventBuildRequest.class);

        String expectedCapturedMessage = """
            The position 33 has been canceled and the cancellation of \
            the 1Source loan contract proposal 1111-2222-3333-4444 generated \
            from this position has been instructed to 1Source""";

        when(contractService.findContractByIdExcludeProcessingStatus("1111-2222-3333-4444", DECLINED))
            .thenReturn(Optional.of(contract));
        when(oneSourceService.instructCancelLoanContract("1111-2222-3333-4444")).thenReturn(true);
        when(contractService.save(any(Contract.class))).thenReturn(contract);
        when(cloudEventRecordService.getFactory()).thenReturn(recordFactory);
        doNothing().when(cloudEventRecordService).record(argumentCaptor.capture());

        service.cancelContractForCancelLoanTrade(position);

        final CloudEventBuildRequest expectedEventRequest = argumentCaptor.getValue();
        final List<RelatedObject> expectedRelatedObjects = List.of(
            new RelatedObject("1111-2222-3333-4444", ONESOURCE_LOAN_CONTRACT),
            new RelatedObject("33", POSITION),
            new RelatedObject("333", SPIRE_TRADE));

        assertEquals(POSITION_CANCEL_SUBMITTED, expectedEventRequest.getRecordType());
        assertEquals("Position - 33", expectedEventRequest.getSubject());
        assertEquals(CONTRACT_INITIATION, expectedEventRequest.getRelatedProcess());
        assertEquals(CANCEL_LOAN_CONTRACT_PROPOSAL, expectedEventRequest.getRelatedSubProcess());
        assertEquals(expectedCapturedMessage, expectedEventRequest.getData().getMessage());
        assertIterableEquals(expectedRelatedObjects, expectedEventRequest.getData().getRelatedObjects());
        assertEquals(3, expectedEventRequest.getData().getRelatedObjects().size());
    }

    @Test
    void testRecordPositionCanceledSystemEvent_shouldCreateSystemRecord() {
        Position position = ModelTestFactory.buildPosition();
        position.setPositionId(33L);

        var recordFactory = new CloudEventFactoryImpl(Map.of(CONTRACT_INITIATION,
            new ContractInitiationCloudEventBuilder("1.0", "http://localhost:8000")));

        var argumentCaptor = ArgumentCaptor.forClass(CloudEventBuildRequest.class);

        when(cloudEventRecordService.getFactory()).thenReturn(recordFactory);
        doNothing().when(cloudEventRecordService).record(argumentCaptor.capture());

        service.recordPositionCanceledSystemEvent(position);

        final CloudEventBuildRequest expectedEventRequest = argumentCaptor.getValue();

        assertEquals(POSITION_CANCELED, expectedEventRequest.getRecordType());
        assertEquals("Position - 33", expectedEventRequest.getSubject());
        assertEquals(CONTRACT_INITIATION, expectedEventRequest.getRelatedProcess());
        assertEquals(GET_UPDATED_POSITIONS_PENDING_CONFIRMATION, expectedEventRequest.getRelatedSubProcess());
        assertEquals("The position 33 has been canceled", expectedEventRequest.getData().getMessage());
        assertIterableEquals(List.of(new RelatedObject("33", POSITION)),
            expectedEventRequest.getData().getRelatedObjects());
        assertEquals(1, expectedEventRequest.getData().getRelatedObjects().size());
    }

    @Test
    void testUpdateLoanContract_shouldUpdateCancelBorrowContractAndSaveContract() {
        Position position = ModelTestFactory.buildPosition();
        position.setPositionId(33L);
        position.setMatching1SourceLoanContractId("1111");

        Contract contract = ModelTestFactory.buildContract();
        contract.setContractId("2222");
        contract.setProcessingStatus(ProcessingStatus.CREATED);
        contract.setMatchingSpirePositionId(33L);
        final LocalDateTime lastUpdateDateTime = LocalDateTime.of(2024, 3, 7, 12, 14, 33);
        contract.setLastUpdateDateTime(lastUpdateDateTime);

        var argumentCaptor = ArgumentCaptor.forClass(Contract.class);

        when(contractService.findContractById("1111")).thenReturn(Optional.of(contract));
        when(contractService.save(argumentCaptor.capture())).thenReturn(null);

        service.updateLoanContract(position);

        Contract expectedContract = argumentCaptor.getValue();

        assertNull(expectedContract.getMatchingSpirePositionId());
        assertEquals(ProcessingStatus.PROPOSED, expectedContract.getProcessingStatus());
        assertTrue(expectedContract.getLastUpdateDateTime().isAfter(lastUpdateDateTime));
    }


}