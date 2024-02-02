package com.intellecteu.onesource.integration.model.onesource;

public enum RerateStatus {
    PROPOSED("PROPOSED"),
    PENDING("PENDING"),
    CANCEL_PENDING("CANCEL_PENDING"),
    CANCELED("CANCELED"),
    APPLIED("APPLIED");
    private String value;

    RerateStatus(String value) {
        this.value = value;
    }
}
