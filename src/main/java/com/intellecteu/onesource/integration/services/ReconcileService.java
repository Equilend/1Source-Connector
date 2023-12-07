package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.exception.ReconcileException;

/**
 * General interface for reconciliation flow.
 */
public interface ReconcileService<T extends Reconcilable, R extends Reconcilable> {

    /**
     * Reconciliation first vs second
     *
     * @param first Reconcilable instance
     * @param second Reconcilable instance
     * @throws ReconcileException if at least one reconciliation rule fails
     */
    void reconcile(T first, R second) throws ReconcileException;

}
