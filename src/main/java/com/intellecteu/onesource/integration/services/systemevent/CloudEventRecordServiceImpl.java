package com.intellecteu.onesource.integration.services.systemevent;

import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.dto.record.IntegrationCloudEvent;
import com.intellecteu.onesource.integration.mapper.RecordMapper;
import com.intellecteu.onesource.integration.repository.CloudEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CloudEventRecordServiceImpl implements CloudEventRecordService {

    private final CloudEventFactory<IntegrationCloudEvent> recordFactory;
    private final CloudEventRepository eventRepository;
    private final RecordMapper recordMapper;

    @Override
    public void record(CloudEventBuildRequest buildRequest) {
        final IntegrationCloudEvent record = createEvent(buildRequest);
        log.debug("A cloud event for related sub process: {} was recorded.", buildRequest.getRelatedSubProcess());
        record(record);
    }

    private void record(IntegrationCloudEvent record) {
        eventRepository.save(recordMapper.toCloudEventEntity(record));
    }

    private IntegrationCloudEvent createEvent(CloudEventBuildRequest buildRequest) {
        return recordFactory.createRecord(buildRequest);
    }

    public CloudEventRecordServiceImpl(CloudEventFactory<IntegrationCloudEvent> recordFactory,
        CloudEventRepository eventRepository, RecordMapper recordMapper) {
        this.recordFactory = recordFactory;
        this.eventRepository = eventRepository;
        this.recordMapper = recordMapper;
    }

    @Override
    public CloudEventFactory<IntegrationCloudEvent> getFactory() {
        return recordFactory;
    }
}
