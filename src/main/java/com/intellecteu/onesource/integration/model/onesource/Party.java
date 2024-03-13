package com.intellecteu.onesource.integration.model.onesource;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.GLEIF_LEI;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwIfFieldMissedException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
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
        throwIfFieldMissedException(gleifLei, GLEIF_LEI);
    }

}
