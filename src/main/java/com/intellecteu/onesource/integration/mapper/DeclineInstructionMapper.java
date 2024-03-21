package com.intellecteu.onesource.integration.mapper;

import com.intellecteu.onesource.integration.model.integrationtoolkit.DeclineInstruction;
import com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class DeclineInstructionMapper {

    public abstract DeclineInstruction toModel(DeclineInstructionEntity declineInstructionEntity);

    public abstract DeclineInstructionEntity toEntity(DeclineInstruction declineInstruction);
}
