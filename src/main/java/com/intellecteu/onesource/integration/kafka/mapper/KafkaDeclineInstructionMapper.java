package com.intellecteu.onesource.integration.kafka.mapper;

import com.intellecteu.onesource.integration.kafka.dto.DeclineInstructionDTO;
import com.intellecteu.onesource.integration.model.integrationtoolkit.DeclineInstruction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class KafkaDeclineInstructionMapper {

    public abstract DeclineInstruction toModel(DeclineInstructionDTO declineInstructionDTO);

}
