package com.intellecteu.onesource.integration.services.reconciliation;

import com.intellecteu.onesource.integration.exception.ValidationException;

/**
 * The object implemented this interface is a subject of a reconciliation process
 */
public interface Reconcilable {

    /**
     * Validate required fields for reconciliation
     *
     * @throws ValidationException if validation fails
     */
    void validateForReconciliation() throws ValidationException;

}
