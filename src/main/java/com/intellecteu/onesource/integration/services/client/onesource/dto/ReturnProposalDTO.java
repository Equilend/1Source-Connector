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
import java.time.LocalDate;
import java.util.Objects;
/**
 * ReturnProposalDTO
 */



public class ReturnProposalDTO {
  @JsonProperty("executionVenue")
  private VenueDTO executionVenue = null;

  @JsonProperty("quantity")
  private Integer quantity = null;

  @JsonProperty("returnDate")
  private LocalDate returnDate = null;

  @JsonProperty("returnSettlementDate")
  private LocalDate returnSettlementDate = null;

  @JsonProperty("collateralValue")
  private Double collateralValue = null;

  @JsonProperty("settlementType")
  private SettlementTypeDTO settlementType = null;

  @JsonProperty("settlement")
  private PartySettlementInstructionDTO settlement = null;

  public ReturnProposalDTO executionVenue(VenueDTO executionVenue) {
    this.executionVenue = executionVenue;
    return this;
  }

   /**
   * Get executionVenue
   * @return executionVenue
  **/
  @Schema(description = "")
  public VenueDTO getExecutionVenue() {
    return executionVenue;
  }

  public void setExecutionVenue(VenueDTO executionVenue) {
    this.executionVenue = executionVenue;
  }

  public ReturnProposalDTO quantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

   /**
   * Get quantity
   * @return quantity
  **/
  @Schema(required = true, description = "")
  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public ReturnProposalDTO returnDate(LocalDate returnDate) {
    this.returnDate = returnDate;
    return this;
  }

   /**
   * Get returnDate
   * @return returnDate
  **/
  @Schema(required = true, description = "")
  public LocalDate getReturnDate() {
    return returnDate;
  }

  public void setReturnDate(LocalDate returnDate) {
    this.returnDate = returnDate;
  }

  public ReturnProposalDTO returnSettlementDate(LocalDate returnSettlementDate) {
    this.returnSettlementDate = returnSettlementDate;
    return this;
  }

   /**
   * Get returnSettlementDate
   * @return returnSettlementDate
  **/
  @Schema(required = true, description = "")
  public LocalDate getReturnSettlementDate() {
    return returnSettlementDate;
  }

  public void setReturnSettlementDate(LocalDate returnSettlementDate) {
    this.returnSettlementDate = returnSettlementDate;
  }

  public ReturnProposalDTO collateralValue(Double collateralValue) {
    this.collateralValue = collateralValue;
    return this;
  }

   /**
   * Get collateralValue
   * @return collateralValue
  **/
  @Schema(description = "")
  public Double getCollateralValue() {
    return collateralValue;
  }

  public void setCollateralValue(Double collateralValue) {
    this.collateralValue = collateralValue;
  }

  public ReturnProposalDTO settlementType(SettlementTypeDTO settlementType) {
    this.settlementType = settlementType;
    return this;
  }

   /**
   * Get settlementType
   * @return settlementType
  **/
  @Schema(description = "")
  public SettlementTypeDTO getSettlementType() {
    return settlementType;
  }

  public void setSettlementType(SettlementTypeDTO settlementType) {
    this.settlementType = settlementType;
  }

  public ReturnProposalDTO settlement(PartySettlementInstructionDTO settlement) {
    this.settlement = settlement;
    return this;
  }

   /**
   * Get settlement
   * @return settlement
  **/
  @Schema(description = "")
  public PartySettlementInstructionDTO getSettlement() {
    return settlement;
  }

  public void setSettlement(PartySettlementInstructionDTO settlement) {
    this.settlement = settlement;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReturnProposalDTO returnProposal = (ReturnProposalDTO) o;
    return Objects.equals(this.executionVenue, returnProposal.executionVenue) &&
        Objects.equals(this.quantity, returnProposal.quantity) &&
        Objects.equals(this.returnDate, returnProposal.returnDate) &&
        Objects.equals(this.returnSettlementDate, returnProposal.returnSettlementDate) &&
        Objects.equals(this.collateralValue, returnProposal.collateralValue) &&
        Objects.equals(this.settlementType, returnProposal.settlementType) &&
        Objects.equals(this.settlement, returnProposal.settlement);
  }

  @Override
  public int hashCode() {
    return Objects.hash(executionVenue, quantity, returnDate, returnSettlementDate, collateralValue, settlementType, settlement);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReturnProposalDTO {\n");
    
    sb.append("    executionVenue: ").append(toIndentedString(executionVenue)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    returnDate: ").append(toIndentedString(returnDate)).append("\n");
    sb.append("    returnSettlementDate: ").append(toIndentedString(returnSettlementDate)).append("\n");
    sb.append("    collateralValue: ").append(toIndentedString(collateralValue)).append("\n");
    sb.append("    settlementType: ").append(toIndentedString(settlementType)).append("\n");
    sb.append("    settlement: ").append(toIndentedString(settlement)).append("\n");
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
