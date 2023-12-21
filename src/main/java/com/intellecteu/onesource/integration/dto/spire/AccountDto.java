package com.intellecteu.onesource.integration.dto.spire;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto {

    private Long dtc;
    private String lei;
    private String shortName;
    @JsonProperty("accountId")
    private String info; //todo rename to accountId (conflicts with counterpartyDto)

}
