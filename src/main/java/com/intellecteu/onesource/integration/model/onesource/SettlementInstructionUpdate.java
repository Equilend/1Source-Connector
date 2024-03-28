package com.intellecteu.onesource.integration.model.onesource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * 1SOURCE v.1.0.4 has PartySettlementInstruction as an inner object
 * for SettlementInstructionUpdate.
 * In current implementation PartySettlementInstruction is unwrapped
 * to SettlementInstructionUpdate.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementInstructionUpdate {

    private Long id;
    private String venueRefId;
    private Long instructionId;
    private PartyRole partyRole;
    private SettlementInstruction instruction;
    private String internalAcctCd;

    public SettlementInstructionUpdate(SettlementInstruction instruction) {
        this.instruction = instruction;
    }
}
