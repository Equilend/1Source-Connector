package com.intellecteu.onesource.integration.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProcessingStatus {
    ACK_SUBMITTED("ACK_SUBMITTED"),
    APPLIED("APPLIED"),
    APPROVAL_SUBMITTED("APPROVAL_SUBMITTED"),
    APPROVED("APPROVED"),
    CANCEL_PENDING("CANCEL_PENDING"),
    CANCEL_SUBMITTED("CANCEL_SUBMITTED", true),
    CLOSED("CLOSED"),
    CANCELED("CANCELED"),
    CONFIRMATION_POSTPONED("CONFIRMATION_POSTPONED"),
    CONFIRMED("CONFIRMED"),
    CONFIRMED_BORROWER("CONFIRMED_BORROWER"),
    CONFIRMED_LENDER("CONFIRMED_LENDER"),
    CREATED("CREATED"),
    DECLINE_SUBMITTED("DECLINE_SUBMITTED"),
    DECLINED("DECLINED"),
    DISCREPANCIES("DISCREPANCIES"),
    MATCHED_CANCELED_POSITION("MATCHED_CANCELED_POSITION"),
    MATCHED_POSITION("MATCHED_POSITION"),
    MATCHED("MATCHED"),
    MULTIPLE_FIGI("MULTIPLE_FIGI"),
    NACK_SUBMITTED("NACK_SUBMITTED"),
    NEGATIVELY_ACKNOWLEDGED("NEGATIVELY_ACKNOWLEDGED"),
    NEW("NEW"),
    NO_FIGI("NO_FIGI"),
    ONESOURCE_ISSUE("ONESOURCE_ISSUE"),
    PROCESSED("PROCESSED"),
    PROPOSAL_APPROVED("PROPOSAL_APPROVED"),
    PROPOSAL_DECLINED("PROPOSAL_DECLINED"),
    PROPOSED("PROPOSED"),
    POSITIVELY_ACKNOWLEDGED("POSITIVELY_ACKNOWLEDGED"),
    RECONCILED("RECONCILED"),
    REPLACED("REPLACED"),
    SAVED("SAVED"),
    SENT_FOR_APPROVAL("SENT_FOR_APPROVAL", true),
    SETTLED("SETTLED"),
    SPIRE_POSITION_CANCELED("SPIRE_POSITION_CANCELED"),
    SUBMITTED("SUBMITTED"),
    TO_CANCEL("TO_CANCEL"),
    TO_CONFIRM("TO_CONFIRM"),
    TO_DECLINE("TO_DECLINE"),
    TO_VALIDATE("TO_VALIDATE"),
    UNMATCHED("UNMATCHED"),
    UPDATE_SUBMITTED("UPDATE_SUBMITTED"),
    UPDATED("UPDATED"),
    VALIDATED("VALIDATED"),
    WAITING_PROPOSAL("WAITING_PROPOSAL");

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
