package com.intellecteu.onesource.integration.kafka.mapper;

import com.intellecteu.onesource.integration.kafka.dto.NackInstructionDTO;
import com.intellecteu.onesource.integration.model.integrationtoolkit.NackInstruction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class KafkaNackInstructionMapper {

    public abstract NackInstruction toModel(NackInstructionDTO nackInstructionDTO);
}
