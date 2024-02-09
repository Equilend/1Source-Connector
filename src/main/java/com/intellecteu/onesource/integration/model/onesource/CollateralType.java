package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CollateralType {
    CASH("CASH"),
    NONCASH("NONCASH"),
    CASHPOOL("CASHPOOL"),
    TRIPARTY("TRIPARTY");

    private final String value;

    CollateralType(String value) {
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
    public static CollateralType fromValue(String value) {
        for (CollateralType ct : CollateralType.values()) {
            if (ct.value.equalsIgnoreCase(value)) {
                return ct;
            }
        }
        return null;
    }
}
