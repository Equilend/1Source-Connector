package com.intellecteu.onesource.integration.utils;

import com.intellecteu.onesource.integration.dto.TransactingPartyDto;
import com.intellecteu.onesource.integration.model.PartyRole;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@UtilityClass
@Slf4j
public class IntegrationUtils {


  /**
   * Retrieve party role from Transacting party's `gleifLei` and Position's `lei` fields.
   * The legal identity identifier (lei) from transactingParties.party.gleiflei
   * shall match with positionOutDTO.lei field to retrieve the PartyRole.
   *
   * @param transactingParties List<TransactingPartyDto>
   * @param positionLei String
   * @return PartyRole for matched fields or null if there are no matches
   */
  public static PartyRole extractPartyRole(List<TransactingPartyDto> transactingParties, String positionLei) {
    final PartyRole partyRole = transactingParties.stream()
        .filter(t -> positionLei.equals(t.getParty().getGleifLei()))
        .map(TransactingPartyDto::getPartyRole)
        .findAny()
        .orElse(null);
    log.debug("Position lei: {} matches with party role: {}", positionLei, partyRole);
    return partyRole;
  }
}
