package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import java.util.List;

public interface SettlementService {

    List<SettlementDto> getSettlementInstruction(PositionDto positionDto);

    SettlementDto persistSettlement(SettlementDto settlement);

}
