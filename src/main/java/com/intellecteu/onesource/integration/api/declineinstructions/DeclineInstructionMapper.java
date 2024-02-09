package com.intellecteu.onesource.integration.api.declineinstructions;

import com.intellecteu.onesource.integration.api.dto.DeclineInstruction;
import com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface DeclineInstructionMapper {

    DeclineInstructionEntity toEntity(DeclineInstruction model);

    DeclineInstruction toModel(DeclineInstructionEntity entity);
}
