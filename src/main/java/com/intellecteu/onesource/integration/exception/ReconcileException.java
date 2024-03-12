package com.intellecteu.onesource.integration.exception;

import com.intellecteu.onesource.integration.model.ProcessExceptionDetails;
import java.util.List;
import lombok.Getter;

@Getter
public class ReconcileException extends Exception {


    public static final String RECONCILE_MISMATCH = "Reconciliation mismatch. OneSource %s:%s "
        + "is not matched with Spire %s:%s";

    List<ProcessExceptionDetails> errorList;

    public ReconcileException(List<ProcessExceptionDetails> list) {
        errorList = list;
    }

    public ReconcileException() {
        super();
    }

    public ReconcileException(String message) {
        super(message);
    }

    public ReconcileException(String message, List<ProcessExceptionDetails> list) {
        super(message);
        errorList = list;
    }

}
