/*
 * Spire-Trade-Service
 * Spire-Trade-REST-API
 *
 * OpenAPI spec version: 4.0
 * Contact: support.stonewain.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.intellecteu.onesource.integration.services.client.spire.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/**
 * StatusDTO
 */

public class StatusDTO {
  @JsonProperty("forAccount")
  private Integer forAccount = null;

  @JsonProperty("forAld")
  private Integer forAld = null;

  @JsonProperty("forFeeds")
  private Integer forFeeds = null;

  @JsonProperty("forInstruction")
  private Integer forInstruction = null;

  @JsonProperty("forLocates")
  private Integer forLocates = null;

  @JsonProperty("forOrder")
  private Integer forOrder = null;

  @JsonProperty("forPosition")
  private Integer forPosition = null;

  @JsonProperty("forRecall")
  private Integer forRecall = null;

  @JsonProperty("forSecurity")
  private Integer forSecurity = null;

  @JsonProperty("forSettlement")
  private Integer forSettlement = null;

  @JsonProperty("forTrade")
  private Integer forTrade = null;

  @JsonProperty("status")
  private String status = null;

  @JsonProperty("statusId")
  private Integer statusId = null;

  public StatusDTO forAccount(Integer forAccount) {
    this.forAccount = forAccount;
    return this;
  }

   /**
   * Get forAccount
   * @return forAccount
  **/
  @ApiModelProperty(value = "")
  public Integer getForAccount() {
    return forAccount;
  }

  public void setForAccount(Integer forAccount) {
    this.forAccount = forAccount;
  }

  public StatusDTO forAld(Integer forAld) {
    this.forAld = forAld;
    return this;
  }

   /**
   * Get forAld
   * @return forAld
  **/
  @ApiModelProperty(value = "")
  public Integer getForAld() {
    return forAld;
  }

  public void setForAld(Integer forAld) {
    this.forAld = forAld;
  }

  public StatusDTO forFeeds(Integer forFeeds) {
    this.forFeeds = forFeeds;
    return this;
  }

   /**
   * Get forFeeds
   * @return forFeeds
  **/
  @ApiModelProperty(value = "")
  public Integer getForFeeds() {
    return forFeeds;
  }

  public void setForFeeds(Integer forFeeds) {
    this.forFeeds = forFeeds;
  }

  public StatusDTO forInstruction(Integer forInstruction) {
    this.forInstruction = forInstruction;
    return this;
  }

   /**
   * Get forInstruction
   * @return forInstruction
  **/
  @ApiModelProperty(value = "")
  public Integer getForInstruction() {
    return forInstruction;
  }

  public void setForInstruction(Integer forInstruction) {
    this.forInstruction = forInstruction;
  }

  public StatusDTO forLocates(Integer forLocates) {
    this.forLocates = forLocates;
    return this;
  }

   /**
   * Get forLocates
   * @return forLocates
  **/
  @ApiModelProperty(value = "")
  public Integer getForLocates() {
    return forLocates;
  }

  public void setForLocates(Integer forLocates) {
    this.forLocates = forLocates;
  }

  public StatusDTO forOrder(Integer forOrder) {
    this.forOrder = forOrder;
    return this;
  }

   /**
   * Get forOrder
   * @return forOrder
  **/
  @ApiModelProperty(value = "")
  public Integer getForOrder() {
    return forOrder;
  }

  public void setForOrder(Integer forOrder) {
    this.forOrder = forOrder;
  }

  public StatusDTO forPosition(Integer forPosition) {
    this.forPosition = forPosition;
    return this;
  }

   /**
   * Get forPosition
   * @return forPosition
  **/
  @ApiModelProperty(value = "")
  public Integer getForPosition() {
    return forPosition;
  }

  public void setForPosition(Integer forPosition) {
    this.forPosition = forPosition;
  }

  public StatusDTO forRecall(Integer forRecall) {
    this.forRecall = forRecall;
    return this;
  }

   /**
   * Get forRecall
   * @return forRecall
  **/
  @ApiModelProperty(value = "")
  public Integer getForRecall() {
    return forRecall;
  }

  public void setForRecall(Integer forRecall) {
    this.forRecall = forRecall;
  }

  public StatusDTO forSecurity(Integer forSecurity) {
    this.forSecurity = forSecurity;
    return this;
  }

