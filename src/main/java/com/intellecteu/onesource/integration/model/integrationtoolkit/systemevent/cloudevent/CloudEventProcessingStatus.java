package com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CloudEventProcessingStatus {

    CREATED("CREATED"),
    PROCESSED("PROCESSED"),
    FAILED("FAILED");

    private final String value;

    CloudEventProcessingStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static CloudEventProcessingStatus fromValue(String value) {
        for (CloudEventProcessingStatus eventStatus : CloudEventProcessingStatus.values()) {
            if (eventStatus.value.equalsIgnoreCase(value)) {
                return eventStatus;
            }
        }
        return null;
    }
}
