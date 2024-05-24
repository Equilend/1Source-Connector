package com.intellecteu.onesource.integration.repository.entity.onesource;


import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecallStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recall_1source")
public class Recall1SourceEntity {

    @Id
    private String recallId;
    @Column(unique = true)
    private String contractId;
    @Enumerated(value = EnumType.STRING)
    private RecallStatus recallStatus;
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;
    private Long matchingSpireRecallId;
    private Long relatedSpirePositionId;
    private LocalDateTime createUpdateDateTime;
    @UpdateTimestamp
    private LocalDateTime lastUpdateDateTime;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "venue_id")
    private VenueEntity executionVenue;
    private Integer openQuantity;
    private Integer quantity;
    private LocalDate recallDate;
    private LocalDate recallDueDate;

}
