package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Party {

    @JsonIgnore
    private Long id;
    private String partyId;
    private String partyName;
    private String gleifLei;
    private String internalPartyId;

}
