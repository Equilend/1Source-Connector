package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class LocalVenueField {

    @JsonIgnore
    private Long id;
    private String localFieldName;
    private String localFieldValue;
}
