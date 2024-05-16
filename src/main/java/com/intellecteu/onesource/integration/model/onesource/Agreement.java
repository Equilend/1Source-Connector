package com.intellecteu.onesource.integration.model.onesource;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.TRADE;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwIfFieldMissedException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.enums.FieldSource;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.time.LocalDateTime;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Agreement implements Reconcilable {

    private Long id;
    private String agreementId;
    private LocalDateTime createDateTime;
    private LocalDateTime lastUpdateDateTime;
    private ProcessingStatus processingStatus;
    private String matchingSpirePositionId;
    private TradeAgreement trade;

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        if (agreementId != null) {
            log.debug("ProcessingStatus: {} was set for agreementId: {}", processingStatus, agreementId);
        }
        this.processingStatus = processingStatus;
    }

    @Override
    public void validateForReconciliation() throws ValidationException {
        throwIfFieldMissedException(trade, TRADE, FieldSource.ONE_SOURCE_TRADE_AGREEMENT);
        trade.validateForReconciliation();
    }

    public String unwrapVenueRefKey() {
        if (trade == null) {
            return null;
        }
        return trade.getVenues() == null ? null : trade.getVenues().get(0).getVenueRefKey();
    }
}
