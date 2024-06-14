package com.intellecteu.onesource.integration.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RecallInstructionType {

    RECALL("RECALL"),
    RECALL_CANCELLATION("RECALL_CANCELLATION");
    private final String value;

    RecallInstructionType(String value) {
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
    public static RecallInstructionType fromValue(String value) {
        for (RecallInstructionType status : RecallInstructionType.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
