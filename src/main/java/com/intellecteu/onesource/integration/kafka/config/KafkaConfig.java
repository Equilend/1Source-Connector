package com.intellecteu.onesource.integration.kafka.config;

import com.intellecteu.onesource.integration.kafka.dto.CorrectionInstructionDTO;
import com.intellecteu.onesource.integration.kafka.dto.DeclineInstructionDTO;
import java.util.HashMap;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableKafka
public class KafkaConfig {

    private final static String AUTH_SECRET = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";";

    private String bootstrapServer;
    private String username;
    private String password;

    public KafkaConfig(@Value("${spire.kafka.consumer.bootstrap-server}") String bootstrapServer,
        @Value("${spire.kafka.consumer.username}") String username,
        @Value("${spire.kafka.consumer.password}") String password) {
        this.bootstrapServer = bootstrapServer;
        this.username = username;
        this.password = password;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        String saslConfig = String.format(AUTH_SECRET, username, password);
        HashMap<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.TYPE_MAPPINGS,
            "CorrectionInstruction:com.intellecteu.onesource.integration.kafka.dto.CorrectionInstructionDTO, "
                + "DeclineInstruction:com.intellecteu.onesource.integration.kafka.dto.DeclineInstructionDTO");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, saslConfig);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CorrectionInstructionDTO> correctionInstructionContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CorrectionInstructionDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeclineInstructionDTO> declineInstructionInstructionContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DeclineInstructionDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}
