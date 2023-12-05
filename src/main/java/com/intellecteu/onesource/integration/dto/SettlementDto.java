package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.SettlementStatus;
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
    private Integer instructionId;
    @JsonProperty("settlementStatus")
    private SettlementStatus settlementStatus;
    @JsonProperty("instruction")
    private SettlementInstructionDto instruction;
}
