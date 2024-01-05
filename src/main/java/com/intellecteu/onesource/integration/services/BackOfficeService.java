package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_NEW_POSITIONS_PENDING_CONFIRMATION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.exception.PositionRetrievementException;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

@Service
@Slf4j
@RequiredArgsConstructor
public class BackOfficeService {

    private static final String STARTING_POSITION_ID = "0";

    private final SpireApiService spireApiService;
    private final SpireMapper spireMapper;
    private final CloudEventRecordService cloudEventRecordService;

    public List<Position> getNewSpirePositions(Optional<String> lastPositionId) {
        String maxPositionId = lastPositionId.orElse(STARTING_POSITION_ID);
        try {
            List<PositionDto> positionDtoList = spireApiService.requestNewPositions(maxPositionId);
            return positionDtoList.stream().map(spireMapper::toPosition).collect(Collectors.toList());
        } catch (RestClientException e) {
            if (e instanceof HttpStatusCodeException exception) {
                log.warn("SPIRE error response for {} subprocess. Details: {}",
                    GET_NEW_POSITIONS_PENDING_CONFIRMATION, exception.getStatusCode());
                recordExceptionEvent(exception);
            }
            return List.of();
        }
    }

    private void recordExceptionEvent(HttpStatusCodeException exception) {
        if (Set.of(CREATED, UNAUTHORIZED, FORBIDDEN).contains(exception.getStatusCode())) {
            var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
            final CloudEventBuildRequest recordRequest = eventBuilder.buildExceptionRequest(exception,
                IntegrationSubProcess.GET_NEW_POSITIONS_PENDING_CONFIRMATION);
            cloudEventRecordService.record(recordRequest);
        }
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
