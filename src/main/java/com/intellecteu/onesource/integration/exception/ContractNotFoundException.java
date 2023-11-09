package com.intellecteu.onesource.integration.exception;

public class ContractNotFoundException extends Exception {

    public static final String CONTRACT_MESSAGE = "Contract not found for event id: %s";

    public ContractNotFoundException() {
        super();
    }

    public ContractNotFoundException(String message) {
        super(message);
    }

}
