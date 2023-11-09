package com.intellecteu.onesource.integration.exception;

public class AgreementNotFoundException extends Exception {

    public static final String AGREEMENT_MESSAGE = "Agreement not found for event id: %s";

    public AgreementNotFoundException() {
        super();
    }

    public AgreementNotFoundException(String message) {
        super(message);
    }

}
