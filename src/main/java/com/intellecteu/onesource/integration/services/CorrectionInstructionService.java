package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.mapper.CorrectionInstructionMapper;
import com.intellecteu.onesource.integration.model.integrationtoolkit.CorrectionInstruction;
import com.intellecteu.onesource.integration.repository.CorrectionInstructionRepository;
import com.intellecteu.onesource.integration.repository.entity.toolkit.CorrectionInstructionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CorrectionInstructionService {

    private final CorrectionInstructionRepository correctionInstructionRepository;
    private final CorrectionInstructionMapper mapper;

    public CorrectionInstruction save(CorrectionInstruction correctionInstruction) {
        CorrectionInstructionEntity correctionInstructionEntity = mapper.toEntity(correctionInstruction);
        log.debug("Saving correctionInstructionEntity with id: {}", correctionInstructionEntity.getInstructionId());
        correctionInstructionEntity = correctionInstructionRepository.save(correctionInstructionEntity);
        return mapper.toModel(correctionInstructionEntity);
    }

}
