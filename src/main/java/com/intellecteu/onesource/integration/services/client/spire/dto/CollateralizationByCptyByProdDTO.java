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
 * CollateralizationByCptyByProdDTO
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-03-23T11:29:17.054Z")
public class CollateralizationByCptyByProdDTO {
  @JsonProperty("broker")
  private String broker = null;

  @JsonProperty("productType")
  private String productType = null;

  @JsonProperty("quotient")
  private Double quotient = null;

  @JsonProperty("sumCashHeld")
  private Double sumCashHeld = null;

  @JsonProperty("sumMarketValue")
  private Double sumMarketValue = null;

  public CollateralizationByCptyByProdDTO broker(String broker) {
    this.broker = broker;
    return this;
  }

   /**
   * Get broker
   * @return broker
  **/
  @ApiModelProperty(value = "")
  public String getBroker() {
    return broker;
  }

  public void setBroker(String broker) {
    this.broker = broker;
  }

  public CollateralizationByCptyByProdDTO productType(String productType) {
    this.productType = productType;
    return this;
  }

   /**
   * Get productType
   * @return productType
  **/
  @ApiModelProperty(value = "")
  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public CollateralizationByCptyByProdDTO quotient(Double quotient) {
    this.quotient = quotient;
    return this;
  }

   /**
   * Get quotient
   * @return quotient
  **/
  @ApiModelProperty(value = "")
  public Double getQuotient() {
    return quotient;
  }

  public void setQuotient(Double quotient) {
    this.quotient = quotient;
  }

  public CollateralizationByCptyByProdDTO sumCashHeld(Double sumCashHeld) {
    this.sumCashHeld = sumCashHeld;
    return this;
  }

   /**
   * Get sumCashHeld
   * @return sumCashHeld
  **/
  @ApiModelProperty(value = "")
  public Double getSumCashHeld() {
    return sumCashHeld;
  }

  public void setSumCashHeld(Double sumCashHeld) {
    this.sumCashHeld = sumCashHeld;
  }

  public CollateralizationByCptyByProdDTO sumMarketValue(Double sumMarketValue) {
    this.sumMarketValue = sumMarketValue;
    return this;
  }

   /**
   * Get sumMarketValue
   * @return sumMarketValue
  **/
  @ApiModelProperty(value = "")
  public Double getSumMarketValue() {
    return sumMarketValue;
  }

  public void setSumMarketValue(Double sumMarketValue) {
    this.sumMarketValue = sumMarketValue;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CollateralizationByCptyByProdDTO collateralizationByCptyByProdDTO = (CollateralizationByCptyByProdDTO) o;
    return Objects.equals(this.broker, collateralizationByCptyByProdDTO.broker) &&
        Objects.equals(this.productType, collateralizationByCptyByProdDTO.productType) &&
        Objects.equals(this.quotient, collateralizationByCptyByProdDTO.quotient) &&
        Objects.equals(this.sumCashHeld, collateralizationByCptyByProdDTO.sumCashHeld) &&
        Objects.equals(this.sumMarketValue, collateralizationByCptyByProdDTO.sumMarketValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(broker, productType, quotient, sumCashHeld, sumMarketValue);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CollateralizationByCptyByProdDTO {\n");
    
    sb.append("    broker: ").append(toIndentedString(broker)).append("\n");
    sb.append("    productType: ").append(toIndentedString(productType)).append("\n");
    sb.append("    quotient: ").append(toIndentedString(quotient)).append("\n");
    sb.append("    sumCashHeld: ").append(toIndentedString(sumCashHeld)).append("\n");
    sb.append("    sumMarketValue: ").append(toIndentedString(sumMarketValue)).append("\n");
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

