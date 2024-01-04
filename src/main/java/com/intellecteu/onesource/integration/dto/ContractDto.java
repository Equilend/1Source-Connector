package com.intellecteu.onesource.integration.dto;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.TRADE;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.ONESOURCE_ISSUE;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SPIRE_ISSUE;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwIfFieldMissedException;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.ContractStatus;
import com.intellecteu.onesource.integration.model.EventType;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.SettlementStatus;
import com.intellecteu.onesource.integration.services.Reconcilable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class ContractDto implements Reconcilable {

    private Long id;
    @JsonProperty("contractId")
    private String contractId;
    @JsonProperty("lastEvent")
    private TradeEventDto lastEvent;
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

    @Override
    public void validateForReconciliation() throws ValidationException {
        throwIfFieldMissedException(trade, TRADE);
        trade.validateForReconciliation();
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
        log.debug("Updated processing status to {} for contract: {}", processingStatus, contractId);
    }
}
