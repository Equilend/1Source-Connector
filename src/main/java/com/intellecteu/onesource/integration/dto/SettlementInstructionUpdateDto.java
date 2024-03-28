package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.intellecteu.onesource.integration.model.onesource.RoundingMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class SettlementInstructionUpdateDto {

    private Integer roundingRule;
    private RoundingMode roundingMode;
    private SettlementDto settlement;

    public SettlementInstructionUpdateDto(SettlementDto settlement) {
        this.settlement = settlement;
    }
}
