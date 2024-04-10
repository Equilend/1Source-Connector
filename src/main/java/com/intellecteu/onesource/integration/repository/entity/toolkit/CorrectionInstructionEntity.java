package com.intellecteu.onesource.integration.repository.entity.toolkit;

import com.intellecteu.onesource.integration.model.enums.CorrectionInstructionType;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RelatedProposalType;
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
@Table(name = "correction_instruction")
public class CorrectionInstructionEntity {
    @Id
    @Column(name = "instruction_id")
    private String instructionId;
    @Column(name = "instruction_type")
    @Enumerated(value = EnumType.STRING)
    private CorrectionInstructionType instructionType;
    @Column(name = "proposal_id")
    private String relatedProposalId;
    @Column(name = "proposal_type")
    @Enumerated(value = EnumType.STRING)
    private RelatedProposalType relatedProposalType;
    @Column(name = "creation_date_time", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime creationDateTime;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "amended_trade_id")
    private Long amendedTradeId;
    @Column(name = "old_trade_id")
    private Long oldTradeId;
    @Column(name = "processing_status")
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;
}
