package com.intellecteu.onesource.integration.routes.common.processor;

import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.PROCESSED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.intellecteu.onesource.integration.dto.record.CloudEvent;
import com.intellecteu.onesource.integration.exception.ConvertException;
import com.intellecteu.onesource.integration.mapper.RecordMapper;
import com.intellecteu.onesource.integration.repository.CloudEventRepository;
import com.intellecteu.onesource.integration.repository.entity.onesource.CloudEventEntity;
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
    private final RecordMapper recordMapper;

    @Value("${notification.spire.topic}")
    private String spireTopic;

    @Transactional
    public void sendAllEvents() {
        final Set<CloudEventEntity> entities = getNotProcessedEvents();
        log.debug(">>>>> Sending notifications. Events to send: " + entities.size());
        var errorList = new HashSet<CloudEventEntity>();
        for (var eventEntity : entities) {
            CloudEvent cloudEvent = convertToCloudEvent(eventEntity);
            if (cloudEvent == null) {
                errorList.add(eventEntity);
            } else {
                try {
                    sendEvent(cloudEvent);
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

    private CloudEvent convertToCloudEvent(CloudEventEntity eventEntity) {
        try {
            return recordMapper.toCloudEvent(eventEntity);
        } catch (ConvertException e) {
            log.warn(String.format(ConvertException.CONVERT_MESSAGE, e.getMessage()));
            return null;
        }
    }

    public void sendEvent(CloudEvent event) {
        // think whether we should send and consume events in a batch
        try {
            final ProducerRecord<String, String> record = new ProducerRecord<>(spireTopic, event.getRelatedProcess(),
                recordMapper.toJson(event));
            kafkaTemplate.send(record);
        } catch (JsonProcessingException e) {
            log.warn("Fail to serialize");
            throw new RuntimeException();
        }
    }

    private Set<CloudEventEntity> getNotProcessedEvents() {
        return repository.findAllWhereProcessingStatusIsNull();
    }
}
