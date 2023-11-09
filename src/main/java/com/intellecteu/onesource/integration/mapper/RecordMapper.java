package com.intellecteu.onesource.integration.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellecteu.onesource.integration.dto.record.IntegrationCloudEvent;
import com.intellecteu.onesource.integration.model.CloudEventEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecordMapper {

  private final ObjectMapper objectMapper;

  public CloudEventEntity toCloudEventEntity(IntegrationCloudEvent record) {
    final CloudEventEntity entity = objectMapper.convertValue(record.getMetadata(), CloudEventEntity.class);
    try {
      final String data = objectMapper.writeValueAsString(record.getData());
      entity.setData(data);
    } catch (JsonProcessingException e) {
      log.warn("Couldn't write value to CloudEventEntity id: {}", entity.getId());
    }
    return entity;
  }
}
