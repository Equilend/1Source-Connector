package com.intellecteu.onesource.integration.routes.processor;

import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.routes.processor.strategy.contract.ContractProcessFlowStrategy;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.NotYetImplementedException;
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
        contractRepository.findAllNotProcessed().stream()
            .map(eventMapper::toContractDto)
            .forEach(this::processContract);
    }

    private void processContract(ContractDto contract) {
        log.debug("***** Processing Contract id: {} with status {}, processing status: {} ",
            contract.getContractId(), contract.getContractStatus(), contract.getProcessingStatus());
        var strategy = strategyByFlow.get(contract.getFlowStatus());
        if (strategy == null) {
            throw new NotYetImplementedException(
                "Strategy is not implemented for the flow: " + contract.getFlowStatus());
        }
        strategy.process(contract);
    }
}
