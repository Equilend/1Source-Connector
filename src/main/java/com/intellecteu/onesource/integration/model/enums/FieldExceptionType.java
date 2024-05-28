package com.intellecteu.onesource.integration.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FieldExceptionType {

    DISCREPANCY("Discrepancy"),
    INFORMATIONAL("Informational"),
    MISSING("Missing"),
    UNMATCHED("Unmatched");

    private final String value;

    FieldExceptionType(String value) {
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
    public static FieldExceptionType fromValue(String value) {
        for (FieldExceptionType fet : FieldExceptionType.values()) {
            if (fet.value.equalsIgnoreCase(value)) {
                return fet;
            }
        }
        return null;
    }
}
