package com.intellecteu.onesource.integration.model.integrationtoolkit;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
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
public class NackInstruction {

    private String nackInstructionId;
    private String relatedCloudEventId;
    private String relatedReturnId;
    private LocalDateTime creationDateTime;
    private String userId;
    private String nackReasonCode;
    private String nackReasonText;
    private ProcessingStatus processingStatus;
}
