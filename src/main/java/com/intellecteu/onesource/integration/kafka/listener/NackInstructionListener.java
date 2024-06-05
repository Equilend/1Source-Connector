package com.intellecteu.onesource.integration.kafka.listener;

import com.intellecteu.onesource.integration.kafka.dto.NackInstructionDTO;
import com.intellecteu.onesource.integration.kafka.mapper.KafkaNackInstructionMapper;
import com.intellecteu.onesource.integration.services.NackInstructionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@KafkaListener(id = "${spire.kafka.consumer.listener.nack-instruction.group-id}", topics = "${spire.kafka.consumer.listener.nack-instruction.topic}", containerFactory = "nackInstructionContainerFactory")
@ConditionalOnProperty(
    value = "spire.kafka.consumer.listener.nack-instruction.enable",
    havingValue = "true",
    matchIfMissing = true)
public class NackInstructionListener {

    private final NackInstructionService nackInstructionService;
    private final KafkaNackInstructionMapper mapper;

    @Autowired
    public NackInstructionListener(NackInstructionService nackInstructionService,
        KafkaNackInstructionMapper mapper) {
        this.nackInstructionService = nackInstructionService;
        this.mapper = mapper;
    }

    @KafkaHandler
    /**
     * There is the listener for messages with header __TypeId__:NackInstruction
     */
    public void handleCorrectionInstruction(NackInstructionDTO nackInstructionDTO) {
        nackInstructionService.save(mapper.toModel(nackInstructionDTO));
    }

    @KafkaHandler(isDefault = true)
    public void unknown(Object object) {
        log.error("Unkown type received: " + object);
    }
}
