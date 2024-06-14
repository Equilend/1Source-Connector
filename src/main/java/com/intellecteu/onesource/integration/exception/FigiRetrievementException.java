package com.intellecteu.onesource.integration.exception;

public class FigiRetrievementException extends RuntimeException {

    private boolean isFigiRetrieved;

    public boolean isFigiRetrieved() {
        return isFigiRetrieved;
    }

    public FigiRetrievementException(boolean isFigiRetrieved) {
        this.isFigiRetrieved = isFigiRetrieved;
    }

    public FigiRetrievementException(String message) {
        super(message);
    }

    public FigiRetrievementException(String message, boolean isFigiRetrieved) {
        super(message);
        this.isFigiRetrieved = isFigiRetrieved;
    }

    public FigiRetrievementException(String message, Throwable cause, boolean isFigiRetrieved) {
        super(message, cause);
        this.isFigiRetrieved = isFigiRetrieved;
    }

    public FigiRetrievementException(Throwable cause, boolean isFigiRetrieved) {
        super(cause);
        this.isFigiRetrieved = isFigiRetrieved;
    }

    public FigiRetrievementException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace,
        boolean isFigiRetrieved) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.isFigiRetrieved = isFigiRetrieved;
    }
}
