package com.intellecteu.onesource.integration.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SpireTradeType {
    CANCEL_BORROW("CANCEL BORROW"),
    CANCEL_LOAN("CANCEL LOAN"),
    RERATE("RERATE"),
    RERATE_BORROW("RERATE BORROW"),
    ROLL_BORROW("ROLL BORROW"),
    ROLL_LOAN("ROLL LOAN");

    private final String value;

    SpireTradeType(String value) {
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
    public static SpireTradeType fromValue(String value) {
        for (SpireTradeType type : SpireTradeType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }

}
