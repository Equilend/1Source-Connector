package com.intellecteu.onesource.integration.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.SystemEventData;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventMetadata;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudSystemEvent;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.IntegrationCloudEvent;
import com.intellecteu.onesource.integration.repository.entity.onesource.SystemEventDataEntity;
import com.intellecteu.onesource.integration.repository.entity.toolkit.CloudSystemEventEntity;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class CloudSystemEventMapper {

    @Mapping(target = "eventData", source = "cloudEventEntity.data")
    public abstract CloudSystemEvent toCloudEvent(CloudSystemEventEntity cloudEventEntity);

    public CloudSystemEventEntity toCloudEventEntity(CloudSystemEvent cloudEvent) {
        SystemEventDataEntity dataEntity = toSystemEventEntity(cloudEvent.getEventData());
        final CloudSystemEventEntity entity = CloudSystemEventEntity.builder()
            .id(cloudEvent.getId())
            .specVersion(cloudEvent.getSpecVersion())
            .type(cloudEvent.getType())
            .source(cloudEvent.getSource())
            .subject(cloudEvent.getSubject())
            .time(cloudEvent.getTime())
            .relatedProcess(cloudEvent.getRelatedProcess())
            .relatedSubProcess(cloudEvent.getRelatedSubProcess())
            .dataContentType(cloudEvent.getDataContentType())
            .build();
        entity.setData(dataEntity);
        return entity;
    }

    public CloudSystemEventEntity toCloudEventEntity(IntegrationCloudEvent cloudEvent) {
        CloudSystemEventEntity eventEntity = toCloudEventEntity(cloudEvent.getMetadata());
        SystemEventDataEntity dataEntity = toSystemEventEntity(cloudEvent.getData());
        eventEntity.setData(dataEntity);
        return eventEntity;
    }

    @Mapping(target = "source", expression = "java(metadata.getSource().toString())")
    public abstract CloudSystemEventEntity toCloudEventEntity(CloudEventMetadata metadata);

    public abstract SystemEventDataEntity toSystemEventEntity(SystemEventData data);

    public String toJson(CloudSystemEvent event) {
        var objectMapper = new ObjectMapper();
        try {
            final String metadata = objectMapper.writeValueAsString(event.toString());
            final String data = objectMapper.writeValueAsString(event.getEventData().toString());
            return metadata + data;
        } catch (IOException e) {
            log.debug("Error on parsing: {}", e.getMessage());
        }
        return null;
    }
}
