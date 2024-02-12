package com.intellecteu.onesource.integration.mapper;

import com.intellecteu.onesource.integration.api.dto.DeclineInstruction;
import com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeclineInstructionMapper {

    DeclineInstructionEntity toEntity(DeclineInstruction model);

    DeclineInstruction toModel(DeclineInstructionEntity entity);
}
