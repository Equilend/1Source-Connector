package com.intellecteu.onesource.integration.model.integrationtoolkit;

import com.intellecteu.onesource.integration.model.enums.CorrectionInstructionType;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RelatedProposalType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionInstruction {
    private String instructionId;
    private CorrectionInstructionType instructionType;
    private String relatedProposalId;
    private RelatedProposalType relatedProposalType;
    private LocalDateTime creationDateTime;
    private String userId;
    private Long amendedTradeId;
    private Long oldTradeId;
    private ProcessingStatus processingStatus;
}
