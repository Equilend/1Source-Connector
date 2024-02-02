package com.intellecteu.onesource.integration.repository.entity.backoffice;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PositionExposureEntity {

    private Double cpHaircut;
    private Integer cpMarkRoundTo;
    private Integer depoId;

}
