package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.CANCELLED;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.FAILED;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PositionService {

    public final static Set<String> CANCEL_POSITION_STATUSES = Set.of(CANCELLED.getValue(), FAILED.getValue());

    private final PositionRepository positionRepository;
    private final BackOfficeMapper backOfficeMapper;

    @Autowired
    public PositionService(PositionRepository positionRepository, BackOfficeMapper backOfficeMapper) {
        this.positionRepository = positionRepository;
        this.backOfficeMapper = backOfficeMapper;
    }

    /**
     * Save position and update the last update date time for it
     *
     * @param position Position model
     * @return representation of persisted Position entity
     */
    public Position savePosition(Position position) {
        position.setLastUpdateDateTime(LocalDateTime.now());
        PositionEntity positionEntity = positionRepository.save(backOfficeMapper.toEntity(position));
        log.debug("Position with id={} with processing status={} was saved.",
            positionEntity.getPositionId(), position.getProcessingStatus());
        return backOfficeMapper.toModel(positionEntity);
    }

    public Optional<Position> findByVenueRefId(String venueRefId) {
        return positionRepository.findByVenueRefId(venueRefId).map(backOfficeMapper::toModel);
    }

    public List<Position> getByMatchingTradeAgreementId(String agreementId) {
        return positionRepository.findByMatching1SourceTradeAgreementId(agreementId).stream()
            .map(backOfficeMapper::toModel).collect(
                Collectors.toList());
    }

    public Position getById(Long positionId) {
        PositionEntity positionEntity = positionRepository.getById(positionId);
        return backOfficeMapper.toModel(positionEntity);
    }

    public List<Position> saveAllPositions(List<Position> positions) {
        positions.forEach(position -> position.setLastUpdateDateTime(LocalDateTime.now()));
        List<PositionEntity> positionEntities = positions.stream().map(backOfficeMapper::toEntity).toList();
        positionEntities = positionRepository.saveAll(positionEntities);
        if (!positionEntities.isEmpty()) {
            String idList = positionEntities.stream()
                .map(p -> String.valueOf(p.getPositionId()))
                .collect(Collectors.joining(", "));
            log.debug("Saved {} positions. Ids: {}", positionEntities.size(), idList);
        }
        return positionEntities.stream().map(backOfficeMapper::toModel).toList();
    }

    public List<Position> findAllNotCanceledAndSettled() {
        return positionRepository.findAllNotCanceledAndSettled().stream().map(backOfficeMapper::toModel).toList();
    }

    public Optional<String> getMaxPositionId() { // todo rework change logic with SQL query
        List<PositionEntity> storedPositions = positionRepository.findAll();
        log.debug("Found {} positions. Getting the latest id recorded.", storedPositions.size());
        return storedPositions.stream()
            .map(PositionEntity::getPositionId)
            .max(Comparator.naturalOrder())
            .map(String::valueOf);
    }

    public List<Position> findAllByProcessingStatus(ProcessingStatus status) {
        return positionRepository.findAllByProcessingStatus(status).stream()
            .map(backOfficeMapper::toModel).toList();
    }
}
