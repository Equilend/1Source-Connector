package com.intellecteu.onesource.integration.utils;

import static com.intellecteu.onesource.integration.constant.PositionConstant.BORROWER_POSITION_TYPE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.LENDER_POSITION_TYPE;
import static com.intellecteu.onesource.integration.model.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;

import com.intellecteu.onesource.integration.dto.TransactingPartyDto;
import com.intellecteu.onesource.integration.model.PartyRole;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

@UtilityClass
@Slf4j
public class IntegrationUtils {

    public final static String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static String formattedDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        return localDateTime.format(formatter);
    }

    /**
     * Deprecated since Flow II was implemented. See position.positiontypeDTO.positionType field to retrieve Lender or
     * Borrower role. Retrieve party role from Transacting party's `gleifLei` and Position's `lei` fields. The legal
     * identity identifier (lei) from transactingParties.party.gleiflei shall match with positionOutDTO.lei field to
     * retrieve the PartyRole.
     *
     * @param transactingParties List<TransactingPartyDto>
     * @param positionLei String
     * @return PartyRole for matched fields or null if there are no matches
     */
    @Deprecated(since = "Flow II", forRemoval = true)
    public static PartyRole extractPartyRole(List<TransactingPartyDto> transactingParties, String positionLei) {
        final PartyRole partyRole = transactingParties.stream()
            .filter(t -> positionLei.equals(t.getParty().getGleifLei()))
            .map(TransactingPartyDto::getPartyRole)
            .findAny()
            .orElse(null);
        log.debug("Position lei: {} matches with party role: {}", positionLei, partyRole);
        return partyRole;
    }

    /**
     * Retrieve party role from position type.
     *
     * @param positionType String
     * @return Optional of partyRole or empty if there are no matches
     */
    public static Optional<PartyRole> extractPartyRole(@Nullable String positionType) {
        if (positionType == null) {
            return Optional.empty();
        }
        if (positionType.contains(LENDER_POSITION_TYPE)) {
            return Optional.of(LENDER);
        }
        if (positionType.contains(BORROWER_POSITION_TYPE)) {
            return Optional.of(BORROWER);
        }
        return Optional.empty();

    }
}
