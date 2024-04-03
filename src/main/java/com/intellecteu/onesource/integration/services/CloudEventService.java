package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventProcessingStatus.FAILED;

import com.intellecteu.onesource.integration.exception.ConvertException;
import com.intellecteu.onesource.integration.mapper.CloudSystemEventMapper;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudSystemEvent;
import com.intellecteu.onesource.integration.repository.CloudEventRepository;
import com.intellecteu.onesource.integration.repository.entity.toolkit.CloudSystemEventEntity;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CloudEventService {

    private final CloudEventRepository cloudEventRepository;
    private final CloudSystemEventMapper cloudEventMapper;

    /**
     * Find all system events with processing status is NULL.
     * Convert to cloudEvent model.
     * Save FAILED event if the entity cannot be converted to the model.
     *
     * @return Set of CloudSystemEvent
     */
    @Transactional
    public Set<CloudSystemEvent> getNotProcessedEvents() {
        Set<CloudSystemEventEntity> eventEntities = cloudEventRepository.findAllByProcessingStatusIsNull();
        if (CollectionUtils.isNotEmpty(eventEntities)) {
            log.debug("Not yet processed events retrieved: {}", eventEntities.size());
        }
        return eventEntities.stream()
            .map(this::convertToCloudEvent)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    @Transactional
    public void saveAllCloudEvents(Collection<CloudSystemEvent> cloudEvents) {
        final Set<CloudSystemEventEntity> cloudEventEntities = cloudEvents.stream()
            .map(cloudEventMapper::toCloudEventEntity)
            .collect(Collectors.toSet());
        cloudEventRepository.saveAll(cloudEventEntities);
    }

    private CloudSystemEvent convertToCloudEvent(CloudSystemEventEntity eventEntity) {
        try {
            return cloudEventMapper.toCloudEvent(eventEntity);
        } catch (ConvertException e) {
            log.debug(String.format(ConvertException.CONVERT_MESSAGE, e.getMessage()));
            eventEntity.setProcessingStatus(FAILED);
            cloudEventRepository.save(eventEntity);
            return null;
        }
    }
}
