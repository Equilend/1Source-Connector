package com.intellecteu.onesource.integration.repository.entity.backoffice;


import com.intellecteu.onesource.integration.model.backoffice.RecallId;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecallStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recall")
@IdClass(RecallId.class)
public class RecallEntity {

    @Id
    private String recallId;
    @Id
    private Long relatedPositionId;
    @Column(name = "matching_1source_recall_id")
    private String matching1SourceRecallId;
    @NotNull
    private String relatedContractId;
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;
    @Enumerated(value = EnumType.STRING)
    private RecallStatus status;
    @NotNull
    private LocalDateTime creationDateTime;
    @NotNull
    @UpdateTimestamp(source = SourceType.DB)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastUpdateDateTime;
    private Integer openQuantity;
    private Integer quantity;
    private LocalDate recallDate;
    private LocalDate recallDueDate;

}
