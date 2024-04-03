package com.intellecteu.onesource.integration.services.systemevent;

import com.intellecteu.onesource.integration.mapper.CloudSystemEventMapper;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.IntegrationCloudEvent;
import com.intellecteu.onesource.integration.repository.CloudEventRepository;
import com.intellecteu.onesource.integration.repository.entity.toolkit.CloudSystemEventEntity;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloudEventRecordServiceImpl implements CloudEventRecordService {

    private final CloudEventFactory<IntegrationCloudEvent> recordFactory;
    private final CloudEventRepository eventRepository;
    private final CloudSystemEventMapper systemEventMapper;

    @Override
    @Transactional
    public void record(CloudEventBuildRequest buildRequest) {
        final IntegrationCloudEvent record = createEvent(buildRequest);
        log.debug("A cloud event for related sub process: {} was recorded.", buildRequest.getRelatedSubProcess());
        record(record);
    }

    private void record(IntegrationCloudEvent record) {
        final CloudSystemEventEntity entity = systemEventMapper.toCloudEventEntity(record);
        eventRepository.save(entity);
    }

    private IntegrationCloudEvent createEvent(CloudEventBuildRequest buildRequest) {
        return recordFactory.createRecord(buildRequest);
    }


    @Override
    public CloudEventFactory<IntegrationCloudEvent> getFactory() {
        return recordFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getToolkitCloudEventId(String relatedObjectId, IntegrationSubProcess subProcess,
        RecordType recordType) {
        try {
            return eventRepository.findToolkitCloudEventId(relatedObjectId, subProcess.name(), recordType.name());
        } catch (Exception e) {
            log.warn("Unexpected exception on fetch cloud event ID. Related Object Id:{}", relatedObjectId);
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getToolkitCloudEventIdForRerateWorkaround(String subject, IntegrationSubProcess subProcess,
        RecordType recordType) {
        try {
            return eventRepository.findToolkitCloudEventIdBySubject(subject, subProcess.name(), recordType.name());
        } catch (Exception e) {
            log.warn("Unexpected exception on fetch cloud event ID. Subject:{}", subject);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void updateTime(@NonNull String eventId) {
        eventRepository.updateTime(eventId);
        log.debug("The timestamp is updated for the event id = {}", eventId);
    }
}
