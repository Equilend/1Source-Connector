package com.intellecteu.onesource.integration.api.cloudevents;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface CloudSystemEventMapper {

    @Mapping(target = "eventData", source = "cloudEventEntity.data")
    CloudSystemEvent toCloudEvent(CloudSystemEventEntity cloudEventEntity);

    @Mapping(target = "data", source = "cloudEvent.eventData")
    @Mapping(target = "processingStatus", ignore = true)
    CloudSystemEventEntity toCloudEventEntity(CloudSystemEvent cloudEvent);

}
