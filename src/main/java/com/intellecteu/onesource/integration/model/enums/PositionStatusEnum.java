package com.intellecteu.onesource.integration.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PositionStatusEnum {

    OPEN("OPEN"),
    CLOSEd("CLOSED"),
    FUTURE("FUTURE"),
    PREPAID("PREPAID"),
    FAILED("FAILED"),
    CANCELLED("CANCELLED"),
    NEW("NEW"),
    READY("READY"),
    PENDING("PENDING"),
    SETTLED("SETTLED"),
    DELETED("DELETED"),
    NO_INSTRUCTION("NO INSTRUCTION"),
    PRINTED("PRINTED"),
    ACCEPTED("ACCEPTED"),
    VOID("VOID");
    private final String value;

    PositionStatusEnum(String value) {
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
    public static PositionStatusEnum fromValue(String value) {
        for (PositionStatusEnum status : PositionStatusEnum.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
