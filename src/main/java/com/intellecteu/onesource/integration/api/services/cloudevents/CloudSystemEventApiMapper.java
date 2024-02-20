package com.intellecteu.onesource.integration.api.services.cloudevents;

import com.intellecteu.onesource.integration.api.dto.CloudSystemEventDto;
import com.intellecteu.onesource.integration.repository.entity.toolkit.CloudSystemEventEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface CloudSystemEventApiMapper {

    CloudSystemEventDto toCloudEventDto(CloudSystemEventEntity cloudEventEntity);

}
