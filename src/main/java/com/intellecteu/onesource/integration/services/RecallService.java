package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.constant.PositionConstant.SPIRE_RECALL_SPLIT;

import com.intellecteu.onesource.integration.kafka.dto.RecallInstructionDTO;
import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.backoffice.RecallSpire;
import com.intellecteu.onesource.integration.model.backoffice.RecallSpireInstruction;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.Recall1Source;
import com.intellecteu.onesource.integration.repository.Recall1SourceRepository;
import com.intellecteu.onesource.integration.repository.RecallSpireInstructionRepository;
import com.intellecteu.onesource.integration.repository.RecallSpireRepository;
import com.intellecteu.onesource.integration.repository.entity.backoffice.RecallSpireEntity;
import com.intellecteu.onesource.integration.repository.entity.backoffice.RecallSpireInstructionEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.Recall1SourceEntity;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecallService {

    private final RecallSpireRepository recallSpireRepository;
    private final Recall1SourceRepository recall1SourceRepository;
    private final RecallSpireInstructionRepository recallSpireInstructionRepository;
    private final BackOfficeMapper backOfficeMapper;
    private final OneSourceMapper oneSourceMapper;


    /**
     * Create an internal recall record from the recall instruction and save to the database.
     *
     * @param recallInstructionDTO instruction
     * @return RecallSpire model
     */
    @Transactional
    public RecallSpire createRecall(RecallInstructionDTO recallInstructionDTO) {
        RecallSpire recallSpire = createRecallFromInstruction(recallInstructionDTO);
        log.debug("Saving RecallSpire from instruction id: {}", recallInstructionDTO.getInstructionId());
        final RecallSpireEntity savedRecallSpireEntity = recallSpireRepository.save(backOfficeMapper.toEntity(
            recallSpire));
        return backOfficeMapper.toModel(savedRecallSpireEntity);
    }

    private RecallSpire createRecallFromInstruction(RecallInstructionDTO recallInstructionDTO) {
        final RecallSpire recallSpire = backOfficeMapper.toModel(recallInstructionDTO);
        recallSpire.setCreationDateTime(LocalDateTime.now());
        recallSpire.setProcessingStatus(ProcessingStatus.CREATED);
        return recallSpire;
    }

    /**
     * Lookup for a matching SPIRe recall with a processing status "CREATED" by the Lender's venuePartyRefKey.
     * VenuePartyRefKey should be equal to RecallSpire.recallId-RecallSpire.relatedPositionId
     *
     * @param recall1Source Recall1Source an initial parameter
     * @return Optional of RecallSpire or an empty Optional if not found
     */
    @Transactional
    public Optional<RecallSpire> getMatchedSpireRecall(Recall1Source recall1Source) {
        final String venuePartyRefKey = recall1Source.getVenuePartyRefKeyByRole(PartyRole.LENDER);
        if (venuePartyRefKey == null) {
            throw new EntityNotFoundException();
        }
        log.debug("Search for Recall SPIRE by venuePartyRefKey:{}", venuePartyRefKey);
        final String[] spireRecallIdAndRelatedPositionId = venuePartyRefKey.split(SPIRE_RECALL_SPLIT);
        Long spireRecallId = Long.valueOf(spireRecallIdAndRelatedPositionId[0]);
        Long relatedPositionId = Long.valueOf(spireRecallIdAndRelatedPositionId[1]);
        final Optional<RecallSpireEntity> entityOptional = recallSpireRepository.findByRecallIdAndRelatedPositionId(
            spireRecallId, relatedPositionId);
        return entityOptional.map(backOfficeMapper::toModel);

    }

    /**
     * Persist a SPIRE recall. The last update timestamp is updated during the persist process.
     *
     * @param recallSpire RecallSpire model
     * @return RecallSpire persisted model
     */
    @Transactional
    public RecallSpire save(RecallSpire recallSpire) {
        final RecallSpireEntity persistedEntity = recallSpireRepository.save(backOfficeMapper.toEntity(recallSpire));
        log.debug("RecallSpire with recallId:{} was saved", persistedEntity.getRecallId());
        return backOfficeMapper.toModel(persistedEntity);
    }

    /**
     * Persist a 1Source recall.
     *
     * @param recall1Source Recall1Source model
     * @return Recall1Source persisted model
     */
    @Transactional
    public Recall1Source save(Recall1Source recall1Source) {
        final Recall1SourceEntity recall1SourceEntity = recall1SourceRepository.save(oneSourceMapper.toEntity(
            recall1Source));
        log.debug("Recall1Source with recallId:{} was saved", recall1SourceEntity.getRecallId());
        return oneSourceMapper.toModel(recall1SourceEntity);
    }

    @Transactional
    public RecallSpireInstruction save(RecallSpireInstruction instruction) {
        final RecallSpireInstructionEntity entity = recallSpireInstructionRepository.save(
            backOfficeMapper.toEntity(instruction));
        log.debug("RecallSpireInstruction with id:{} was saved", instruction.getInstructionId());
        return backOfficeMapper.toModel(entity);
    }

    @Transactional
    public void saveRecallInstruction(RecallInstructionDTO recallInstruction) {
        log.debug("Saving recall instruction: {} with type: {}", recallInstruction.getInstructionId(),
            recallInstruction.getInstructionType());
        final RecallSpireInstructionEntity entity = backOfficeMapper.toEntity(recallInstruction);
        recallSpireInstructionRepository.save(entity);
    }

    @Transactional
    public Optional<RecallSpire> getSpireRecallByIdAndPosition(Long spireRecallId, Long relatedPositionId) {
        return recallSpireRepository.findByRecallIdAndRelatedPositionId(spireRecallId, relatedPositionId)
            .map(backOfficeMapper::toModel);
    }
}
