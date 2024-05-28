package com.intellecteu.onesource.integration.model.backoffice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecallStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class RecallSpire {

    @NotNull
    private Long recallId;
    @NotNull
    private Long relatedPositionId;
    private String matching1SourceRecallId;
    @NotNull
    private String relatedContractId;
    private ProcessingStatus processingStatus;
    private RecallStatus status;
    @NotNull
    private LocalDateTime creationDateTime;
    @NotNull
    private LocalDateTime lastUpdateDateTime;
    private Integer openQuantity;
    private Integer quantity;
    private LocalDate recallDate;
    private LocalDate recallDueDate;

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
        if (recallId != null) {
            log.debug("Updated processing status to {} for recallId: {}", processingStatus, recallId);
        }
    }
}
