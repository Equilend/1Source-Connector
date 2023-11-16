package com.intellecteu.onesource.integration.config;

import com.intellecteu.onesource.integration.dto.record.CloudEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

@Configuration
public class KafkaProducerConfig {

  @Value("${notification.spire.bootstrap-server}")
  private String bootstrapServer;

  @Bean
  public ProducerFactory<String, CloudEvent> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
//    configProps.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    configProps.put(KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    configProps.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate<String, CloudEvent> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
