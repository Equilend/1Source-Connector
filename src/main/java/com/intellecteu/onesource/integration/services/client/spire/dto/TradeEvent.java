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
import java.time.LocalDate;
import java.util.Objects;

/**
 * TradeEvent
 */

public class TradeEvent {
  @JsonProperty("accountId")
  private Long accountId = null;

  @JsonProperty("allowNegativeInventory")
  private Boolean allowNegativeInventory = null;

  @JsonProperty("changeQuantity")
  private Integer changeQuantity = null;

  @JsonProperty("counterpartyId")
  private Long counterpartyId = null;

  @JsonProperty("cptySubTypeId")
  private Integer cptySubTypeId = null;

  @JsonProperty("depoId")
  private Integer depoId = null;

  @JsonProperty("doNotRecall")
  private Boolean doNotRecall = null;

  @JsonProperty("forceValidity")
  private Boolean forceValidity = null;

  @JsonProperty("oldSettleStatusId")
  private Integer oldSettleStatusId = null;

  @JsonProperty("positionTypeId")
  private Integer positionTypeId = null;

  @JsonProperty("postOrSettle")
  private Boolean postOrSettle = null;

  @JsonProperty("quantity")
  private Double quantity = null;

  @JsonProperty("securityId")
  private Long securityId = null;

  @JsonProperty("settleDate")
  private LocalDate settleDate = null;

  @JsonProperty("settleStatusId")
  private Integer settleStatusId = null;

  @JsonProperty("statusId")
  private Integer statusId = null;

  @JsonProperty("tradeDate")
  private LocalDate tradeDate = null;

  @JsonProperty("tradeId")
  private Long tradeId = null;

  @JsonProperty("tradeTypeId")
  private Integer tradeTypeId = null;

  public TradeEvent accountId(Long accountId) {
    this.accountId = accountId;
    return this;
  }

   /**
   * Get accountId
   * @return accountId
  **/
  @ApiModelProperty(value = "")
  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public TradeEvent allowNegativeInventory(Boolean allowNegativeInventory) {
    this.allowNegativeInventory = allowNegativeInventory;
    return this;
  }

   /**
   * Get allowNegativeInventory
   * @return allowNegativeInventory
  **/
  @ApiModelProperty(value = "")
  public Boolean isAllowNegativeInventory() {
    return allowNegativeInventory;
  }

  public void setAllowNegativeInventory(Boolean allowNegativeInventory) {
    this.allowNegativeInventory = allowNegativeInventory;
  }

  public TradeEvent changeQuantity(Integer changeQuantity) {
    this.changeQuantity = changeQuantity;
    return this;
  }

   /**
   * Get changeQuantity
   * @return changeQuantity
  **/
  @ApiModelProperty(value = "")
  public Integer getChangeQuantity() {
    return changeQuantity;
  }

  public void setChangeQuantity(Integer changeQuantity) {
    this.changeQuantity = changeQuantity;
  }

  public TradeEvent counterpartyId(Long counterpartyId) {
    this.counterpartyId = counterpartyId;
    return this;
  }

   /**
   * Get counterpartyId
   * @return counterpartyId
  **/
  @ApiModelProperty(value = "")
  public Long getCounterpartyId() {
    return counterpartyId;
  }

  public void setCounterpartyId(Long counterpartyId) {
    this.counterpartyId = counterpartyId;
  }

  public TradeEvent cptySubTypeId(Integer cptySubTypeId) {
    this.cptySubTypeId = cptySubTypeId;
    return this;
  }

   /**
   * Get cptySubTypeId
   * @return cptySubTypeId
  **/
  @ApiModelProperty(value = "")
  public Integer getCptySubTypeId() {
    return cptySubTypeId;
  }

  public void setCptySubTypeId(Integer cptySubTypeId) {
    this.cptySubTypeId = cptySubTypeId;
  }

  public TradeEvent depoId(Integer depoId) {
    this.depoId = depoId;
    return this;
  }

   /**
   * Get depoId
   * @return depoId
  **/
  @ApiModelProperty(value = "")
  public Integer getDepoId() {
    return depoId;
  }

  public void setDepoId(Integer depoId) {
    this.depoId = depoId;
  }

  public TradeEvent doNotRecall(Boolean doNotRecall) {
    this.doNotRecall = doNotRecall;
    return this;
  }

