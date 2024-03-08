package com.intellecteu.onesource.integration.dto;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.INSTRUMENT;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.QUANTITY;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SETTLEMENT_DATE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SETTLEMENT_TYPE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.TRADE_DATE;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwIfFieldMissedException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.onesource.CurrencyCd;
import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.SettlementType;
import com.intellecteu.onesource.integration.model.onesource.TermType;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeAgreementDto implements Reconcilable {

    private Long id;
    @JsonProperty("executionVenue")
    private VenueDto executionVenue;
    @JsonProperty("instrument")
    private InstrumentDto instrument;
    @JsonProperty("rate")
    private RateDto rate;
    @JsonProperty("quantity")
    private Integer quantity;
    @JsonProperty("billingCurrency")
    private CurrencyCd billingCurrency;
    @JsonProperty("dividendRatePct")
    private Integer dividendRatePct;
    @JsonProperty("tradeDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tradeDate;
    @JsonProperty("termType")
    private TermType termType;
    @JsonProperty("termDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate termDate;
    @JsonProperty("settlementDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate settlementDate;
    @JsonProperty("settlementType")
    private SettlementType settlementType;
    @JsonProperty("collateral")
    private CollateralDto collateral;
    @JsonProperty("transactingParties")
    private List<TransactingPartyDto> transactingParties;
    @JsonProperty("eventId")
    private Long eventId;
    @JsonProperty("resourceUri")
    private String resourceUri;
    @Builder.Default
    private ProcessingStatus processingStatus = ProcessingStatus.CREATED;

    @JsonCreator
    public TradeAgreementDto(@JsonProperty("executionVenue") VenueDto executionVenue,
        @JsonProperty("instrument") InstrumentDto instrument, @JsonProperty("rate") RateDto rate,
        @JsonProperty("quantity") Integer quantity,
        @JsonProperty("billingCurrency") CurrencyCd billingCurrency,
        @JsonProperty("dividendRatePct") Integer dividendRatePct, @JsonProperty("tradeDate") LocalDate tradeDate,
        @JsonProperty("termType") TermType termType, @JsonProperty("termDate") LocalDate termDate,
        @JsonProperty("settlementDate") LocalDate settlementDate,
        @JsonProperty("settlementType") SettlementType settlementType,
        @JsonProperty("collateral") CollateralDto collateral,
        @JsonProperty("transactingParties") List<TransactingPartyDto> transactingParties,
        @JsonProperty("eventId") Long eventId,
        @JsonProperty("resourceUri") String resourceUri
    ) {
        this.executionVenue = executionVenue;
        this.instrument = instrument;
        this.rate = rate;
        this.quantity = quantity;
        this.billingCurrency = billingCurrency;
        this.dividendRatePct = dividendRatePct;
        this.tradeDate = tradeDate;
        this.termType = termType;
        this.termDate = termDate;
        this.settlementDate = settlementDate;
        this.settlementType = settlementType;
        this.collateral = collateral;
        this.transactingParties = transactingParties;
        this.eventId = eventId;
        this.resourceUri = resourceUri;
    }

    @Override
    public void validateForReconciliation() throws ValidationException {
        throwIfFieldMissedException(instrument, INSTRUMENT);
        throwIfFieldMissedException(quantity, QUANTITY);
        throwIfFieldMissedException(tradeDate, TRADE_DATE);
        throwIfFieldMissedException(settlementDate, SETTLEMENT_DATE);
        throwIfFieldMissedException(settlementType, SETTLEMENT_TYPE);
        throwIfFieldMissedException(collateral, COLLATERAL);
        rate.validateForReconciliation();
        collateral.validateForReconciliation();
        instrument.validateForReconciliation();
        validateParties();
    }

    public String retrieveVenueName() {
        return executionVenue.getVenueName();
    }

    private void validateParties() throws ValidationException {
        for (var party : transactingParties) {
            party.getParty().validateForReconciliation();
        }
    }
}
