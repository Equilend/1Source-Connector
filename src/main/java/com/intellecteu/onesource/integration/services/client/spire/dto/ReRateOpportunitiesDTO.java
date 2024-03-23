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
 * ReRateOpportunitiesDTO
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-03-23T11:29:17.054Z")
public class ReRateOpportunitiesDTO {
  @JsonProperty("amount")
  private Double amount = null;

  @JsonProperty("counterpartyShortName")
  private String counterpartyShortName = null;

  @JsonProperty("opportunityCost")
  private Double opportunityCost = null;

  @JsonProperty("positionId")
  private Long positionId = null;

  @JsonProperty("positionType")
  private String positionType = null;

  @JsonProperty("rate")
  private Double rate = null;

  @JsonProperty("security")
  private String security = null;

  @JsonProperty("shortName")
  private String shortName = null;

  @JsonProperty("status")
  private String status = null;

  @JsonProperty("suggestedRate")
  private Double suggestedRate = null;

  public ReRateOpportunitiesDTO amount(Double amount) {
    this.amount = amount;
    return this;
  }

   /**
   * Get amount
   * @return amount
  **/
  @ApiModelProperty(value = "")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public ReRateOpportunitiesDTO counterpartyShortName(String counterpartyShortName) {
    this.counterpartyShortName = counterpartyShortName;
    return this;
  }

   /**
   * Get counterpartyShortName
   * @return counterpartyShortName
  **/
  @ApiModelProperty(value = "")
  public String getCounterpartyShortName() {
    return counterpartyShortName;
  }

  public void setCounterpartyShortName(String counterpartyShortName) {
    this.counterpartyShortName = counterpartyShortName;
  }

  public ReRateOpportunitiesDTO opportunityCost(Double opportunityCost) {
    this.opportunityCost = opportunityCost;
    return this;
  }

   /**
   * Get opportunityCost
   * @return opportunityCost
  **/
  @ApiModelProperty(value = "")
  public Double getOpportunityCost() {
    return opportunityCost;
  }

  public void setOpportunityCost(Double opportunityCost) {
    this.opportunityCost = opportunityCost;
  }

  public ReRateOpportunitiesDTO positionId(Long positionId) {
    this.positionId = positionId;
    return this;
  }

   /**
   * Get positionId
   * @return positionId
  **/
  @ApiModelProperty(value = "")
  public Long getPositionId() {
    return positionId;
  }

  public void setPositionId(Long positionId) {
    this.positionId = positionId;
  }

  public ReRateOpportunitiesDTO positionType(String positionType) {
    this.positionType = positionType;
    return this;
  }

   /**
   * Get positionType
   * @return positionType
  **/
  @ApiModelProperty(value = "")
  public String getPositionType() {
    return positionType;
  }

  public void setPositionType(String positionType) {
    this.positionType = positionType;
  }

  public ReRateOpportunitiesDTO rate(Double rate) {
    this.rate = rate;
    return this;
  }

   /**
   * Get rate
   * @return rate
  **/
  @ApiModelProperty(value = "")
  public Double getRate() {
    return rate;
  }

  public void setRate(Double rate) {
    this.rate = rate;
  }

  public ReRateOpportunitiesDTO security(String security) {
    this.security = security;
    return this;
  }

   /**
   * Get security
   * @return security
  **/
  @ApiModelProperty(value = "")
  public String getSecurity() {
    return security;
  }

  public void setSecurity(String security) {
    this.security = security;
  }

  public ReRateOpportunitiesDTO shortName(String shortName) {
    this.shortName = shortName;
    return this;
  }

   /**
   * Get shortName
   * @return shortName
  **/
  @ApiModelProperty(value = "")
  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public ReRateOpportunitiesDTO status(String status) {
    this.status = status;
    return this;
  }

   /**
   * Get status
   * @return status
  **/
  @ApiModelProperty(value = "")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public ReRateOpportunitiesDTO suggestedRate(Double suggestedRate) {
    this.suggestedRate = suggestedRate;
    return this;
  }

   /**
   * Get suggestedRate
   * @return suggestedRate
  **/
  @ApiModelProperty(value = "")
  public Double getSuggestedRate() {
    return suggestedRate;
  }

  public void setSuggestedRate(Double suggestedRate) {
    this.suggestedRate = suggestedRate;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReRateOpportunitiesDTO reRateOpportunitiesDTO = (ReRateOpportunitiesDTO) o;
    return Objects.equals(this.amount, reRateOpportunitiesDTO.amount) &&
        Objects.equals(this.counterpartyShortName, reRateOpportunitiesDTO.counterpartyShortName) &&
        Objects.equals(this.opportunityCost, reRateOpportunitiesDTO.opportunityCost) &&
        Objects.equals(this.positionId, reRateOpportunitiesDTO.positionId) &&
        Objects.equals(this.positionType, reRateOpportunitiesDTO.positionType) &&
        Objects.equals(this.rate, reRateOpportunitiesDTO.rate) &&
        Objects.equals(this.security, reRateOpportunitiesDTO.security) &&
        Objects.equals(this.shortName, reRateOpportunitiesDTO.shortName) &&
        Objects.equals(this.status, reRateOpportunitiesDTO.status) &&
        Objects.equals(this.suggestedRate, reRateOpportunitiesDTO.suggestedRate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, counterpartyShortName, opportunityCost, positionId, positionType, rate, security, shortName, status, suggestedRate);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReRateOpportunitiesDTO {\n");
    
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    counterpartyShortName: ").append(toIndentedString(counterpartyShortName)).append("\n");
    sb.append("    opportunityCost: ").append(toIndentedString(opportunityCost)).append("\n");
    sb.append("    positionId: ").append(toIndentedString(positionId)).append("\n");
    sb.append("    positionType: ").append(toIndentedString(positionType)).append("\n");
    sb.append("    rate: ").append(toIndentedString(rate)).append("\n");
    sb.append("    security: ").append(toIndentedString(security)).append("\n");
    sb.append("    shortName: ").append(toIndentedString(shortName)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    suggestedRate: ").append(toIndentedString(suggestedRate)).append("\n");
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

