package com.intellecteu.onesource.integration.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProcessingStatus {
    APPROVAL_SUBMITTED("APPROVAL_SUBMITTED"),
    APPROVED("APPROVED"),
    CANCELED("CANCELED"),
    CANCEL_SUBMITTED("CANCEL_SUBMITTED", true),
    CREATED("CREATED"),
    CONFIRMED("CONFIRMED"),
    DECLINED("DECLINED"),
    DECLINE_SUBMITTED("DECLINE_SUBMITTED"),
    DISCREPANCIES("DISCREPANCIES"),
    PROPOSED("PROPOSED"),
    MATCHED("MATCHED"),
    MATCHED_CANCELED_POSITION("MATCHED_CANCELED_POSITION"),
    MATCHED_POSITION("MATCHED_POSITION"),
    NEW("NEW"),
    ONESOURCE_ISSUE("ONESOURCE_ISSUE"),
    PROCESSED("PROCESSED"),
    PROPOSAL_DECLINED("PROPOSAL_DECLINED"),
    RECONCILED("RECONCILED"),
    SAVED("SAVED"),
    SENT_FOR_APPROVAL("SENT_FOR_APPROVAL", true),
    SETTLED("SETTLED"),
    SPIRE_POSITION_CANCELED("SPIRE_POSITION_CANCELED"),
    SUBMITTED("SUBMITTED"),
    TO_CANCEL("TO_CANCEL"),
    TO_DECLINE("TO_DECLINE"),
    TO_VALIDATE("TO_VALIDATE"),
    UNMATCHED("UNMATCHED"),
    UPDATE_SUBMITTED("UPDATE_SUBMITTED"),
    UPDATED("UPDATED"),
    VALIDATED("VALIDATED");

    private final String value;
    private boolean isTechnical;

    ProcessingStatus(String value) {
        this.value = value;
    }

    ProcessingStatus(String value, Boolean isTechnical) {
        this.value = value;
        this.isTechnical = isTechnical;
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
    public static ProcessingStatus fromValue(String value) {
        for (ProcessingStatus ps : ProcessingStatus.values()) {
            if (ps.value.equalsIgnoreCase(value)) {
                return ps;
            }
        }
        return null;
    }
}
