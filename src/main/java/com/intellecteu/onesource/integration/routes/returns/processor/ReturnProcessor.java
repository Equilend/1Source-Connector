package com.intellecteu.onesource.integration.routes.returns.processor;

import static com.intellecteu.onesource.integration.model.enums.FieldSource.ONE_SOURCE_RETURN;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RETURN;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RETURN_CANCELLATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.ACKNOWLEDGE_RETURN_NEGATIVELY;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.ACKNOWLEDGE_RETURN_POSITIVELY;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.CAPTURE_RETURN_TRADE_CANCELLATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.CAPTURE_RETURN_TRADE_SETTLED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.CONFIRM_RETURN_TRADE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_NEW_RETURN_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.MATCH_RETURN;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_RETURN;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_RETURN_TRADE_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_RETURN_TRADE_SETTLED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.VALIDATE_RETURN;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CANCEL_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CONFIRMED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.NACK_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TO_CONFIRM;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TO_VALIDATE;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.VALIDATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.WAITING_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.RecordType.NEGATIVE_ACKNOWLEDGEMENT_NOT_AUTHORIZED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_MATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_PENDING_ACKNOWLEDGEMENT;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_SETTLED_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_TRADE_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_TRADE_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_VALIDATED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_SPIRE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_ISSUE_INTEGRATION_TOOLKIT;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwExceptionForRedeliveryPolicy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.model.backoffice.CancelReturnTrade;
import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.backoffice.ReturnTrade;
import com.intellecteu.onesource.integration.model.enums.FieldExceptionType;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.NackInstruction;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudSystemEvent;
import com.intellecteu.onesource.integration.model.onesource.Return;
import com.intellecteu.onesource.integration.model.onesource.ReturnStatus;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.NackInstructionService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.RerateTradeService;
import com.intellecteu.onesource.integration.services.ReturnService;
import com.intellecteu.onesource.integration.services.ReturnTradeService;
import com.intellecteu.onesource.integration.services.reconciliation.ReturnReconcileService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.ReturnCancellationCloudEventBuilder;
import com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder;
import com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder.DataBuilder;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReturnProcessor {

    private final BackOfficeService backOfficeService;
    private final OneSourceService oneSourceService;
    private final ReturnTradeService returnTradeService;
    private final ReturnService returnService;
    private final ReturnReconcileService returnReconcileService;
    private final NackInstructionService nackInstructionService;
    private final RerateTradeService rerateTradeService;
    private final CloudEventRecordService cloudEventRecordService;
    private final ReturnCloudEventBuilder returnCloudEventBuilder;
    private final ReturnCancellationCloudEventBuilder returnCancellationCloudEventBuilder;

    @Autowired
    public ReturnProcessor(BackOfficeService backOfficeService, OneSourceService oneSourceService,
        ReturnTradeService returnTradeService, ReturnService returnService,
        ReturnReconcileService returnReconcileService,
        NackInstructionService nackInstructionService, RerateTradeService rerateTradeService,
        CloudEventRecordService cloudEventRecordService) {
        this.backOfficeService = backOfficeService;
        this.oneSourceService = oneSourceService;
        this.returnTradeService = returnTradeService;
        this.returnService = returnService;
        this.returnReconcileService = returnReconcileService;
        this.nackInstructionService = nackInstructionService;
        this.rerateTradeService = rerateTradeService;
        this.cloudEventRecordService = cloudEventRecordService;
        this.returnCloudEventBuilder = (ReturnCloudEventBuilder) cloudEventRecordService.getFactory()
            .eventBuilder(RETURN);
        this.returnCancellationCloudEventBuilder = (ReturnCancellationCloudEventBuilder) cloudEventRecordService.getFactory()
            .eventBuilder(RETURN_CANCELLATION);
    }

    public ReturnTrade saveReturnTrade(ReturnTrade returnTrade) {
        returnTrade.setLastUpdateDatetime(LocalDateTime.now());
        return returnTradeService.save(returnTrade);
    }

    public ReturnTrade saveReturnTradeWithProcessingStatus(ReturnTrade returnTrade, ProcessingStatus processingStatus) {
        returnTrade.setProcessingStatus(processingStatus);
        return saveReturnTrade(returnTrade);
    }

    public ReturnTrade updateReturnTradeCreationDatetime(ReturnTrade returnTrade) {
        returnTrade.setCreationDatetime(LocalDateTime.now());
        return returnTrade;
    }

    public Return saveReturn(Return oneSourceReturn) {
        oneSourceReturn.setLastUpdateDatetime(LocalDateTime.now());
        return returnService.saveReturn(oneSourceReturn);
    }

    public Return saveReturnWithProcessingStatus(Return oneSourceReturn, ProcessingStatus processingStatus) {
        oneSourceReturn.setLastUpdateDatetime(LocalDateTime.now());
        oneSourceReturn.setProcessingStatus(processingStatus);
        return returnService.saveReturn(oneSourceReturn);
    }

    public NackInstruction saveNackInstructionWithProcessingStatus(NackInstruction nackInstruction,
        ProcessingStatus processingStatus) {
        nackInstruction.setProcessingStatus(processingStatus);
        return nackInstructionService.save(nackInstruction);
    }

    public List<ReturnTrade> fetchNewReturnTrades() {
        Optional<Long> lastTradeId = returnTradeService.getMaxTradeId();
        List<ReturnTrade> rerateTradeList = new ArrayList<>();
        try {
            rerateTradeList = backOfficeService.retrieveNewReturnTrades(lastTradeId);
        } catch (HttpStatusCodeException exception) {
            recordHttpExceptionCloudEvent(GET_NEW_RETURN_PENDING_CONFIRMATION, TECHNICAL_EXCEPTION_SPIRE, exception,
                null, null);
        }
        return rerateTradeList;
    }

    public ReturnTrade postReturnTrade(ReturnTrade returnTrade) {
        try {
            oneSourceService.postReturnTrade(returnTrade);
            recordCloudEvent(POST_RETURN, RETURN_TRADE_SUBMITTED, returnTrade.getTradeId(),
                returnTrade.getRelatedPositionId(), returnTrade.getRelatedContractId(), null, List.of());
        } catch (HttpStatusCodeException exception) {
            recordHttpExceptionCloudEvent(POST_RETURN, TECHNICAL_EXCEPTION_1SOURCE, exception,
                returnTrade.getTradeId(), null);
            throwExceptionForRedeliveryPolicy(exception);
        }
        return returnTrade;
    }

    public Return matchingReturn(Return oneSourceReturn) {
        if (isBorrower(oneSourceReturn)) {
            oneSourceReturn = matchingBorrowerReturn(oneSourceReturn);
        }
        if (!oneSourceReturn.getProcessingStatus().equals(CONFIRMED)) {
            oneSourceReturn = matchingLenderReturn(oneSourceReturn);
        }
        return oneSourceReturn;
    }

    public boolean isBorrower(Return oneSourceReturn) {
        return oneSourceReturn.getExecutionVenue().getVenueParties().stream()
            .anyMatch(venueParty -> venueParty.getPartyRole().equals(BORROWER));
    }

    private Return matchingBorrowerReturn(Return oneSourceReturn) {
        String tradeIdStr = oneSourceReturn.getExecutionVenue().getVenueRefKey();
        ReturnTrade returnTrade = returnTradeService.findUnmatchedReturnTrade(Long.valueOf(tradeIdStr), SUBMITTED)
            .orElse(null);
        if (returnTrade != null) {
            oneSourceReturn.setMatchingSpireTradeId(returnTrade.getTradeId());
            oneSourceReturn.setLastUpdateDatetime(LocalDateTime.now());
            oneSourceReturn.setProcessingStatus(CONFIRMED);
            returnTrade.setProcessingStatus(TO_CONFIRM);
            returnTradeService.markReturnTradeAsMatched(returnTrade, oneSourceReturn);
            recordCloudEvent(MATCH_RETURN, RETURN_PENDING_ACKNOWLEDGEMENT, oneSourceReturn.getMatchingSpireTradeId(),
                oneSourceReturn.getRelatedSpirePositionId(), oneSourceReturn.getContractId(),
                oneSourceReturn.getReturnId(), List.of());
        }
        return oneSourceReturn;
    }

    private Return matchingLenderReturn(Return oneSourceReturn) {
        ReturnTrade returnTrade = returnTradeService.findUnmatchedReturnTrade(oneSourceReturn.getContractId(),
            oneSourceReturn.getReturnDate(), oneSourceReturn.getQuantity(), CREATED).orElse(null);
        if (returnTrade != null) {
            oneSourceReturn.setMatchingSpireTradeId(returnTrade.getTradeId());
            oneSourceReturn.setRelatedSpirePositionId(returnTrade.getRelatedPositionId());
            oneSourceReturn.setLastUpdateDatetime(LocalDateTime.now());
            oneSourceReturn.setProcessingStatus(TO_VALIDATE);
            returnTradeService.markReturnTradeAsMatched(returnTrade, oneSourceReturn);
            recordCloudEvent(MATCH_RETURN, RETURN_MATCHED, oneSourceReturn.getMatchingSpireTradeId(),
                oneSourceReturn.getRelatedSpirePositionId(), oneSourceReturn.getContractId(),
                oneSourceReturn.getReturnId(), List.of());
        } else {
            oneSourceReturn.setLastUpdateDatetime(LocalDateTime.now());
            oneSourceReturn.setProcessingStatus(UNMATCHED);
            recordCloudEvent(MATCH_RETURN, RETURN_UNMATCHED, oneSourceReturn.getMatchingSpireTradeId(),
                oneSourceReturn.getRelatedSpirePositionId(), oneSourceReturn.getContractId(),
                oneSourceReturn.getReturnId(), buildUnmatchedFieldImpactedList(oneSourceReturn));
        }
        return oneSourceReturn;
    }

    private List<FieldImpacted> buildUnmatchedFieldImpactedList(Return oneSourceReturn) {
        List<FieldImpacted> unmatchedFieldImpactedList = new ArrayList<>();
        unmatchedFieldImpactedList.add(
            new FieldImpacted(ONE_SOURCE_RETURN, "Loan Contract Id", oneSourceReturn.getContractId(),
                FieldExceptionType.UNMATCHED));
        unmatchedFieldImpactedList.add(
            new FieldImpacted(ONE_SOURCE_RETURN, "Return Date", String.valueOf(oneSourceReturn.getReturnDate()),
                FieldExceptionType.UNMATCHED));
        unmatchedFieldImpactedList.add(
            new FieldImpacted(ONE_SOURCE_RETURN, "Quantity", String.valueOf(oneSourceReturn.getQuantity()),
                FieldExceptionType.UNMATCHED));
        return unmatchedFieldImpactedList;
    }

    public Return validateReturn(Return oneSourceReturn) {
        ReturnTrade returnTrade = returnTradeService.getByTradeId(oneSourceReturn.getMatchingSpireTradeId());
        try {
            returnReconcileService.reconcile(oneSourceReturn, returnTrade);
            oneSourceReturn.setProcessingStatus(VALIDATED);
            recordCloudEvent(VALIDATE_RETURN, RETURN_VALIDATED,
                new ReturnCloudEventBuilder.DataBuilder()
                    .putReturnId(oneSourceReturn.getReturnId())
                    .putTradeId(oneSourceReturn.getMatchingSpireTradeId())
                    .putPositionId(oneSourceReturn.getRelatedSpirePositionId())
                    .putContractId(oneSourceReturn.getContractId())
                    .getData(), List.of());
        } catch (ReconcileException e) {
            log.error("Reconciliation fails with message: {} ", e.getMessage());
            e.getErrorList().forEach(msg -> log.debug(msg.getFieldValue()));
            createFailedReconciliationEvent(oneSourceReturn, e);
            oneSourceReturn.setProcessingStatus(DISCREPANCIES);
        }
        return oneSourceReturn;
    }

    public Return sendPositiveAck(Return oneSourceReturn) {
        ReturnTrade returnTrade = returnTradeService.getByTradeId(oneSourceReturn.getMatchingSpireTradeId());
        try {
            oneSourceService.sendPositiveAck(oneSourceReturn, returnTrade);
        } catch (HttpStatusCodeException exception) {
            recordCloudEvent(ACKNOWLEDGE_RETURN_POSITIVELY, TECHNICAL_EXCEPTION_1SOURCE,
                new ReturnCloudEventBuilder.DataBuilder().putHttpStatusText(exception.getStatusText())
                    .putReturnId(oneSourceReturn.getReturnId()).putTradeId(oneSourceReturn.getMatchingSpireTradeId())
                    .putPositionId(oneSourceReturn.getRelatedSpirePositionId())
                    .putContractId(oneSourceReturn.getContractId()).getData(), List.of());
            throwExceptionForRedeliveryPolicy(exception);
        }
        return oneSourceReturn;
    }

    public NackInstruction sendNegativeAck(NackInstruction nackInstruction) {
        try {
            CloudSystemEvent cloudSystemEvent = cloudEventRecordService.getCloudSystemEvent(
                nackInstruction.getRelatedCloudEventId());
            if (RETURN_DISCREPANCIES.name().equals(cloudSystemEvent.getType()) || RETURN_UNMATCHED.name()
                .equals(cloudSystemEvent.getType())) {
                Return oneSourceReturn = returnService.getByReturnId(nackInstruction.getRelatedReturnId());
                try {
                    oneSourceService.sendNegativeAck(oneSourceReturn, nackInstruction.getNackReasonCode(),
                        nackInstruction.getNackReasonText());
                    saveReturnWithProcessingStatus(oneSourceReturn, NACK_SUBMITTED);
                } catch (HttpStatusCodeException exception) {
                    recordCloudEvent(ACKNOWLEDGE_RETURN_NEGATIVELY, TECHNICAL_EXCEPTION_1SOURCE,
                        new ReturnCloudEventBuilder.DataBuilder().putHttpStatusText(exception.getStatusText())
                            .putReturnId(oneSourceReturn.getReturnId())
                            .putTradeId(oneSourceReturn.getMatchingSpireTradeId())
                            .putPositionId(oneSourceReturn.getRelatedSpirePositionId())
                            .putContractId(oneSourceReturn.getContractId()).getData(), List.of());
                    throwExceptionForRedeliveryPolicy(exception);
                }
            } else {
                recordCloudEvent(ACKNOWLEDGE_RETURN_NEGATIVELY, NEGATIVE_ACKNOWLEDGEMENT_NOT_AUTHORIZED,
                    new ReturnCloudEventBuilder.DataBuilder().putReturnId(nackInstruction.getRelatedReturnId())
                        .getData(),
                    List.of());
            }
        } catch (EntityNotFoundException e) {
            recordCloudEvent(ACKNOWLEDGE_RETURN_NEGATIVELY, TECHNICAL_ISSUE_INTEGRATION_TOOLKIT,
                new ReturnCloudEventBuilder.DataBuilder().putReturnId(nackInstruction.getRelatedReturnId()).getData(),
                List.of());
        }
        return nackInstruction;
    }

    public boolean isReturnTradePostponed(ReturnTrade returnTrade) {
        Optional<ReturnTrade> notConfirmedReturnTrade = findNotConfirmedReturnTradeWithLowerTradeId(returnTrade);
        if (notConfirmedReturnTrade.isPresent()) {
            return true;
        } else {
            Optional<RerateTrade> notConfirmedRerateTrade = findNotConfirmedRerateTradeWithLowerTradeId(returnTrade);
            return notConfirmedRerateTrade.isPresent();
        }
    }

    private Optional<RerateTrade> findNotConfirmedRerateTradeWithLowerTradeId(ReturnTrade returnTrade) {
        return rerateTradeService.findReturnTrade(
            returnTrade.getRelatedPositionId(),
            List.of(CREATED, SUBMITTED, WAITING_PROPOSAL)).stream().filter(
            rerate -> returnTrade.getTradeId() > rerate.getTradeId() && (
                "Rerate".equals(rerate.getTradeOut().getTradeType())
                    || "Rerate Borrow".equals(
                    rerate.getTradeOut().getTradeType()))).findFirst();
    }

    private Optional<ReturnTrade> findNotConfirmedReturnTradeWithLowerTradeId(ReturnTrade returnTrade) {
        return returnTradeService.findReturnTrade(
            returnTrade.getRelatedPositionId(),
            List.of(CREATED, SUBMITTED)).stream().filter(
            rt -> returnTrade.getTradeId() > rt.getTradeId() && ("Return Loan".equals(rt.getTradeOut().getTradeType())
                || "Return Borrow".equals(
                rt.getTradeOut().getTradeType()))).findFirst();
    }

    public ReturnTrade confirmReturnTrade(ReturnTrade returnTrade) {
        try {
            backOfficeService.confirmReturnTrade(returnTrade);
            returnTrade.getTradeOut().setStatus("FUTURE");
        } catch (HttpStatusCodeException exception) {
            recordCloudEvent(CONFIRM_RETURN_TRADE, TECHNICAL_EXCEPTION_SPIRE,
                new ReturnCloudEventBuilder.DataBuilder().putHttpStatusText(exception.getStatusText())
                    .putReturnId(returnTrade.getMatching1SourceReturnId())
                    .putTradeId(returnTrade.getTradeId())
                    .putPositionId(returnTrade.getRelatedPositionId())
                    .putContractId(returnTrade.getRelatedContractId()).getData(), List.of());
            throwExceptionForRedeliveryPolicy(exception);
        }
        return returnTrade;
    }

    public List<ReturnTrade> fetchOpenReturnTrades() {
        List<ReturnTrade> returnTradesWithFUTURE = returnTradeService.findReturnTradeWithStatus(Set.of("FUTURE"));
        if (!returnTradesWithFUTURE.isEmpty()) {
            try {
                List<Long> openReturnTradeIds = backOfficeService.retrieveOpenReturnTrades(
                        returnTradesWithFUTURE.stream().map(ReturnTrade::getTradeId).toList()).stream()
                    .map(ReturnTrade::getTradeId).toList();
                return returnTradesWithFUTURE.stream()
                    .filter(rt -> openReturnTradeIds.contains(rt.getTradeId())).toList();
            } catch (HttpStatusCodeException exception) {
                recordCloudEvent(CAPTURE_RETURN_TRADE_SETTLED, TECHNICAL_EXCEPTION_SPIRE,
                    new ReturnCloudEventBuilder.DataBuilder().putHttpStatusText(exception.getStatusText()).getData(),
                    List.of());
            }
        }
        return List.of();
    }

    public ReturnTrade postSettlementStatus(ReturnTrade returnTrade) {
        try {
            oneSourceService.instructReturnStatus(returnTrade, ReturnStatus.SETTLED);
            returnTrade.getTradeOut().setStatus("SETTLED");
            recordCloudEvent(PROCESS_RETURN_TRADE_SETTLED, RETURN_SETTLED_SUBMITTED,
                new ReturnCloudEventBuilder.DataBuilder()
                    .putReturnId(returnTrade.getMatching1SourceReturnId())
                    .putTradeId(returnTrade.getTradeId())
                    .putPositionId(returnTrade.getRelatedPositionId())
                    .putContractId(returnTrade.getRelatedContractId()).getData(),
                List.of());
        } catch (HttpStatusCodeException exception) {
            recordCloudEvent(PROCESS_RETURN_TRADE_SETTLED, TECHNICAL_EXCEPTION_1SOURCE,
                new ReturnCloudEventBuilder.DataBuilder().putHttpStatusText(exception.getStatusText())
                    .putReturnId(returnTrade.getMatching1SourceReturnId())
                    .putTradeId(returnTrade.getTradeId())
                    .putPositionId(returnTrade.getRelatedPositionId())
                    .putContractId(returnTrade.getRelatedContractId()).getData(),
                List.of());
        }
        return returnTrade;
    }

    @Transactional
    public List<ReturnTrade> fetchAndProcessCanceledReturnTrades() {
        List<ReturnTrade> returnTrades = returnTradeService.findReturnTradeWithStatus(
            Set.of("FUTURE", "PENDING LEDGER CONFIRMATION"));
        Optional<Long> lastTradeId = returnTradeService.getMaxTradeId();
        if (!returnTrades.isEmpty()) {
            try {
                List<CancelReturnTrade> cancelReturnTrades = backOfficeService.retrieveCancelledReturnTrades(
                    lastTradeId, returnTrades.stream().map(ReturnTrade::getTradeId).toList());
                List<ReturnTrade> updatedReturnTrades = new ArrayList<>();
                for (CancelReturnTrade cancelReturnTrade : cancelReturnTrades) {
                    ReturnTrade returnTrade = findRelatedReturnTrade(cancelReturnTrade, returnTrades);
                    returnTrade.setCancelingTradeId(cancelReturnTrade.getTradeId());
                    if ((returnTrade.getMatching1SourceReturnId() != null
                        || !returnTrade.getMatching1SourceReturnId().isEmpty())) {
                        Return oneSourceReturn = returnService.getByReturnId(returnTrade.getMatching1SourceReturnId());
                        if (Set.of("Cancel Return Loan", "Cancel Return loan (Full)")
                            .contains(cancelReturnTrade.getType())) {
                            cancelLenderMatchingReturn(oneSourceReturn);
                        }
                        if (Set.of("Cancel Return Borrow", "Cancel Return Borrow (Full)")
                            .contains(cancelReturnTrade.getType())) {
                            cancelBorrowerMatchingReturn(oneSourceReturn);
                        }
                    }
                    recordReturnCancellationCloudEvent(PROCESS_RETURN_TRADE_CANCELED, RETURN_TRADE_CANCELED,
                        new ReturnCancellationCloudEventBuilder.DataBuilder().putTradeId(returnTrade.getTradeId())
                            .putPositionId(returnTrade.getRelatedPositionId())
                            .putContractId(returnTrade.getRelatedContractId()).getData(),
                        List.of());
                    updatedReturnTrades.add(returnTrade);
                }
                return updatedReturnTrades;
            } catch (HttpStatusCodeException exception) {
                recordReturnCancellationCloudEvent(CAPTURE_RETURN_TRADE_CANCELLATION, TECHNICAL_EXCEPTION_SPIRE,
                    new ReturnCancellationCloudEventBuilder.DataBuilder().putHttpStatusText(exception.getStatusText())
                        .getData(),
                    List.of());
            }
        }
        return List.of();
    }

    private static ReturnTrade findRelatedReturnTrade(CancelReturnTrade cancelReturnTrade,
        List<ReturnTrade> returnTrades) {
        return returnTrades.stream()
            .filter(rt -> rt.getTradeId().equals(cancelReturnTrade.getOffsetId())).findFirst()
            .get();
    }

    private Return cancelLenderMatchingReturn(Return oneSourceReturn) {
        oneSourceReturn.setMatchingSpireTradeId(null);
        oneSourceReturn.setLastUpdateDatetime(LocalDateTime.now());
        oneSourceReturn.setProcessingStatus(UNMATCHED);
        return returnService.saveReturn(oneSourceReturn);
    }

    private Return cancelBorrowerMatchingReturn(Return oneSourceReturn) {
        try {
            oneSourceService.instructReturnCancellation(oneSourceReturn.getReturnId(), oneSourceReturn.getContractId());
            oneSourceReturn.setLastUpdateDatetime(LocalDateTime.now());
            oneSourceReturn.setProcessingStatus(CANCEL_SUBMITTED);
            return returnService.saveReturn(oneSourceReturn);
        } catch (HttpStatusCodeException exception) {
            recordReturnCancellationCloudEvent(PROCESS_RETURN_TRADE_CANCELED, TECHNICAL_EXCEPTION_1SOURCE,
                new ReturnCancellationCloudEventBuilder.DataBuilder().putHttpStatusText(exception.getStatusText())
                    .putReturnId(oneSourceReturn.getReturnId())
                    .putPositionId(oneSourceReturn.getRelatedSpirePositionId())
                    .putTradeId(oneSourceReturn.getMatchingSpireTradeId())
                    .putContractId(oneSourceReturn.getContractId())
                    .getData(),
                List.of());
        }
        return oneSourceReturn;
    }

    private void recordCloudEvent(IntegrationSubProcess subProcess, RecordType recordType,
        Map<String, String> data, List<FieldImpacted> fieldImpacteds) {
        var recordRequest = returnCloudEventBuilder.buildRequest(subProcess, recordType, data, fieldImpacteds);
        cloudEventRecordService.record(recordRequest);
    }

    private void recordReturnCancellationCloudEvent(IntegrationSubProcess subProcess, RecordType recordType,
        Map<String, String> data, List<FieldImpacted> fieldImpacteds) {
        var recordRequest = returnCancellationCloudEventBuilder.buildRequest(subProcess, recordType, data,
            fieldImpacteds);
        cloudEventRecordService.record(recordRequest);
    }

    private void recordCloudEvent(IntegrationSubProcess subProcess, RecordType recordType, Long tradeId,
        Long positionId, String contractId, String returnId, List<FieldImpacted> fieldImpacteds) {
        Map<String, String> data = new DataBuilder()
            .putTradeId(tradeId)
            .putPositionId(positionId)
            .putContractId(contractId)
            .putReturnId(returnId).getData();
        recordCloudEvent(subProcess, recordType, data, fieldImpacteds);
    }

    private void recordHttpExceptionCloudEvent(IntegrationSubProcess subProcess, RecordType recordType,
        HttpStatusCodeException e, Long tradeId, Long positionId) {
        Map<String, String> data = new DataBuilder()
            .putHttpStatusText(e.getStatusText())
            .putPositionId(positionId)
            .putTradeId(tradeId)
            .getData();
        var recordRequest = returnCloudEventBuilder.buildRequest(subProcess, recordType, data, List.of());
        cloudEventRecordService.record(recordRequest);
    }

    private void createFailedReconciliationEvent(Return oneSourceReturn, ReconcileException e) {
        List<FieldImpacted> fieldImpacteds = e.getErrorList().stream().map(
                ed -> new FieldImpacted(ed.getSource(), ed.getFieldName(), ed.getFieldValue(), ed.getFieldExceptionType()))
            .collect(Collectors.toList());
        recordCloudEvent(VALIDATE_RETURN, RETURN_DISCREPANCIES, oneSourceReturn.getMatchingSpireTradeId(),
            oneSourceReturn.getRelatedSpirePositionId(), oneSourceReturn.getContractId(), oneSourceReturn.getReturnId(),
            fieldImpacteds);
    }

}
