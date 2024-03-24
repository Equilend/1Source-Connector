package com.intellecteu.onesource.integration.dto;

import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class PartyDto implements Reconcilable {

    private String partyId;
    private String partyName;
    private String gleifLei;
    private String internalPartyId;

    @Override
    public void validateForReconciliation() throws ValidationException {
//        throwIfFieldMissedException(gleifLei, GLEIF_LEI);
    }
}
