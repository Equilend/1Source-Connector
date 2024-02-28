package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.onesource.VenueType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VenueDto {

    private Long id;
    @JsonProperty("partyId")
    private String partyId;
    @JsonProperty("type")
    private VenueType type;
    @JsonProperty("venueName")
    private String venueName;
    @JsonProperty("venueRefKey")
    private String venueRefKey;
    @JsonProperty("transactionDatetime")
    private LocalDateTime transactionDatetime;
    @JsonProperty("venueParties")
    private List<VenuePartyDto> venueParties;
    @JsonProperty("localVenueFields")
    private List<LocalVenueFieldsDto> localVenueFields;

    @JsonCreator
    public VenueDto(@JsonProperty("id") Long id, @JsonProperty("partyId") String partyId,
        @JsonProperty("type") VenueType type,
        @JsonProperty("venueName") String venueName, @JsonProperty("venueRefKey") String venueRefKey,
        @JsonProperty("transactionDatetime") LocalDateTime transactionDatetime,
        @JsonProperty("venueParties") List<VenuePartyDto> venueParties,
        @JsonProperty("localVenueFields") List<LocalVenueFieldsDto> localVenueFields) {
        this.id = id;
        this.partyId = partyId;
        this.type = type;
        this.venueName = venueName;
        this.venueRefKey = venueRefKey;
        this.transactionDatetime = transactionDatetime;
        this.venueParties = venueParties;
        this.localVenueFields = localVenueFields;
    }
}
