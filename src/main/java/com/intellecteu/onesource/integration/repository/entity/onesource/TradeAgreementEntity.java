package com.intellecteu.onesource.integration.repository.entity.onesource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.CurrencyCd;
import com.intellecteu.onesource.integration.model.onesource.SettlementType;
import com.intellecteu.onesource.integration.model.onesource.TermType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "trade")
public class TradeAgreementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @OneToMany(mappedBy = "tradeAgreement", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonProperty("venues")
    private List<VenueEntity> venues;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "instrument_id")
    private InstrumentEntity instrument;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "rate_id")
    private RateEntity rate;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "currency")
    @Enumerated(value = EnumType.STRING)
    private CurrencyCd billingCurrency;
    @Column(name = "dividend_rate")
    private Double dividendRatePct;
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
    private CollateralEntity collateral;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "transacting_party_id")
    private List<TransactingPartyEntity> transactingParties;
    @Column(name = "event_id")
    private String eventId;
    @Column(name = "resource_uri")
    private String resourceUri;
    @Column(name = "processing_status")
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;

    public void addVenue(VenueEntity venue) {
        if (this.venues == null) {
            this.venues = new ArrayList<>();
        }
        this.venues.add(venue);
        venue.setTradeAgreement(this);
    }

    public TradeAgreementEntity() {
        venues = new ArrayList<>();
    }
}
