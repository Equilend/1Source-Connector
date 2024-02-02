package com.intellecteu.onesource.integration.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellecteu.onesource.integration.dto.record.CloudEvent;
import com.intellecteu.onesource.integration.dto.record.IntegrationCloudEvent;
import com.intellecteu.onesource.integration.exception.ConvertException;
import com.intellecteu.onesource.integration.model.onesource.CloudEventEntity;
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

    public String toJson(CloudEvent event) throws JsonProcessingException {
        return objectMapper.writeValueAsString(event);
    }

    public CloudEvent toCloudEvent(CloudEventEntity entity) {
        try {
            return CloudEvent.builder()
                .id(entity.getId())
                .specVersion(entity.getSpecVersion())
                .type(entity.getType())
                .source(entity.getSource())
                .subject(entity.getSubject())
                .time(entity.getTime())
                .relatedProcess(entity.getRelatedProcess())
                .relatedSubProcess(entity.getRelatedSubProcess())
                .dataContentType(entity.getDataContentType())
                .eventData(entity.getData())
                .build();
        } catch (Exception e) {
            String id = entity == null ? "null" : entity.getId();
            log.warn("Couldn't create CloudEvent from CloudEventEntity, id: {}", id);
            throw new ConvertException(e);
        }
    }
}
