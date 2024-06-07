package com.intellecteu.onesource.integration.routes.rerate.processor;

import static com.intellecteu.onesource.integration.model.enums.FieldSource.ONE_SOURCE_RERATE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.GENERIC;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RERATE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.APPROVE_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.DECLINE_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_TRADE_EVENTS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.MATCH_LOAN_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_RERATE_TRADE_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_RERATE_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_TRADE_CANCEL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_TRADE_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_TRADE_UPDATE;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.APPROVAL_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.APPROVED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CANCEL_PENDING;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CANCEL_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DECLINED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DECLINE_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.MATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.PROPOSED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.REPLACED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TO_VALIDATE;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.VALIDATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.WAITING_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.RecordType.DECLINE_INSTRUCTION_NOT_AUTHORIZED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_MATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_PENDING_APPROVAL;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_TRADE_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_TRADE_CREATED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_TRADE_REPLACED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_TRADE_REPLACE_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_SPIRE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_ISSUE_INTEGRATION_TOOLKIT;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.CONTRACT_ID;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.HTTP_STATUS_TEXT;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.POSITION_ID;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.RERATE_ID;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.TRADE_ID;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwExceptionForRedeliveryPolicy;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.toStringNullSafe;

import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.backoffice.ReturnTrade;
import com.intellecteu.onesource.integration.model.enums.FieldExceptionType;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.CorrectionInstruction;
import com.intellecteu.onesource.integration.model.integrationtoolkit.DeclineInstruction;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.CorrectionInstructionService;
import com.intellecteu.onesource.integration.services.DeclineInstructionService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.RerateService;
import com.intellecteu.onesource.integration.services.RerateTradeService;
import com.intellecteu.onesource.integration.services.ReturnTradeService;
import com.intellecteu.onesource.integration.services.reconciliation.RerateReconcileService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

@Component
@Slf4j
public class RerateProcessor {

    private final BackOfficeService backOfficeService;
    private final OneSourceService oneSourceService;
    private final RerateTradeService rerateTradeService;
    private final RerateService rerateService;
    private final RerateReconcileService rerateReconcileService;
    private final DeclineInstructionService declineInstructionService;
    private final CorrectionInstructionService correctionInstructionService;
    private final ReturnTradeService returnTradeService;
    private final CloudEventRecordService cloudEventRecordService;
    private final RerateCloudEventBuilder eventBuilder;

    @Autowired
    public RerateProcessor(BackOfficeService backOfficeService, OneSourceService oneSourceService,
        RerateTradeService rerateTradeService,
        RerateService rerateService,
        RerateReconcileService rerateReconcileService, DeclineInstructionService declineInstructionService,
        CorrectionInstructionService correctionInstructionService, ReturnTradeService returnTradeService, CloudEventRecordService cloudEventRecordService) {
        this.backOfficeService = backOfficeService;
        this.oneSourceService = oneSourceService;
        this.rerateTradeService = rerateTradeService;
        this.rerateService = rerateService;
        this.rerateReconcileService = rerateReconcileService;
        this.declineInstructionService = declineInstructionService;
        this.correctionInstructionService = correctionInstructionService;
        this.returnTradeService = returnTradeService;
        this.cloudEventRecordService = cloudEventRecordService;
        this.eventBuilder = (RerateCloudEventBuilder) cloudEventRecordService.getFactory().eventBuilder(RERATE);
    }

    public RerateTrade saveRerateTrade(RerateTrade rerateTrade) {
        rerateTrade.setLastUpdateDatetime(LocalDateTime.now());
        return rerateTradeService.save(rerateTrade);
    }

    public RerateTrade updateRerateTradeCreationDatetime(RerateTrade rerateTrade) {
        rerateTrade.setCreationDatetime(LocalDateTime.now());
        return rerateTrade;
    }

    public RerateTrade updateRerateTradeProcessingStatus(RerateTrade rerateTrade, ProcessingStatus processingStatus) {
        rerateTrade.setProcessingStatus(processingStatus);
        return rerateTrade;
    }

