package com.intellecteu.onesource.integration.routes.processor;

import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.ContractStatus;
import com.intellecteu.onesource.integration.model.spire.RerateTrade;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.RerateTradeService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RerateProcessor {

    private final BackOfficeService lenderBackOfficeService;
    private final BackOfficeService borrowerBackOfficeService;
    private final ContractService contractService;
    private final RerateTradeService rerateTradeService;

    @Autowired
    public RerateProcessor(BackOfficeService lenderBackOfficeService, BackOfficeService borrowerBackOfficeService,
        ContractService contractService, RerateTradeService rerateTradeService) {
        this.lenderBackOfficeService = lenderBackOfficeService;
        this.borrowerBackOfficeService = borrowerBackOfficeService;
        this.contractService = contractService;
        this.rerateTradeService = rerateTradeService;
    }

    public void fetchNewTradeOut() {
        Optional<Long> lastTradeId = rerateTradeService.getMaxTradeId();
        List<Contract> openedContracts = contractService.findAllByContractStatus(ContractStatus.OPEN);
        List<String> matchingSpirePositionIds = openedContracts.stream().map(Contract::getMatchingSpirePositionId)
            .collect(
                Collectors.toList());
        List<RerateTrade> rerateTradeList = new ArrayList<>();
        rerateTradeList.addAll(
            lenderBackOfficeService.getNewBackOfficeTradeEvents(lastTradeId, matchingSpirePositionIds));
        rerateTradeList.addAll(
            borrowerBackOfficeService.getNewBackOfficeTradeEvents(lastTradeId, matchingSpirePositionIds));
        rerateTradeService.save(rerateTradeList);
    }
}
