package com.intellecteu.onesource.integration.api.services.declineinstructions;

import com.intellecteu.onesource.integration.api.dto.DeclineInstructionDto;
import com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface DeclineInstructionApiMapper {

    DeclineInstructionEntity toEntity(DeclineInstructionDto model);

    DeclineInstructionDto toDto(DeclineInstructionEntity entity);
}
