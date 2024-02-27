package com.intellecteu.onesource.integration.routes.common.processor;

import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.CREATED;

import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.PositionService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PositionListenerProcessor {

    private final PositionService positionService;
    private final BackOfficeService backOfficeService;

    @Autowired
    public PositionListenerProcessor(PositionService positionService, BackOfficeService backOfficeService) {
        this.positionService = positionService;
        this.backOfficeService = backOfficeService;
    }

    public void fetchNewPositions() {
        Optional<String> maxPositionId = positionService.getMaxPositionId();
        List<Position> newSpirePositions = backOfficeService.getNewSpirePositions(maxPositionId);
        newSpirePositions.forEach(position -> {
            position.setVenueRefId(position.getCustomValue2());
            position.setCreationDatetime(LocalDateTime.now());
            position.setProcessingStatus(CREATED);
        });
        positionService.savePositions(newSpirePositions);
    }

    public void updateIfMatched(Position position) {

    }

}
