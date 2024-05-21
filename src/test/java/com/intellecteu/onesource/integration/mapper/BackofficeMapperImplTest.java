package com.intellecteu.onesource.integration.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.intellecteu.onesource.integration.EntityTestFactory;
import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.kafka.dto.RecallInstructionDTO;
import com.intellecteu.onesource.integration.model.backoffice.Recall;
import com.intellecteu.onesource.integration.repository.entity.backoffice.RecallEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class BackofficeMapperImplTest {

    private BackOfficeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(BackOfficeMapper.class);
    }

    @Test
    void testModelToEntity() {
        final Recall recall = ModelTestFactory.buildRecall("111", 111L);

        final RecallEntity entity = mapper.toEntity(recall);

        assertNotNull(entity);
        assertEquals(recall.getRecallId(), entity.getRecallId());
        assertEquals(recall.getRelatedPositionId(), entity.getRelatedPositionId());
        assertEquals(recall.getMatching1SourceRecallId(), entity.getMatching1SourceRecallId());
        assertEquals(recall.getRelatedContractId(), entity.getRelatedContractId());
        assertEquals(recall.getStatus(), entity.getStatus());
        assertEquals(recall.getCreationDateTime(), entity.getCreationDateTime());
        assertNotNull(entity.getLastUpdateDateTime());
        assertEquals(recall.getOpenQuantity(), entity.getOpenQuantity());
        assertEquals(recall.getQuantity(), entity.getQuantity());
        assertEquals(recall.getRecallDate(), entity.getRecallDate());
        assertEquals(recall.getRecallDueDate(), entity.getRecallDueDate());
    }

    @Test
    void testEntityToModel() {
        final RecallEntity entity = EntityTestFactory.buildRecallEntity("testId", 222L);
        final Recall recall = mapper.toModel(entity);

        assertNotNull(recall);
        assertEquals(recall.getRecallId(), entity.getRecallId());
        assertEquals(recall.getRelatedPositionId(), entity.getRelatedPositionId());
        assertEquals(recall.getMatching1SourceRecallId(), entity.getMatching1SourceRecallId());
        assertEquals(recall.getRelatedContractId(), entity.getRelatedContractId());
        assertEquals(recall.getStatus(), entity.getStatus());
        assertEquals(recall.getCreationDateTime(), entity.getCreationDateTime());
        assertNotNull(entity.getLastUpdateDateTime());
        assertEquals(recall.getOpenQuantity(), entity.getOpenQuantity());
        assertEquals(recall.getQuantity(), entity.getQuantity());
        assertEquals(recall.getRecallDate(), entity.getRecallDate());
        assertEquals(recall.getRecallDueDate(), entity.getRecallDueDate());
    }

    @Test
    void testRecallInstructionToModel() {
        final RecallInstructionDTO instruction = ModelTestFactory.buildRecallInstruction("5");
        final Recall recall = mapper.toModel(instruction);

        assertNotNull(recall);
        assertEquals(recall.getRecallId(), String.valueOf(instruction.getSpireRecallId()));
        assertEquals(recall.getRelatedContractId(), instruction.getRelatedContractId());
        assertEquals(recall.getRelatedPositionId(), instruction.getRelatedPositionId());
        assertEquals(recall.getQuantity(), instruction.getQuantity());
        assertEquals(recall.getRecallDate(), instruction.getRecallDate());
        assertEquals(recall.getRecallDueDate(), instruction.getRecallDueDate());
    }
}