package com.intellecteu.onesource.integration.routes.returns.processor;

import static com.intellecteu.onesource.integration.model.enums.FieldSource.ONE_SOURCE_RETURN;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RETURN;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.ACKNOWLEDGE_RETURN_NEGATIVELY;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.ACKNOWLEDGE_RETURN_POSITIVELY;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_NEW_RETURN_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.MATCH_RETURN;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_RETURN;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.VALIDATE_RETURN;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CONFIRMED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.NACK_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TO_CONFIRM;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TO_VALIDATE;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.VALIDATED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.NEGATIVE_ACKNOWLEDGEMENT_NOT_AUTHORIZED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_MATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_PENDING_ACKNOWLEDGEMENT;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_TRADE_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RETURN_UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_SPIRE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_ISSUE_INTEGRATION_TOOLKIT;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwExceptionForRedeliveryPolicy;

import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.model.backoffice.ReturnTrade;
import com.intellecteu.onesource.integration.model.enums.FieldExceptionType;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.NackInstruction;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudSystemEvent;
import com.intellecteu.onesource.integration.model.onesource.Return;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.NackInstructionService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.ReturnService;
import com.intellecteu.onesource.integration.services.ReturnTradeService;
import com.intellecteu.onesource.integration.services.reconciliation.ReturnReconcileService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder;
import com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder.DataBuilder;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Component
@Slf4j
public class ReturnProcessor {

    private final BackOfficeService backOfficeService;
    private final OneSourceService oneSourceService;
    private final ReturnTradeService returnTradeService;
    private final ReturnService returnService;
    private final ReturnReconcileService returnReconcileService;
    private final CloudEventRecordService cloudEventRecordService;
    private final NackInstructionService nackInstructionService;
    private final ReturnCloudEventBuilder eventBuilder;

    @Autowired
    public ReturnProcessor(BackOfficeService backOfficeService, OneSourceService oneSourceService,
        ReturnTradeService returnTradeService, ReturnService returnService,
        ReturnReconcileService returnReconcileService,
        NackInstructionService nackInstructionService,
        CloudEventRecordService cloudEventRecordService) {
        this.backOfficeService = backOfficeService;
        this.oneSourceService = oneSourceService;
        this.returnTradeService = returnTradeService;
        this.returnService = returnService;
        this.returnReconcileService = returnReconcileService;
        this.nackInstructionService = nackInstructionService;
        this.cloudEventRecordService = cloudEventRecordService;
        this.eventBuilder = (ReturnCloudEventBuilder) cloudEventRecordService.getFactory().eventBuilder(RETURN);
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

    public NackInstruction saveNackInstructionWithProcessingStatus(NackInstruction nackInstruction, ProcessingStatus processingStatus) {
        nackInstruction.setProcessingStatus(processingStatus);
        return nackInstructionService.save(nackInstruction);
    }

    public List<ReturnTrade> fetchNewReturnTrades() {
        Optional<Long> lastTradeId = returnTradeService.getMaxTradeId();
        List<ReturnTrade> rerateTradeList = new ArrayList<>();
        try {
            rerateTradeList = backOfficeService.retrieveReturnTrades(lastTradeId);
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

    private void recordCloudEvent(IntegrationSubProcess subProcess, RecordType recordType,
        Map<String, String> data, List<FieldImpacted> fieldImpacteds) {
        var recordRequest = eventBuilder.buildRequest(subProcess, recordType, data, fieldImpacteds);
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
        var recordRequest = eventBuilder.buildRequest(subProcess, recordType, data, List.of());
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
