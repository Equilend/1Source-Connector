package com.intellecteu.onesource.integration.model.backoffice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecallInstructionType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecallSpireInstruction {

    @NotNull
    private String instructionId;
    @NotNull
    private RecallInstructionType instructionType;
    @NotNull
    private Long spireRecallId;
    @NotNull
    private String relatedContractId;
    @NotNull
    private Long relatedPositionId;
    @NotNull
    private LocalDateTime creationDateTime;
    private Integer quantity;
    private LocalDate recallDate;
    private LocalDate recallDueDate;
    private ProcessingStatus processingStatus;

}
