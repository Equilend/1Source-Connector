package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.PartyRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactingPartyDto {

    @JsonProperty("partyRole")
    private PartyRole partyRole;
    @JsonProperty("party")
    private PartyDto party;
}
