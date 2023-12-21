package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.exception.PositionRetrievementException;
import com.intellecteu.onesource.integration.mapper.PositionMapper;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PositionService {

    private static final String STARTING_POSITION_ID = "0";

    private PositionRepository positionRepository;
    private SpireApiService spireApiService;
    private PositionMapper positionMapper;

    @Autowired
    public PositionService(PositionRepository positionRepository, SpireApiService spireApiService,
        PositionMapper positionMapper) {
        this.positionRepository = positionRepository;
        this.spireApiService = spireApiService;
        this.positionMapper = positionMapper;
    }

    public List<Position> getNewSpirePositions() {
        String maxPositionId = getMaxPositionId().orElse(STARTING_POSITION_ID);
        List<PositionDto> positionDtoList = spireApiService.requestNewPositions(maxPositionId);
        return positionDtoList.stream().map(positionMapper::toPosition).collect(Collectors.toList());
    }

    public Optional<Position> fetchSpirePositionByVenueRefId(String venueRefId) {
        try {
            return Optional.of(positionMapper.toPosition(spireApiService.requestPositionByVenueRefId(venueRefId)));
        } catch (PositionRetrievementException e) {
            log.error("Retrieve position by venueRefId.", e);
        }
        return Optional.empty();
    }

    public Position getById(Long positionId) {
        return positionRepository.getById(positionId);
    }

    public Position savePosition(Position position) {
        position.setLastUpdateDateTime(LocalDateTime.now());
        return positionRepository.save(position);
    }

    public List<Position> savePositions(List<Position> positions) {
        positions.forEach(position -> position.setLastUpdateDateTime(LocalDateTime.now()));
        return positionRepository.saveAll(positions);
    }

    public Optional<String> getMaxPositionId() {
        List<Position> storedPositions = positionRepository.findAll();
        log.debug("Found {} positions. Creating loan contracts.", storedPositions.size());
        return storedPositions.stream().map(Position::getPositionId)
            .max(Comparator.comparingInt(positionId -> Integer.parseInt(positionId)));
    }

}
