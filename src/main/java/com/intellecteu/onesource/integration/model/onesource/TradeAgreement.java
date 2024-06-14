package com.intellecteu.onesource.integration.model.onesource;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.BILLING_CURRENCY;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.INSTRUMENT;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.QUANTITY;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.RATE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SETTLEMENT_DATE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SETTLEMENT_TYPE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.TRADE_DATE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.TRANSACTING_PARTIES;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.time.LocalDate;
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
public class TradeAgreement implements Reconcilable {

    @JsonIgnore
    private Long id;
    @JsonProperty("venues")
    private List<Venue> venues;
    private Instrument instrument;
    private Rate rate;
    private Integer quantity;
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
        List<String> missedFields = new ArrayList<>();

        if (instrument == null) {
            missedFields.add(INSTRUMENT);
        } else {
            missedFields.addAll(getMissedRequiredFields(instrument));
        }
        if (quantity == null) {
            missedFields.add(QUANTITY);
        }
        if (billingCurrency == null) {
            missedFields.add(BILLING_CURRENCY);
        }
        if (tradeDate == null) {
            missedFields.add(TRADE_DATE);
        }
        if (settlementDate == null) {
            missedFields.add(SETTLEMENT_DATE);
        }
        if (settlementType == null) {
            missedFields.add(SETTLEMENT_TYPE);
        }
        if (rate == null) {
            missedFields.add(RATE);
        } else {
            missedFields.addAll(getMissedRequiredFields(rate));
        }
        if (collateral == null) {
            missedFields.add(COLLATERAL);
        } else {
            missedFields.addAll(getMissedRequiredFields(collateral));
        }
        if (transactingParties == null) {
            missedFields.add(TRANSACTING_PARTIES);
        } else {
            missedFields.addAll(validateParties());
        }
        if (!missedFields.isEmpty()) {
            throw new ValidationException(missedFields);
        }
    }

    private List<String> validateParties() {
        List<String> missedFields = new ArrayList<>();
        for (var party : transactingParties) {
            try {
                party.getParty().validateForReconciliation();
            } catch (ValidationException e) {
                missedFields.addAll(e.getInvalidFields());
            }
        }
        return missedFields;
    }
}
