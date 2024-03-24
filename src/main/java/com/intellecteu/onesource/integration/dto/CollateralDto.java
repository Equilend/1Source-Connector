package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.onesource.CollateralDescription;
import com.intellecteu.onesource.integration.model.onesource.CollateralType;
import com.intellecteu.onesource.integration.model.onesource.RoundingMode;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class CollateralDto implements Reconcilable {

    @JsonProperty("contractPrice")
    private Double contractPrice;
    @JsonProperty("contractValue")
    private Double contractValue;
    @JsonProperty("collateralValue")
    private Double collateralValue;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("type")
    private CollateralType type;
    @JsonProperty("descriptionCd")
    private CollateralDescription descriptionCd;
    @JsonProperty("margin")
    private Double margin;
    @JsonProperty("roundingRule")
    private Integer roundingRule;
    @JsonProperty("roundingMode")
    private RoundingMode roundingMode;

    @Override
    public void validateForReconciliation() throws ValidationException {
//        throwIfFieldMissedException(contractPrice, CONTRACT_PRICE);
//        throwIfFieldMissedException(collateralValue, COLLATERAL_VALUE);
//        throwIfFieldMissedException(currency, CURRENCY);
//        throwIfFieldMissedException(type, COLLATERAL_TYPE);
//        throwIfFieldMissedException(margin, COLLATERAL_MARGIN);
    }

}
