package com.intellecteu.onesource.integration.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FieldSource {
    ONE_SOURCE_LOAN_CONTRACT("1sourceLoanContract"),
    ONE_SOURCE_RERATE("1sourceRerate"),
    ONE_SOURCE_RETURN("1SourceReturn"),
    ONE_SOURCE_TRADE_AGREEMENT("1sourceTradeAgreement"),
    BACKOFFICE_POSITION("Position"),
    BACKOFFICE_RERATE("BackOficeRerate");

    private final String value;

    FieldSource(String value) {
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
    public static FieldSource fromValue(String value) {
        for (FieldSource fs : FieldSource.values()) {
            if (fs.value.equalsIgnoreCase(value)) {
                return fs;
            }
        }
        return null;
    }

}
