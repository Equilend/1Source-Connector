package com.intellecteu.onesource.integration.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intellecteu.onesource.integration.DtoTestFactory;
import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.TestConfig;
import com.intellecteu.onesource.integration.mapper.RecordMapper;
import com.intellecteu.onesource.integration.repository.CloudEventRepository;
import java.util.Set;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CloudEventNotificationServiceTest {

    private static final String SPIRE_TOPIC_FIELD = "spireTopic";
    private static final String SPIRE_TOPIC_VALUE = "testTopic";

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private CloudEventRepository repository;
    private RecordMapper recordMapper;

    private CloudEventNotificationService service;

    @BeforeEach
    void setUp() {
        recordMapper = new RecordMapper(TestConfig.createTestObjectMapper());
        service = new CloudEventNotificationService(kafkaTemplate, repository, recordMapper);
        ReflectionTestUtils.setField(service, SPIRE_TOPIC_FIELD, SPIRE_TOPIC_VALUE);
    }

    @Test
    void sendAllEvents() {
        var cloudEventEntity = ModelTestFactory.buildCloudEventEntity();
        when(repository.findAllWhereProcessingStatusIsNull()).thenReturn(Set.of(cloudEventEntity));
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(null);
        doNothing().when(repository).updateProcessingStatusById(any(), any());

        service.sendAllEvents();

        verify(repository).findAllWhereProcessingStatusIsNull();
        verify(kafkaTemplate).send(any(ProducerRecord.class));
        verify(repository).updateProcessingStatusById(any(), any());
    }

    @Test
    void sendEvent_shouldUseKafkaTemplate() {
        var event = DtoTestFactory.buildTestCloudEvent();
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(null);
        service.sendEvent(event);
        Mockito.verify(kafkaTemplate).send(any(ProducerRecord.class));
    }
}