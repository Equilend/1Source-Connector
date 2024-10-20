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
import java.util.Objects;
/**
 * BuyinInitiateDTO
 */



public class BuyinInitiateDTO {
  @JsonProperty("buyinIniitiateId")
  private String buyinIniitiateId = null;

  @JsonProperty("contractId")
  private String contractId = null;

  @JsonProperty("status")
  private BuyinInitiateStatusDTO status = null;

  @JsonProperty("openQuantity")
  private Integer openQuantity = null;

  @JsonProperty("quantity")
  private Integer quantity = null;

  @JsonProperty("lastUpdateDatetime")
  private LocalDateTime lastUpdateDatetime = null;

  public BuyinInitiateDTO buyinIniitiateId(String buyinIniitiateId) {
    this.buyinIniitiateId = buyinIniitiateId;
    return this;
  }

   /**
   * Get buyinIniitiateId
   * @return buyinIniitiateId
  **/
  @Schema(description = "")
  public String getBuyinIniitiateId() {
    return buyinIniitiateId;
  }

  public void setBuyinIniitiateId(String buyinIniitiateId) {
    this.buyinIniitiateId = buyinIniitiateId;
  }

  public BuyinInitiateDTO contractId(String contractId) {
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

  public BuyinInitiateDTO status(BuyinInitiateStatusDTO status) {
    this.status = status;
    return this;
  }

   /**
   * Get status
   * @return status
  **/
  @Schema(required = true, description = "")
  public BuyinInitiateStatusDTO getStatus() {
    return status;
  }

  public void setStatus(BuyinInitiateStatusDTO status) {
    this.status = status;
  }

  public BuyinInitiateDTO openQuantity(Integer openQuantity) {
    this.openQuantity = openQuantity;
    return this;
  }

   /**
   * Get openQuantity
   * @return openQuantity
  **/
  @Schema(required = true, description = "")
  public Integer getOpenQuantity() {
    return openQuantity;
  }

  public void setOpenQuantity(Integer openQuantity) {
    this.openQuantity = openQuantity;
  }

  public BuyinInitiateDTO quantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

   /**
   * Get quantity
   * @return quantity
  **/
  @Schema(description = "")
  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public BuyinInitiateDTO lastUpdateDatetime(LocalDateTime lastUpdateDatetime) {
    this.lastUpdateDatetime = lastUpdateDatetime;
    return this;
  }

   /**
   * Get lastUpdateDatetime
   * @return lastUpdateDatetime
  **/
  @Schema(required = true, description = "")
  public LocalDateTime getLastUpdateDatetime() {
    return lastUpdateDatetime;
  }

  public void setLastUpdateDatetime(LocalDateTime lastUpdateDatetime) {
    this.lastUpdateDatetime = lastUpdateDatetime;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BuyinInitiateDTO buyinInitiate = (BuyinInitiateDTO) o;
    return Objects.equals(this.buyinIniitiateId, buyinInitiate.buyinIniitiateId) &&
        Objects.equals(this.contractId, buyinInitiate.contractId) &&
        Objects.equals(this.status, buyinInitiate.status) &&
        Objects.equals(this.openQuantity, buyinInitiate.openQuantity) &&
        Objects.equals(this.quantity, buyinInitiate.quantity) &&
        Objects.equals(this.lastUpdateDatetime, buyinInitiate.lastUpdateDatetime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(buyinIniitiateId, contractId, status, openQuantity, quantity, lastUpdateDatetime);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BuyinInitiateDTO {\n");
    
    sb.append("    buyinIniitiateId: ").append(toIndentedString(buyinIniitiateId)).append("\n");
    sb.append("    contractId: ").append(toIndentedString(contractId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    openQuantity: ").append(toIndentedString(openQuantity)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    lastUpdateDatetime: ").append(toIndentedString(lastUpdateDatetime)).append("\n");
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
