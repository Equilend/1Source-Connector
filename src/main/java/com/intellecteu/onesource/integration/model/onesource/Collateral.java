package com.intellecteu.onesource.integration.model.onesource;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_TYPE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_VALUE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CONTRACT_PRICE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CURRENCY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.util.LinkedList;
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
        var missedFields = new LinkedList<String>();
        if (contractPrice == null) {
            missedFields.add(CONTRACT_PRICE);
        }
        if (collateralValue == null) {
            missedFields.add(COLLATERAL_VALUE);
        }
        if (currency == null) {
            missedFields.add(CURRENCY);
        }
        if (type == null) {
            missedFields.add(COLLATERAL_TYPE);
        }
        if (!missedFields.isEmpty()) {
            throw new ValidationException(missedFields);
        }
    }

}
