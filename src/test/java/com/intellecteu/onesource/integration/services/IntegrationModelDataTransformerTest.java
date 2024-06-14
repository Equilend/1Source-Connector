package com.intellecteu.onesource.integration.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.model.backoffice.RecallSpire;
import com.intellecteu.onesource.integration.services.client.onesource.dto.PartyRoleDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RecallProposalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.VenueTypeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IntegrationModelDataTransformerTest {

    @Mock
    private FigiService figiService;
    private IntegrationModelDataTransformer dataTransformer;

    @BeforeEach
    void setUp() {
        dataTransformer = new IntegrationModelDataTransformer("testSpireUserId", figiService);
    }

    @Test
    void test_to1SourceRecallProposal() {
        final RecallSpire recallSpire = ModelTestFactory.buildRecall(8888L, 123L);
        final RecallProposalDTO proposal = dataTransformer.to1SourceRecallProposal(recallSpire);

        assertNotNull(proposal);
        assertNotNull(proposal.getExecutionVenue());
        assertNotNull(proposal.getExecutionVenue().getVenueParties());
        assertEquals(PartyRoleDTO.LENDER, proposal.getExecutionVenue().getVenueParties().get(0).getPartyRole());
        assertEquals("8888-123",
            proposal.getExecutionVenue().getVenueParties().get(0).getVenuePartyRefKey());
        assertEquals(VenueTypeDTO.OFFPLATFORM, proposal.getExecutionVenue().getType());
        assertEquals(recallSpire.getQuantity(), proposal.getQuantity());
        assertEquals(recallSpire.getRecallDate(), proposal.getRecallDate());
        assertEquals(recallSpire.getRecallDueDate(), proposal.getRecallDueDate());
    }
}