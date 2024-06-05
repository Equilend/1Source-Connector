package com.intellecteu.onesource.integration.mapper;

import com.intellecteu.onesource.integration.model.integrationtoolkit.NackInstruction;
import com.intellecteu.onesource.integration.repository.entity.toolkit.NackInstructionEntity;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class NackInstructionMapper {

    public abstract NackInstruction toModel(NackInstructionEntity nackInstructionEntity);

    public abstract List<NackInstruction> toModel(List<NackInstructionEntity> nackInstructionEntities);

    public abstract NackInstructionEntity toEntity(NackInstruction nackInstruction);
}
