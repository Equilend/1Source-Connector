package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformDto {

    @JsonProperty("gleifLei")
    private String gleifLei;
    @JsonProperty("legalName")
    private String legalName;
    @JsonProperty("mic")
    private String mic;
    @JsonProperty("venueName")
    private String venueName;
    @JsonProperty("venueRefId")
    private String venueRefId;
    @JsonProperty("transactionDatetime")
    private LocalDateTime transactionDatetime;
}
