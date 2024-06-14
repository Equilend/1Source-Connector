/*
 * 1Source Ledger API
 * 1Source Ledger API provides client access to the 1Source Ledger. You can find out more about 1Source at [https://equilend.com](https://equilend.com).  This specification is work in progress. The design is meant to model the securities lending life cycle in as clean a way as possible while being robust enough to easily translate to ISLA CDM workflows and data model.  API specification is the intellectual property of EquiLend LLC and should not be copied or disseminated in any way. 
 *
 * OpenAPI spec version: 1.0.4
 * Contact: 1source_help@equilend.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package com.intellecteu.onesource.integration.services.client.onesource.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * ContractDTO
 */



public class ContractDTO {
  @JsonProperty("contractId")
  private String contractId = null;

  @JsonProperty("lastEvent")
  private EventDTO lastEvent = null;

  @JsonProperty("contractStatus")
  private ContractStatusDTO contractStatus = null;

  @JsonProperty("lastUpdatePartyId")
  private String lastUpdatePartyId = null;

  @JsonProperty("lastUpdateDateTime")
  private LocalDateTime lastUpdateDateTime = null;

  @JsonProperty("isInitiator")
  private Boolean isInitiator = null;

  @JsonProperty("trade")
  private TradeAgreementDTO trade = null;

  @JsonProperty("settlement")
  private List<PartySettlementInstructionDTO> settlement = null;

  @JsonProperty("parentContractId")
  private String parentContractId = null;

  public ContractDTO contractId(String contractId) {
    this.contractId = contractId;
    return this;
  }

   /**
   * Get contractId
   * @return contractId
  **/
  @Schema(required = true, description = "")
  public String getContractId() {
    return contractId;
  }

  public void setContractId(String contractId) {
    this.contractId = contractId;
  }

  public ContractDTO lastEvent(EventDTO lastEvent) {
    this.lastEvent = lastEvent;
    return this;
  }

   /**
   * Get lastEvent
   * @return lastEvent
  **/
  @Schema(description = "")
  public EventDTO getLastEvent() {
    return lastEvent;
  }

  public void setLastEvent(EventDTO lastEvent) {
    this.lastEvent = lastEvent;
  }

  public ContractDTO contractStatus(ContractStatusDTO contractStatus) {
    this.contractStatus = contractStatus;
    return this;
  }

   /**
   * Get contractStatus
   * @return contractStatus
  **/
  @Schema(required = true, description = "")
  public ContractStatusDTO getContractStatus() {
    return contractStatus;
  }

  public void setContractStatus(ContractStatusDTO contractStatus) {
    this.contractStatus = contractStatus;
  }

  public ContractDTO lastUpdatePartyId(String lastUpdatePartyId) {
    this.lastUpdatePartyId = lastUpdatePartyId;
    return this;
  }

   /**
   * Get lastUpdatePartyId
   * @return lastUpdatePartyId
  **/
  @Schema(description = "")
  public String getLastUpdatePartyId() {
    return lastUpdatePartyId;
  }

  public void setLastUpdatePartyId(String lastUpdatePartyId) {
    this.lastUpdatePartyId = lastUpdatePartyId;
  }

  public ContractDTO lastUpdateDateTime(LocalDateTime lastUpdateDateTime) {
    this.lastUpdateDateTime = lastUpdateDateTime;
    return this;
  }

   /**
   * Get lastUpdateDateTime
   * @return lastUpdateDateTime
  **/
  @Schema(description = "")
  public LocalDateTime getLastUpdateDateTime() {
    return lastUpdateDateTime;
  }

  public void setLastUpdateDateTime(LocalDateTime lastUpdateDateTime) {
    this.lastUpdateDateTime = lastUpdateDateTime;
  }

  public ContractDTO isInitiator(Boolean isInitiator) {
    this.isInitiator = isInitiator;
    return this;
  }

   /**
   * Get isInitiator
   * @return isInitiator
  **/
  @Schema(description = "")
  public Boolean isIsInitiator() {
    return isInitiator;
  }

  public void setIsInitiator(Boolean isInitiator) {
    this.isInitiator = isInitiator;
  }

  public ContractDTO trade(TradeAgreementDTO trade) {
    this.trade = trade;
    return this;
  }

   /**
   * Get trade
   * @return trade
  **/
  @Schema(description = "")
  public TradeAgreementDTO getTrade() {
    return trade;
  }

  public void setTrade(TradeAgreementDTO trade) {
    this.trade = trade;
  }

  public ContractDTO settlement(List<PartySettlementInstructionDTO> settlement) {
    this.settlement = settlement;
    return this;
  }

  public ContractDTO addSettlementItem(PartySettlementInstructionDTO settlementItem) {
    if (this.settlement == null) {
      this.settlement = new ArrayList<>();
    }
    this.settlement.add(settlementItem);
    return this;
  }

   /**
   * Get settlement
   * @return settlement
  **/
  @Schema(description = "")
  public List<PartySettlementInstructionDTO> getSettlement() {
    return settlement;
  }

  public void setSettlement(List<PartySettlementInstructionDTO> settlement) {
    this.settlement = settlement;
  }

  public ContractDTO parentContractId(String parentContractId) {
    this.parentContractId = parentContractId;
    return this;
  }

   /**
   * Get parentContractId
   * @return parentContractId
  **/
  @Schema(description = "")
  public String getParentContractId() {
    return parentContractId;
  }

  public void setParentContractId(String parentContractId) {
    this.parentContractId = parentContractId;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContractDTO contract = (ContractDTO) o;
    return Objects.equals(this.contractId, contract.contractId) &&
        Objects.equals(this.lastEvent, contract.lastEvent) &&
        Objects.equals(this.contractStatus, contract.contractStatus) &&
        Objects.equals(this.lastUpdatePartyId, contract.lastUpdatePartyId) &&
        Objects.equals(this.lastUpdateDateTime, contract.lastUpdateDateTime) &&
        Objects.equals(this.isInitiator, contract.isInitiator) &&
        Objects.equals(this.trade, contract.trade) &&
        Objects.equals(this.settlement, contract.settlement) &&
        Objects.equals(this.parentContractId, contract.parentContractId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contractId, lastEvent, contractStatus, lastUpdatePartyId, lastUpdateDateTime, isInitiator, trade, settlement, parentContractId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContractDTO {\n");
    
    sb.append("    contractId: ").append(toIndentedString(contractId)).append("\n");
    sb.append("    lastEvent: ").append(toIndentedString(lastEvent)).append("\n");
    sb.append("    contractStatus: ").append(toIndentedString(contractStatus)).append("\n");
    sb.append("    lastUpdatePartyId: ").append(toIndentedString(lastUpdatePartyId)).append("\n");
    sb.append("    lastUpdateDateTime: ").append(toIndentedString(lastUpdateDateTime)).append("\n");
    sb.append("    isInitiator: ").append(toIndentedString(isInitiator)).append("\n");
    sb.append("    trade: ").append(toIndentedString(trade)).append("\n");
    sb.append("    settlement: ").append(toIndentedString(settlement)).append("\n");
    sb.append("    parentContractId: ").append(toIndentedString(parentContractId)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
