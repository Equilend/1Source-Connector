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
public class SettlementInstruction {

    private Long id;
    private String settlementBic;
    private String localAgentBic;
    private String localAgentName;
    private String localAgentAcct;
    private String dtcParticipantNumber;

}
