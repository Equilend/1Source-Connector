package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.CurrencyCd;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.SettlementType;
import com.intellecteu.onesource.integration.model.TermType;
import com.intellecteu.onesource.integration.services.Reconcilable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.INSTRUMENT;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.QUANTITY;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SETTLEMENT_DATE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SETTLEMENT_TYPE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.TRADE_DATE;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwFieldMissedException;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeAgreementDto implements Reconcilable {

    private long id;
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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime tradeDate;
    @JsonProperty("termType")
    private TermType termType;
    @JsonProperty("termDate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime termDate;
    @JsonProperty("settlementDate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime settlementDate;
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
    private ProcessingStatus processingStatus = ProcessingStatus.NEW;

    @JsonCreator
    public TradeAgreementDto(@JsonProperty("executionVenue") VenueDto executionVenue,
        @JsonProperty("instrument") InstrumentDto instrument, @JsonProperty("rate") RateDto rate,
        @JsonProperty("quantity") Integer quantity,
        @JsonProperty("billingCurrency") CurrencyCd billingCurrency,
        @JsonProperty("dividendRatePct") Integer dividendRatePct, @JsonProperty("tradeDate") LocalDateTime tradeDate,
        @JsonProperty("termType") TermType termType, @JsonProperty("termDate") LocalDateTime termDate,
        @JsonProperty("settlementDate") LocalDateTime settlementDate,
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
        if (instrument == null) {
            throwFieldMissedException(INSTRUMENT);
        } else {
            instrument.validateForReconciliation();
        }
        rate.validateForReconciliation();
        if (quantity == null) {
            throwFieldMissedException(QUANTITY);
        }
        if (tradeDate == null) {
            throwFieldMissedException(TRADE_DATE);
        }
        if (settlementDate == null) {
            throwFieldMissedException(SETTLEMENT_DATE);
        }
        if (settlementType == null) {
            throwFieldMissedException(SETTLEMENT_TYPE);
        }
        if (collateral == null) {
            throwFieldMissedException(COLLATERAL);
        }
        collateral.validateForReconciliation();
        validateParties();
    }

    public String retrieveVenueName() {
        return executionVenue.getPlatform().getVenueName();
    }

    private void validateParties() throws ValidationException {
        for (var party : transactingParties) {
            party.getParty().validateForReconciliation();
        }
    }
}
