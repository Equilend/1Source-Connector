package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ContractProposal;
import com.intellecteu.onesource.integration.model.onesource.ContractProposalApproval;

public interface IntegrationDataTransformer {

    ContractProposal toLenderContractProposal(Position position);

    ContractProposalApproval toBorrowerContractProposalApproval(Contract contract, Position position);

}
