package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.model.ContractStatus;
import com.intellecteu.onesource.integration.model.EventType;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.SettlementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.intellecteu.onesource.integration.model.ProcessingStatus.ONESOURCE_ISSUE;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SPIRE_ISSUE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractDto {

    private long id;
    @JsonProperty("contractId")
    private String contractId;
    @JsonProperty("lastEventId")
    private Long lastEventId;
    @JsonProperty("contractStatus")
    private ContractStatus contractStatus;
    @JsonProperty("settlementStatus")
    private SettlementStatus settlementStatus;
    @JsonProperty("lastUpdatePartyId")
    private String lastUpdatePartyId;
    @JsonAlias({"lastUpdateDatetime", "lastUpdateDateTime"})
    private LocalDateTime lastUpdateDatetime;
    @JsonProperty("trade")
    private TradeAgreementDto trade;
    @JsonProperty("settlement")
    private List<SettlementDto> settlement;
    private ProcessingStatus processingStatus;
    private EventType eventType;
    private String matchingSpirePositionId;
    private FlowStatus flowStatus;

    public boolean isProcessedWithoutErrors() {
        return !Set.of(SPIRE_ISSUE, ONESOURCE_ISSUE).contains(processingStatus);
    }
}
