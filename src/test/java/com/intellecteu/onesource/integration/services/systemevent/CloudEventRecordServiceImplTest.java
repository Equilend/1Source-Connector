package com.intellecteu.onesource.integration.services.systemevent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intellecteu.onesource.integration.EntityTestFactory;
import com.intellecteu.onesource.integration.mapper.CloudSystemEventMapper;
import com.intellecteu.onesource.integration.model.enums.FieldExceptionType;
import com.intellecteu.onesource.integration.model.enums.FieldSource;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RelatedObject;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.SystemEventData;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.IntegrationCloudEvent;
import com.intellecteu.onesource.integration.repository.CloudEventRepository;
import com.intellecteu.onesource.integration.repository.entity.toolkit.CloudSystemEventEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CloudEventRecordServiceImplTest {

    private CloudEventFactory<IntegrationCloudEvent> recordFactory;
    @Mock
    private CloudEventRepository eventRepository;

    private CloudSystemEventMapper recordMapper;

    private CloudEventRecordServiceImpl recordService;

    @BeforeEach
    void setUp() {
        Map<IntegrationProcess, IntegrationCloudEventBuilder> eventBuilderByProcess = new HashMap<>();
        var contractInitiationBuilder = new ContractInitiationCloudEventBuilder("1.0", "http://integration.toolkit");
        eventBuilderByProcess.put(IntegrationProcess.CONTRACT_INITIATION, contractInitiationBuilder);
        recordFactory = new CloudEventFactoryImpl(eventBuilderByProcess);
        recordMapper = Mappers.getMapper(CloudSystemEventMapper.class);
        recordService = new CloudEventRecordServiceImpl(recordFactory, eventRepository, recordMapper);
    }

    @Test
    void record_shouldCreateAndSaveRecord() {
        CloudEventBuildRequest request = CloudEventBuildRequest.builder()
            .recordType(RecordType.LOAN_CONTRACT_PROPOSAL_DISCREPANCIES)
            .subject("No subject")
            .relatedProcess(IntegrationProcess.CONTRACT_INITIATION)
            .relatedSubProcess(IntegrationSubProcess.RECONCILE_TRADE_AGREEMENT)
            .data(new SystemEventData(
                UUID.randomUUID().toString(),
                "testmsg",
                List.of(new FieldImpacted(FieldSource.ONE_SOURCE_RERATE, "testName", "testValue",
                    FieldExceptionType.DISCREPANCY)),
                List.of(RelatedObject.notApplicable())))
            .build();

        CloudSystemEventEntity entity = EntityTestFactory.createSystemEventEntity();

        when(eventRepository.save(any(CloudSystemEventEntity.class))).thenReturn(entity);

        recordService.record(request);

        verify(eventRepository).save(any(CloudSystemEventEntity.class));
    }

}
