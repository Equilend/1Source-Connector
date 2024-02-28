package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.Set;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Venue {

    @JsonIgnore
    private Long id;
    private String partyId;
    private VenueType type;
    private String venueName;
    private String venueRefKey;
    @JsonAlias({"transactionDatetime", "transactionDateTime"})
    private LocalDateTime transactionDateTime;
    private Set<VenueParty> venueParties;
    private Set<LocalVenueField> localVenueFields;

}