   /**
   * Get doNotRecall
   * @return doNotRecall
  **/
  @ApiModelProperty(value = "")
  public Boolean isDoNotRecall() {
    return doNotRecall;
  }

  public void setDoNotRecall(Boolean doNotRecall) {
    this.doNotRecall = doNotRecall;
  }

  public TradeEvent forceValidity(Boolean forceValidity) {
    this.forceValidity = forceValidity;
    return this;
  }

   /**
   * Get forceValidity
   * @return forceValidity
  **/
  @ApiModelProperty(value = "")
  public Boolean isForceValidity() {
    return forceValidity;
  }

  public void setForceValidity(Boolean forceValidity) {
    this.forceValidity = forceValidity;
  }

  public TradeEvent oldSettleStatusId(Integer oldSettleStatusId) {
    this.oldSettleStatusId = oldSettleStatusId;
    return this;
  }

   /**
   * Get oldSettleStatusId
   * @return oldSettleStatusId
  **/
  @ApiModelProperty(value = "")
  public Integer getOldSettleStatusId() {
    return oldSettleStatusId;
  }

  public void setOldSettleStatusId(Integer oldSettleStatusId) {
    this.oldSettleStatusId = oldSettleStatusId;
  }

  public TradeEvent positionTypeId(Integer positionTypeId) {
    this.positionTypeId = positionTypeId;
    return this;
  }

   /**
   * Get positionTypeId
   * @return positionTypeId
  **/
  @ApiModelProperty(value = "")
  public Integer getPositionTypeId() {
    return positionTypeId;
  }

  public void setPositionTypeId(Integer positionTypeId) {
    this.positionTypeId = positionTypeId;
  }

  public TradeEvent postOrSettle(Boolean postOrSettle) {
    this.postOrSettle = postOrSettle;
    return this;
  }

   /**
   * Get postOrSettle
   * @return postOrSettle
  **/
  @ApiModelProperty(value = "")
  public Boolean isPostOrSettle() {
    return postOrSettle;
  }

  public void setPostOrSettle(Boolean postOrSettle) {
    this.postOrSettle = postOrSettle;
  }

  public TradeEvent quantity(Double quantity) {
    this.quantity = quantity;
    return this;
  }

   /**
   * Get quantity
   * @return quantity
  **/
  @ApiModelProperty(value = "")
  public Double getQuantity() {
    return quantity;
  }

  public void setQuantity(Double quantity) {
    this.quantity = quantity;
  }

  public TradeEvent securityId(Long securityId) {
    this.securityId = securityId;
    return this;
  }

   /**
   * Get securityId
   * @return securityId
  **/
  @ApiModelProperty(value = "")
  public Long getSecurityId() {
    return securityId;
  }

  public void setSecurityId(Long securityId) {
    this.securityId = securityId;
  }

  public TradeEvent settleDate(LocalDate settleDate) {
    this.settleDate = settleDate;
    return this;
  }

   /**
   * Get settleDate
   * @return settleDate
  **/
  @ApiModelProperty(value = "")
  public LocalDate getSettleDate() {
    return settleDate;
  }

  public void setSettleDate(LocalDate settleDate) {
    this.settleDate = settleDate;
  }

  public TradeEvent settleStatusId(Integer settleStatusId) {
    this.settleStatusId = settleStatusId;
    return this;
  }

   /**
   * Get settleStatusId
   * @return settleStatusId
  **/
  @ApiModelProperty(value = "")
  public Integer getSettleStatusId() {
    return settleStatusId;
  }

  public void setSettleStatusId(Integer settleStatusId) {
    this.settleStatusId = settleStatusId;
  }

