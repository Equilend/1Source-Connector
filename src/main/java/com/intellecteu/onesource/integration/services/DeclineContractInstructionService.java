package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.repository.DeclineContractInstructionRepository;
import com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeclineContractInstructionService {

    private final DeclineContractInstructionRepository declineContractInstructionRepository;

    @Transactional
    public DeclineInstructionEntity save(DeclineInstructionEntity declineInstructionEntity) {
        log.debug("Saving declineInstruction with id: {}", declineInstructionEntity.getDeclineInstructionId());
        return declineContractInstructionRepository.save(declineInstructionEntity);
    }

}
