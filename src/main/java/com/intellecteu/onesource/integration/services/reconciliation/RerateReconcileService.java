package com.intellecteu.onesource.integration.services.reconciliation;

import static com.intellecteu.onesource.integration.model.enums.FieldSource.BACKOFFICE_RERATE;
import static com.intellecteu.onesource.integration.model.enums.FieldSource.ONE_SOURCE_RERATE;
import static com.intellecteu.onesource.integration.model.onesource.FixedRate.FIXED_INDEX_NAME;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.model.ProcessExceptionDetails;
import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.enums.FieldExceptionType;
import com.intellecteu.onesource.integration.model.enums.FieldSource;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RerateReconcileService implements ReconcileService<Rerate, RerateTrade> {

    public static final String RECONCILE_EXCEPTION = "The 1source rerate %s is in discrepancies with the rerate %s in backoffice";

    @Override
    public void reconcile(Rerate onesourceRerate, RerateTrade backofficeRerate) throws ReconcileException {
        var reconciliationFailMessages = new ArrayList<ProcessExceptionDetails>();
        reconciliationFailMessages.addAll(validateReconcilableObject(onesourceRerate, ONE_SOURCE_RERATE));
        reconciliationFailMessages.addAll(validateReconcilableObject(backofficeRerate, BACKOFFICE_RERATE));
        reconciliationFailMessages.addAll(reconcileRerate(onesourceRerate, backofficeRerate));
        if (!reconciliationFailMessages.isEmpty()) {
            String errorMsg = format(RECONCILE_EXCEPTION, onesourceRerate.getRerateId(), backofficeRerate.getTradeId());
            reconciliationFailMessages.forEach(msg -> log.debug(msg.getFieldValue()));
            throw new ReconcileException(errorMsg, reconciliationFailMessages);
        }
    }

    private List<ProcessExceptionDetails> validateReconcilableObject(Reconcilable reconcilable,
        FieldSource fieldSource) {
        return validateReconcilableObject().apply(reconcilable).stream().map(
            invalidField -> new ProcessExceptionDetails(fieldSource, invalidField, "null",
                FieldExceptionType.MISSING)).toList();
    }

    private Collection<? extends ProcessExceptionDetails> reconcileRerate(Rerate onesourceRerate,
        RerateTrade backofficeRerate) {
        var failedList = new ArrayList<ProcessExceptionDetails>();
        if (backofficeRerate.getTradeOut().getPosition().getIndex().getIndexName().equals(FIXED_INDEX_NAME) ||
            backofficeRerate.getTradeOut().getPosition().getIndex().getIndexId().equals(12)) {
            if (onesourceRerate.getRerate() != null && onesourceRerate.getRerate().getRebate() != null
                && onesourceRerate.getRerate().getRebate().getFixed() != null
                && backofficeRerate.getTradeOut() != null) {

                if (onesourceRerate.getRerate().getRebate().getFixed().getBaseRate() != null
                    && backofficeRerate.getTradeOut().getRateOrSpread() != null) {
                    checkEquality(onesourceRerate.getRerate().getRebate().getFixed().getBaseRate(),
                        "baseRate",
                        backofficeRerate.getTradeOut().getRateOrSpread(),
                        "rateOrSpread").ifPresent(failedList::add);
                }

                if (onesourceRerate.getRerate().getRebate().getFixed().getEffectiveDate() != null
                    && backofficeRerate.getTradeOut().getAccrualDate() != null) {
                    checkEquality(onesourceRerate.getRerate().getRebate().getFixed().getEffectiveDate(),
                        "effectiveDate",
                        backofficeRerate.getTradeOut().getAccrualDate().toLocalDate(),
                        "accrualDate").ifPresent(failedList::add);
                }
            }
        } else {
            if (onesourceRerate.getRerate() != null && onesourceRerate.getRerate().getRebate() != null
                && onesourceRerate.getRerate().getRebate().getFloating() != null
                && backofficeRerate.getTradeOut() != null && backofficeRerate.getTradeOut().getPosition() != null
            ) {

                if (onesourceRerate.getRerate().getRebate().getFloating().getBenchmark() != null
                    && backofficeRerate.getTradeOut().getPosition().getIndex().getIndexName() != null) {
                    checkEquality(onesourceRerate.getRerate().getRebate().getFloating().getBenchmark().toString(),
                        "benchmark",
                        backofficeRerate.getTradeOut().getPosition().getIndex().getIndexName(),
                        "indexName").ifPresent(failedList::add);
                }

                if (onesourceRerate.getRerate().getRebate().getFloating().getSpread() != null
                    && backofficeRerate.getTradeOut().getRateOrSpread() != null) {
                    checkEquality(onesourceRerate.getRerate().getRebate().getFloating().getSpread(),
                        "spread",
                        backofficeRerate.getTradeOut().getRateOrSpread(),
                        "rateOrSpread").ifPresent(failedList::add);
                }

                if (onesourceRerate.getRerate().getRebate().getFloating().getEffectiveDate() != null
                    && backofficeRerate.getTradeOut().getPosition().getSettleDate() != null) {
                    checkEquality(onesourceRerate.getRerate().getRebate().getFloating().getEffectiveDate(),
                        "effectiveDate",
                        backofficeRerate.getTradeOut().getAccrualDate().toLocalDate(),
                        "accrualDate").ifPresent(failedList::add);
                }
            }
        }
        return failedList;
    }

    Optional<ProcessExceptionDetails> checkEquality(@NonNull Object oneSourceRerateField,
        String oneSourceRerateFieldName, @NonNull Object backOfficeRerateField,
        String backOfficeRerateFieldName) {
        if (!Objects.equals(oneSourceRerateField, backOfficeRerateField)) {
            return Optional.of(new ProcessExceptionDetails(ONE_SOURCE_RERATE, oneSourceRerateFieldName,
                String.valueOf(oneSourceRerateField),
                FieldExceptionType.DISCREPANCY));
        }
        return Optional.empty();
    }

}
