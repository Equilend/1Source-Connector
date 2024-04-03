package com.intellecteu.onesource.integration.mapper;

import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.SystemEventData;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventMetadata;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudSystemEvent;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.IntegrationCloudEvent;
import com.intellecteu.onesource.integration.repository.entity.onesource.SystemEventDataEntity;
import com.intellecteu.onesource.integration.repository.entity.toolkit.CloudSystemEventEntity;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = JsonMapper.class)
@Slf4j
public abstract class CloudSystemEventMapper {

    protected JsonMapper jsonMapper;

    @Autowired
    public void setJsonMapper(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Mapping(target = "eventData", source = "data")
    public abstract CloudSystemEvent toCloudEvent(CloudSystemEventEntity cloudEventEntity);

    /**
     * Bidirectional mapping for SystemEventDataEntity and CloudSystemEventEntity
     * should handle initiation of the inner object.
     * The setter entity.setData(dataEntity) should be after the initiation of the cloudEventEntity
     *
     * @param cloudEvent CloudSystemEvent model
     * @return CloudSystemEventEntity entity
     */
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
            .processingStatus(cloudEvent.getProcessingStatus())
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
        return jsonMapper.recordToJson(event);
    }

}
