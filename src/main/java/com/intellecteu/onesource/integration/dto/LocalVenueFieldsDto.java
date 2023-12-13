package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalVenueFieldsDto {

    @JsonProperty("localFieldName")
    private String localFieldName;
    @JsonProperty("localFieldValue")
    private String localFieldValue;
}
