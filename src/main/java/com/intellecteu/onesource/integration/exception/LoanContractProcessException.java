package com.intellecteu.onesource.integration.exception;

public class LoanContractProcessException extends RuntimeException {

    public static final String PROCESS_EXCEPTION_MESSAGE = "The loan contract proposal instruction "
        + "has not been processed by 1Source for the trade agreement: %s (SPIRE Position: %s) "
        + "for the following reason: %s";

    public LoanContractProcessException() {
        super();
    }

    public LoanContractProcessException(String message) {
        super(message);
    }

}
