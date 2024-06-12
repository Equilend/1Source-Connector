package com.intellecteu.onesource.integration.services.reconciliation;

import com.intellecteu.onesource.integration.exception.ValidationException;
import java.util.ArrayList;
import java.util.List;

/**
 * The object implemented this interface is a subject of a reconciliation process
 */
public interface Reconcilable {

    /**
     * Validate required fields for reconciliation. The candidate for deprecation as the purpose of this function in the
     * current business logic is only to check missed fields in the reconcilable object. Might be replaced by
     * getMissedRequiredFields()
     *
     * @throws ValidationException if validation fails
     */
    void validateForReconciliation() throws ValidationException;

    /**
     * Should replace validateForReconciliation in further development. According to the existed logic there is no need
     * to have abstract validateForReconciliation method as reconciliation has two main aims: find missed required
     * fields and compare required fields in two objects. This method can be an abstraction and each implementation
     * should return the list of objects (not strings) that combine all necessary information about the missed fields.
     *
     * @param reconcilable Reconcilable the object of reconciliation process
     * @return List of Strings with missed fields names. In further development might be changed to a List of Objects
     */
    default List<String> getMissedRequiredFields(Reconcilable reconcilable) {
        List<String> missedFields = new ArrayList<>();
        try {
            reconcilable.validateForReconciliation();
        } catch (ValidationException e) {
            missedFields.addAll(e.getInvalidFields());
        }
        return missedFields;
    }

}
