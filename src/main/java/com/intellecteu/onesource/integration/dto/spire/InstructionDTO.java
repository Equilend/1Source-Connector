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
public class InstructionDTO {

    @JsonProperty("agentBicDTO")
    private SwiftbicDTO agentBicDTO;
    @JsonProperty("accountDTO")
    private AccountDto accountDTO;
    @JsonProperty("agentName")
    private String agentName;
    @JsonProperty("agentSafe")
    private String agentSafe;

}
