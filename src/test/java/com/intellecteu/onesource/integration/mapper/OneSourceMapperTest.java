package com.intellecteu.onesource.integration.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.model.onesource.ContractProposalApproval;
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

}