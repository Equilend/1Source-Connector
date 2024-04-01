package com.intellecteu.onesource.integration.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.intellecteu.onesource.integration.EntityTestFactory;
import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.model.onesource.ContractProposalApproval;
import com.intellecteu.onesource.integration.model.onesource.TradeAgreement;
import com.intellecteu.onesource.integration.repository.entity.onesource.TradeAgreementEntity;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractProposalApprovalDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class OneSourceMapperTest {

    private OneSourceMapper oneSourceMapper;

    @BeforeEach
    void setUp() {
        oneSourceMapper = Mappers.getMapper(OneSourceMapper.class);
    }

    @Test
    void testContractProposalMapping_shouldNotFail_whenRoundingRuleIsNull() {
        final ContractProposalApproval approval = ModelTestFactory.buildContractProposalApproval();
        approval.setRoundingRule(null);
        final ContractProposalApprovalDTO result = oneSourceMapper.toRequestDto(approval);

        assertNull(result.getRoundingRule());
        assertEquals(approval.getInternalRefId(), result.getInternalRefId());
    }

    @Test
    void testVenueMappingToModel_shouldAssignTradeId() {
        TradeAgreementEntity entity = EntityTestFactory.buildTradeAgreement();
        entity.setId(12345L);
        final TradeAgreement model = oneSourceMapper.toModel(entity);

        assertEquals(model.getVenues().get(0).getTradeId(), model.getId());
        assertEquals(12345L, model.getId());
    }

    @Test
    void testVenueMappingToEntity_shouldAssignTradeId() {
        final TradeAgreement model = ModelTestFactory.buildTradeAgreement();
        TradeAgreementEntity entity = oneSourceMapper.toEntity(model);

        assertEquals(entity.getVenues().get(0).getTradeAgreement().getId(), model.getId());
    }

}