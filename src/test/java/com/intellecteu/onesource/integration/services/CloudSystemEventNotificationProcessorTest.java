package com.intellecteu.onesource.integration.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intellecteu.onesource.integration.EntityTestFactory;
import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.mapper.CloudSystemEventMapper;
import com.intellecteu.onesource.integration.repository.CloudEventRepository;
import com.intellecteu.onesource.integration.repository.entity.toolkit.CloudSystemEventEntity;
import com.intellecteu.onesource.integration.routes.common.processor.CloudEventNotificationProcessor;
import java.util.Set;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CloudSystemEventNotificationProcessorTest {

    private static final String SPIRE_TOPIC_FIELD = "spireTopic";
    private static final String SPIRE_TOPIC_VALUE = "testTopic";

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private CloudEventRepository repository;

    private CloudSystemEventMapper eventMapper;

    private CloudEventNotificationProcessor service;

    @BeforeEach
    void setUp() {
        eventMapper = Mappers.getMapper(CloudSystemEventMapper.class);
        service = new CloudEventNotificationProcessor(kafkaTemplate, repository, eventMapper);
        ReflectionTestUtils.setField(service, SPIRE_TOPIC_FIELD, SPIRE_TOPIC_VALUE);
    }

    @Test
    void sendAllEvents() {
        CloudSystemEventEntity cloudEventEntity = EntityTestFactory.createSystemEventEntity();
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
        var event = ModelTestFactory.buildCloudEventModel();
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(null);
        service.sendEvent(event);
        Mockito.verify(kafkaTemplate).send(any(ProducerRecord.class));
    }
}