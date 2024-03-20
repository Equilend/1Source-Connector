package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Benchmark {
    EFFR("EFFR"),
    OBFR("OBFR"),
    TGCR("TGCR"),
    BFCR("BFCR"),
    SOFR("SOFR"),
    BGCR("BGCR");

    private final String value;

    Benchmark(String value) {
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
    public static Benchmark fromValue(String value) {
        for (Benchmark benchmark : Benchmark.values()) {
            if (benchmark.value.equalsIgnoreCase(value)) {
                return benchmark;
            }
        }
        return null;
    }

}
