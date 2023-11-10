package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.CollateralDescription;
import com.intellecteu.onesource.integration.model.CollateralType;
import com.intellecteu.onesource.integration.model.CurrencyCd;
import com.intellecteu.onesource.integration.model.RoundingMode;
import com.intellecteu.onesource.integration.services.Reconcilable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_MARGIN;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_TYPE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_VALUE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CONTRACT_VALUE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CURRENCY;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwFieldMissedException;

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
        // temporary remove contract price checking as the data should be provided later
//        if (contractPrice == null) {
//            throwFieldMissedException(CONTRACT_PRICE);
//        }
        if (collateralValue == null) {
            throwFieldMissedException(COLLATERAL_VALUE);
        }
        if (currency == null) {
            throwFieldMissedException(CURRENCY);
        }
        if (type == null) {
            throwFieldMissedException(COLLATERAL_TYPE);
        }
        if (margin == null) {
            throwFieldMissedException(COLLATERAL_MARGIN);
        }
    }

}
