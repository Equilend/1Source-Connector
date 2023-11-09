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
public class TradeDTO {

    @JsonProperty("positionDTO")
    private PositionRequestDTO positionDTO;
    @JsonProperty("positionId")
    private Integer positionId;
}
