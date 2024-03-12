package com.intellecteu.onesource.integration.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellecteu.onesource.integration.model.onesource.ContractStatus;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.onesource.SettlementStatus;
import com.intellecteu.onesource.integration.model.onesource.TradeAgreement;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ContractDto {

    @JsonIgnore
    private Long id;
    private String contractId;
    private TradeEvent lastEvent;
    private ContractStatus contractStatus;
    private SettlementStatus settlementStatus;
    private String lastUpdatePartyId;
    @JsonAlias({"lastUpdateDatetime", "lastUpdateDateTime"})
    private LocalDateTime lastUpdateDateTime;
    private TradeAgreement trade;
    private List<Settlement> settlement;
    private ProcessingStatus processingStatus;
    private EventType eventType;
    private String matchingSpirePositionId;

}