  public TradeEvent statusId(Integer statusId) {
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

  public TradeEvent tradeDate(LocalDate tradeDate) {
    this.tradeDate = tradeDate;
    return this;
  }

   /**
   * Get tradeDate
   * @return tradeDate
  **/
  @ApiModelProperty(value = "")
  public LocalDate getTradeDate() {
    return tradeDate;
  }

  public void setTradeDate(LocalDate tradeDate) {
    this.tradeDate = tradeDate;
  }

  public TradeEvent tradeId(Long tradeId) {
    this.tradeId = tradeId;
    return this;
  }

   /**
   * Get tradeId
   * @return tradeId
  **/
  @ApiModelProperty(value = "")
  public Long getTradeId() {
    return tradeId;
  }

  public void setTradeId(Long tradeId) {
    this.tradeId = tradeId;
  }

  public TradeEvent tradeTypeId(Integer tradeTypeId) {
    this.tradeTypeId = tradeTypeId;
    return this;
  }

   /**
   * Get tradeTypeId
   * @return tradeTypeId
  **/
  @ApiModelProperty(value = "")
  public Integer getTradeTypeId() {
    return tradeTypeId;
  }

  public void setTradeTypeId(Integer tradeTypeId) {
    this.tradeTypeId = tradeTypeId;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TradeEvent tradeEvent = (TradeEvent) o;
    return Objects.equals(this.accountId, tradeEvent.accountId) &&
        Objects.equals(this.allowNegativeInventory, tradeEvent.allowNegativeInventory) &&
        Objects.equals(this.changeQuantity, tradeEvent.changeQuantity) &&
        Objects.equals(this.counterpartyId, tradeEvent.counterpartyId) &&
        Objects.equals(this.cptySubTypeId, tradeEvent.cptySubTypeId) &&
        Objects.equals(this.depoId, tradeEvent.depoId) &&
        Objects.equals(this.doNotRecall, tradeEvent.doNotRecall) &&
        Objects.equals(this.forceValidity, tradeEvent.forceValidity) &&
        Objects.equals(this.oldSettleStatusId, tradeEvent.oldSettleStatusId) &&
        Objects.equals(this.positionTypeId, tradeEvent.positionTypeId) &&
        Objects.equals(this.postOrSettle, tradeEvent.postOrSettle) &&
        Objects.equals(this.quantity, tradeEvent.quantity) &&
        Objects.equals(this.securityId, tradeEvent.securityId) &&
        Objects.equals(this.settleDate, tradeEvent.settleDate) &&
        Objects.equals(this.settleStatusId, tradeEvent.settleStatusId) &&
        Objects.equals(this.statusId, tradeEvent.statusId) &&
        Objects.equals(this.tradeDate, tradeEvent.tradeDate) &&
        Objects.equals(this.tradeId, tradeEvent.tradeId) &&
        Objects.equals(this.tradeTypeId, tradeEvent.tradeTypeId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, allowNegativeInventory, changeQuantity, counterpartyId, cptySubTypeId, depoId, doNotRecall, forceValidity, oldSettleStatusId, positionTypeId, postOrSettle, quantity, securityId, settleDate, settleStatusId, statusId, tradeDate, tradeId, tradeTypeId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TradeEvent {\n");
    
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
    sb.append("    allowNegativeInventory: ").append(toIndentedString(allowNegativeInventory)).append("\n");
    sb.append("    changeQuantity: ").append(toIndentedString(changeQuantity)).append("\n");
    sb.append("    counterpartyId: ").append(toIndentedString(counterpartyId)).append("\n");
    sb.append("    cptySubTypeId: ").append(toIndentedString(cptySubTypeId)).append("\n");
    sb.append("    depoId: ").append(toIndentedString(depoId)).append("\n");
    sb.append("    doNotRecall: ").append(toIndentedString(doNotRecall)).append("\n");
    sb.append("    forceValidity: ").append(toIndentedString(forceValidity)).append("\n");
    sb.append("    oldSettleStatusId: ").append(toIndentedString(oldSettleStatusId)).append("\n");
    sb.append("    positionTypeId: ").append(toIndentedString(positionTypeId)).append("\n");
    sb.append("    postOrSettle: ").append(toIndentedString(postOrSettle)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    securityId: ").append(toIndentedString(securityId)).append("\n");
    sb.append("    settleDate: ").append(toIndentedString(settleDate)).append("\n");
    sb.append("    settleStatusId: ").append(toIndentedString(settleStatusId)).append("\n");
    sb.append("    statusId: ").append(toIndentedString(statusId)).append("\n");
    sb.append("    tradeDate: ").append(toIndentedString(tradeDate)).append("\n");
    sb.append("    tradeId: ").append(toIndentedString(tradeId)).append("\n");
    sb.append("    tradeTypeId: ").append(toIndentedString(tradeTypeId)).append("\n");
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

