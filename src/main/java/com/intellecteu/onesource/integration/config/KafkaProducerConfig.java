package com.intellecteu.onesource.integration.config;

import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Configuration
public class KafkaProducerConfig {

  private final static String AUTH_SECRET = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";";

  @Value("${notification.spire.bootstrap-server}")
  private String bootstrapServer;

  @Value("${notification.spire.auth.key}")
  private String key;

  @Value("${notification.spire.auth.secret}")
  private String secret;

  @Bean
  public ProducerFactory<String, String> producerFactory() {
    String saslConfig = String.format(AUTH_SECRET, key, secret);
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    configProps.put(KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    configProps.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    configProps.put(SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
    configProps.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
    configProps.put(SaslConfigs.SASL_JAAS_CONFIG, saslConfig);
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
