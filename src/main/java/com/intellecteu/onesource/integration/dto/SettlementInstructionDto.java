package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SettlementInstructionDto {

    @JsonProperty("settlementBic")
    private String settlementBic;
    @JsonProperty("localAgentBic")
    private String localAgentBic;
    @JsonProperty("localAgentName")
    private String localAgentName;
    @JsonProperty("localAgentAcct")
    private String localAgentAcct;
    @JsonProperty("dtcParticipantNumber")
    private String dtcParticipantNumber;

    @JsonCreator
    public SettlementInstructionDto(@JsonProperty("settlementBic") String settlementBic,
        @JsonProperty("localAgentBic") String localAgentBic, @JsonProperty("localAgentName") String localAgentName,
        @JsonProperty("localAgentAcct") String localAgentAcct,
        @JsonProperty("dtcParticipantNumber") String dtcParticipantNumber) {
        this.settlementBic = settlementBic;
        this.localAgentBic = localAgentBic;
        this.localAgentName = localAgentName;
        this.localAgentAcct = localAgentAcct;
        this.dtcParticipantNumber = dtcParticipantNumber;
    }
}
