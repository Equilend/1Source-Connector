package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EventType {
    ALLOCATION("ALLOCATION"),
    BUYIN_CANCELED("BUYIN_CANCELED"),
    BUYIN_CLOSED("BUYIN_CLOSED"),
    BUYIN_COMPLETED("BUYIN_COMPLETED"),
    BUYIN_OPENED("BUYIN_OPENED"),
    BUYIN_PENDING("BUYIN_PENDING"),
    BUYIN_UPDATED("BUYIN_UPDATED"),
    CONTRACT_ALLOCATION("CONTRACT_ALLOCATION"),
    CONTRACT_CANCELED("CONTRACT_CANCELED"),
    CONTRACT_CANCEL_PENDING("CONTRACT_CANCEL_PENDING"),
    CONTRACT_CLOSED("CONTRACT_CLOSED"),
    CONTRACT_DECLINED("CONTRACT_DECLINED"),
    CONTRACT_OPENED("CONTRACT_OPENED"),
    CONTRACT_PENDING("CONTRACT_PENDING"),
    CONTRACT_PROPOSED("CONTRACT_PROPOSED"),
    CONTRACT_SPLIT("CONTRACT_SPLIT"),
    DELEGATION_APPROVED("DELEGATION_APPROVED"),
    DELEGATION_CANCELED("DELEGATION_CANCELED"),
    DELEGATION_PROPOSED("DELEGATION_PROPOSED"),
    RECALL_CANCELED("RECALL_CANCELED"),
    RECALL_CLOSED("RECALL_CLOSED"),
    RECALL_OPEN("RECALL_OPEN"),
    RECALL_UPDATED("RECALL_UPDATED"),
    RERATE_APPLIED("RERATE_APPLIED"),
    RERATE_CANCEL_PENDING("RERATE_CANCEL_PENDING"),
    RERATE_CANCELED("RERATE_CANCELED"),
    RERATE_DECLINED("RERATE_DECLINED"),
    RERATE_PENDING("RERATE_PENDING"),
    RERATE_PROPOSED("RERATE_PROPOSED"),
    RETURN_CANCELED("RETURN_CANCELED"),
    RETURN_PENDING("RETURN_PENDING"),
    RETURN_SETTLED("RETURN_SETTLED"),
    SETTLEMENT_INSTRUCTION_UPDATE("SETTLEMENT_INSTRUCTION_UPDATE"),
    SETTLEMENT_STATUS_UPDATE("SETTLEMENT_STATUS_UPDATE"),
    TRADE_AGREED("TRADE_AGREED"),
    TRADE_CANCELED("TRADE_CANCELED");

    private String value;

    EventType(String value) {
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
    public static EventType fromValue(String input) {
        for (EventType b : EventType.values()) {
            if (b.value.equals(input)) {
                return b;
            }
        }
        return null;
    }
}
