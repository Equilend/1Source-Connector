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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rerate {

    @Id
    private String rerateId;
    private String contractId;
    @Enumerated(value = EnumType.STRING)
    private RerateStatus status;
    private Long matchingSpireTradeId;
    private Long relatedSpirePositionId;
    private LocalDateTime lastUpdateDatetime;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "venue_id")
    private Venue executionVenue;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "rate_rate_id")
    private Rate rate;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "rerate_rate_id")
    private Rate rerate;
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;
}
