package com.intellecteu.onesource.integration.services.reconciliation;

import static com.intellecteu.onesource.integration.model.enums.FieldSource.BACKOFFICE_RETURN;
import static com.intellecteu.onesource.integration.model.enums.FieldSource.ONE_SOURCE_RETURN;
import static com.intellecteu.onesource.integration.model.onesource.SettlementType.DVP;
import static com.intellecteu.onesource.integration.model.onesource.SettlementType.FOP;

import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.model.ProcessExceptionDetails;
import com.intellecteu.onesource.integration.model.backoffice.ReturnTrade;
import com.intellecteu.onesource.integration.model.enums.FieldExceptionType;
import com.intellecteu.onesource.integration.model.enums.FieldSource;
import com.intellecteu.onesource.integration.model.onesource.Return;
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
public class ReturnReconcileService implements ReconcileService<Return, ReturnTrade> {

    public static final String RECONCILE_EXCEPTION = """
        The 1source return id=%s is in discrepancies with the backoffice return id=%s""";

    @Override
    public void reconcile(Return onesourceReturn, ReturnTrade returnTrade) throws ReconcileException {
        var reconciliationFailMessages = new ArrayList<ProcessExceptionDetails>();
        reconciliationFailMessages.addAll(validateReconcilableObject(onesourceReturn, ONE_SOURCE_RETURN));
        reconciliationFailMessages.addAll(validateReconcilableObject(returnTrade, BACKOFFICE_RETURN));
        reconciliationFailMessages.addAll(reconcileReturn(onesourceReturn, returnTrade));
        if (!reconciliationFailMessages.isEmpty()) {
            String errorMsg = String.format(RECONCILE_EXCEPTION, onesourceReturn.getReturnId(),
                returnTrade.getTradeId());
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

    private Collection<? extends ProcessExceptionDetails> reconcileReturn(Return onesourceReturn,
        ReturnTrade returnTrade) {
        var failedList = new ArrayList<ProcessExceptionDetails>();
        if (onesourceReturn.getQuantity() != null && returnTrade.getTradeOut().getQuantity() != null) {
            checkEquality(onesourceReturn.getQuantity(), "1sourceReturn.quantity",
                Integer.valueOf(returnTrade.getTradeOut().getQuantity().intValue()),
                "sprireReturnTrade.tradeOut.quantity").ifPresent(failedList::add);
        }
        if (onesourceReturn.getCollateral() != null && onesourceReturn.getCollateral().getCollateralValue() != null
            && returnTrade.getTradeOut().getAmount() != null) {
            checkEquality(onesourceReturn.getCollateral().getCollateralValue(),
                "1sourceReturn.collateral.collateralValue",
                returnTrade.getTradeOut().getAmount(),
                "sprireReturnTrade.tradeOut.amount").ifPresent(failedList::add);
        }
        if (onesourceReturn.getSettlementType() != null
            && returnTrade.getTradeOut().getPosition().getDeliverFree() != null) {
            if (!returnTrade.getTradeOut().getPosition().getDeliverFree()) {
                if (!DVP.equals(onesourceReturn.getSettlementType())) {
                    failedList.add(new ProcessExceptionDetails(ONE_SOURCE_RETURN, "1sourceReturn.settlementType",
                        String.valueOf(onesourceReturn.getSettlementType()),
                        FieldExceptionType.DISCREPANCY));
                }
            } else {
                if (!FOP.equals(onesourceReturn.getSettlementType())) {
                    failedList.add(new ProcessExceptionDetails(ONE_SOURCE_RETURN, "1sourceReturn.settlementType",
                        String.valueOf(onesourceReturn.getSettlementType()),
                        FieldExceptionType.DISCREPANCY));
                }
            }
        }
        if (onesourceReturn.getReturnDate() != null && returnTrade.getTradeOut().getTradeDate() != null) {
            checkEquality(onesourceReturn.getReturnDate(), "1sourceReturn.returnDate",
                returnTrade.getTradeOut().getTradeDate().toLocalDate(),
                "sprireReturnTrade.tradeOut.tradeDate").ifPresent(failedList::add);
        }
        if (onesourceReturn.getReturnSettlementDate() != null && returnTrade.getTradeOut().getSettleDate() != null) {
            checkEquality(onesourceReturn.getReturnSettlementDate(), "1sourceReturn.returnSettlementDate",
                returnTrade.getTradeOut().getSettleDate().toLocalDate(),
                "sprireReturnTrade.tradeOut.settleDate").ifPresent(failedList::add);
        }
        return failedList;
    }

    Optional<ProcessExceptionDetails> checkEquality(@NonNull Object oneSourceReturnField,
        String oneSourceRerateFieldName, @NonNull Object backOfficeReturnField,
        String backOfficeRerateFieldName) {
        if (!Objects.equals(oneSourceReturnField, backOfficeReturnField)) {
            return Optional.of(new ProcessExceptionDetails(ONE_SOURCE_RETURN, oneSourceRerateFieldName,
                String.valueOf(oneSourceReturnField),
                FieldExceptionType.DISCREPANCY));
        }
        return Optional.empty();
    }
}
