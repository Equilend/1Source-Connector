package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.exception.PositionRetrievementException;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.spire.Position;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BackOfficeService {
    private static final String STARTING_POSITION_ID = "0";

    private final SpireApiService spireApiService;
    private final SpireMapper spireMapper;

    @Autowired
    public BackOfficeService(SpireApiService spireApiService, SpireMapper spireMapper) {
        this.spireApiService = spireApiService;
        this.spireMapper = spireMapper;
    }

    public List<Position> getNewSpirePositions(Optional<String> lastPositionId) {
        String maxPositionId = lastPositionId.orElse(STARTING_POSITION_ID);
        List<PositionDto> positionDtoList = spireApiService.requestNewPositions(maxPositionId);
        return positionDtoList.stream().map(spireMapper::toPosition).collect(Collectors.toList());
    }

    public Optional<Position> fetchSpirePositionByVenueRefId(String venueRefId) {
        try {
            return Optional.of(spireMapper.toPosition(spireApiService.requestPositionByVenueRefId(venueRefId)));
        } catch (PositionRetrievementException e) {
            log.error("Retrieve position by venueRefId.", e);
        }
        return Optional.empty();
    }

}
