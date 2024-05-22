package com.intellecteu.onesource.integration.model.onesource;

public enum AcknowledgementType {
    POSITIVE("POSITIVE"),
    NEGATIVE("NEGATIVE");

    private final String value;

    AcknowledgementType(String value) {
        this.value = value;
    }
}
