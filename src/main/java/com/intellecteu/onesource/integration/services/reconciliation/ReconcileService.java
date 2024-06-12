package com.intellecteu.onesource.integration.services.reconciliation;

import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.exception.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * General interface for reconciliation flow.
 */
public interface ReconcileService<T extends Reconcilable, R extends Reconcilable> {

    /*
     * Function to wrap try-catch and validate objects that implement Reconcilable interface.
     * Might be deprecated on reconcilable abstraction changes.
     */
    default Function<Reconcilable, List<String>> validateReconcilableObject() {
        return reconcilable -> {
            try {
                reconcilable.validateForReconciliation();
            } catch (ValidationException e) {
                return e.getInvalidFields();
            }
            return new ArrayList<>();
        };
    }

    /**
     * Reconciliation first vs second
     *
     * @param first Reconcilable instance
     * @param second Reconcilable instance
     * @throws ReconcileException if at least one reconciliation rule fails
     */
    void reconcile(T first, R second) throws ReconcileException;

}
