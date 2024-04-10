package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.TestConfig.createTestObjectMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.mapper.CloudSystemEventMapper;
import com.intellecteu.onesource.integration.mapper.JsonMapper;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudSystemEvent;
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

@ExtendWith(MockitoExtension.class)
class CloudSystemEventNotificationProcessorTest {

    private static final String SPIRE_TOPIC_VALUE = "testTopic";

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private CloudEventService cloudEventService;

    private CloudSystemEventMapper eventMapper;

    private CloudEventNotificationProcessor service;

    @BeforeEach
    void setUp() {
        eventMapper = Mappers.getMapper(CloudSystemEventMapper.class);
        eventMapper.setJsonMapper(new JsonMapper(createTestObjectMapper()));
        service = new CloudEventNotificationProcessor(kafkaTemplate, cloudEventService, eventMapper, SPIRE_TOPIC_VALUE);
    }

    @Test
    void sendAllEvents() {
        CloudSystemEvent cloudEvent = ModelTestFactory.buildCloudEventModel();
        when(cloudEventService.getNotProcessedEvents()).thenReturn(Set.of(cloudEvent));
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(null);

        service.sendAllEvents();

        verify(cloudEventService).getNotProcessedEvents();
        verify(kafkaTemplate).send(any(ProducerRecord.class));
    }

    @Test
    void sendEvent_shouldUseKafkaTemplate() {
        var event = ModelTestFactory.buildCloudEventModel();
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(null);
        service.sendEvent(event);
        Mockito.verify(kafkaTemplate).send(any(ProducerRecord.class));
    }
}