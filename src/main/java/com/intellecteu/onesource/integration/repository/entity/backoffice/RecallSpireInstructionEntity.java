package com.intellecteu.onesource.integration.repository.entity.backoffice;


import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecallInstructionType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recall_spire_instruction")
public class RecallSpireInstructionEntity {

    @Id
    private String instructionId;
    @NotNull
    @Enumerated(value = EnumType.STRING)
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
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;

}
