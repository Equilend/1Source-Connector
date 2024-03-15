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
public class DeclineInstructionDto {

    @NotEmpty(message = "The Decline Instruction Id is required.")
    @JsonProperty(required = true)
    private String declineInstructionId;
    @NotEmpty(message = "The Related Exception Event Id is required.")
    @JsonProperty(required = true)
    private String relatedExceptionEventId;
    @NotEmpty(message = "The Related Proposal Id is required.")
    @JsonProperty(required = true)
    private String relatedProposalId;
    private RelatedProposalType relatedProposalType;
    @NotEmpty(message = "The Creation Date Time is required.")
    @JsonProperty(required = true)
    private LocalDateTime creationDateTime;
    private String userId;
    private String declineReasonCode;
    private String declineReasonText;
}
