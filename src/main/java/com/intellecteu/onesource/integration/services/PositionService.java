package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.constant.PositionConstant.PositionStatus.CANCEL;
import static com.intellecteu.onesource.integration.constant.PositionConstant.PositionStatus.FAILED;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.model.backoffice.Position;
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

    public final static Set<String> CANCEL_POSITION_STATUSES = Set.of(CANCEL, FAILED);

    private final PositionRepository positionRepository;
    private final BackOfficeMapper backOfficeMapper;

    @Autowired
    public PositionService(PositionRepository positionRepository, BackOfficeMapper backOfficeMapper) {
        this.positionRepository = positionRepository;
        this.backOfficeMapper = backOfficeMapper;
    }

    public Position savePosition(Position position) {
        position.setLastUpdateDateTime(LocalDateTime.now());
        PositionEntity positionEntity = positionRepository.save(backOfficeMapper.toEntity(position));
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
        PositionEntity positionEntity = positionRepository.getById(String.valueOf(positionId));
        return backOfficeMapper.toModel(positionEntity);
    }

    public List<Position> savePositions(List<Position> positions) {
        positions.forEach(position -> position.setLastUpdateDateTime(LocalDateTime.now()));
        List<PositionEntity> positionEntities = positions.stream().map(backOfficeMapper::toEntity)
            .collect(Collectors.toList());
        positionEntities = positionRepository.saveAll(positionEntities);
        return positionEntities.stream().map(backOfficeMapper::toModel).collect(Collectors.toList());
    }

    public List<Position> findAllNotCanceledAndSettled() {
        return positionRepository.findAllNotCanceledAndSettled().stream().map(backOfficeMapper::toModel).collect(
            Collectors.toList());
    }

    public Optional<String> getMaxPositionId() {
        List<PositionEntity> storedPositions = positionRepository.findAll();
        log.debug("Found {} positions. Creating loan contracts.", storedPositions.size());
        return storedPositions.stream().map(PositionEntity::getPositionId)
            .max(Comparator.comparingInt(Integer::parseInt));
    }

}
