package com.intellecteu.onesource.integration.model.integrationtoolkit;

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
public class DeclineInstruction {

    private String declineInstructionId;
    private String relatedExceptionEventId;
    private String relatedProposalId;
    private RelatedProposalType relatedProposalType;
    private LocalDateTime creationDateTime;
    private String userId;
    private String declineReasonCode;
    private String declineReasonText;
    private ProcessingStatus processingStatus;

}
