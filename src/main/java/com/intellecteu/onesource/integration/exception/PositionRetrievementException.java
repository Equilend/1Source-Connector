package com.intellecteu.onesource.integration.exception;

public class PositionRetrievementException extends RuntimeException {

    public static final String TRADE_RELATED_EXCEPTION = "The position related to the trade: %s "
        + "negotiated on %s on %s cannot be retrieved from SPIRE for the following reason: %s";

    public PositionRetrievementException() {
        super();
    }

    public PositionRetrievementException(String message) {
        super(message);
    }

    public PositionRetrievementException(Throwable throwable) {
        super(throwable);
    }

}
