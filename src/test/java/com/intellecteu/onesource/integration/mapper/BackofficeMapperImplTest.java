package com.intellecteu.onesource.integration.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.intellecteu.onesource.integration.EntityTestFactory;
import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.kafka.dto.RecallInstructionDTO;
import com.intellecteu.onesource.integration.model.backoffice.RecallSpire;
import com.intellecteu.onesource.integration.repository.entity.backoffice.RecallSpireEntity;
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
        final RecallSpire recallSpire = ModelTestFactory.buildRecall(111L, 111L);

        final RecallSpireEntity entity = mapper.toEntity(recallSpire);

        assertNotNull(entity);
        assertEquals(recallSpire.getRecallId(), entity.getRecallId());
        assertEquals(recallSpire.getRelatedPositionId(), entity.getRelatedPositionId());
        assertEquals(recallSpire.getMatching1SourceRecallId(), entity.getMatching1SourceRecallId());
        assertEquals(recallSpire.getRelatedContractId(), entity.getRelatedContractId());
        assertEquals(recallSpire.getStatus(), entity.getStatus());
        assertEquals(recallSpire.getCreationDateTime(), entity.getCreationDateTime());
        assertEquals(recallSpire.getOpenQuantity(), entity.getOpenQuantity());
        assertEquals(recallSpire.getQuantity(), entity.getQuantity());
        assertEquals(recallSpire.getRecallDate(), entity.getRecallDate());
        assertEquals(recallSpire.getRecallDueDate(), entity.getRecallDueDate());
    }

    @Test
    void testEntityToModel() {
        final RecallSpireEntity entity = EntityTestFactory.buildRecallEntity(888L, 222L);
        final RecallSpire recallSpire = mapper.toModel(entity);

        assertNotNull(recallSpire);
        assertEquals(recallSpire.getRecallId(), entity.getRecallId());
        assertEquals(recallSpire.getRelatedPositionId(), entity.getRelatedPositionId());
        assertEquals(recallSpire.getMatching1SourceRecallId(), entity.getMatching1SourceRecallId());
        assertEquals(recallSpire.getRelatedContractId(), entity.getRelatedContractId());
        assertEquals(recallSpire.getStatus(), entity.getStatus());
        assertEquals(recallSpire.getCreationDateTime(), entity.getCreationDateTime());
        assertNotNull(entity.getLastUpdateDateTime());
        assertEquals(recallSpire.getOpenQuantity(), entity.getOpenQuantity());
        assertEquals(recallSpire.getQuantity(), entity.getQuantity());
        assertEquals(recallSpire.getRecallDate(), entity.getRecallDate());
        assertEquals(recallSpire.getRecallDueDate(), entity.getRecallDueDate());
    }

    @Test
    void testRecallInstructionToModel() {
        final RecallInstructionDTO instruction = ModelTestFactory.buildRecallInstruction("5", 55L);
        final RecallSpire recallSpire = mapper.toModel(instruction);

        assertNotNull(recallSpire);
        assertEquals(recallSpire.getRecallId(), instruction.getSpireRecallId());
        assertEquals(recallSpire.getRelatedContractId(), instruction.getRelatedContractId());
        assertEquals(recallSpire.getRelatedPositionId(), instruction.getRelatedPositionId());
        assertEquals(recallSpire.getQuantity(), instruction.getQuantity());
        assertEquals(recallSpire.getRecallDate(), instruction.getRecallDate());
        assertEquals(recallSpire.getRecallDueDate(), instruction.getRecallDueDate());
    }
}