package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellecteu.onesource.integration.model.enums.FlowStatus;
import java.time.LocalDateTime;
import java.util.List;
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
public class Contract {

    @JsonIgnore
    private Long id;
    private String contractId;
    private TradeEvent lastEvent;
    private ContractStatus contractStatus;
    private SettlementStatus settlementStatus;
    private String lastUpdatePartyId;
    private LocalDateTime lastUpdateDatetime;
    private TradeAgreement trade;
    private List<Settlement> settlement;
    private ProcessingStatus processingStatus;
    private EventType eventType;
    private String matchingSpirePositionId;
    @JsonIgnore
    private FlowStatus flowStatus;

}
