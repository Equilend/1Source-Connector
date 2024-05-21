package com.intellecteu.onesource.integration.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.intellecteu.onesource.integration.model.enums.RecallInstructionType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecallInstructionDTO {

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

}
