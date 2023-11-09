package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.exception.ReconcileException;

/**
 * General interface for reconciliation flow.
 */
public interface ReconcileService {

  /**
   * Reconciliation first vs second
   *
   * @param first Reconcilable instance
   * @param second Reconcilable instance
   * @param <T> Reconcilable type
   * @throws ReconcileException if at least one reconciliation rule fails
   */
  <T extends Reconcilable> void reconcile(T first, T second) throws ReconcileException;

}
