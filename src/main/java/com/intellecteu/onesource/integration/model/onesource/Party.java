package com.intellecteu.onesource.integration.model.onesource;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.GLEIF_LEI;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.util.LinkedList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Party implements Reconcilable {

    @JsonIgnore
    private Long id;
    private String partyId;
    private String partyName;
    private String gleifLei;
    private String internalPartyId;

    @Override
    public void validateForReconciliation() throws ValidationException {
        var missedFields = new LinkedList<String>();
        if (gleifLei == null) {
            missedFields.add(GLEIF_LEI);
        }
        if (!missedFields.isEmpty()) {
            throw new ValidationException(missedFields);
        }
    }

}
