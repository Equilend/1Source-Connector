package com.intellecteu.onesource.integration.routes.common.processor;

import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.PROCESSED;

import com.intellecteu.onesource.integration.exception.ConvertException;
import com.intellecteu.onesource.integration.mapper.CloudSystemEventMapper;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudSystemEvent;
import com.intellecteu.onesource.integration.repository.CloudEventRepository;
import com.intellecteu.onesource.integration.repository.entity.toolkit.CloudSystemEventEntity;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloudEventNotificationProcessor {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final CloudEventRepository repository;
    private final CloudSystemEventMapper systemEventMapper;

    @Value("${notification.spire.topic}")
    private String spireTopic;

    @Transactional
    public void sendAllEvents() {
        final Set<CloudSystemEventEntity> entities = getNotProcessedEvents();
        log.debug(">>>>> Sending notifications. Events to send: " + entities.size());
        var errorList = new HashSet<CloudSystemEventEntity>();
        for (var eventEntity : entities) {
            CloudSystemEvent cloudSystemEvent = convertToCloudEvent(eventEntity);
            if (cloudSystemEvent == null) {
                errorList.add(eventEntity);
            } else {
                try {
                    sendEvent(cloudSystemEvent);
                    repository.updateProcessingStatusById(eventEntity.getId(), PROCESSED.name());
                } catch (RuntimeException e) {
                    errorList.add(eventEntity);
                }
            }
        }
        // think about batching
        if (!errorList.isEmpty()) {
            log.warn("Events were not sent. Failed amount: " + errorList.size());
            errorList.forEach(event -> repository.updateProcessingStatusById(event.getId(), "FAILED"));
        }
        log.debug("<<<<< Sending notifications finished.");
    }

    private CloudSystemEvent convertToCloudEvent(CloudSystemEventEntity eventEntity) {
        try {
            return systemEventMapper.toCloudEvent(eventEntity);
        } catch (ConvertException e) {
            log.warn(String.format(ConvertException.CONVERT_MESSAGE, e.getMessage()));
        }
        return null;
    }

    public void sendEvent(CloudSystemEvent event) {
        // think whether we should send and consume events in a batch
        final ProducerRecord<String, String> record = new ProducerRecord<>(spireTopic,
            event.getRelatedProcess(),
            systemEventMapper.toJson(event)
        );
        kafkaTemplate.send(record);
    }

    private Set<CloudSystemEventEntity> getNotProcessedEvents() {
        return repository.findAllWhereProcessingStatusIsNull();
    }
}
