package com.intellecteu.onesource.integration.routes.rerate.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_MATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_PENDING_APPROVAL;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_TRADE_CREATED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.MATCHED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.SUBMITTED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.TO_VALIDATE;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.UNMATCHED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.UPDATED;

import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.RerateService;
import com.intellecteu.onesource.integration.services.RerateTradeService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class RerateProcessor {

    private final BackOfficeService lenderBackOfficeService;
    private final BackOfficeService borrowerBackOfficeService;
    private final OneSourceService oneSourceService;
    private final ContractService contractService;
    private final RerateTradeService rerateTradeService;
    private final RerateService rerateService;
    private final CloudEventRecordService cloudEventRecordService;

    @Autowired
    public RerateProcessor(BackOfficeService lenderBackOfficeService, BackOfficeService borrowerBackOfficeService,
        OneSourceService oneSourceService, ContractService contractService, RerateTradeService rerateTradeService,
        RerateService rerateService,
        CloudEventRecordService cloudEventRecordService) {
        this.lenderBackOfficeService = lenderBackOfficeService;
        this.borrowerBackOfficeService = borrowerBackOfficeService;
        this.oneSourceService = oneSourceService;
        this.contractService = contractService;
        this.rerateTradeService = rerateTradeService;
        this.rerateService = rerateService;
        this.cloudEventRecordService = cloudEventRecordService;
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

    public List<RerateTrade> fetchNewRerateTrades() {
        Optional<Long> lastTradeId = rerateTradeService.getMaxTradeId();
        List<RerateTrade> rerateTradeList = new ArrayList<>();
        rerateTradeList.addAll(
            lenderBackOfficeService.getNewBackOfficeRerateTradeEvents(lastTradeId));
        rerateTradeList.addAll(
            borrowerBackOfficeService.getNewBackOfficeRerateTradeEvents(lastTradeId));
        return rerateTradeList;
    }

    public RerateTrade matchBackOfficeRerateTradeWith1SourceRerate(RerateTrade rerateTrade) {
        LocalDate settleDate = rerateTrade.getTradeOut().getSettleDate().toLocalDate();
        Rerate rerate = rerateService.findUnmatchedRerate(rerateTrade.getRelatedContractId(),
            settleDate).orElse(null);
        if (rerate != null) {
            rerateTrade.setMatchingRerateId(rerate.getRerateId());
            rerateService.markRerateAsMatchedWithRerateTrade(rerate, rerateTrade);
            recordRerateTradeSuccessMatched1SourceRerateCloudEvent(rerate);
        } else {
            recordCreatedRerateTradeCloudEvent(rerateTrade);
        }
        return rerateTrade;
    }

    public RerateTrade instructRerateTrade(RerateTrade rerateTrade) {
        try {
            oneSourceService.instructRerate(rerateTrade);
            rerateTrade.setProcessingStatus(ProcessingStatus.SUBMITTED);
        } catch (HttpClientErrorException httpClientErrorException) {
            if (httpClientErrorException.getStatusCode().value() != 400) {
                recordTechnicalException1source(httpClientErrorException, rerateTrade.getTradeId());
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
                if(SUBMITTED.equals(rerateTrade.getProcessingStatus())){
                    //Initiator
                    rerate.setMatchingSpireTradeId(rerateTrade.getTradeId());
                    rerateTradeService.markRerateTradeAsMatchedWithRerate(rerateTrade, rerate);
                    rerate.setProcessingStatus(MATCHED);
                    recordInitiator1SourceRerateSuccessMatchedCloudEvent(rerate);
                    return rerate;
                }
                if(CREATED.equals(rerateTrade.getProcessingStatus()) || UPDATED.equals(rerateTrade.getProcessingStatus())){
                    //Receiver
                    rerate.setMatchingSpireTradeId(rerateTrade.getTradeId());
                    rerateTradeService.markRerateTradeAsMatchedWithRerate(rerateTrade, rerate);
                    rerate.setProcessingStatus(TO_VALIDATE);
                    recordReceiver1SourceRerateSuccessMatchedCloudEvent(rerate);
                    return rerate;
                }
            }
        }
        rerate.setProcessingStatus(UNMATCHED);
        record1SourceRerateSuccessUnMatchedRerateTradeCloudEvent(rerate);
        return rerate;
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

    private void recordTechnicalException1source(HttpClientErrorException httpClientErrorException,
        Long spireRerateTradeId) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.RERATE);
        var recordRequest = eventBuilder.buildExceptionRequest(httpClientErrorException,
            POST_RERATE_PROPOSAL, String.valueOf(spireRerateTradeId));
        cloudEventRecordService.record(recordRequest);
    }

    private void recordRerateTradeSuccessMatched1SourceRerateCloudEvent(Rerate rerate) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.RERATE);
        var recordRequest = eventBuilder.buildRequest(rerate.getRerateId(),
            RERATE_PROPOSAL_MATCHED, String.valueOf(rerate.getMatchingSpireTradeId()));
        cloudEventRecordService.record(recordRequest);
    }

    private void recordCreatedRerateTradeCloudEvent(RerateTrade rerateTrade) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.RERATE);
        var recordRequest = eventBuilder.buildRequest(String.valueOf(rerateTrade.getTradeId()),
            RERATE_TRADE_CREATED, "");
        cloudEventRecordService.record(recordRequest);
    }

    private void recordInitiator1SourceRerateSuccessMatchedCloudEvent(Rerate rerate) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.RERATE);
        var recordRequest = eventBuilder.buildRequest(rerate.getRerateId(),
            RERATE_PROPOSAL_PENDING_APPROVAL, String.valueOf(rerate.getMatchingSpireTradeId()));
        cloudEventRecordService.record(recordRequest);
    }

    private void recordReceiver1SourceRerateSuccessMatchedCloudEvent(Rerate rerate) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.RERATE);
        var recordRequest = eventBuilder.buildRequest(rerate.getRerateId(),
            RERATE_PROPOSAL_MATCHED, String.valueOf(rerate.getMatchingSpireTradeId()));
        cloudEventRecordService.record(recordRequest);
    }

    private void record1SourceRerateSuccessUnMatchedRerateTradeCloudEvent(Rerate rerate) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.RERATE);
        var recordRequest = eventBuilder.buildRequest(rerate.getRerateId(),
            RERATE_PROPOSAL_UNMATCHED, String.valueOf(rerate.getMatchingSpireTradeId()));
        cloudEventRecordService.record(recordRequest);
    }

}
