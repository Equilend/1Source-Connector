package com.intellecteu.onesource.integration.routes.common.processor;

import com.intellecteu.onesource.integration.mapper.CloudSystemEventMapper;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventProcessingStatus;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudSystemEvent;
import com.intellecteu.onesource.integration.services.CloudEventService;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CloudEventNotificationProcessor {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final CloudEventService cloudEventService;
    private final CloudSystemEventMapper systemEventMapper;
    private final String spireTopic;

    public CloudEventNotificationProcessor(KafkaTemplate<String, String> kafkaTemplate,
        CloudEventService cloudEventService,
        CloudSystemEventMapper systemEventMapper,
        @Value("${spire.kafka.producer.topic}") String spireTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.cloudEventService = cloudEventService;
        this.systemEventMapper = systemEventMapper;
        this.spireTopic = spireTopic;
    }

    @Transactional
    public void sendAllEvents() {
        final Set<CloudSystemEvent> events = cloudEventService.getNotProcessedEvents();
        log.debug("Sending notifications. Events to send: " + events.size());
        var errorList = new HashSet<CloudSystemEvent>();
        var successList = new HashSet<CloudSystemEvent>();
        for (var cloudSystemEvent : events) {
            try {
                sendEvent(cloudSystemEvent);
                cloudSystemEvent.setProcessingStatus(CloudEventProcessingStatus.PROCESSED);
                successList.add(cloudSystemEvent);
            } catch (RuntimeException e) {
                cloudSystemEvent.setProcessingStatus(CloudEventProcessingStatus.FAILED);
                errorList.add(cloudSystemEvent);
            }
        }

        var eventListToSave = Stream.of(successList, errorList).flatMap(Collection::stream).toList();
        if (CollectionUtils.isNotEmpty(eventListToSave)) {
            cloudEventService.saveAllCloudEvents(eventListToSave);
            if (CollectionUtils.isNotEmpty(successList)) {
                log.debug("System events successfully sent: {}", successList.size());
            }
            if (CollectionUtils.isNotEmpty(errorList)) {
                log.warn("Events were not sent. Failed amount: " + errorList.size());
            }
        }
    }

    public void sendEvent(CloudSystemEvent event) {
        // think whether we should send and consume events in a batch
        final String json = systemEventMapper.toJson(event);
        String key = event.getRelatedProcess() + "." + event.getRelatedSubProcess();
        final ProducerRecord<String, String> record = new ProducerRecord<>(spireTopic, key, json);
        kafkaTemplate.send(record);
    }
}
