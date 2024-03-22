package com.intellecteu.onesource.integration.utils;

import static com.intellecteu.onesource.integration.constant.PositionConstant.BORROWER_POSITION_TYPE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.LENDER_POSITION_TYPE;
import static com.intellecteu.onesource.integration.exception.NoRequiredPartyRoleException.NO_PARTY_ROLE_EXCEPTION;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.LENDER;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.dto.TransactingPartyDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.exception.NoRequiredPartyRoleException;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.TradeOut;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
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

    public static Optional<PartyRole> extractPartyRole(Position position) {
        String positionType = position.getPositionType() != null ? position.getPositionType().getPositionType() : null;
        return extractPartyRole(positionType);
    }

    public static boolean isLender(Position position) {
        return extractPartyRole(position).filter(role -> role == LENDER).isPresent();
    }

    public static boolean isLender(TradeOut tradeOut) {
        return isLender(tradeOut.getPosition());
    }

    public static boolean isBorrower(TradeOut tradeOut) {
        return isBorrower(tradeOut.getPosition());
    }

    public static boolean isBorrower(Position position) {
        return extractPartyRole(position).filter(role -> role == BORROWER).isPresent();
    }

    /**
     * Retrieve Lender or Borrower or throw NoRequiredPartyRoleException exception otherwise.
     *
     * @param positionDto PositionDto
     * @return Lender or Borrower PartyRole
     */
    public static PartyRole extractLenderOrBorrower(@Nullable PositionDto positionDto) {
        if (positionDto == null) {
            throw new NoRequiredPartyRoleException();
        }
        return extractLenderOrBorrower(positionDto.unwrapPositionType(), positionDto.getPositionId());
    }

    /**
     * Retrieve Lender or Borrower or throw NoRequiredPartyRoleException exception otherwise.
     *
     * @param positionType String
     * @param positionId String to log the position id for exception
     * @return Lender or Borrower PartyRole
     */
    public static PartyRole extractLenderOrBorrower(@Nullable String positionType, @Nullable String positionId) {
        if (positionType == null) {
            throw new NoRequiredPartyRoleException();
        }
        return extractPartyRole(positionType)
            .filter(role -> role == LENDER || role == BORROWER)
            .orElseThrow(() -> new NoRequiredPartyRoleException(
                format(NO_PARTY_ROLE_EXCEPTION, positionId)));
    }

    /**
     * Retrieve contract id from the 1Source event resource Uri. Expected URI format:
     * /v1/ledger/contracts/93f834ff-66b5-4195-892b-8f316ed77006
     *
     * @param resourceUri String
     * @return String contract id or the initial string if the format is unexpected
     */
    public static String parseContractIdFrom1SourceResourceUri(String resourceUri) {
        if (!resourceUri.contains("/")) {
            return resourceUri;
        }
        if (resourceUri.endsWith("/")) {
            resourceUri = resourceUri.substring(0, resourceUri.length() - 1);
        }
        return resourceUri.substring(resourceUri.lastIndexOf("/") + 1);
    }
}

