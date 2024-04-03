package com.intellecteu.onesource.integration.mapper;

import com.intellecteu.onesource.integration.model.integrationtoolkit.CorrectionInstruction;
import com.intellecteu.onesource.integration.repository.entity.toolkit.CorrectionInstructionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CorrectionInstructionMapper {

    public abstract CorrectionInstruction toModel(CorrectionInstructionEntity correctionInstructionEntity);

    public abstract CorrectionInstructionEntity toEntity(CorrectionInstruction correctionInstruction);

}
