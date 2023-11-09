package com.intellecteu.onesource.integration.config;

import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.services.processor.strategy.agreement.AgreementProcessFlowStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
