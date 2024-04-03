package com.intellecteu.onesource.integration.kafka.mapper;

import com.intellecteu.onesource.integration.kafka.dto.CorrectionInstructionDTO;
import com.intellecteu.onesource.integration.model.integrationtoolkit.CorrectionInstruction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class KafkaCorrectionInstructionMapper {

    public abstract CorrectionInstruction toModel(CorrectionInstructionDTO correctionInstructionDTO);

}
