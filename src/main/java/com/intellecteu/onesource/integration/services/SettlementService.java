package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.model.PartyRole;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

public interface SettlementService {

    List<SettlementDto> getSettlementInstruction(PositionDto positionDto);

    /**
     * Request settlement details by party and account id. The purpose of accountId parameter is to retrieve
     * counterpart's settlement instruction by providing account id for the counterparty. Return ResponseEntity to get
     * opportunity to record and handle response by its code.
     *
     * @param positionDto PositionDto position
     * @param partyRole PartyRole the role for the current position
     * @param accountId String the id for the current account or the counterpart's account
     * @return ResponseEntity of SettlementDto or empty list
     */
    ResponseEntity<SettlementDto> retrieveSettlementDetails(PositionDto positionDto,
        PartyRole partyRole, String accountId);

    SettlementDto persistSettlement(SettlementDto settlement);

    void updateSpireInstruction(@NonNull SettlementDto spireCpSettlement,
        @NonNull SettlementDto contractCpSettlement, @NonNull PartyRole partyRole);
}
