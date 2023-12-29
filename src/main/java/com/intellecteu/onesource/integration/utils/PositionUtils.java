package com.intellecteu.onesource.integration.utils;

import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.spire.Position;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class PositionUtils {

    public static void processPosition(Position position, ProcessingStatus status) {
        position.setProcessingStatus(status);
        position.setLastUpdateDateTime(LocalDateTime.now());
    }

    public static void processPositionDto(PositionDto position, ProcessingStatus status) {
        position.setProcessingStatus(status);
        position.setLastUpdateDateTime(LocalDateTime.now());
    }
}
