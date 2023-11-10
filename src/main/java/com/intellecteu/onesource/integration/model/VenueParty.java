package com.intellecteu.onesource.integration.model;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "venue_party")
public class VenueParty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "party_role")
    @Enumerated(value = EnumType.STRING)
    private PartyRole partyRole;
    @JsonProperty("venuePartyId")
    @Column(name = "venue_id")
    private String venueId;
    @OneToOne(fetch = FetchType.EAGER, cascade= CascadeType.ALL)
    @JoinColumn(name = "internal_ref_id")
    private InternalReference internalRef;
}
