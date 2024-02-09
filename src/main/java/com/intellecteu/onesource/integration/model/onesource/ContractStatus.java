package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ContractStatus {
    PROPOSED("PROPOSED"),
    APPROVED("APPROVED"),
    CANCELED("CANCELED"),
    DECLINED("DECLINED"),
    OPEN("OPEN");

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
