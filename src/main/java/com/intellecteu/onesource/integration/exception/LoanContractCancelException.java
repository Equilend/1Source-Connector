package com.intellecteu.onesource.integration.exception;

public class LoanContractCancelException extends RuntimeException {

    public static final String CANCEL_EXCEPTION_MESSAGE = "The loan contract %s cannot be canceled "
        + "for position type: %s";

    public LoanContractCancelException() {
        super();
    }

    public LoanContractCancelException(String message) {
        super(message);
    }

}
