package com.intellecteu.onesource.integration.exception;

public class ConvertException extends RuntimeException {

    public static final String CONVERT_MESSAGE = "Exception during converting. Details: %s";

    public ConvertException() {
        super();
    }

    public ConvertException(Throwable cause) {
        super(cause);
    }
    public ConvertException(String message) {
        super(message);
    }

}
