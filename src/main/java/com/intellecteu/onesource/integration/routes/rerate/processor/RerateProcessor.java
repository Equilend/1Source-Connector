package com.intellecteu.onesource.integration.routes.rerate.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RERATE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.APPROVE_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.DECLINE_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.MATCH_LOAN_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_RERATE_TRADE_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_RERATE_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.APPROVAL_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CONFIRMED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DECLINE_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.MATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TO_VALIDATE;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UPDATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.VALIDATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.WAITING_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_MATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_PENDING_APPROVAL;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_TRADE_CREATED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_SPIRE;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.HTTP_STATUS_TEXT;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.RERATE_ID;
import static com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder.TRADE_ID;

import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.DeclineInstruction;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.DeclineInstructionService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.RerateService;
import com.intellecteu.onesource.integration.services.RerateTradeService;
import com.intellecteu.onesource.integration.services.reconciliation.RerateReconcileService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.RerateCloudEventBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

@Component
@Slf4j
public class RerateProcessor {

    private final BackOfficeService lenderBackOfficeService;
    private final BackOfficeService borrowerBackOfficeService;
    private final OneSourceService oneSourceService;
    private final RerateTradeService rerateTradeService;
    private final RerateService rerateService;
    private final RerateReconcileService rerateReconcileService;
    private final DeclineInstructionService declineInstructionService;
    private final CloudEventRecordService cloudEventRecordService;
    private final RerateCloudEventBuilder eventBuilder;

