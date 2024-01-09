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
 * PositionBySecurityDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-01-07T21:15:14.632Z")
public class PositionBySecurityDTO {
  @JsonProperty("__qualifiedName")
  private String qualifiedName = null;

  @JsonProperty("avgRate")
  private Double avgRate = null;

  @JsonProperty("cusip")
  private String cusip = null;

  @JsonProperty("isin")
  private String isin = null;

  @JsonProperty("positionType")
  private String positionType = null;

  @JsonProperty("sedol")
  private String sedol = null;

  @JsonProperty("status")
  private String status = null;

  @JsonProperty("totalAmount")
  private Double totalAmount = null;

  @JsonProperty("totalDailyLoss")
  private Double totalDailyLoss = null;

  @JsonProperty("totalDailyPAndL")
  private Double totalDailyPAndL = null;

  @JsonProperty("totalDailyProfit")
  private Double totalDailyProfit = null;

  public PositionBySecurityDTO qualifiedName(String qualifiedName) {
    this.qualifiedName = qualifiedName;
    return this;
  }

   /**
   * Get qualifiedName
   * @return qualifiedName
  **/
  @ApiModelProperty(value = "")
  public String getQualifiedName() {
    return qualifiedName;
  }

  public void setQualifiedName(String qualifiedName) {
    this.qualifiedName = qualifiedName;
  }

  public PositionBySecurityDTO avgRate(Double avgRate) {
    this.avgRate = avgRate;
    return this;
  }

   /**
   * Get avgRate
   * @return avgRate
  **/
  @ApiModelProperty(value = "")
  public Double getAvgRate() {
    return avgRate;
  }

  public void setAvgRate(Double avgRate) {
    this.avgRate = avgRate;
  }

  public PositionBySecurityDTO cusip(String cusip) {
    this.cusip = cusip;
    return this;
  }

   /**
   * Get cusip
   * @return cusip
  **/
  @ApiModelProperty(value = "")
  public String getCusip() {
    return cusip;
  }

  public void setCusip(String cusip) {
    this.cusip = cusip;
  }

  public PositionBySecurityDTO isin(String isin) {
    this.isin = isin;
    return this;
  }

   /**
   * Get isin
   * @return isin
  **/
  @ApiModelProperty(value = "")
  public String getIsin() {
    return isin;
  }

  public void setIsin(String isin) {
    this.isin = isin;
  }

  public PositionBySecurityDTO positionType(String positionType) {
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

  public PositionBySecurityDTO sedol(String sedol) {
    this.sedol = sedol;
    return this;
  }

   /**
   * Get sedol
   * @return sedol
  **/
  @ApiModelProperty(value = "")
  public String getSedol() {
    return sedol;
  }

  public void setSedol(String sedol) {
    this.sedol = sedol;
  }

  public PositionBySecurityDTO status(String status) {
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

  public PositionBySecurityDTO totalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
    return this;
  }

   /**
   * Get totalAmount
   * @return totalAmount
  **/
  @ApiModelProperty(value = "")
  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public PositionBySecurityDTO totalDailyLoss(Double totalDailyLoss) {
    this.totalDailyLoss = totalDailyLoss;
    return this;
  }

   /**
   * Get totalDailyLoss
   * @return totalDailyLoss
  **/
  @ApiModelProperty(value = "")
  public Double getTotalDailyLoss() {
    return totalDailyLoss;
  }

  public void setTotalDailyLoss(Double totalDailyLoss) {
    this.totalDailyLoss = totalDailyLoss;
  }

  public PositionBySecurityDTO totalDailyPAndL(Double totalDailyPAndL) {
    this.totalDailyPAndL = totalDailyPAndL;
    return this;
  }

   /**
   * Get totalDailyPAndL
   * @return totalDailyPAndL
  **/
  @ApiModelProperty(value = "")
  public Double getTotalDailyPAndL() {
    return totalDailyPAndL;
  }

  public void setTotalDailyPAndL(Double totalDailyPAndL) {
    this.totalDailyPAndL = totalDailyPAndL;
  }

  public PositionBySecurityDTO totalDailyProfit(Double totalDailyProfit) {
    this.totalDailyProfit = totalDailyProfit;
    return this;
  }

   /**
   * Get totalDailyProfit
   * @return totalDailyProfit
  **/
  @ApiModelProperty(value = "")
  public Double getTotalDailyProfit() {
    return totalDailyProfit;
  }

  public void setTotalDailyProfit(Double totalDailyProfit) {
    this.totalDailyProfit = totalDailyProfit;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PositionBySecurityDTO positionBySecurityDTO = (PositionBySecurityDTO) o;
    return Objects.equals(this.qualifiedName, positionBySecurityDTO.qualifiedName) &&
        Objects.equals(this.avgRate, positionBySecurityDTO.avgRate) &&
        Objects.equals(this.cusip, positionBySecurityDTO.cusip) &&
        Objects.equals(this.isin, positionBySecurityDTO.isin) &&
        Objects.equals(this.positionType, positionBySecurityDTO.positionType) &&
        Objects.equals(this.sedol, positionBySecurityDTO.sedol) &&
        Objects.equals(this.status, positionBySecurityDTO.status) &&
        Objects.equals(this.totalAmount, positionBySecurityDTO.totalAmount) &&
        Objects.equals(this.totalDailyLoss, positionBySecurityDTO.totalDailyLoss) &&
        Objects.equals(this.totalDailyPAndL, positionBySecurityDTO.totalDailyPAndL) &&
        Objects.equals(this.totalDailyProfit, positionBySecurityDTO.totalDailyProfit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(qualifiedName, avgRate, cusip, isin, positionType, sedol, status, totalAmount, totalDailyLoss, totalDailyPAndL, totalDailyProfit);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PositionBySecurityDTO {\n");
    
    sb.append("    qualifiedName: ").append(toIndentedString(qualifiedName)).append("\n");
    sb.append("    avgRate: ").append(toIndentedString(avgRate)).append("\n");
    sb.append("    cusip: ").append(toIndentedString(cusip)).append("\n");
    sb.append("    isin: ").append(toIndentedString(isin)).append("\n");
    sb.append("    positionType: ").append(toIndentedString(positionType)).append("\n");
    sb.append("    sedol: ").append(toIndentedString(sedol)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    totalAmount: ").append(toIndentedString(totalAmount)).append("\n");
    sb.append("    totalDailyLoss: ").append(toIndentedString(totalDailyLoss)).append("\n");
    sb.append("    totalDailyPAndL: ").append(toIndentedString(totalDailyPAndL)).append("\n");
    sb.append("    totalDailyProfit: ").append(toIndentedString(totalDailyProfit)).append("\n");
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