    public Rerate saveRerate(Rerate rerate) {
        rerate.setLastUpdateDatetime(LocalDateTime.now());
        return rerateService.saveRerate(rerate);
    }

    public Rerate updateRerateProcessingStatus(Rerate rerate, ProcessingStatus processingStatus) {
        rerate.setProcessingStatus(processingStatus);
        return rerate;
    }

    public DeclineInstruction saveDeclineInstruction(DeclineInstruction declineInstruction) {
        return declineInstructionService.save(declineInstruction);
    }

    public DeclineInstruction updateDeclineInstructionProcessingStatus(DeclineInstruction declineInstruction,
        ProcessingStatus processingStatus) {
        declineInstruction.setProcessingStatus(processingStatus);
        return declineInstruction;
    }

    public CorrectionInstruction saveCorrectionInstruction(CorrectionInstruction correctionInstruction) {
        return correctionInstructionService.save(correctionInstruction);
    }

    public CorrectionInstruction updateCorrectionInstructionProcessingStatus(
        CorrectionInstruction correctionInstruction,
        ProcessingStatus processingStatus) {
        correctionInstruction.setProcessingStatus(processingStatus);
        return correctionInstruction;
    }

    public List<RerateTrade> fetchNewRerateTrades() {
        Optional<Long> lastTradeId = rerateTradeService.getMaxTradeId();
        List<RerateTrade> rerateTradeList = new ArrayList<>();
        try {
            rerateTradeList = backOfficeService.getNewBackOfficeRerateTradeEvents(lastTradeId);
        } catch (HttpStatusCodeException exception) {
            log.warn("SPIRE error response for {} subprocess. Details: {}",
                GET_TRADE_EVENTS_PENDING_CONFIRMATION, exception.getStatusCode());
            recordTradeEventExceptionEvent(exception);
        }
        return rerateTradeList;
    }

    @Transactional
    public RerateTrade matchBackOfficeRerateTradeWith1SourceRerate(RerateTrade rerateTrade) {
        LocalDate accrualDate = rerateTrade.getTradeOut().getAccrualDate().toLocalDate();
        Rerate rerate = rerateService.findUnmatchedRerate(rerateTrade.getRelatedContractId(),
            accrualDate).orElse(null);
        if (rerate != null) {
            rerateTrade.setMatchingRerateId(rerate.getRerateId());
            rerateService.markRerateAsMatchedWithRerateTrade(rerate, rerateTrade);
            recordCloudEvent(PROCESS_RERATE_PENDING_CONFIRMATION, RERATE_PROPOSAL_MATCHED,
                rerateTrade.getMatchingRerateId(), rerateTrade.getTradeId(), rerate.getRelatedSpirePositionId(),
                rerate.getContractId());
        } else {
            recordCloudEvent(PROCESS_RERATE_PENDING_CONFIRMATION, RERATE_TRADE_CREATED,
                rerateTrade.getMatchingRerateId(), rerateTrade.getTradeId(), rerateTrade.getRelatedPositionId(),
                rerateTrade.getRelatedContractId());
        }
        return rerateTrade;
    }

    public RerateTrade instructRerateTrade(RerateTrade rerateTrade) {
        try {
            oneSourceService.instructRerate(rerateTrade);
            rerateTrade.setProcessingStatus(SUBMITTED);
        } catch (HttpStatusCodeException codeException) {
            if (codeException.getStatusCode().value() != 400) {
                recordHttpExceptionCloudEvent(POST_RERATE_PROPOSAL, TECHNICAL_EXCEPTION_1SOURCE,
                    codeException, rerateTrade.getMatchingRerateId(), rerateTrade.getTradeId(),
                    rerateTrade.getRelatedPositionId());
                throwExceptionForRedeliveryPolicy(codeException);
            } else {
                rerateTrade.setProcessingStatus(WAITING_PROPOSAL);
            }
        }
        return rerateTrade;
    }

