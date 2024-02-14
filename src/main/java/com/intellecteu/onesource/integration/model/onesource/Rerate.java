package com.intellecteu.onesource.integration.model.onesource;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rerate {

    private String rerateId;
    private String contractId;
    private RerateStatus status;
    private Long matchingSpireTradeId;
    private Long relatedSpirePositionId;
    private LocalDateTime lastUpdateDatetime;
    private Venue executionVenue;
    private Rate rate;
    private Rate rerate;
    private ProcessingStatus processingStatus;
}
