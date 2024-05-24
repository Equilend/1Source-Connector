package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.kafka.dto.RecallInstructionDTO;
import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.backoffice.Recall;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.Recall1Source;
import com.intellecteu.onesource.integration.repository.Recall1SourceRepository;
import com.intellecteu.onesource.integration.repository.RecallRepository;
import com.intellecteu.onesource.integration.repository.entity.backoffice.RecallEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.Recall1SourceEntity;
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
    private final Recall1SourceRepository recall1SourceRepository;
    private final BackOfficeMapper backOfficeMapper;
    private final OneSourceMapper oneSourceMapper;


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
        final RecallEntity savedRecallEntity = recallRepository.save(backOfficeMapper.toEntity(recall));
        return backOfficeMapper.toModel(savedRecallEntity);
    }

    private Recall createRecallFromInstruction(RecallInstructionDTO recallInstructionDTO) {
        final Recall recall = backOfficeMapper.toModel(recallInstructionDTO);
        recall.setCreationDateTime(LocalDateTime.now());
        recall.setProcessingStatus(ProcessingStatus.CREATED);
        return recall;
    }

    /**
     * Persist a SPIRE recall.
     *
     * @param recall Recall model
     * @return Recall persisted model
     */
    public Recall save(Recall recall) {
        final RecallEntity persistedEntity = recallRepository.save(backOfficeMapper.toEntity(recall));
        log.debug("Recall with recallId:{} was saved", persistedEntity.getRecallId());
        return backOfficeMapper.toModel(persistedEntity);
    }

    /**
     * Persist a 1Source recall.
     *
     * @param recall1Source Recall1Source model
     * @return Recall1Source persisted model
     */
    public Recall1Source save(Recall1Source recall1Source) {
        final Recall1SourceEntity recall1SourceEntity = recall1SourceRepository.save(oneSourceMapper.toEntity(
            recall1Source));
        log.debug("Recall with recallId:{} was saved", recall1SourceEntity.getRecallId());
        return oneSourceMapper.toModel(recall1SourceEntity);
    }
}
