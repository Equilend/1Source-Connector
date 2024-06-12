package com.intellecteu.onesource.integration.model.backoffice;


import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_ACCOUNT_LEI;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_CP_ACCOUNT_LEI;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Account implements Reconcilable {

    @JsonIgnore
    private Long id;
    private Long accountId;
    private String shortName;
    private String lei;
    private String oneSourceId;
    private Long dtc;

    @Override
    public void validateForReconciliation() throws ValidationException {
        List<String> missedFields = new ArrayList<>();

        if (lei == null) {
            missedFields.add(String.format("%s or %s", POSITION_ACCOUNT_LEI, POSITION_CP_ACCOUNT_LEI));
        }
        if (!missedFields.isEmpty()) {
            throw new ValidationException(missedFields);
        }
    }
}
