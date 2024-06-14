package com.intellecteu.onesource.integration.utils;

import static com.intellecteu.onesource.integration.constant.PositionConstant.BORROWER_POSITION_TYPE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.LENDER_POSITION_TYPE;
import static com.intellecteu.onesource.integration.exception.NoRequiredPartyRoleException.NO_PARTY_ROLE_EXCEPTION;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.LENDER;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.intellecteu.onesource.integration.exception.NoRequiredPartyRoleException;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.TradeOut;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpServerErrorException;

@UtilityClass
@Slf4j
public class IntegrationUtils {

    public final static String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static String formattedDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        return localDateTime.format(formatter);
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
        return extractPartyRole(position.unwrapPositionType());
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
     * Retrieve id from the 1Source event resource Uri. Expected URI format:
     * /v1/ledger/contracts/93f834ff-66b5-4195-892b-8f316ed77006
     *
     * @param resourceUri String
     * @return String contract id or the initial string if the format is unexpected
     */
    public static String parseIdFrom1SourceResourceUri(String resourceUri) {
        if (!resourceUri.contains("/")) {
            return resourceUri;
        }
        if (resourceUri.endsWith("/")) {
            resourceUri = resourceUri.substring(0, resourceUri.length() - 1);
        }
        return resourceUri.substring(resourceUri.lastIndexOf("/") + 1);
    }

    /**
     * Retrieve agreement id from the 1Source event resource Uri. Expected URI format:
     * /v1/ledger/agreements/93f834ff-66b5-4195-892b-8f316ed77006 The method is duplicate for contract id parsing, but
     * should be the separate as there are in progress requirements for parsing sub-entities from the resource uri
     *
     * @param resourceUri String
     * @return String agreement id or the initial string if the format is unexpected
     */
    public static String parseAgreementIdFrom1SourceResourceUri(String resourceUri) {
        try {
            if (!resourceUri.contains("/")) {
                return resourceUri;
            }
            if (resourceUri.endsWith("/")) {
                resourceUri = resourceUri.substring(0, resourceUri.length() - 1);
            }
            return resourceUri.substring(resourceUri.lastIndexOf("/") + 1);
        } catch (RuntimeException e) {
            String msg = "Error when parsing resource uri:" + resourceUri;
            log.debug(msg);
            throw new HttpServerErrorException(INTERNAL_SERVER_ERROR, msg);
        }

    }

    public static String toStringNullSafe(Object obj) {
        return obj != null ? obj.toString() : null;
    }
}

