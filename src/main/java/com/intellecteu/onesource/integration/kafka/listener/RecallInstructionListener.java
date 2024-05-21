package com.intellecteu.onesource.integration.kafka.listener;

import com.intellecteu.onesource.integration.kafka.dto.RecallInstructionDTO;
import com.intellecteu.onesource.integration.services.RecallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@KafkaListener(
    id = "${spire.kafka.consumer.listener.recall-instruction.group-id}",
    topics = "${spire.kafka.consumer.listener.recall-instruction.topic}",
    containerFactory = "recallInstructionContainerFactory")
@Profile("!local")
public class RecallInstructionListener {

    private final RecallService recallService;

    /**
     * There is the listener for messages with header __TypeId__:RecallInstruction
     */
    @KafkaHandler
    public void handleCorrectionInstruction(@Valid RecallInstructionDTO recallInstructionDTO) {
        recallService.createRecall(recallInstructionDTO);
    }

    @KafkaHandler(isDefault = true)
    public void unknown(Object object) {
        log.error("Unknown type received: " + object);
    }
}
