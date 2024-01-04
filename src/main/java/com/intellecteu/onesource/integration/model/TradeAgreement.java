package com.intellecteu.onesource.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "trade")
public class TradeAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "venue_id")
    @JsonProperty("executionVenue")
    private Venue venue;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "instrument_id")
    private Instrument instrument;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "rate_id")
    private Rate rate;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "currency")
    @Enumerated(value = EnumType.STRING)
    private CurrencyCd billingCurrency;
    @Column(name = "dividend_rate")
    private Integer dividendRatePct;
    @Column(name = "trade_date", columnDefinition = "DATE")
    private LocalDate tradeDate;
    @Column(name = "term_type")
    @Enumerated(value = EnumType.STRING)
    private TermType termType;
    @Column(name = "term_date", columnDefinition = "DATE")
    private LocalDate termDate;
    @Column(name = "settlement_date", columnDefinition = "DATE")
    private LocalDate settlementDate;
    @Column(name = "settlement_type")
    @Enumerated(value = EnumType.STRING)
    private SettlementType settlementType;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "collateral")
    private Collateral collateral;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "transacting_party_id")
    private List<TransactingParty> transactingParties;
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "resource_uri")
    private String resourceUri;
    @Column(name = "processing_status")
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;
}
