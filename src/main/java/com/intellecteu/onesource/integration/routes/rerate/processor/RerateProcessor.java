package com.intellecteu.onesource.integration.routes.rerate.processor;

import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_MATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_PENDING_APPROVAL;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_TRADE_CREATED;

import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
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

@Component
public class RerateProcessor {

    private final BackOfficeService lenderBackOfficeService;
    private final BackOfficeService borrowerBackOfficeService;
    private final ContractService contractService;
    private final RerateTradeService rerateTradeService;
    private final RerateService rerateService;
    private final CloudEventRecordService cloudEventRecordService;

    @Autowired
    public RerateProcessor(BackOfficeService lenderBackOfficeService, BackOfficeService borrowerBackOfficeService,
        ContractService contractService, RerateTradeService rerateTradeService, RerateService rerateService,
        CloudEventRecordService cloudEventRecordService) {
        this.lenderBackOfficeService = lenderBackOfficeService;
        this.borrowerBackOfficeService = borrowerBackOfficeService;
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

    public RerateTrade matchRerate(RerateTrade rerateTrade) {
        LocalDate accrualDate = rerateTrade.getTradeOut().getAccrualDate().toLocalDate();
        Optional<Rerate> rerateOptional = rerateService.findRerate(rerateTrade.getRelatedPositionId(),
            accrualDate, ProcessingStatus.UNMATCHED);
        if(rerateOptional.isPresent()){
            Rerate rerate = rerateOptional.get();
            rerateTrade.setMatchingRerateId(rerate.getRerateId());
            rerateService.markRerateAsMatchedWithRerateTradeIdAndPositionId(rerate, rerateTrade.getTradeId(), rerateTrade.getRelatedPositionId());
            recordRerateTradeSuccessMatched1SourceRerateCloudEvent(rerate);
        }else{
            recordCreatedRerateTradeCloudEvent(rerateTrade);
        }
        return rerateTrade;
    }

    public Rerate matchRerateTrade(Rerate rerate) {
        Optional<LocalDate> rerateEffectiveDateOptional = getRerateEffectiveDate(rerate);
        if (rerateEffectiveDateOptional.isPresent()) {
            Optional<RerateTrade> rerateTradeOptional = rerateTradeService.findRerateTradeByContractIdAndSettleDate(
                rerate.getContractId(), rerateEffectiveDateOptional.get());
            if (rerateTradeOptional.isPresent()) {
                rerate.setMatchingSpireTradeId(rerateTradeOptional.get().getTradeId());
                record1SourceRerateSuccessMatchedRerateTradeCloudEvent(rerate);
                return rerate;
            }
        }
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

    private void record1SourceRerateSuccessMatchedRerateTradeCloudEvent(Rerate rerate) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.RERATE);
        var recordRequest = eventBuilder.buildRequest(rerate.getRerateId(),
            RERATE_PROPOSAL_PENDING_APPROVAL, String.valueOf(rerate.getMatchingSpireTradeId()));
        cloudEventRecordService.record(recordRequest);
    }

}
