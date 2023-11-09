package com.intellecteu.onesource.integration.dto.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class IntegrationCloudEvent implements CloudEventRecord {

  private CloudEventMetadata metadata;
  private CloudEventData data;

}
