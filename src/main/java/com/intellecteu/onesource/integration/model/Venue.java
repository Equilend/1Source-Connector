package com.intellecteu.onesource.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.Set;
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
@Table(name = "venue")
public class Venue {

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
    private Set<VenueParty> venueParties;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "local_venue_field_id")
    private Set<LocalVenueField> localVenueFields;
}
