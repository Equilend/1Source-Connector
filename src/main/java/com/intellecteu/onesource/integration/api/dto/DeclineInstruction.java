package com.intellecteu.onesource.integration.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.enums.RelatedProposalType;
import java.time.LocalDateTime;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeclineInstruction {

    @NotEmpty(message = "The Decline Instruction Id is required.")
    @JsonProperty(required = true)
    private String declineInstructionId;
    private String relatedExceptionEventId;
    private String relatedProposalId;
    private RelatedProposalType relatedProposalType;
    private LocalDateTime creationDateTime;
    private String userId;
    private String declineReasonCode;
    private String declineReasonText;
}