    public Rerate match1SourceRerateWithBackOfficeRerateTrade(Rerate rerate) {
        LocalDate rerateEffectiveDate = getRerateEffectiveDate(rerate).orElse(null);
        if (rerateEffectiveDate != null) {
            RerateTrade rerateTrade = rerateTradeService.findUnmatchedRerateTrade(
                rerate.getContractId(), rerateEffectiveDate).orElse(null);
            if (rerateTrade != null) {
                if (SUBMITTED.equals(rerateTrade.getProcessingStatus())) {
                    //Initiator
                    rerate.setMatchingSpireTradeId(rerateTrade.getTradeId());
                    rerate.setRelatedSpirePositionId(rerateTrade.getRelatedPositionId());
                    rerateTradeService.markRerateTradeAsMatchedWithRerate(rerateTrade, rerate);
                    rerate.setProcessingStatus(MATCHED);
                    recordCloudEvent(MATCH_LOAN_RERATE_PROPOSAL, RERATE_PROPOSAL_PENDING_APPROVAL,
                        rerate.getRerateId(), rerate.getMatchingSpireTradeId(), rerate.getRelatedSpirePositionId(),
                        rerate.getContractId());
                    return rerate;
                }
                if (WAITING_PROPOSAL.equals(rerateTrade.getProcessingStatus())) {
                    //Receiver
                    rerate.setMatchingSpireTradeId(rerateTrade.getTradeId());
                    rerate.setRelatedSpirePositionId(rerateTrade.getRelatedPositionId());
                    rerateTradeService.markRerateTradeAsMatchedWithRerate(rerateTrade, rerate);
                    rerate.setProcessingStatus(TO_VALIDATE);
                    recordCloudEvent(PROCESS_RERATE_PENDING_CONFIRMATION, RERATE_PROPOSAL_MATCHED,
                        rerate.getRerateId(), rerate.getMatchingSpireTradeId(), rerate.getRelatedSpirePositionId(),
                        rerate.getContractId());
                    return rerate;
                }
            }
        }
        rerate.setProcessingStatus(UNMATCHED);
        recordCloudEvent(MATCH_LOAN_RERATE_PROPOSAL, RERATE_PROPOSAL_UNMATCHED,
            rerate.getRerateId(), rerate.getMatchingSpireTradeId(), rerate.getRelatedSpirePositionId(),
            rerate.getContractId(), buildUnmatchedFieldImpactedList(rerate));
        return rerate;
    }

    private List<FieldImpacted> buildUnmatchedFieldImpactedList(Rerate rerate) {
        List<FieldImpacted> unmatchedFieldImpactedList = new ArrayList<>();
        unmatchedFieldImpactedList.add(
            new FieldImpacted(ONE_SOURCE_RERATE, "1sourceRerate.contractId", rerate.getContractId(),
                FieldExceptionType.UNMATCHED));
        if (rerate.getRerate() != null && rerate.getRerate().getRebate() != null
            && rerate.getRerate().getRebate().getFixed() != null) {
            unmatchedFieldImpactedList.add(
                new FieldImpacted(ONE_SOURCE_RERATE, "1sourceRerate.rate.rebate.fixed.effectiveDate",
                    toStringNullSafe(rerate.getRerate().getRebate().getFixed().getEffectiveDate()),
                    FieldExceptionType.UNMATCHED));
            unmatchedFieldImpactedList.add(
                new FieldImpacted(ONE_SOURCE_RERATE, "1sourceRerate.rate.rebate.fixed.baseRate",
                    toStringNullSafe(rerate.getRerate().getRebate().getFixed().getBaseRate()),
                    FieldExceptionType.UNMATCHED));
        } else if (rerate.getRerate() != null && rerate.getRerate().getRebate() != null
            && rerate.getRerate().getRebate().getFloating() != null) {
            unmatchedFieldImpactedList.add(
                new FieldImpacted(ONE_SOURCE_RERATE, "1sourceRerate.rate.rebate.floating.effectiveDate",
                    toStringNullSafe(rerate.getRerate().getRebate().getFloating().getEffectiveDate()),
                    FieldExceptionType.UNMATCHED));
            unmatchedFieldImpactedList.add(
                new FieldImpacted(ONE_SOURCE_RERATE, "1sourceRerate.rate.rebate.floating.baseRate",
                    toStringNullSafe(rerate.getRerate().getRebate().getFloating().getBaseRate()),
                    FieldExceptionType.UNMATCHED));
        }
        return unmatchedFieldImpactedList;
    }

