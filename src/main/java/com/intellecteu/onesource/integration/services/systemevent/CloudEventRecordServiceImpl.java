package com.intellecteu.onesource.integration.services.systemevent;

import com.intellecteu.onesource.integration.mapper.CloudSystemEventMapper;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.IntegrationCloudEvent;
import com.intellecteu.onesource.integration.repository.CloudEventRepository;
import com.intellecteu.onesource.integration.repository.entity.toolkit.CloudSystemEventEntity;
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
}