   /**
   * Get forSecurity
   * @return forSecurity
  **/
  @ApiModelProperty(value = "")
  public Integer getForSecurity() {
    return forSecurity;
  }

  public void setForSecurity(Integer forSecurity) {
    this.forSecurity = forSecurity;
  }

  public StatusDTO forSettlement(Integer forSettlement) {
    this.forSettlement = forSettlement;
    return this;
  }

   /**
   * Get forSettlement
   * @return forSettlement
  **/
  @ApiModelProperty(value = "")
  public Integer getForSettlement() {
    return forSettlement;
  }

  public void setForSettlement(Integer forSettlement) {
    this.forSettlement = forSettlement;
  }

  public StatusDTO forTrade(Integer forTrade) {
    this.forTrade = forTrade;
    return this;
  }

   /**
   * Get forTrade
   * @return forTrade
  **/
  @ApiModelProperty(value = "")
  public Integer getForTrade() {
    return forTrade;
  }

  public void setForTrade(Integer forTrade) {
    this.forTrade = forTrade;
  }

  public StatusDTO status(String status) {
    this.status = status;
    return this;
  }

   /**
   * Allowed values for position and trade status:  OPEN, CLOSED, FUTURE, PREPAID, FAILED, CANCELLED.  Allowed values for trade settled status:  NEW, READY, PENDING, SETTLED, DELETED, FAILED, NO INSTRUCTION, PRINTED, ACCEPTED, VOID
   * @return status
  **/
  @ApiModelProperty(value = "Allowed values for position and trade status:  OPEN, CLOSED, FUTURE, PREPAID, FAILED, CANCELLED.  Allowed values for trade settled status:  NEW, READY, PENDING, SETTLED, DELETED, FAILED, NO INSTRUCTION, PRINTED, ACCEPTED, VOID")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public StatusDTO statusId(Integer statusId) {
    this.statusId = statusId;
    return this;
  }

   /**
   * Get statusId
   * @return statusId
  **/
  @ApiModelProperty(value = "")
  public Integer getStatusId() {
    return statusId;
  }

  public void setStatusId(Integer statusId) {
    this.statusId = statusId;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StatusDTO statusDTO = (StatusDTO) o;
    return Objects.equals(this.forAccount, statusDTO.forAccount) &&
        Objects.equals(this.forAld, statusDTO.forAld) &&
        Objects.equals(this.forFeeds, statusDTO.forFeeds) &&
        Objects.equals(this.forInstruction, statusDTO.forInstruction) &&
        Objects.equals(this.forLocates, statusDTO.forLocates) &&
        Objects.equals(this.forOrder, statusDTO.forOrder) &&
        Objects.equals(this.forPosition, statusDTO.forPosition) &&
        Objects.equals(this.forRecall, statusDTO.forRecall) &&
        Objects.equals(this.forSecurity, statusDTO.forSecurity) &&
        Objects.equals(this.forSettlement, statusDTO.forSettlement) &&
        Objects.equals(this.forTrade, statusDTO.forTrade) &&
        Objects.equals(this.status, statusDTO.status) &&
        Objects.equals(this.statusId, statusDTO.statusId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(forAccount, forAld, forFeeds, forInstruction, forLocates, forOrder, forPosition, forRecall, forSecurity, forSettlement, forTrade, status, statusId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StatusDTO {\n");
    
    sb.append("    forAccount: ").append(toIndentedString(forAccount)).append("\n");
    sb.append("    forAld: ").append(toIndentedString(forAld)).append("\n");
    sb.append("    forFeeds: ").append(toIndentedString(forFeeds)).append("\n");
    sb.append("    forInstruction: ").append(toIndentedString(forInstruction)).append("\n");
    sb.append("    forLocates: ").append(toIndentedString(forLocates)).append("\n");
    sb.append("    forOrder: ").append(toIndentedString(forOrder)).append("\n");
    sb.append("    forPosition: ").append(toIndentedString(forPosition)).append("\n");
    sb.append("    forRecall: ").append(toIndentedString(forRecall)).append("\n");
    sb.append("    forSecurity: ").append(toIndentedString(forSecurity)).append("\n");
    sb.append("    forSettlement: ").append(toIndentedString(forSettlement)).append("\n");
    sb.append("    forTrade: ").append(toIndentedString(forTrade)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    statusId: ").append(toIndentedString(statusId)).append("\n");
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

