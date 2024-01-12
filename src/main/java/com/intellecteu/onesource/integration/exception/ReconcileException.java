package com.intellecteu.onesource.integration.exception;

import com.intellecteu.onesource.integration.dto.ExceptionMessageDto;
import java.util.List;
import lombok.Getter;

@Getter
public class ReconcileException extends Exception {

    public static final String RECONCILE_EXCEPTION = "The trade agreement %s is in discrepancies "
        + "with the position %s in Spire";

    public static final String RECONCILE_MISMATCH = "Reconciliation mismatch. OneSource %s:%s "
        + "is not matched with Spire %s:%s";

    List<ExceptionMessageDto> errorList;

    public ReconcileException(List<ExceptionMessageDto> list) {
        errorList = list;
    }

    public ReconcileException() {
        super();
    }

    public ReconcileException(String message) {
        super(message);
    }

    public ReconcileException(String message, List<ExceptionMessageDto> list) {
        super(message);
        errorList = list;
    }

}
