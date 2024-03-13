package com.intellecteu.onesource.integration.model.onesource;

import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.time.LocalDateTime;
import java.util.LinkedList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rerate implements Reconcilable {

    private String rerateId;
    private String contractId;
    private RerateStatus rerateStatus;
    private Long matchingSpireTradeId;
    private Long relatedSpirePositionId;
    private LocalDateTime createUpdateDatetime;
    private LocalDateTime lastUpdateDatetime;
    private Venue executionVenue;
    private Rate rate;
    private Rate rerate;
    private ProcessingStatus processingStatus;

    @Override
    public void validateForReconciliation() throws ValidationException {
        var missedFields = new LinkedList<String>();
        if (rate != null && rate.getRebate() != null && rate.getRebate().getFixed() != null
            && rate.getRebate().getFixed().getBaseRate() == null) {
            missedFields.add("baseRate");
        }
        if (rate != null && rate.getRebate() != null && rate.getRebate().getFloating() != null
            && rate.getRebate().getFloating().getBenchmark() == null) {
            missedFields.add("benchmark");
        }
        if (rate != null && rate.getRebate() != null && rate.getRebate().getFloating() != null
            && rate.getRebate().getFloating().getEffectiveRate() == null) {
            missedFields.add("effectiveRate");
        }
        if (!missedFields.isEmpty()) {
            throw new ValidationException(missedFields);
        }
    }
}