    public Rerate validateRerate(Rerate rerate) {
        RerateTrade rerateTrade = rerateTradeService.getByTradeId(rerate.getMatchingSpireTradeId());
        try {
            rerateReconcileService.reconcile(rerate, rerateTrade);
            rerate.setProcessingStatus(VALIDATED);
        } catch (ReconcileException e) {
            log.error("Reconciliation fails with message: {} ", e.getMessage());
            e.getErrorList().forEach(msg -> log.debug(msg.getFieldValue()));
            createFailedReconciliationEvent(rerate, e);
            rerate.setProcessingStatus(DISCREPANCIES);
        }
        return rerate;
    }

    public Rerate approveRerate(Rerate rerate) {
        try {
            oneSourceService.approveRerate(rerate.getContractId(), rerate.getRerateId());
            rerate.setProcessingStatus(APPROVAL_SUBMITTED);
        } catch (HttpStatusCodeException codeException) {
            recordHttpExceptionCloudEvent(APPROVE_RERATE_PROPOSAL, TECHNICAL_EXCEPTION_1SOURCE,
                codeException, rerate.getRerateId(), rerate.getMatchingSpireTradeId(),
                rerate.getRelatedSpirePositionId());
            throwExceptionForRedeliveryPolicy(codeException);
        }
        return rerate;
    }

    public boolean isRerateTradePostponed(RerateTrade rerateTrade) {
        Optional<RerateTrade> notConfirmedRerateTrade = findNotConfirmedRerateTradeWithLowerTradeId(rerateTrade);
        if (notConfirmedRerateTrade.isPresent()) {
            return true;
        } else {
            Optional<ReturnTrade> notConfirmedReturnTrade = findNotConfirmedReturnTradeWithLowerTradeId(rerateTrade);
            if (notConfirmedReturnTrade.isPresent()) {
                return true;
            }
        }
        return false;
    }

    private Optional<RerateTrade> findNotConfirmedRerateTradeWithLowerTradeId(RerateTrade rerateTrade) {
        return rerateTradeService.findReturnTrade(
            rerateTrade.getRelatedPositionId(),
            List.of(CREATED, SUBMITTED, WAITING_PROPOSAL)).stream().filter(
            rerate -> rerateTrade.getTradeId() > rerate.getTradeId() && (
                "Rerate".equals(rerate.getTradeOut().getTradeType())
                    || "Rerate Borrow".equals(
                    rerate.getTradeOut().getTradeType()))).findFirst();
    }

    private Optional<ReturnTrade> findNotConfirmedReturnTradeWithLowerTradeId(RerateTrade rerateTrade) {
        return returnTradeService.findReturnTrade(
            rerateTrade.getRelatedPositionId(),
            List.of(CREATED, SUBMITTED)).stream().filter(
            rt -> rerateTrade.getTradeId() > rt.getTradeId() && ("Return Loan".equals(rt.getTradeOut().getTradeType())
                || "Return Borrow".equals(
                rt.getTradeOut().getTradeType()))).findFirst();
    }

    public RerateTrade confirmRerateTrade(RerateTrade rerateTrade) {
        try {
            backOfficeService.confirmBackOfficeRerateTrade(rerateTrade);
            rerateTrade.getTradeOut().setStatus("FUTURE");
        } catch (HttpStatusCodeException codeException) {
            recordHttpExceptionCloudEvent(POST_RERATE_TRADE_CONFIRMATION, TECHNICAL_EXCEPTION_SPIRE,
                codeException, rerateTrade.getMatchingRerateId(), rerateTrade.getTradeId(),
                rerateTrade.getRelatedPositionId());
            throwExceptionForRedeliveryPolicy(codeException);
        }
        return rerateTrade;
    }

