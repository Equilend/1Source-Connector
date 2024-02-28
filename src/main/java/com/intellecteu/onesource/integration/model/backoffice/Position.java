package com.intellecteu.onesource.integration.model.backoffice;

import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.ACCOUNT_LEI;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.COMMA_DELIMITER;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.CP_HAIRCUT;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.CP_LEI;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.DELIVER_FREE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_AMOUNT;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_CURRENCY;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_CUSIP;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_ISIN;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_PRICE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_QUANTITY;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_QUICK;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_SECURITY;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_SEDOL;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_TRADE_DATE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.RATE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.SETTLE_DATE;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwFieldMissedException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import com.intellecteu.onesource.integration.services.Reconcilable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Position implements Reconcilable {

    private String positionId;
    private String venueRefId;
    private String customValue2;
    private String positionRef;
    private Double rate;
    private Double quantity;
    private LocalDateTime tradeDate;
    private Integer termId;
    private LocalDateTime endDate;
    private LocalDateTime settleDate;
    private Boolean deliverFree;
    private Double amount;
    private Double price;
    private Double contractValue;
    private Integer currencyId;
    private Long securityId;
    private Integer positionTypeId;
    private ProcessingStatus processingStatus;
    private LocalDateTime lastUpdateDateTime;
    private String matching1SourceTradeAgreementId;
    private String matching1SourceLoanContractId;
    private Long applicableInstructionId;
    @JsonProperty("securityDetailDTO")
    private PositionSecurityDetail positionSecurityDetail;
    @JsonProperty("currencyDTO")
    private Currency currency;
    @JsonProperty("loanBorrowDTO")
    private LoanBorrow loanBorrow;
    @JsonProperty("collateralTypeDTO")
    private PositionCollateralType positionCollateralType;
    @JsonProperty("exposureDTO")
    private PositionExposure exposure;
    @JsonProperty("positiontypeDTO")
    private PositionType positionType;
    @JsonProperty("indexDTO")
    private Index index;
    @JsonProperty("accountDTO")
    private PositionAccount positionAccount;
    @JsonProperty("counterPartyDTO")
    private PositionAccount positionCpAccount;
    @JsonProperty("statusDTO")
    private PositionStatus positionStatus;

    public String getCollateralType() {
        return positionCollateralType == null ? null : positionCollateralType.getCollateralType();
    }

    public Double getCpHaircut() {
        return exposure == null ? null : exposure.getCpHaircut();
    }

    public Integer getCpMarkRoundTo() {
        return exposure == null ? null : exposure.getCpMarkRoundTo();
    }

    public String getShortName() {
        return positionAccount == null ? "" : positionAccount.getShortName();
    }

    public String getAccountLei() {
        return positionAccount == null ? "" : positionAccount.getLei();
    }

    public String getCpLei() {
        return positionCpAccount == null ? "" : positionCpAccount.getLei();
    }

    public Integer getDepoId() {
        return exposure == null ? null : exposure.getDepoId();
    }

    public String unwrapPositionType() {
        if (positionType == null) {
            return null;
        }
        return positionType.getPositionType();
    }

    public String unwrapPositionStatus() {
        if (positionStatus == null) {
            return null;
        }
        return positionStatus.getStatus();
    }

    @Override
    public void validateForReconciliation() throws ValidationException {
        var sb = new StringBuilder();
        if (rate == null) {
            sb.append(RATE).append(COMMA_DELIMITER);
        }
        if (quantity == null) {
            sb.append(POSITION_QUANTITY).append(COMMA_DELIMITER);
        }
        if (tradeDate == null) {
            sb.append(POSITION_TRADE_DATE).append(COMMA_DELIMITER);
        }
        if (settleDate == null) {
            sb.append(SETTLE_DATE).append(COMMA_DELIMITER);
        }
        if (price == null) {
            sb.append(POSITION_PRICE).append(COMMA_DELIMITER);
        }
        if (amount == null) {
            sb.append(POSITION_AMOUNT).append(COMMA_DELIMITER);
        }
        if (currency == null || currency.getCurrencyKy() == null) {
            sb.append(POSITION_CURRENCY).append(COMMA_DELIMITER);
        }
        if (exposure == null || exposure.getCpHaircut() == null) {
            sb.append(CP_HAIRCUT).append(COMMA_DELIMITER);
        }
        if (positionAccount == null || positionAccount.getLei() == null) {
            sb.append(ACCOUNT_LEI).append(COMMA_DELIMITER);
        }
        if (positionCpAccount == null || positionCpAccount.getLei() == null) {
            sb.append(CP_LEI).append(COMMA_DELIMITER);
        }
        if (positionSecurityDetail == null) {
            sb.append(POSITION_SECURITY).append(COMMA_DELIMITER);
        }
        if (deliverFree == null) {
            sb.append(DELIVER_FREE).append(COMMA_DELIMITER);
        }
        final String failedIdentifiers = getSecurityIdentifiersOnFailedValidation();
        if (!failedIdentifiers.isEmpty()) {
            sb.append(failedIdentifiers);
        }
        if (!sb.isEmpty()) {
            throwFieldMissedException(sb.toString());
        }
    }

    /*
     * Return a String with security identifiers that are null.
     * Return empty String if validation success and at least one identifier is present.
     */
    private String getSecurityIdentifiersOnFailedValidation() {
        if (positionSecurityDetail != null) {
            var isAtLeastOneSecurityFieldPresent = Stream
                .of(positionSecurityDetail.getCusip(), positionSecurityDetail.getIsin(),
                    positionSecurityDetail.getSedol(), positionSecurityDetail.getQuickCode())
                .anyMatch(Objects::nonNull);
            if (!isAtLeastOneSecurityFieldPresent) {
                log.debug("Validation failed. At least one security field must be present!");
                return String.format("%s, %s, %s, %s", POSITION_CUSIP, POSITION_ISIN, POSITION_SEDOL, POSITION_QUICK);
            }
        }

        return "";
    }
}
