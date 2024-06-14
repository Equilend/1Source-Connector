package com.intellecteu.onesource.integration.repository.entity.toolkit;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
@Table(name = "nack_instruction")
public class NackInstructionEntity {
    @Id
    @Column(name = "nack_instruction_id")
    private String nackInstructionId;
    @Column(name = "related_cloud_event_id")
    private String relatedCloudEventId;
    @Column(name = "related_return_id")
    private String relatedReturnId;
    @Column(name = "creation_date_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDateTime;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "nack_reason_code")
    private String nackReasonCode;
    @Column(name = "nack_reason_text")
    private String nackReasonText;
    @Column(name = "processing_status")
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;
}
