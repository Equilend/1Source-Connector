package com.intellecteu.onesource.integration.model.backoffice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PositionExposure {

    private Integer exposureId;
    private Double cpHaircut;
    private Integer cpMarkRoundTo;
    private Integer depoId;

}
