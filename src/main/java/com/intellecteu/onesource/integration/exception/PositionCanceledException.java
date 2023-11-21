package com.intellecteu.onesource.integration.exception;

public class PositionCanceledException extends RuntimeException {

    public static final String POSITION_CANCELED_EXCEPTION = "The position related to the trade: %s "
        + "negotiated on %s on %s is canceled in SPIRE";

    public PositionCanceledException() {
        super();
    }

    public PositionCanceledException(String message) {
        super(message);
    }

}
