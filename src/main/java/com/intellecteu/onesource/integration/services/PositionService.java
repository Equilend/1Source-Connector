package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.constant.PositionConstant.BORROWER_POSITION_TYPE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.LENDER_POSITION_TYPE;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.CANCELLED;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.FAILED;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PositionService {

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
    @Transactional
    public Position savePosition(Position position) {
        position.setLastUpdateDateTime(LocalDateTime.now());
        PositionEntity positionEntity = positionRepository.save(backOfficeMapper.toEntity(position));
        log.debug("Position with id={} with processing status={} was saved.",
            positionEntity.getPositionId(), position.getProcessingStatus());
        return backOfficeMapper.toModel(positionEntity);
    }

    public List<Position> getByMatchingTradeAgreementId(String agreementId) {
        return positionRepository.findByMatching1SourceTradeAgreementId(agreementId).stream()
            .map(backOfficeMapper::toModel).collect(
                Collectors.toList());
    }

    public Optional<Position> getByPositionId(Long positionId) {
        Optional<PositionEntity> positionEntity = positionRepository.getByPositionId(positionId);
        return positionEntity.map(backOfficeMapper::toModel);
    }

    public Optional<Position> getByPositionIdAndRole(Long positionId, PartyRole partyRole) {
        String positionType = null;
        if (PartyRole.BORROWER == partyRole) {
            positionType = BORROWER_POSITION_TYPE;
        }
        if (PartyRole.LENDER == partyRole) {
            positionType = LENDER_POSITION_TYPE;
        }
        if (positionType == null) {
            return Optional.empty();
        }
        Optional<PositionEntity> position = positionRepository.getByPositionIdAndType(positionId, positionType);
        return position.map(backOfficeMapper::toModel);
    }

    public Optional<Position> getNotMatchedByPositionId(Long positionId) {
        Optional<PositionEntity> positionEntity = positionRepository.getNotMatchedByPositionId(positionId);
        return positionEntity.map(backOfficeMapper::toModel);
    }

    public Set<Position> getNotMatched() {
        List<PositionEntity> positionList = positionRepository.getNotMatchedForBorrower();
        return positionList.stream().map(backOfficeMapper::toModel).collect(Collectors.toSet());
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

    /**
     * Return the latest trade id for the persisted positions.
     * If the list of persisted positions is empty, "0" will be returned.
     *
     * @return String last trade id of persisted positions or "0"
     */
    public String getMaxTradeId() { // todo rework change logic with SQL query
        List<PositionEntity> storedPositions = positionRepository.findAll();
        log.debug("Found {} positions. Getting the latest id recorded.", storedPositions.size());
        return storedPositions.stream()
            .map(PositionEntity::getTradeId)
            .filter(Objects::nonNull)
            .max(Comparator.naturalOrder())
            .map(String::valueOf)
            .orElse("0");
    }

    public List<Position> getAllByPositionStatus(String status) {
        return positionRepository.findAllByPositionStatus(status).stream()
            .map(backOfficeMapper::toModel).toList();
    }
}
