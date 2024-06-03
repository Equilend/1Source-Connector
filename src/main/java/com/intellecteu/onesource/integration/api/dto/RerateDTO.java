/*
 * Equilend Integration API
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 1.0.1
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package com.intellecteu.onesource.integration.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * RerateDTO
 */

@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2024-03-22T08:22:20.593613900Z[Europe/London]")

public class RerateDTO {

    @JsonProperty("rerateId")
    private String rerateId = null;

    @JsonProperty("contractId")
    private String contractId = null;

    /**
     * Status of the rerate.
     */
    public enum RerateStatusEnum {
        PROPOSED("PROPOSED"),
        PENDING("PENDING"),
        CANCEL_PENDING("CANCEL_PENDING"),
        CANCELED("CANCELED"),
        APPLIED("APPLIED");

        private String value;

        RerateStatusEnum(String value) {
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
        public static RerateStatusEnum fromValue(String input) {
            for (RerateStatusEnum b : RerateStatusEnum.values()) {
                if (b.value.equals(input)) {
                    return b;
                }
            }
            return null;
        }

    }

    @JsonProperty("rerateStatus")
    private RerateStatusEnum rerateStatus = null;

    /**
     * Processing status of the rerate.
     */
    public enum ProcessingStatusEnum {
        ACK_SUBMITTED("ACK_SUBMITTED"),
        APPROVED("APPROVED"),
        CANCELED("CANCELED"),
        CONFIRMED_BORROWER("CONFIRMED_BORROWER"),
        CONFIRMED_LENDER("CONFIRMED_LENDER"),
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
        TO_CONFIRM("TO_CONFIRM"),
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
        DECLINE_SUBMITTED("DECLINE_SUBMITTED"),
        CANCEL_SUBMITTED("CANCEL_SUBMITTED"),
        SENT_FOR_APPROVAL("SENT_FOR_APPROVAL"),
        UPDATE_SUBMITTED("UPDATE_SUBMITTED"),
        REPLACED("REPLACED"),
        CANCEL_PENDING("CANCEL_PENDING");

        private String value;

        ProcessingStatusEnum(String value) {
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
        public static ProcessingStatusEnum fromValue(String input) {
            for (ProcessingStatusEnum b : ProcessingStatusEnum.values()) {
                if (b.value.equals(input)) {
                    return b;
                }
            }
            return null;
        }

    }

    @JsonProperty("processingStatus")
    private ProcessingStatusEnum processingStatus = null;

    @JsonProperty("matchingSpireTradeId")
    private String matchingSpireTradeId = null;

    @JsonProperty("relatedSpirePositionId")
    private String relatedSpirePositionId = null;

    @JsonProperty("createDatetime")
    private LocalDateTime createDatetime = null;

    @JsonProperty("lastUpdateDatetime")
    private LocalDateTime lastUpdateDatetime = null;

    @JsonProperty("effectiveDate")
    private LocalDateTime effectiveDate = null;

    public RerateDTO rerateId(String rerateId) {
        this.rerateId = rerateId;
        return this;
    }

    /**
     * Unique identifier of the rerate.
     *
     * @return rerateId
     **/
    @Schema(required = true, description = "Unique identifier of the rerate.")
    public String getRerateId() {
        return rerateId;
    }

    public void setRerateId(String rerateId) {
        this.rerateId = rerateId;
    }

    public RerateDTO contractId(String contractId) {
        this.contractId = contractId;
        return this;
    }

    /**
     * Identifier of the contract associated with the rerate.
     *
     * @return contractId
     **/
    @Schema(required = true, description = "Identifier of the contract associated with the rerate.")
    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public RerateDTO rerateStatus(RerateStatusEnum rerateStatus) {
        this.rerateStatus = rerateStatus;
        return this;
    }

    /**
     * Status of the rerate.
     *
     * @return rerateStatus
     **/
    @Schema(required = true, description = "Status of the rerate.")
    public RerateStatusEnum getRerateStatus() {
        return rerateStatus;
    }

    public void setRerateStatus(RerateStatusEnum rerateStatus) {
        this.rerateStatus = rerateStatus;
    }

    public RerateDTO processingStatus(ProcessingStatusEnum processingStatus) {
        this.processingStatus = processingStatus;
        return this;
    }

    /**
     * Processing status of the rerate.
     *
     * @return processingStatus
     **/
    @Schema(required = true, description = "Processing status of the rerate.")
    public ProcessingStatusEnum getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatusEnum processingStatus) {
        this.processingStatus = processingStatus;
    }

