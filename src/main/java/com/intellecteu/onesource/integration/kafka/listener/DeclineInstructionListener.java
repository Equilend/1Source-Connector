package com.intellecteu.onesource.integration.kafka.listener;

import com.intellecteu.onesource.integration.kafka.dto.DeclineInstructionDTO;
import com.intellecteu.onesource.integration.kafka.mapper.KafkaDeclineInstructionMapper;
import com.intellecteu.onesource.integration.services.DeclineInstructionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@KafkaListener(id = "${spire.kafka.consumer.listener.decline-instruction.group-id}", topics = "${spire.kafka.consumer.listener.decline-instruction.topic}", containerFactory = "declineInstructionContainerFactory")
@ConditionalOnProperty(
    value = "spire.kafka.consumer.listener.decline-instruction.enable",
    havingValue = "true",
    matchIfMissing = true)
public class DeclineInstructionListener {

    private final DeclineInstructionService declineInstructionService;
    private final KafkaDeclineInstructionMapper mapper;

    @Autowired
    public DeclineInstructionListener(DeclineInstructionService declineInstructionService,
        KafkaDeclineInstructionMapper mapper) {
        this.declineInstructionService = declineInstructionService;
        this.mapper = mapper;
    }

    @KafkaHandler
    /**
     * There is the listener for messages with header __TypeId__:DeclineInstruction
     */
    public void handleCorrectionInstruction(DeclineInstructionDTO declineInstructionDTO) {
        declineInstructionService.save(mapper.toModel(declineInstructionDTO));
    }

    @KafkaHandler(isDefault = true)
    public void unknown(Object object) {
        log.error("Unkown type received: " + object);
    }
}
