package com.intellecteu.onesource.integration.api.mappers;

import com.intellecteu.onesource.integration.api.entities.CloudSystemEventEntity;
import com.intellecteu.onesource.integration.api.models.CloudSystemEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CloudSystemEventMapper {

    @Mapping(target = "eventData", source = "cloudEventEntity.data")
    CloudSystemEvent toCloudEvent(CloudSystemEventEntity cloudEventEntity);

    @Mapping(target = "data", source = "cloudEvent.eventData")
    @Mapping(target = "processingStatus", ignore = true)
    CloudSystemEventEntity toCloudEventEntity(CloudSystemEvent cloudEvent);

}
