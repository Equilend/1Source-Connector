package com.intellecteu.onesource.integration.exception;

public class ContractNotFoundException extends RuntimeException {

    public static final String CONTRACT_MESSAGE = "Contract not found";

    public ContractNotFoundException() {
        super();
    }

    public ContractNotFoundException(String message) {
        super(message);
    }

}
