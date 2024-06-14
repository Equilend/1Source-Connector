package com.intellecteu.onesource.integration.model.onesource;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.REBATE_FLOATING_BENCHMARK;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.REBATE_FLOATING_EFFECTIVE_RATE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.time.LocalDate;
import java.util.LinkedList;
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
public class FloatingRate implements Reconcilable {

    @JsonIgnore
    private Long id;
    private Benchmark benchmark;
    private Double baseRate;
    private Double spread;
    private Double effectiveRate;
    private Boolean isAutoRerate;
    private Integer effectiveDateDelay;
    private LocalDate effectiveDate;
    private String cutoffTime;

    @Override
    public void validateForReconciliation() throws ValidationException {
        var missedFields = new LinkedList<String>();
        if (benchmark == null) {
            missedFields.add(REBATE_FLOATING_BENCHMARK);
        }
        if (effectiveRate == null) {
            missedFields.add(REBATE_FLOATING_EFFECTIVE_RATE);
        }
        if (!missedFields.isEmpty()) {
            throw new ValidationException(missedFields);
        }
    }
}
