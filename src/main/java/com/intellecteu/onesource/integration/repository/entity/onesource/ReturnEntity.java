package com.intellecteu.onesource.integration.repository.entity.onesource;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.AcknowledgementType;
import com.intellecteu.onesource.integration.model.onesource.ReturnStatus;
import com.intellecteu.onesource.integration.model.onesource.SettlementType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "return")
public class ReturnEntity {
    @Id
    private String returnId;
    private String contractId;
    @Enumerated(value = EnumType.STRING)
    private ReturnStatus returnStatus;
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;
    private Long matchingSpireTradeId;
    private Long relatedSpirePositionId;
    private LocalDateTime createUpdateDatetime;
    private LocalDateTime lastUpdateDatetime;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "venue_id")
    private VenueEntity executionVenue;
    private Integer quantity;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "collateral_id")
    private CollateralEntity collateral;
    private SettlementType settlementType;
    private LocalDateTime returnSettlementDate;
    @Enumerated(value = EnumType.STRING)
    private AcknowledgementType acknowledgementType;
    private String description;
    @OneToMany(fetch = FetchType.LAZY,
        cascade = CascadeType.ALL)
    @JoinColumn(name = "return_id")
    private List<SettlementEntity> settlement;
}