    public DeclineInstruction declineRerate(DeclineInstruction declineInstruction) {
        try {
            Rerate rerate = rerateService.getByRerateId(declineInstruction.getRelatedProposalId());
            if (DISCREPANCIES.equals(rerate.getProcessingStatus()) || UNMATCHED.equals(rerate.getProcessingStatus())) {
                try {
                    oneSourceService.declineRerate(rerate.getContractId(), rerate.getRerateId());
                    rerate.setProcessingStatus(DECLINE_SUBMITTED);
                    rerateService.saveRerate(rerate);
                } catch (HttpStatusCodeException codeException) {
                    recordHttpExceptionCloudEvent(DECLINE_RERATE_PROPOSAL, TECHNICAL_EXCEPTION_1SOURCE,
                        codeException, rerate.getRerateId(), rerate.getMatchingSpireTradeId(),
                        rerate.getRelatedSpirePositionId());
                    throwExceptionForRedeliveryPolicy(codeException);
                }
            } else {
                recordCloudEvent(DECLINE_RERATE_PROPOSAL, DECLINE_INSTRUCTION_NOT_AUTHORIZED, rerate.getRerateId(),
                    rerate.getMatchingSpireTradeId(), rerate.getRelatedSpirePositionId(), rerate.getContractId());
            }
        } catch (EntityNotFoundException e) {
            recordRerateEntityNotFoundTechnicalException(DECLINE_RERATE_PROPOSAL,
                TECHNICAL_ISSUE_INTEGRATION_TOOLKIT, declineInstruction.getRelatedProposalId());
        }
        return declineInstruction;
    }

    public CorrectionInstruction amendRerateTrade(CorrectionInstruction correctionInstruction) {
        try {
            RerateTrade rerateTrade = rerateTradeService.getByTradeId(correctionInstruction.getOldTradeId());
            rerateTrade.setProcessingStatus(REPLACED);
            saveRerateTrade(rerateTrade);
            recordRerateTradeReplacedCloudEvent(PROCESS_TRADE_UPDATE, RERATE_TRADE_REPLACED, rerateTrade.getTradeId(),
                correctionInstruction.getOldTradeId(), correctionInstruction.getAmendedTradeId());
            if (StringUtils.isNotBlank(rerateTrade.getMatchingRerateId())) {
                cancelLinkedRerate(correctionInstruction, rerateTrade);
            }
        } catch (EntityNotFoundException e) {
            recordCloudEvent(PROCESS_TRADE_UPDATE, TECHNICAL_ISSUE_INTEGRATION_TOOLKIT, null,
                correctionInstruction.getOldTradeId(), null, null);
        }
        return correctionInstruction;
    }

    private void cancelLinkedRerate(CorrectionInstruction correctionInstruction, RerateTrade rerateTrade) {
        Rerate rerate = rerateService.getByRerateId(rerateTrade.getMatchingRerateId());
        if (Set.of(MATCHED, APPROVED, CANCEL_PENDING).contains(rerate.getProcessingStatus())) {
            cancelRerate(rerate, PROCESS_TRADE_UPDATE);
            rerate.setProcessingStatus(CANCEL_SUBMITTED);
            saveRerate(rerate);
            recordRerateTradeReplaceSubmittedCloudEvent(PROCESS_TRADE_UPDATE, RERATE_TRADE_REPLACE_SUBMITTED,
                correctionInstruction.getOldTradeId(), correctionInstruction.getAmendedTradeId(), rerate);
        } else if (DISCREPANCIES == rerate.getProcessingStatus()) {
            delinkRerate(rerate);
        }
    }

