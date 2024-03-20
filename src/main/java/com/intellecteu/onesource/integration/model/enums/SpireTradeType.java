package com.intellecteu.onesource.integration.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SpireTradeType {
    CANCEL_BORROW("Cancel Borrow"),
    CANCEL_LOAN("Cancel Loan"),
    RERATE("Rerate"),
    RERATE_BORROW("Rerate Borrow"),
    ROLL_BORROW("Roll Borrow"),
    ROLL_LOAN("Roll Loan");

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
