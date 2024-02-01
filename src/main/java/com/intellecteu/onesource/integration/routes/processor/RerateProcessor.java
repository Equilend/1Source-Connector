package com.intellecteu.onesource.integration.routes.processor;

import static com.intellecteu.onesource.integration.enums.RecordType.RERATE_PROPOSAL_MATCHED_RERATE_TRADE;
import static com.intellecteu.onesource.integration.enums.RecordType.RERATE_PROPOSAL_UNMATCHED;

import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.ContractStatus;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.Rerate;
import com.intellecteu.onesource.integration.model.RerateStatus;
import com.intellecteu.onesource.integration.model.spire.RerateTrade;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.RerateService;
import com.intellecteu.onesource.integration.services.RerateTradeService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        List<Contract> openedContracts = contractService.findAllByContractStatus(ContractStatus.OPEN);
        List<String> matchingSpirePositionIds = openedContracts.stream().map(Contract::getMatchingSpirePositionId)
            .collect(
                Collectors.toList());
        List<RerateTrade> rerateTradeList = new ArrayList<>();
        if(!matchingSpirePositionIds.isEmpty()) {
            rerateTradeList.addAll(
                lenderBackOfficeService.getNewBackOfficeTradeEvents(lastTradeId, matchingSpirePositionIds));
            rerateTradeList.addAll(
                borrowerBackOfficeService.getNewBackOfficeTradeEvents(lastTradeId, matchingSpirePositionIds));
        }
        return rerateTradeList;
    }

    public RerateTrade matchContract(RerateTrade rerateTrade) {
        Optional<Contract> matchedContractOptional = contractService.findByPositionId(
            String.valueOf(rerateTrade.getRelatedPositionId()));
        rerateTrade.setRelatedContractId(matchedContractOptional.get().getContractId());
        return rerateTrade;
    }


    public RerateTrade matchRerate(RerateTrade rerateTrade) {
        LocalDate settleDate = rerateTrade.getTradeOut().getSettleDate().toLocalDate();
        Optional<Rerate> rerateOptional = rerateService.findRerate(rerateTrade.getRelatedPositionId(),
            settleDate, RerateStatus.PROPOSED);
        rerateOptional.ifPresent(rerate -> {
            rerateService.markRerateAsMatchedWithRerateTradeId(rerate, rerateTrade.getTradeId());
            recordRerateSuccessMatchedRerateTradeCloudEvent(rerate);
        });
        return rerateTrade;
    }

    public Rerate matchContract(Rerate rerate) {
        Optional<Contract> contractOptional = contractService.findAllByContractId(rerate.getContractId()).stream()
            .findFirst();
        contractOptional.ifPresent(
            contract -> rerate.setRelatedSpirePositionId(Long.valueOf(contract.getMatchingSpirePositionId())));
        return rerate;
    }

    public Rerate matchRerateTrade(Rerate rerate) {
        Optional<LocalDate> rerateEffectiveDateOptional = getRerateEffectiveDate(rerate);
        if (rerateEffectiveDateOptional.isPresent()) {
            Optional<RerateTrade> rerateTradeOptional = rerateTradeService.findRerateTradeByContractIdAndSettleDate(
                rerate.getContractId(), rerateEffectiveDateOptional.get());
            if (rerateTradeOptional.isPresent()) {
                rerate.setMatchingSpireTradeId(rerateTradeOptional.get().getTradeId());
                recordRerateSuccessMatchedRerateTradeCloudEvent(rerate);
                return rerate;
            }
        }
        recordUnMatchedRerateTradeCloudEvent(rerate);
        return rerate;
    }

    private Optional<LocalDate> getRerateEffectiveDate(Rerate rerate) {
        if (rerate.getRate() != null && rerate.getRate().getRebate() != null
            && rerate.getRate().getRebate().getFixed() != null) {
            return Optional.of(rerate.getRate().getRebate().getFixed().getEffectiveDate());
        } else if (rerate.getRate() != null && rerate.getRate().getRebate() != null
            && rerate.getRate().getRebate().getFloating() != null) {
            return Optional.of(rerate.getRate().getRebate().getFloating().getEffectiveDate());
        }
        return Optional.empty();
    }

    private void recordRerateSuccessMatchedRerateTradeCloudEvent(Rerate rerate) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.RERATE);
        var recordRequest = eventBuilder.buildRequest(rerate.getRerateId(),
            RERATE_PROPOSAL_MATCHED_RERATE_TRADE, String.valueOf(rerate.getMatchingSpireTradeId()));
        cloudEventRecordService.record(recordRequest);
    }

    private void recordUnMatchedRerateTradeCloudEvent(Rerate rerate) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.RERATE);
        var recordRequest = eventBuilder.buildRequest(rerate.getRerateId(),
            RERATE_PROPOSAL_UNMATCHED, "");
        cloudEventRecordService.record(recordRequest);
    }

}
