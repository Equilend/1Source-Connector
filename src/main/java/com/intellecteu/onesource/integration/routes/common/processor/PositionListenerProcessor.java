package com.intellecteu.onesource.integration.routes.common.processor;

import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;

import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.PositionService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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
        String maxTradeId = positionService.getMaxTradeId();
        List<Position> newSpirePositions = backOfficeService.getNewSpirePositions(maxTradeId);
        if (!newSpirePositions.isEmpty()) {
            log.debug("{} new positions were retrieved. Ids:{}", newSpirePositions.size(),
            newSpirePositions.stream().map(Position::getPositionId).map(String::valueOf)
                .collect(Collectors.joining(",")));
        }
        newSpirePositions.forEach(position -> {
            position.setVenueRefId(position.getCustomValue2());
            position.setCreationDatetime(LocalDateTime.now());
            position.setProcessingStatus(CREATED);
        });
        positionService.saveAllPositions(newSpirePositions);
    }

}
