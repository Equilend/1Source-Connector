package com.intellecteu.onesource.integration.config;

import com.intellecteu.onesource.integration.model.enums.FlowStatus;
import com.intellecteu.onesource.integration.routes.contract_initiation_without_trade.processor.strategy.agreement.AgreementProcessFlowStrategy;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AgreementProcessStrategyConfig {

    private final List<AgreementProcessFlowStrategy> agreementFlowStrategies;

    @Bean
    public Map<FlowStatus, AgreementProcessFlowStrategy> agreementProcessByFlow() {
        Map<FlowStatus, AgreementProcessFlowStrategy> agreementFlowToProcess = new EnumMap<>(FlowStatus.class);
        agreementFlowStrategies.forEach(strategy -> agreementFlowToProcess.put(strategy.getProcessFlow(), strategy));
        return agreementFlowToProcess;
    }
}
