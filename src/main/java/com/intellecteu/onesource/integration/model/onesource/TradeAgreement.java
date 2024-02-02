package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeAgreement {

    private Long id;
    @JsonProperty("executionVenue")
    private Venue venue;
    private Instrument instrument;
    private Rate rate;
    private Integer quantity;
    private CurrencyCd billingCurrency;
    private Integer dividendRatePct;
    private LocalDate tradeDate;
    private TermType termType;
    private LocalDate termDate;
    private LocalDate settlementDate;
    private SettlementType settlementType;
    private Collateral collateral;
    private List<TransactingParty> transactingParties;
    private Long eventId;
    private String resourceUri;
    private ProcessingStatus processingStatus;

}
