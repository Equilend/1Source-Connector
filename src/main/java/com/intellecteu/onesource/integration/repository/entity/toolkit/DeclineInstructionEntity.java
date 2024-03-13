package com.intellecteu.onesource.integration.repository.entity.toolkit;

import com.intellecteu.onesource.integration.model.enums.RelatedProposalType;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "decline_instruction")
public class DeclineInstructionEntity {

    @Id
    @Column(name = "instruction_id")
    private String declineInstructionId;
    @Column(name = "exception_event_id")
    private String relatedExceptionEventId;
    @Column(name = "proposal_id")
    private String relatedProposalId;
    @Column(name = "proposal_type")
    @Enumerated(value = EnumType.STRING)
    private RelatedProposalType relatedProposalType;
    @Column(name = "creation_date_time", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime creationDateTime;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "reason_code")
    private String declineReasonCode;
    @Column(name = "reason_text")
    private String declineReasonText;
    @Column(name = "processing_status")
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;

    @PrePersist
    protected void onCreate() {
        if (creationDateTime == null) {
            creationDateTime = LocalDateTime.now();
        }
    }

}
