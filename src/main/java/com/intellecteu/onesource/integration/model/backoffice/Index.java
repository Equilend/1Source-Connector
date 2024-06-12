package com.intellecteu.onesource.integration.model.backoffice;


import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_INDEX_NAME;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Index implements Reconcilable {

    private Integer indexId;
    private String indexName;
    private Double spread;

    @Override
    public void validateForReconciliation() throws ValidationException {
        List<String> missedFields = new ArrayList<>();
        if (indexName == null) {
            missedFields.add(POSITION_INDEX_NAME);
        }
        if (!missedFields.isEmpty()) {
            throw new ValidationException(missedFields);
        }
    }
}
