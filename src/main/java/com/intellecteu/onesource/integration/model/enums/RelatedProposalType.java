package com.intellecteu.onesource.integration.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RelatedProposalType {
    CONTRACT("CONTRACT"),
    RERATE("RERATE");

    private final String value;

    RelatedProposalType(String value) {
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
    public static RelatedProposalType fromValue(String value) {
        for (RelatedProposalType rpt : RelatedProposalType.values()) {
            if (rpt.value.equalsIgnoreCase(value)) {
                return rpt;
            }
        }
        return null;
    }

}
