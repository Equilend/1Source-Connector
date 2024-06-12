package com.intellecteu.onesource.integration.model.backoffice;

import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.DELIVER_FREE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_ACCOUNT;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_AMOUNT;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_CP_ACCOUNT;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_CURRENCY;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_INDEX;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_PRICE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_QUANTITY;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_SECURITY;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_TRADE_DATE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_TYPE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.RATE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.SETTLE_DATE;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private Long positionId;
    private Long tradeId;
    private LocalDateTime creationDatetime;
    private String venueRefId;
    private String customValue2;
    private String positionRef;
    private Double rate;
    private Double quantity;
    private LocalDateTime tradeDate;
    private Integer termId;
    private LocalDateTime endDate;
    private LocalDateTime settleDate;
    private LocalDateTime accrualDate;
    private Boolean deliverFree;
    private Double amount;
    private Double price;
    private Integer currencyId;
    private Long positionSecurityId;
    private ProcessingStatus processingStatus;
    private LocalDateTime lastUpdateDateTime;
    private String matching1SourceTradeAgreementId;
    private String matching1SourceLoanContractId;
    @JsonProperty("securityDetailDTO")
    private PositionSecurityDetail positionSecurityDetail;
    @JsonProperty("currencyDTO")
    private Currency currency;
    @JsonProperty("loanBorrowDTO")
    private LoanBorrow loanBorrow;
    @JsonProperty("exposureDTO")
    private PositionExposure exposure;
    @JsonAlias({"positiontypeDTO", "positionTypeDTO"})
    private PositionType positionType;
    @JsonProperty("indexDTO")
    private Index index;
    @JsonProperty("accountDTO")
    private Account positionAccount;
    @JsonProperty("counterPartyDTO")
    private Account positionCpAccount;
    @JsonProperty("statusDTO")
    private PositionStatus positionStatus;

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        if (positionId != null) {
            log.debug("Processing status {} was set for positionId: {}", processingStatus, positionId);
        }
        this.processingStatus = processingStatus;
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

    public boolean isNgtPosition() {
        return customValue2 != null;
    }

    @Override
    public void validateForReconciliation() throws ValidationException {
        List<String> missedFields = new ArrayList<>();

        if (positionSecurityDetail == null) {
            missedFields.add(POSITION_SECURITY);
        } else {
            missedFields.addAll(getMissedRequiredFields(positionSecurityDetail));
        }
        if (rate == null) {
            missedFields.add(RATE);
        }
        if (index == null) {
            missedFields.add(POSITION_INDEX);
        } else {
            missedFields.addAll(getMissedRequiredFields(index));
        }
        if (currency == null) {
            missedFields.add(POSITION_CURRENCY);
        } else {
            missedFields.addAll(getMissedRequiredFields(currency));
        }
        if (quantity == null) {
            missedFields.add(POSITION_QUANTITY);
        }
        if (tradeDate == null) {
            missedFields.add(POSITION_TRADE_DATE);
        }
        if (settleDate == null) {
            missedFields.add(SETTLE_DATE);
        }
        if (deliverFree == null) {
            missedFields.add(DELIVER_FREE);
        }
        if (price == null) {
            missedFields.add(POSITION_PRICE);
        }
        if (amount == null) {
            missedFields.add(POSITION_AMOUNT);
        }
        if (positionType == null) {
            missedFields.add(POSITION_TYPE);
        } else {
            missedFields.addAll(getMissedRequiredFields(positionType));
        }
        if (positionCpAccount == null) {
            missedFields.add(POSITION_CP_ACCOUNT);
        } else {
            missedFields.addAll(getMissedRequiredFields(positionCpAccount));
        }
        if (positionAccount == null) {
            missedFields.add(POSITION_ACCOUNT);
        } else {
            missedFields.addAll(getMissedRequiredFields(positionAccount));
        }
        if (!missedFields.isEmpty()) {
            throw new ValidationException(missedFields);
        }
    }
}
