package com.intellecteu.onesource.integration.config;

import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.services.record.IntegrationCloudEventBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CloudEventFactoryConfig {

  private final List<IntegrationCloudEventBuilder> cloudEventBuilderList;

  @Bean
  public Map<IntegrationProcess, IntegrationCloudEventBuilder> recordBuilderByProcess() {
    Map<IntegrationProcess, IntegrationCloudEventBuilder> recordBuilderByProcess =
        new EnumMap<>(IntegrationProcess.class);
    cloudEventBuilderList.forEach(builder -> recordBuilderByProcess.put(builder.getVersion(), builder));
    return recordBuilderByProcess;
  }
}