    public CorrectionInstruction cancelRerateTrade(CorrectionInstruction correctionInstruction) {
        try {
            RerateTrade rerateTrade = rerateTradeService.getByTradeId(
                correctionInstruction.getAmendedTradeId());
            rerateTrade.setProcessingStatus(CANCELED);
            saveRerateTrade(rerateTrade);
            recordRerateTradeCanceledCloudEvent(PROCESS_TRADE_CANCELED, RERATE_TRADE_CANCELED,
                rerateTrade.getMatchingRerateId(),
                correctionInstruction.getOldTradeId(), correctionInstruction.getAmendedTradeId());
            if (rerateTrade.getMatchingRerateId() != null && !rerateTrade.getMatchingRerateId().isEmpty()) {
                Rerate rerate = rerateService.getByRerateId(rerateTrade.getMatchingRerateId());
                if (MATCHED.equals(rerate.getProcessingStatus()) || APPROVED.equals(rerate.getProcessingStatus())) {
                    cancelRerate(rerate, PROCESS_TRADE_CANCEL);
                    rerate.setProcessingStatus(CANCEL_SUBMITTED);
                    saveRerate(rerate);
                } else if (!CANCELED.equals(rerate.getProcessingStatus())
                    && !DECLINED.equals(rerate.getProcessingStatus())) {
                    rerate = delinkRerate(rerate);
                    saveRerate(rerate);
                }
            }
        } catch (EntityNotFoundException e) {
            recordCloudEvent(PROCESS_TRADE_CANCEL, TECHNICAL_ISSUE_INTEGRATION_TOOLKIT, null,
                correctionInstruction.getOldTradeId(), null, null);
        }
        return correctionInstruction;
    }

    private void cancelRerate(Rerate rerate, IntegrationSubProcess subProcess) {
        try {
            oneSourceService.cancelRerate(rerate.getContractId(), rerate.getRerateId());
        } catch (HttpStatusCodeException codeException) {
            recordHttpExceptionCloudEvent(subProcess, TECHNICAL_EXCEPTION_1SOURCE,
                codeException, rerate.getRerateId(), rerate.getMatchingSpireTradeId(),
                rerate.getRelatedSpirePositionId());
            throwExceptionForRedeliveryPolicy(codeException);
        }
    }

    private Rerate delinkRerate(Rerate rerate) {
        rerate.setMatchingSpireTradeId(null);
        rerate.setProcessingStatus(PROPOSED);
        return saveRerate(rerate);
    }

    private Optional<LocalDate> getRerateEffectiveDate(Rerate rerate) {
        if (rerate.getRerate() != null && rerate.getRerate().getRebate() != null
            && rerate.getRerate().getRebate().getFixed() != null) {
            return Optional.of(rerate.getRerate().getRebate().getFixed().getEffectiveDate());
        } else if (rerate.getRerate() != null && rerate.getRerate().getRebate() != null
            && rerate.getRerate().getRebate().getFloating() != null) {
            return Optional.of(rerate.getRerate().getRebate().getFloating().getEffectiveDate());
        }
        return Optional.empty();
    }

