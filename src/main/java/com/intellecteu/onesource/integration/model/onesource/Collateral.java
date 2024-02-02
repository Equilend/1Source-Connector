package com.intellecteu.onesource.integration.model.onesource;

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
public class Collateral {

    private Long id;
    private Double contractPrice;
    private Double contractValue;
    private Double collateralValue;
    private String currency;
    private CollateralType type;
    private CollateralDescription description;
    private Double margin;
    private Integer roundingRule;
    private RoundingMode roundingMode;

}
