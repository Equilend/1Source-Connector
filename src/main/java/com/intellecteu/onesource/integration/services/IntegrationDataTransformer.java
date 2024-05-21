package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.PositionConfirmationRequest;
import com.intellecteu.onesource.integration.model.backoffice.Recall;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ContractProposal;
import com.intellecteu.onesource.integration.model.onesource.ContractProposalApproval;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RecallProposalDTO;

public interface IntegrationDataTransformer {

    ContractProposal toLenderContractProposal(Position position);

    ContractProposalApproval toBorrowerContractProposalApproval(Contract contract, Position position);

    PositionConfirmationRequest toPositionConfirmationRequest(Position position);

    RecallProposalDTO to1SourceRecallProposal(Recall recall);

}
