package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SettlementInstruction {

    @JsonIgnore
    private Long id;
    private String settlementBic;
    private String localAgentBic;
    private String localAgentName;
    private String localAgentAcct;
    private String dtcParticipantNumber;
    private String cdsCustomerUnitId;
    private String custodianName;
    private String custodianBic;
    private String custodianAcct;

}
