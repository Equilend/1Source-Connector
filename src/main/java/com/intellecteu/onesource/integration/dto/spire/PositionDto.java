package com.intellecteu.onesource.integration.dto.spire;

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
import com.intellecteu.onesource.integration.services.client.spire.dto.AccountDTO;
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
public class PositionDto implements Reconcilable {

    private String positionId;
    private String venueRefId;
    private String customValue2;
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
    private SecurityDetailDto securityDetailDto;
    @JsonProperty("currencyDTO")
    private CurrencyDto currency;
    @JsonProperty("loanBorrowDTO")
    private LoanBorrowDto loanBorrowDto;
    @JsonProperty("collateralTypeDTO")
    private PositionCollateralTypeDto collateralTypeDto;
    @JsonProperty("exposureDTO")
    private PositionExposureDto exposureDto;
    @JsonProperty("positionTypeDTO")
    private PositionTypeDto positionTypeDto;
    @JsonProperty("accountDTO")
    private AccountDTO accountDto;
    @JsonProperty("counterPartyDTO")
    private AccountDTO cpDto;
    @JsonProperty("statusDTO")
    private PositionStatusDto positionStatusDto;
    @JsonProperty("indexDto")
    private IndexDto indexDto;

    public String getAccountLei() {
        return accountDto == null ? "" : accountDto.getLei();
    }

    public String getShortName() {
        return accountDto == null ? "" : accountDto.getShortName();
    }

    public String getCpLei() {
        return cpDto == null ? "" : cpDto.getLei();
    }

    public Integer getDepoId() {
        return exposureDto == null ? null : exposureDto.getDepoId();
    }

    public Integer getCpMarkRoundTo() {
        return exposureDto == null ? null : exposureDto.getCpMarkRoundTo();
    }

    public Double getCpHaircut() {
        return exposureDto == null ? null : exposureDto.getCpHaircut();
    }

    public String getCollateralType() {
        return collateralTypeDto == null ? null : collateralTypeDto.getCollateralType();
    }

    public String unwrapPositionType() {
        if (positionTypeDto == null) {
            return null;
        }
        return positionTypeDto.getPositionType();
    }

    public String unwrapPositionStatus() {
        if (positionStatusDto == null) {
            return null;
        }
        return positionStatusDto.getStatus();
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
        if (exposureDto == null || exposureDto.getCpHaircut() == null) {
            sb.append(CP_HAIRCUT).append(COMMA_DELIMITER);
        }
        if (accountDto == null || accountDto.getLei() == null) {
            sb.append(ACCOUNT_LEI).append(COMMA_DELIMITER);
        }
        if (cpDto == null || cpDto.getLei() == null) {
            sb.append(CP_LEI).append(COMMA_DELIMITER);
        }
        if (securityDetailDto == null) {
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
        if (securityDetailDto != null) {
            var isAtLeastOneSecurityFieldPresent = Stream
                .of(securityDetailDto.getCusip(), securityDetailDto.getIsin(),
                    securityDetailDto.getSedol(), securityDetailDto.getQuickCode())
                .anyMatch(Objects::nonNull);
            if (!isAtLeastOneSecurityFieldPresent) {
                log.debug("Validation failed. At least one security field must be present!");
                return String.format("%s, %s, %s, %s", POSITION_CUSIP, POSITION_ISIN, POSITION_SEDOL, POSITION_QUICK);
            }
        }

        return "";
    }
}
