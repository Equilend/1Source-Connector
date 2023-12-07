package com.intellecteu.onesource.integration.dto;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.TRADE;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwIfFieldMissedException;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.AgreementStatus;
import com.intellecteu.onesource.integration.model.EventType;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.services.Reconcilable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Builder
public class AgreementDto implements Reconcilable {

    private long id;
    private String agreementId;
    @JsonProperty("status")
    private AgreementStatus status;
    @JsonAlias({"lastUpdateDatetime", "lastUpdateDateTime"})
    private LocalDateTime lastUpdateDatetime;
    @JsonProperty("trade")
    private TradeAgreementDto trade;
    private EventType eventType;
    private String matchingSpirePositionId;
    private String matching1SourceLoanContractId;
    private FlowStatus flowStatus;
    private ProcessingStatus processingStatus;

    @Override
    public void validateForReconciliation() throws ValidationException {
        throwIfFieldMissedException(trade, TRADE);
        trade.validateForReconciliation();
    }
}
