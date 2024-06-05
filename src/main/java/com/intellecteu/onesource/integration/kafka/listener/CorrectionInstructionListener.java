package com.intellecteu.onesource.integration.kafka.listener;

import com.intellecteu.onesource.integration.kafka.dto.CorrectionInstructionDTO;
import com.intellecteu.onesource.integration.kafka.mapper.KafkaCorrectionInstructionMapper;
import com.intellecteu.onesource.integration.services.CorrectionInstructionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@KafkaListener(id = "${spire.kafka.consumer.listener.correction-instruction.group-id}", topics = "${spire.kafka.consumer.listener.correction-instruction.topic}", containerFactory = "correctionInstructionContainerFactory")
@ConditionalOnProperty(
    value = "spire.kafka.consumer.listener.correction-instruction.enable",
    havingValue = "true",
    matchIfMissing = true)
public class CorrectionInstructionListener {

    private final CorrectionInstructionService correctionInstructionService;
    private final KafkaCorrectionInstructionMapper mapper;

    @Autowired
    public CorrectionInstructionListener(CorrectionInstructionService correctionInstructionService,
        KafkaCorrectionInstructionMapper mapper) {
        this.correctionInstructionService = correctionInstructionService;
        this.mapper = mapper;
    }

    @KafkaHandler
    /**
     * There is the listener for messages with header __TypeId__:CorrectionInstruction
     */
    public void handleCorrectionInstruction(CorrectionInstructionDTO correctionInstructionDTO) {
        correctionInstructionService.save(mapper.toModel(correctionInstructionDTO));
    }

    @KafkaHandler(isDefault = true)
    public void unknown(Object object) {
        log.error("Unkown type received: " + object);
    }
}
