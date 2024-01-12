package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PositionService {

    private final PositionRepository positionRepository;

    @Autowired
    public PositionService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    public Position savePosition(Position position) {
        position.setLastUpdateDateTime(LocalDateTime.now());
        return positionRepository.save(position);
    }

    public Optional<Position> findByVenueRefId(String venueRefId) {
        return positionRepository.findByVenueRefId(venueRefId);
    }

    public List<Position> getByMatchingTradeAgreementId(String agreementId) {
        return positionRepository.findByMatching1SourceTradeAgreementId(agreementId);
    }

    public Position getById(Long positionId) {
        return positionRepository.getById(String.valueOf(positionId));
    }

    public List<Position> savePositions(List<Position> positions) {
        positions.forEach(position -> position.setLastUpdateDateTime(LocalDateTime.now()));
        return positionRepository.saveAll(positions);
    }

    public List<Position> findAllNotCanceledAndSettled() {
        return positionRepository.findAllNotCanceledAndSettled();
    }

    public Optional<String> getMaxPositionId() {
        List<Position> storedPositions = positionRepository.findAll();
        log.debug("Found {} positions. Creating loan contracts.", storedPositions.size());
        return storedPositions.stream().map(Position::getPositionId)
            .max(Comparator.comparingInt(Integer::parseInt));
    }

}
