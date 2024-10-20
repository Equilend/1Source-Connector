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
public class PositionTypeEntity {

    private Integer positionTypeId;
    private String positionType;
    private Boolean isCash;

}
