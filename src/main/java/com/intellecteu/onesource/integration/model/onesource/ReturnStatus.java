package com.intellecteu.onesource.integration.model.onesource;

public enum ReturnStatus {
    PENDING("PENDING"),
    CANCELED("CANCELED"),
    SETTLED("SETTLED");
    private String value;

    ReturnStatus(String settled) {
        this.value = value;
    }
}
