package com.intellecteu.onesource.integration.model.onesource;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.REBATE;

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
public class Rate implements Reconcilable {

    @JsonIgnore
    private Long id;
    private RebateRate rebate;
    private FeeRate fee;

    @Override
    public void validateForReconciliation() throws ValidationException {
        var missedFields = new LinkedList<String>();
        if (rebate == null) {
            missedFields.add(REBATE);
        } else {
            missedFields.addAll(getMissedRequiredFields(rebate));
        }
        if (!missedFields.isEmpty()) {
            throw new ValidationException(missedFields);
        }
    }

    public Double getRebateRate() {
        return rebate == null ? null : rebate.retrieveBaseRate();
    }

}
