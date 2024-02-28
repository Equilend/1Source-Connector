package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.SettlementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementDto {

    @JsonProperty("partyRole")
    private PartyRole partyRole;
    @JsonProperty("instructionId")
    private Long instructionId;
    @JsonProperty("settlementStatus")
    private SettlementStatus settlementStatus;
    @JsonProperty("instruction")
    private SettlementInstructionDto instruction;
}
