package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.onesource.ContractProposal;

public interface IntegrationDataTransformer {

    ContractProposal toLenderContractProposal(Position position);

}
