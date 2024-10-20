package com.intellecteu.onesource.integration.repository.entity.onesource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.intellecteu.onesource.integration.model.onesource.VenueType;
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
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "venue")
public class VenueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "partyId")
    private String partyId;
    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private VenueType type;
    @Column(name = "venue_name")
    private String venueName;
    @Column(name = "venue_ref_key")
    private String venueRefKey;
    @Column(name = "transaction_datetime", columnDefinition = "TIMESTAMP")
    private LocalDateTime transactionDatetime;
    //    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "venue_party_id")
    private Set<VenuePartyEntity> venueParties;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "local_venue_field_id")
    private Set<LocalVenueFieldEntity> localVenueFields;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "trade_id", referencedColumnName = "id")
//    @ToString.Exclude
//    private TradeAgreementEntity tradeAgreement;

}
