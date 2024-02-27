package com.intellecteu.onesource.integration.config;

import com.intellecteu.onesource.integration.model.enums.FlowStatus;
import com.intellecteu.onesource.integration.routes.contract_initiation.delegate_flow.processor.strategy.contract.ContractProcessFlowStrategy;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ContractProcessStrategyConfig {

    private final List<ContractProcessFlowStrategy> contractFlowStrategies;

    @Bean
    public Map<FlowStatus, ContractProcessFlowStrategy> contractProcessByFlow() {
        Map<FlowStatus, ContractProcessFlowStrategy> contractFlowToProcess = new EnumMap<>(FlowStatus.class);
        contractFlowStrategies.forEach(strategy -> contractFlowToProcess.put(strategy.getProcessFlow(), strategy));
        return contractFlowToProcess;
    }
}
