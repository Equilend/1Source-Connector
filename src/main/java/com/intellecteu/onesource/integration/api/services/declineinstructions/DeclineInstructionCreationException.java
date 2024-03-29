package com.intellecteu.onesource.integration.api.services.declineinstructions;

public class DeclineInstructionCreationException extends RuntimeException {

    public static final String NOT_ELIGIBLE = """
        CloudEvent not eligible to trigger a proposal decline""";

    public DeclineInstructionCreationException() {
        super();
    }

    public DeclineInstructionCreationException(String message) {
        super(message);
    }

    public DeclineInstructionCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
