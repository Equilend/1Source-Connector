package com.intellecteu.onesource.integration.kafka.listener;

import com.intellecteu.onesource.integration.kafka.dto.RecallInstructionDTO;
import com.intellecteu.onesource.integration.model.enums.RecallInstructionType;
import com.intellecteu.onesource.integration.services.RecallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(value = "spire.kafka.consumer.listener.recall-instruction.enable")
public class RecallInstructionListener {

    private final RecallService recallService;

    /**
     * There is the listener for messages with header __TypeId__:RecallInstructionDTO
     */
    @KafkaHandler
    public void handleRecallInstruction(@Valid RecallInstructionDTO recallInstructionDTO) {
        if (recallInstructionDTO.getInstructionType() == RecallInstructionType.RECALL_CANCELLATION) {
            recallService.saveRecallInstruction(recallInstructionDTO);
        } else {
            recallService.createRecall(recallInstructionDTO);
        }
    }

    @KafkaHandler(isDefault = true)
    public void unknown(Object object) {
        log.error("Unknown type received: " + object);
    }
}
