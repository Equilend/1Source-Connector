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
public class RebateRate implements Reconcilable {

    @JsonIgnore
    private Long id;
    private FixedRate fixed;
    private FloatingRate floating;

    @Override
    public void validateForReconciliation() throws ValidationException {
        var missedFields = new LinkedList<String>();
        if (fixed == null && floating == null) {
            missedFields.add(REBATE);
        }
        if (fixed != null) {
            missedFields.addAll(getMissedRequiredFields(fixed));
        }
        if (floating != null) {
            missedFields.addAll(getMissedRequiredFields(floating));
        }
        if (!missedFields.isEmpty()) {
            throw new ValidationException(missedFields);
        }
    }

    public Double retrieveBaseRate() {
        if (fixed == null && floating == null) {
            return null;
        }
        return fixed == null ? floating.getEffectiveRate() : fixed.getBaseRate();
    }

}
