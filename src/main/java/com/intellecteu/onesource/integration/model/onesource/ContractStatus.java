package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ContractStatus {
    APPROVED("APPROVED"),
    CANCEL_PENDING("CANCEL_PENDING"),
    CANCELED("CANCELED"),
    CLOSED("CLOSED"),
    DECLINED("DECLINED"),
    OPEN("OPEN"),
    PENDING("PENDING"),
    PROPOSED("PROPOSED"),
    SPLIT_DECLINED("SPLIT_DECLINED"),
    SPLIT_PENDING("SPLIT_PENDING");

    private final String value;

    ContractStatus(String value) {
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
    public static ContractStatus fromValue(String value) {
        for (ContractStatus cs : ContractStatus.values()) {
            if (cs.value.equalsIgnoreCase(value)) {
                return cs;
            }
        }
        return null;
    }
}
