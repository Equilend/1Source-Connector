package com.intellecteu.onesource.integration.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.model.backoffice.Recall;
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
    private FigiHandler figiHandler;
    private IntegrationModelDataTransformer dataTransformer;

    @BeforeEach
    void setUp() {
        dataTransformer = new IntegrationModelDataTransformer("testSpireUserId", figiHandler);
    }

    @Test
    void test_to1SourceRecallProposal() {
        final Recall recall = ModelTestFactory.buildRecall("recallId", 123L);
        final RecallProposalDTO proposal = dataTransformer.to1SourceRecallProposal(recall);

        assertNotNull(proposal);
        assertNotNull(proposal.getExecutionVenue());
        assertNotNull(proposal.getExecutionVenue().getVenueParties());
        assertEquals(PartyRoleDTO.LENDER, proposal.getExecutionVenue().getVenueParties().get(0).getPartyRole());
        assertEquals("recallId-123",
            proposal.getExecutionVenue().getVenueParties().get(0).getVenuePartyRefKey());
        assertEquals(VenueTypeDTO.OFFPLATFORM, proposal.getExecutionVenue().getType());
        assertEquals(recall.getQuantity(), proposal.getQuantity());
        assertEquals(recall.getRecallDate(), proposal.getRecallDate());
        assertEquals(recall.getRecallDueDate(), proposal.getRecallDueDate());
    }
}