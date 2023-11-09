package com.intellecteu.onesource.integration.dto.spire;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SwiftbicDTO {

    @JsonProperty("bic")
    private String bic;
    @JsonProperty("branch")
    private String branch;
}
