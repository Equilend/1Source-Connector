package com.intellecteu.onesource.integration.dto.spire;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tuples {

    @JsonProperty("lValue")
    private String lValue;
    @JsonProperty("operator")
    private String operator;
    @JsonProperty("rValue1")
    private String rValue1;
    @JsonProperty("rValue2")
    private String rValue2;
}
