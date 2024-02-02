package com.intellecteu.onesource.integration.model.onesource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settlement {

    private Long id;
    private Long instructionId;
    private PartyRole partyRole;
    private SettlementInstruction instruction;

}
