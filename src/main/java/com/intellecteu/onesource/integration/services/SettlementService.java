package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.model.Settlement;
import java.util.List;

public interface SettlementService {

    List<Settlement> getSettlementByInstructionId(Long instructionId);

    Settlement persistSettlement(Settlement settlement);

}
