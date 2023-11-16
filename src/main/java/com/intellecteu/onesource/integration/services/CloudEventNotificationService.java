package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.dto.record.CloudEvent;
import com.intellecteu.onesource.integration.exception.ConvertException;
import com.intellecteu.onesource.integration.mapper.RecordMapper;
import com.intellecteu.onesource.integration.model.CloudEventEntity;
import com.intellecteu.onesource.integration.repository.CloudEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static com.intellecteu.onesource.integration.model.ProcessingStatus.PROCESSED;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloudEventNotificationService implements EventNotificationService<CloudEvent> {

  private final KafkaTemplate<String, CloudEvent> kafkaTemplate;
  private final CloudEventRepository repository;
  private final RecordMapper recordMapper;

  @Value("${notification.spire.topic}")
  private String spireTopic;

  @Override
  @Transactional
  public void sendAllEvents() {
    final Set<CloudEventEntity> entities = getNotProcessedEvents();
    log.debug("Sending notifications. Events to send: " + entities.size());
    var errorList = new HashSet<CloudEventEntity>();
    for (var eventEntity : entities) {
      CloudEvent cloudEvent = convertToCloudEvent(eventEntity);
      if (cloudEvent == null) {
        errorList.add(eventEntity);
      }
      try {
        sendEvent(cloudEvent);
        repository.updateProcessingStatusById(eventEntity.getId(), PROCESSED.name());
      } catch (KafkaException e) {
        errorList.add(eventEntity);
      }
    }
    // think about batching
    if (!errorList.isEmpty()) {
      log.warn("Events were not sent: " + errorList.size());
    }
    errorList.forEach(event -> repository.updateProcessingStatusById(event.getId(), "FAILED"));
  }

  private CloudEvent convertToCloudEvent(CloudEventEntity eventEntity) {
    try {
      return recordMapper.toCloudEvent(eventEntity);
    } catch (ConvertException e) {
      log.warn(String.format(ConvertException.CONVERT_MESSAGE, e.getMessage()));
      return null;
    }
  }

  @Override
  public void sendEvent(CloudEvent event) {
    // think whether we should send and consume events in a batch
    kafkaTemplate.send(spireTopic, event);
  }

  private Set<CloudEventEntity> getNotProcessedEvents() {
    return repository.findAllWhereProcessingStatusIsNull();
  }
}
