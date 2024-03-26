package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CurrencyCd {
    USD("USD"),
    EUR("EUR"),
    GBP("GBP"),
    JPY("JPY"),
    AUD("AUD"),
    HKD("HKD"),
    CAD("CAD"),
    CHF("CHF"),
    SEK("SEK"),
    SGD("SGD"),
    NOK("NOK"),
    DKK("DKK");

    private final String value;

    CurrencyCd(String value) {
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
    public static CurrencyCd fromValue(String value) {
        for (CurrencyCd currency : CurrencyCd.values()) {
            if (currency.value.equalsIgnoreCase(value)) {
                return currency;
            }
        }
        return null;
    }
}
