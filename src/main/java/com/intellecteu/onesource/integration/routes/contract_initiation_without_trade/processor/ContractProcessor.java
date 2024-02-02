package com.intellecteu.onesource.integration.routes.contract_initiation_without_trade.processor;

import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.model.enums.FlowStatus;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.routes.contract_initiation_without_trade.processor.strategy.contract.ContractProcessFlowStrategy;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContractProcessor implements TradeDataProcessor {

    private final Map<FlowStatus, ContractProcessFlowStrategy> strategyByFlow;
    private final ContractRepository contractRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public void processTradeData() {
        log.info(">>>>> Starting processing trade data");
        contractRepository.findAllNotProcessed().stream()
            .map(eventMapper::toContractDto)
            .forEach(this::processContract);
        log.info("<<<<<< Finished processing trade data");
    }

    private void processContract(ContractDto contract) {
        log.debug("***** Processing Contract id: {} with status {}, processing status: {} ",
            contract.getContractId(), contract.getContractStatus(), contract.getProcessingStatus());
        var strategy = strategyByFlow.get(contract.getFlowStatus());
        if (strategy == null) {
            throw new RuntimeException(
                "Strategy is not implemented for the flow: " + contract.getFlowStatus());
        }
        strategy.process(contract);
    }
}
