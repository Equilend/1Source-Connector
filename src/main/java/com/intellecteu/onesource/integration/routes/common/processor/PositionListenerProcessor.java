package com.intellecteu.onesource.integration.routes.common.processor;

import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;

import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.PositionService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PositionListenerProcessor {

    private final PositionService positionService;
    private final BackOfficeService borrowerBackOfficeService;
    private final BackOfficeService lenderBackOfficeService;

    @Autowired
    public PositionListenerProcessor(PositionService positionService, BackOfficeService borrowerBackOfficeService,
        BackOfficeService lenderBackOfficeService) {
        this.positionService = positionService;
        this.borrowerBackOfficeService = borrowerBackOfficeService;
        this.lenderBackOfficeService = lenderBackOfficeService;
    }

    public void fetchNewPositions() {
        Optional<String> maxPositionId = positionService.getMaxPositionId();
        List<Position> newSpirePositions = new ArrayList<>();
        newSpirePositions.addAll(borrowerBackOfficeService.getNewSpirePositions(maxPositionId));
        newSpirePositions.addAll(lenderBackOfficeService.getNewSpirePositions(maxPositionId));
        newSpirePositions.forEach(position -> {
            position.setVenueRefId(position.getCustomValue2());
            position.setProcessingStatus(CREATED);
        });
        positionService.savePositions(newSpirePositions);
    }

}
