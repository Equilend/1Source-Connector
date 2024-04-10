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
import static com.intellecteu.onesource.integration.model.enums.FieldSource.BACKOFFICE_POSITION;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwIfFieldMissedException;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
//@NoArgsConstructor
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
    private PositionAccount positionAccount;
    @JsonProperty("counterPartyDTO")
    private PositionAccount positionCpAccount;
    @JsonProperty("statusDTO")
    private PositionStatus positionStatus;

    public Position() {
        setIndexForDemo();
    }

    public void setIndex(Index index) {
        setIndexForDemo();
    }
    //TODO hardcode for the demo. Expected to be removed
    private void setIndexForDemo(){
        index = new Index();
        index.setIndexId(12);
        index.setIndexName("Fixed Rate");
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        log.debug("Processing status {} was set for positionId={}", processingStatus, positionId);
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

    @Override
    public void validateForReconciliation() throws ValidationException {
        throwIfFieldMissedException(positionSecurityDetail, POSITION_SECURITY, BACKOFFICE_POSITION);
        throwIfFieldMissedException(rate, RATE, BACKOFFICE_POSITION);
        throwIfFieldMissedException(index, POSITION_INDEX, BACKOFFICE_POSITION);
        throwIfFieldMissedException(quantity, POSITION_QUANTITY, BACKOFFICE_POSITION);
        throwIfFieldMissedException(currency, POSITION_CURRENCY, BACKOFFICE_POSITION);
        throwIfFieldMissedException(tradeDate, POSITION_TRADE_DATE, BACKOFFICE_POSITION);
        throwIfFieldMissedException(settleDate, SETTLE_DATE, BACKOFFICE_POSITION);
        throwIfFieldMissedException(deliverFree, DELIVER_FREE, BACKOFFICE_POSITION);
        throwIfFieldMissedException(price, POSITION_PRICE, BACKOFFICE_POSITION);
        throwIfFieldMissedException(amount, POSITION_AMOUNT, BACKOFFICE_POSITION);
        throwIfFieldMissedException(positionType, POSITION_TYPE, BACKOFFICE_POSITION);
        throwIfFieldMissedException(positionCpAccount, POSITION_CP_ACCOUNT, BACKOFFICE_POSITION);
        throwIfFieldMissedException(positionAccount, POSITION_ACCOUNT, BACKOFFICE_POSITION);

        positionSecurityDetail.validateForReconciliation();
        index.validateForReconciliation();
        currency.validateForReconciliation();
        positionType.validateForReconciliation();
        positionCpAccount.validateForReconciliation();
        positionAccount.validateForReconciliation();

    }
}
