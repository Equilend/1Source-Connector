package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.VenueType;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VenueDto {
    @JsonProperty("type")
    private VenueType type;
    @JsonProperty("platform")
    private PlatformDto platform;
    @JsonProperty("venueParties")
    private List<VenuePartyDto> venueParties;

    @JsonCreator
    public VenueDto(@JsonProperty("type") VenueType type, @JsonProperty("platform") PlatformDto platform, @JsonProperty("venueParties") List<VenuePartyDto> venueParties) {
        this.type = type;
        this.platform = platform;
        this.venueParties = venueParties;
    }
}
