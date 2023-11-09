package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RebateRateDto {

    @JsonProperty("fixed")
    private FixedRateDto fixed;
    @JsonProperty("floating")
    private FloatingRateDto floating;

    public Double retrieveBaseRate() {
        return fixed == null ? floating.getBaseRate() : fixed.getBaseRate();
    }
}