    public RerateDTO matchingSpireTradeId(String matchingSpireTradeId) {
        this.matchingSpireTradeId = matchingSpireTradeId;
        return this;
    }

    /**
     * Spire trade ID matched with the rerate.
     *
     * @return matchingSpireTradeId
     **/
    @Schema(description = "Spire trade ID matched with the rerate.")
    public String getMatchingSpireTradeId() {
        return matchingSpireTradeId;
    }

    public void setMatchingSpireTradeId(String matchingSpireTradeId) {
        this.matchingSpireTradeId = matchingSpireTradeId;
    }

    public RerateDTO relatedSpirePositionId(String relatedSpirePositionId) {
        this.relatedSpirePositionId = relatedSpirePositionId;
        return this;
    }

    /**
     * Spire position ID related to the rerate.
     *
     * @return relatedSpirePositionId
     **/
    @Schema(description = "Spire position ID related to the rerate.")
    public String getRelatedSpirePositionId() {
        return relatedSpirePositionId;
    }

    public void setRelatedSpirePositionId(String relatedSpirePositionId) {
        this.relatedSpirePositionId = relatedSpirePositionId;
    }

    public RerateDTO createDatetime(LocalDateTime createDatetime) {
        this.createDatetime = createDatetime;
        return this;
    }

    /**
     * Created Time for rerate creation.
     *
     * @return createDatetime
     **/
    @Schema(required = true, description = "Created Time for rerate creation.")
    public LocalDateTime getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(LocalDateTime createDatetime) {
        this.createDatetime = createDatetime;
    }

    public RerateDTO lastUpdateDatetime(LocalDateTime lastUpdateDatetime) {
        this.lastUpdateDatetime = lastUpdateDatetime;
        return this;
    }

    /**
     * Last time of the update.
     *
     * @return lastUpdateDatetime
     **/
    @Schema(required = true, description = "Last time of the update.")
    public LocalDateTime getLastUpdateDatetime() {
        return lastUpdateDatetime;
    }

    public void setLastUpdateDatetime(LocalDateTime lastUpdateDatetime) {
        this.lastUpdateDatetime = lastUpdateDatetime;
    }

    public RerateDTO effectiveDate(LocalDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
        return this;
    }

    /**
     * Effective Rebate date of the rerate.
     *
     * @return effectiveDate
     **/
    @Schema(required = true, description = "Effective Rebate date of the rerate.")
    public LocalDateTime getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RerateDTO rerate = (RerateDTO) o;
        return Objects.equals(this.rerateId, rerate.rerateId) &&
            Objects.equals(this.contractId, rerate.contractId) &&
            Objects.equals(this.rerateStatus, rerate.rerateStatus) &&
            Objects.equals(this.processingStatus, rerate.processingStatus) &&
            Objects.equals(this.matchingSpireTradeId, rerate.matchingSpireTradeId) &&
            Objects.equals(this.relatedSpirePositionId, rerate.relatedSpirePositionId) &&
            Objects.equals(this.createDatetime, rerate.createDatetime) &&
            Objects.equals(this.lastUpdateDatetime, rerate.lastUpdateDatetime) &&
            Objects.equals(this.effectiveDate, rerate.effectiveDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rerateId, contractId, rerateStatus, processingStatus, matchingSpireTradeId,
            relatedSpirePositionId, createDatetime, lastUpdateDatetime, effectiveDate);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RerateDTO {\n");

        sb.append("    rerateId: ").append(toIndentedString(rerateId)).append("\n");
        sb.append("    contractId: ").append(toIndentedString(contractId)).append("\n");
        sb.append("    rerateStatus: ").append(toIndentedString(rerateStatus)).append("\n");
        sb.append("    processingStatus: ").append(toIndentedString(processingStatus)).append("\n");
        sb.append("    matchingSpireTradeId: ").append(toIndentedString(matchingSpireTradeId)).append("\n");
        sb.append("    relatedSpirePositionId: ").append(toIndentedString(relatedSpirePositionId)).append("\n");
        sb.append("    createDatetime: ").append(toIndentedString(createDatetime)).append("\n");
        sb.append("    lastUpdateDatetime: ").append(toIndentedString(lastUpdateDatetime)).append("\n");
        sb.append("    effectiveDate: ").append(toIndentedString(effectiveDate)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
