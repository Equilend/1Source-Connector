package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.kafka.dto.RecallInstructionDTO;
import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.model.backoffice.Recall;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.RecallRepository;
import com.intellecteu.onesource.integration.repository.entity.backoffice.RecallEntity;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecallService {

    private final RecallRepository recallRepository;
    private final BackOfficeMapper mapper;


    /**
     * Create an internal recall record from the recall instruction and save to the database.
     *
     * @param recallInstructionDTO instruction
     * @return Recall model
     */
    @Transactional
    public Recall createRecall(RecallInstructionDTO recallInstructionDTO) {
        Recall recall = createRecallFromInstruction(recallInstructionDTO);
        log.debug("Saving Recall from instruction id: {}", recallInstructionDTO.getInstructionId());
        final RecallEntity savedRecallEntity = recallRepository.save(mapper.toEntity(recall));
        return mapper.toModel(savedRecallEntity);
    }

    private Recall createRecallFromInstruction(RecallInstructionDTO recallInstructionDTO) {
        final Recall recall = mapper.toModel(recallInstructionDTO);
        recall.setCreationDateTime(LocalDateTime.now());
        recall.setProcessingStatus(ProcessingStatus.CREATED);
        return recall;
    }

    /**
     * Update last update date time and persist a recall.
     *
     * @param recall Recall model
     * @return Recall persisted model
     */
    public Recall save(Recall recall) {
        recall.setLastUpdateDateTime(LocalDateTime.now());
        final RecallEntity persistedEntity = recallRepository.save(mapper.toEntity(recall));
        log.debug("Recall with recallId:{} was saved", persistedEntity.getRecallId());
        return mapper.toModel(persistedEntity);
    }
}
