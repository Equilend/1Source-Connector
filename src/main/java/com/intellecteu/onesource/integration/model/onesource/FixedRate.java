package com.intellecteu.onesource.integration.model.onesource;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.REBATE_FIXED_BASE_RATE;
import static com.intellecteu.onesource.integration.model.enums.FieldSource.ONE_SOURCE_LOAN_CONTRACT;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwIfFieldMissedException;

import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fixed")
public class FixedRate implements Reconcilable {

    public static final String FIXED_INDEX_NAME = "Fixed Rate";
    private Long id;
    private Double baseRate;
    private Double effectiveRate;
    private LocalDate effectiveDate;
    private String cutoffTime;

    @Override
    public void validateForReconciliation() throws ValidationException {
        throwIfFieldMissedException(baseRate, REBATE_FIXED_BASE_RATE, ONE_SOURCE_LOAN_CONTRACT);
    }

    public FixedRate(Double baseRate) {
        this.baseRate = baseRate;
    }
}