    private void recordTradeEventExceptionEvent(HttpStatusCodeException exception) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(GENERIC);
        final CloudEventBuildRequest recordRequest = eventBuilder.buildExceptionRequest(exception,
            IntegrationSubProcess.GET_TRADE_EVENTS_PENDING_CONFIRMATION);
        cloudEventRecordService.record(recordRequest);
    }

    private void recordCloudEvent(IntegrationSubProcess subProcess, RecordType recordType,
        Map<String, String> data, List<FieldImpacted> fieldImpacteds) {
        var recordRequest = eventBuilder.buildRequest(subProcess, recordType, data, fieldImpacteds);
        cloudEventRecordService.record(recordRequest);
    }

    private void recordHttpExceptionCloudEvent(IntegrationSubProcess subProcess, RecordType recordType,
        HttpStatusCodeException e, String rerateId, Long tradeId, Long positionId) {
        Map<String, String> data = new HashMap<>();
        data.put(HTTP_STATUS_TEXT, e.getStatusText());
        if (rerateId != null) {
            data.put(RERATE_ID, rerateId);
        }
        if (tradeId != null) {
            data.put(TRADE_ID, toStringNullSafe(tradeId));
        }
        if (positionId != null) {
            data.put(POSITION_ID, toStringNullSafe(positionId));
        }
        recordCloudEvent(subProcess, recordType, data, List.of());
    }

    private void recordRerateEntityNotFoundTechnicalException(IntegrationSubProcess subProcess, RecordType recordType,
        String rerateId) {
        Map<String, String> data = Map.of(RERATE_ID, rerateId);
        var recordRequest = eventBuilder.buildRequest(subProcess, recordType, data, List.of());
        cloudEventRecordService.record(recordRequest);
    }


    private void recordCloudEvent(IntegrationSubProcess subProcess, RecordType recordType, String rerateId,
        Long tradeId, Long positionId, String contractId) {
        recordCloudEvent(subProcess, recordType, rerateId, tradeId, positionId, contractId, List.of());
    }

    private void recordCloudEvent(IntegrationSubProcess subProcess, RecordType recordType, String rerateId,
        Long tradeId, Long positionId, String contractId, List<FieldImpacted> fieldImpacteds) {
        Map<String, String> data = new HashMap<>();
        if (rerateId != null) {
            data.put(RERATE_ID, rerateId);
        }
        if (tradeId != null) {
            data.put(TRADE_ID, toStringNullSafe(tradeId));
        }
        if (positionId != null) {
            data.put(POSITION_ID, toStringNullSafe(positionId));
        }
        if (contractId != null) {
            data.put(CONTRACT_ID, contractId);
        }
        recordCloudEvent(subProcess, recordType, data, fieldImpacteds);
    }

    private void recordRerateTradeReplaceSubmittedCloudEvent(IntegrationSubProcess subProcess, RecordType recordType,
        Long oldTradeId, Long amendedTradeId, Rerate rerate) {
        Map<String, String> data = new HashMap<>();
        data.put("oldTradeId", toStringNullSafe(oldTradeId));
        data.put("amendedTradeId", toStringNullSafe(amendedTradeId));
        data.put(RERATE_ID, rerate.getRerateId());
        data.put(TRADE_ID, toStringNullSafe(rerate.getMatchingSpireTradeId()));
        data.put(POSITION_ID, toStringNullSafe(rerate.getRelatedSpirePositionId()));
        data.put(CONTRACT_ID, rerate.getContractId());
        var recordRequest = eventBuilder.buildRequest(subProcess, recordType,
            data, List.of());
        cloudEventRecordService.record(recordRequest);
    }

    private void recordRerateTradeReplacedCloudEvent(IntegrationSubProcess subProcess, RecordType recordType,
        Long tradeId, Long oldTradeId, Long amendedTradeId) {
        Map<String, String> data = new HashMap<>();
        data.put("oldTradeId", toStringNullSafe(oldTradeId));
        data.put("amendedTradeId", toStringNullSafe(amendedTradeId));
        data.put(TRADE_ID, toStringNullSafe(tradeId));
        var recordRequest = eventBuilder.buildRequest(subProcess, recordType,
            data, List.of());
        cloudEventRecordService.record(recordRequest);
    }

    private void recordRerateTradeCanceledCloudEvent(IntegrationSubProcess subProcess, RecordType recordType,
        String rerateId, Long oldTradeId, Long amendedTradeId) {
        Map<String, String> data = new HashMap<>();
        data.put("oldTradeId", toStringNullSafe(oldTradeId));
        data.put("amendedTradeId", toStringNullSafe(amendedTradeId));
        data.put(RERATE_ID, rerateId);
        var recordRequest = eventBuilder.buildRequest(subProcess, recordType,
            data, List.of());
        cloudEventRecordService.record(recordRequest);
    }

    private void createFailedReconciliationEvent(Rerate rerate, ReconcileException e) {
        var recordRequest = eventBuilder.buildRequest(rerate.getRerateId(),
            RERATE_PROPOSAL_DISCREPANCIES, toStringNullSafe(rerate.getMatchingSpireTradeId()), e.getErrorList());
        cloudEventRecordService.record(recordRequest);
    }

}
