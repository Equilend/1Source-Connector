package com.intellecteu.onesource.integration.model.onesource;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.REBATE;
import static com.intellecteu.onesource.integration.model.enums.FieldSource.ONE_SOURCE_LOAN_CONTRACT;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwFieldMissedException;

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
public class RebateRate implements Reconcilable {

    @JsonIgnore
    private Long id;
    private FixedRate fixed;
    private FloatingRate floating;

    @Override
    public void validateForReconciliation() throws ValidationException {
        if (fixed == null && floating == null) {
            throwFieldMissedException(REBATE, ONE_SOURCE_LOAN_CONTRACT);
        }
        if (fixed != null) {
            fixed.validateForReconciliation();
        }
        if (floating != null) {
            floating.validateForReconciliation();
        }
    }

    public Double retrieveBaseRate() {
        if (fixed == null && floating == null) {
            return null;
        }
        return fixed == null ? floating.getEffectiveRate() : fixed.getBaseRate();
    }

}
