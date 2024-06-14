package com.intellecteu.onesource.integration.model.backoffice;


import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_CURRENCY_KY;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Currency implements Reconcilable {

    private String currencyKy;

    @Override
    public void validateForReconciliation() throws ValidationException {
        List<String> missedFields = new ArrayList<>();
        if (currencyKy == null) {
            missedFields.add(POSITION_CURRENCY_KY);
        }
        if (!missedFields.isEmpty()) {
            throw new ValidationException(missedFields);
        }
    }
}
