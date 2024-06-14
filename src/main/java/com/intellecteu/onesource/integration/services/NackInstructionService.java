package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.mapper.NackInstructionMapper;
import com.intellecteu.onesource.integration.model.integrationtoolkit.NackInstruction;
import com.intellecteu.onesource.integration.repository.NackInstructionRepository;
import com.intellecteu.onesource.integration.repository.entity.toolkit.NackInstructionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NackInstructionService {

    private final NackInstructionRepository nackInstructionRepository;
    private final NackInstructionMapper mapper;

    public NackInstruction save(NackInstruction nackInstruction) {
        NackInstructionEntity nackInstructionEntity = mapper.toEntity(nackInstruction);
        log.debug("Saving correctionInstructionEntity with id: {}", nackInstructionEntity.getNackInstructionId());
        nackInstructionEntity = nackInstructionRepository.save(nackInstructionEntity);
        return mapper.toModel(nackInstructionEntity);
    }
}
