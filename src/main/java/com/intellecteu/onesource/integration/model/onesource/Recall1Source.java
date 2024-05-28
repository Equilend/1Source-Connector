package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecallStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Recall1Source {

    @NotNull
    private String recallId;
    private String contractId;
    private RecallStatus recallStatus;
    private ProcessingStatus processingStatus;
    private Long matchingSpireRecallId;
    private Long relatedSpirePositionId;
    @JsonAlias({"createUpdateDateTime", "createUpdateDatetime"})
    private LocalDateTime createUpdateDateTime;
    @JsonAlias({"lastUpdateDateTime", "lastUpdateDatetime"})
    private LocalDateTime lastUpdateDateTime;
    private Venue executionVenue;
    private Integer openQuantity;
    private Integer quantity;
    private LocalDate recallDate;
    private LocalDate recallDueDate;

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
        if (recallId != null) {
            log.debug("Updated processing status to {} for recallId: {}", processingStatus, recallId);
        }
    }

    /**
     * Get VenuePartyRefKey by the role.
     *
     * @param role PartyRole
     * @return String or null if there is no VenuePartyRefKey for this party for the venue.
     */
    public String getVenuePartyRefKeyByRole(@NotNull PartyRole role) {
        if (executionVenue == null) {
            return null;
        }
        return executionVenue.getVenueParties().stream()
            .filter(party -> role == party.getPartyRole())
            .findAny()
            .map(VenueParty::getVenuePartyRefKey)
            .orElse(null);
    }
}
