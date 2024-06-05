package com.intellecteu.onesource.integration.api.services.nackinstructions;

import com.intellecteu.onesource.integration.api.dto.NackInstructionDTO;
import com.intellecteu.onesource.integration.model.integrationtoolkit.NackInstruction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class NackInstructionApiMapper {

    public abstract NackInstruction toModel(NackInstructionDTO nackInstructionDTO);

    public abstract NackInstructionDTO toDTO(NackInstruction nackInstruction);

}
