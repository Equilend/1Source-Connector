package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import java.util.List;

public interface ContractInitiationService {

    void startContractInitiation();

    List<PositionDto> getPositionDetails();

    void processPositionInformation(PositionDto positionDto);

    void instructLoanContractProposal(PositionDto positionDto, List<SettlementDto> settlementDtos);
}
