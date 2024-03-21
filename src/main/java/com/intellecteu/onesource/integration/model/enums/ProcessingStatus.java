package com.intellecteu.onesource.integration.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProcessingStatus {
    APPROVED("APPROVED"),
    CANCELED("CANCELED"),
    CREATED("CREATED"),
    DECLINED("DECLINED"),
    DISCREPANCIES("DISCREPANCIES"),
    PROPOSED("PROPOSED"),
    MATCHED_CANCELED_POSITION("MATCHED_CANCELED_POSITION"),
    MATCHED_POSITION("MATCHED_POSITION"),
    NEW("NEW"),
    ONESOURCE_ISSUE("ONESOURCE_ISSUE"),
    PROCESSED("PROCESSED"),
    PROPOSAL_APPROVED("PROPOSAL_APPROVED"),
    PROPOSAL_CANCELED("PROPOSAL_CANCELED"),
    PROPOSAL_DECLINED("PROPOSAL_DECLINED"),
    RECONCILED("RECONCILED"),
    TRADE_DISCREPANCIES("TRADE_DISCREPANCIES"),
    TRADE_RECONCILED("TRADE_RECONCILED"),
    TRADE_CANCELED("TRADE_CANCELED"),
    SAVED("SAVED"),
    SETTLED("SETTLED"),
    SI_FETCHED("SI_FETCHED"),
    SPIRE_ISSUE("SPIRE_ISSUE"),
    SPIRE_POSITION_CANCELED("SPIRE_POSITION_CANCELED"),
    TO_CANCEL("TO_CANCEL"),
    TO_DECLINE("TO_DECLINE"),
    UPDATED("UPDATED"),
    VALIDATED("VALIDATED"),
    MATCHED("MATCHED"),
    UNMATCHED("UNMATCHED"),
    SUBMITTED("SUBMITTED"),
    WAITING_PROPOSAL("WAITING_PROPOSAL"),
    TO_VALIDATE("TO_VALIDATE"),
    APPROVAL_SUBMITTED("APPROVAL_SUBMITTED"),
    CONFIRMED("CONFIRMED"),
    APPLIED("APPLIED"),
    DECLINE_SUBMITTED("DECLINE_SUBMITTED");

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
