package com.intellecteu.onesource.integration.model.onesource;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.BILLING_CURRENCY;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.INSTRUMENT;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.QUANTITY;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SETTLEMENT_DATE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SETTLEMENT_TYPE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.TRADE_DATE;
import static com.intellecteu.onesource.integration.model.enums.FieldSource.ONE_SOURCE_LOAN_CONTRACT;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwIfFieldMissedException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.math.BigDecimal;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.time.LocalDate;
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
public class TradeAgreement implements Reconcilable {

    @JsonIgnore
    private Long id;
    @JsonProperty("executionVenue")
    private Venue venue;
    private Instrument instrument;
    private Rate rate;
    private BigDecimal quantity;
    private CurrencyCd billingCurrency;
    private Double dividendRatePct;
    private LocalDate tradeDate;
    private TermType termType;
    private LocalDate termDate;
    private LocalDate settlementDate;
    private SettlementType settlementType;
    private Collateral collateral;
    private List<TransactingParty> transactingParties;
    @JsonIgnore
    private String eventId;
    @JsonIgnore
    private String resourceUri;
    private ProcessingStatus processingStatus;

    @Override
    public void validateForReconciliation() throws ValidationException {
        throwIfFieldMissedException(instrument, INSTRUMENT, ONE_SOURCE_LOAN_CONTRACT);
        throwIfFieldMissedException(quantity, QUANTITY, ONE_SOURCE_LOAN_CONTRACT);
        throwIfFieldMissedException(billingCurrency, BILLING_CURRENCY, ONE_SOURCE_LOAN_CONTRACT);
        throwIfFieldMissedException(tradeDate, TRADE_DATE, ONE_SOURCE_LOAN_CONTRACT);
        throwIfFieldMissedException(settlementDate, SETTLEMENT_DATE, ONE_SOURCE_LOAN_CONTRACT);
        throwIfFieldMissedException(settlementType, SETTLEMENT_TYPE, ONE_SOURCE_LOAN_CONTRACT);
        throwIfFieldMissedException(collateral, COLLATERAL, ONE_SOURCE_LOAN_CONTRACT);
        rate.validateForReconciliation();
        collateral.validateForReconciliation();
        instrument.validateForReconciliation();
        validateParties();
    }

    public String retrieveVenueName() {
        return venue.getVenueName();
    }

    private void validateParties() throws ValidationException {
        for (var party : transactingParties) {
            party.getParty().validateForReconciliation();
        }
    }

}
