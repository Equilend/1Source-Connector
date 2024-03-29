package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SettlementStatus {
    NONE("NONE"),
    PENDING("PENDING"),
    SETTLED("SETTLED");

    private String value;

    SettlementStatus(String value) {
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
    public static SettlementStatus fromValue(String input) {
        for (SettlementStatus s : SettlementStatus.values()) {
            if (s.value.equals(input)) {
                return s;
            }
        }
        return null;
    }
}
