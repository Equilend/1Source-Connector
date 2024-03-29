package com.intellecteu.onesource.integration.model.onesource;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.REBATE;
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
public class Rate implements Reconcilable {

    @JsonIgnore
    private Long id;
    private RebateRate rebate;
    private FeeRate fee;

    @Override
    public void validateForReconciliation() throws ValidationException {
        throwIfFieldMissedException(rebate, REBATE, ONE_SOURCE_LOAN_CONTRACT);
        rebate.validateForReconciliation();
    }

    public Double getRebateRate() {
        return rebate == null ? null : rebate.retrieveBaseRate();
    }

}
