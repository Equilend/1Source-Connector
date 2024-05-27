package com.intellecteu.onesource.integration.model.onesource;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Return {

    private String returnId;
    private String contractId;
    private ReturnStatus returnStatus;
    private ProcessingStatus processingStatus;
    private Long matchingSpireTradeId;
    private Long relatedSpirePositionId;
    private LocalDateTime createUpdateDatetime;
    private LocalDateTime lastUpdateDatetime;
    private Venue executionVenue;
    private Integer quantity;
    private Collateral collateral;
    private SettlementType settlementType;
    private LocalDate returnSettlementDate;
    private LocalDate returnDate;
    private AcknowledgementType acknowledgementType;
    private String description;
    private List<Settlement> settlement;
}