    @Autowired
    public RerateProcessor(BackOfficeService lenderBackOfficeService, BackOfficeService borrowerBackOfficeService,
        OneSourceService oneSourceService, RerateTradeService rerateTradeService,
        RerateService rerateService,
        RerateReconcileService rerateReconcileService, DeclineInstructionService declineInstructionService,
        CloudEventRecordService cloudEventRecordService) {
        this.lenderBackOfficeService = lenderBackOfficeService;
        this.borrowerBackOfficeService = borrowerBackOfficeService;
        this.oneSourceService = oneSourceService;
        this.rerateTradeService = rerateTradeService;
        this.rerateService = rerateService;
        this.rerateReconcileService = rerateReconcileService;
        this.declineInstructionService = declineInstructionService;
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

    public List<RerateTrade> fetchNewRerateTrades() {
        Optional<Long> lastTradeId = rerateTradeService.getMaxTradeId();
        List<RerateTrade> rerateTradeList = new ArrayList<>();
        rerateTradeList.addAll(
            lenderBackOfficeService.getNewBackOfficeRerateTradeEvents(lastTradeId));
        rerateTradeList.addAll(
            borrowerBackOfficeService.getNewBackOfficeRerateTradeEvents(lastTradeId));
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
                rerateTrade.getMatchingRerateId(), rerateTrade.getTradeId());
        } else {
            recordCloudEvent(PROCESS_RERATE_PENDING_CONFIRMATION, RERATE_TRADE_CREATED,
                rerateTrade.getMatchingRerateId(), rerateTrade.getTradeId());
        }
        return rerateTrade;
    }

    public RerateTrade instructRerateTrade(RerateTrade rerateTrade) {
        try {
            oneSourceService.instructRerate(rerateTrade);
            rerateTrade.setProcessingStatus(SUBMITTED);
        } catch (HttpStatusCodeException codeException) {
            if (codeException.getStatusCode().value() != 400) {
                rerateTrade.setProcessingStatus(WAITING_PROPOSAL);
                recordHttpExceptionCloudEvent(POST_RERATE_PROPOSAL, TECHNICAL_EXCEPTION_1SOURCE,
                    codeException, rerateTrade.getMatchingRerateId(), rerateTrade.getTradeId());
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
                    rerateTradeService.markRerateTradeAsMatchedWithRerate(rerateTrade, rerate);
                    rerate.setProcessingStatus(MATCHED);
                    recordCloudEvent(MATCH_LOAN_RERATE_PROPOSAL, RERATE_PROPOSAL_PENDING_APPROVAL,
                        rerate.getRerateId(), rerate.getMatchingSpireTradeId());
                    return rerate;
                }
                if (CREATED.equals(rerateTrade.getProcessingStatus()) || UPDATED.equals(
                    rerateTrade.getProcessingStatus())) {
                    //Receiver
                    rerate.setMatchingSpireTradeId(rerateTrade.getTradeId());
                    rerateTradeService.markRerateTradeAsMatchedWithRerate(rerateTrade, rerate);
                    rerate.setProcessingStatus(TO_VALIDATE);
                    recordCloudEvent(PROCESS_RERATE_PENDING_CONFIRMATION, RERATE_PROPOSAL_MATCHED,
                        rerate.getRerateId(), rerate.getMatchingSpireTradeId());
                    return rerate;
                }
            }
        }
        rerate.setProcessingStatus(UNMATCHED);
        recordCloudEvent(MATCH_LOAN_RERATE_PROPOSAL, RERATE_PROPOSAL_UNMATCHED,
            rerate.getRerateId(), rerate.getMatchingSpireTradeId());
        return rerate;
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
                codeException, rerate.getRerateId(), rerate.getMatchingSpireTradeId());
        }
        return rerate;
    }

    public RerateTrade confirmRerateTrade(RerateTrade rerateTrade) {
        try {
            lenderBackOfficeService.confirmBackOfficeRerateTrade(rerateTrade);
            rerateTrade.setProcessingStatus(CONFIRMED);
        } catch (HttpStatusCodeException codeException) {
            recordHttpExceptionCloudEvent(POST_RERATE_TRADE_CONFIRMATION, TECHNICAL_EXCEPTION_SPIRE,
                codeException, rerateTrade.getMatchingRerateId(), rerateTrade.getTradeId());
        }
        return rerateTrade;
    }

    public DeclineInstruction declineRerate(DeclineInstruction declineInstruction) {
        Rerate rerate = rerateService.getByRerateId(declineInstruction.getRelatedProposalId());
        try {
            oneSourceService.declineRerate(rerate.getContractId(), rerate.getRerateId());
            rerate.setProcessingStatus(DECLINE_SUBMITTED);
            rerateService.saveRerate(rerate);
        } catch (HttpStatusCodeException codeException) {
            recordHttpExceptionCloudEvent(DECLINE_RERATE_PROPOSAL, TECHNICAL_EXCEPTION_1SOURCE,
                codeException, rerate.getRerateId(), rerate.getMatchingSpireTradeId());
        }
        return declineInstruction;
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

    private void recordHttpExceptionCloudEvent(IntegrationSubProcess subProcess, RecordType recordType,
        HttpStatusCodeException e, String rerateId, Long tradeId) {
        Map<String, String> data = new HashMap<>();
        data.put(HTTP_STATUS_TEXT, e.getStatusText());
        if (rerateId != null) {
            data.put(RERATE_ID, rerateId);
        }
        if (tradeId != null) {
            data.put(TRADE_ID, String.valueOf(tradeId));
        }
        var recordRequest = eventBuilder.buildRequest(subProcess, recordType,
            data, List.of());
        cloudEventRecordService.record(recordRequest);
    }

    private void recordCloudEvent(IntegrationSubProcess subProcess, RecordType recordType, String rerateId,
        Long tradeId) {
        Map<String, String> data = new HashMap<>();
        if (rerateId != null) {
            data.put(RERATE_ID, rerateId);
        }
        if (tradeId != null) {
            data.put(TRADE_ID, String.valueOf(tradeId));
        }
        var recordRequest = eventBuilder.buildRequest(subProcess, recordType,
            data, List.of());
        cloudEventRecordService.record(recordRequest);
    }

    private void createFailedReconciliationEvent(Rerate rerate, ReconcileException e) {
        var recordRequest = eventBuilder.buildRequest(rerate.getRerateId(),
            RERATE_PROPOSAL_DISCREPANCIES, String.valueOf(rerate.getMatchingSpireTradeId()), e.getErrorList());
        cloudEventRecordService.record(recordRequest);
    }

}
