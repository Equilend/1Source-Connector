package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.PriceUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceDto {

    @JsonProperty("value")
    private Double value;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("unit")
    private PriceUnit unit;
}
