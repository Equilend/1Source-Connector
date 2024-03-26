package com.intellecteu.onesource.integration.model.onesource;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_TYPE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_VALUE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CONTRACT_PRICE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CURRENCY;
import static com.intellecteu.onesource.integration.model.enums.FieldSource.ONE_SOURCE_LOAN_CONTRACT;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwIfFieldMissedException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
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
public class Collateral implements Reconcilable {

    @JsonIgnore
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

    @Override
    public void validateForReconciliation() throws ValidationException {
        throwIfFieldMissedException(contractPrice, CONTRACT_PRICE, ONE_SOURCE_LOAN_CONTRACT);
        throwIfFieldMissedException(collateralValue, COLLATERAL_VALUE, ONE_SOURCE_LOAN_CONTRACT);
        throwIfFieldMissedException(currency, CURRENCY, ONE_SOURCE_LOAN_CONTRACT);
        throwIfFieldMissedException(type, COLLATERAL_TYPE, ONE_SOURCE_LOAN_CONTRACT);
    }

}
