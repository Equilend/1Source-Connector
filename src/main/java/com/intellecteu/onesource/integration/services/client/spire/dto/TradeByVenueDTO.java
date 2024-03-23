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
 * TradeByVenueDTO
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-03-23T11:29:17.054Z")
public class TradeByVenueDTO {
  @JsonProperty("avgRate")
  private Double avgRate = null;

  @JsonProperty("loanStatus")
  private String loanStatus = null;

  @JsonProperty("totalActivityLoss")
  private Double totalActivityLoss = null;

  @JsonProperty("totalActivityPAndL")
  private Double totalActivityPAndL = null;

  @JsonProperty("totalActivityProfit")
  private Double totalActivityProfit = null;

  @JsonProperty("totalAmount")
  private Double totalAmount = null;

  @JsonProperty("tradeType")
  private String tradeType = null;

  @JsonProperty("venue")
  private String venue = null;

  public TradeByVenueDTO avgRate(Double avgRate) {
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

  public TradeByVenueDTO loanStatus(String loanStatus) {
    this.loanStatus = loanStatus;
    return this;
  }

   /**
   * Get loanStatus
   * @return loanStatus
  **/
  @ApiModelProperty(value = "")
  public String getLoanStatus() {
    return loanStatus;
  }

  public void setLoanStatus(String loanStatus) {
    this.loanStatus = loanStatus;
  }

  public TradeByVenueDTO totalActivityLoss(Double totalActivityLoss) {
    this.totalActivityLoss = totalActivityLoss;
    return this;
  }

   /**
   * Get totalActivityLoss
   * @return totalActivityLoss
  **/
  @ApiModelProperty(value = "")
  public Double getTotalActivityLoss() {
    return totalActivityLoss;
  }

  public void setTotalActivityLoss(Double totalActivityLoss) {
    this.totalActivityLoss = totalActivityLoss;
  }

  public TradeByVenueDTO totalActivityPAndL(Double totalActivityPAndL) {
    this.totalActivityPAndL = totalActivityPAndL;
    return this;
  }

   /**
   * Get totalActivityPAndL
   * @return totalActivityPAndL
  **/
  @ApiModelProperty(value = "")
  public Double getTotalActivityPAndL() {
    return totalActivityPAndL;
  }

  public void setTotalActivityPAndL(Double totalActivityPAndL) {
    this.totalActivityPAndL = totalActivityPAndL;
  }

  public TradeByVenueDTO totalActivityProfit(Double totalActivityProfit) {
    this.totalActivityProfit = totalActivityProfit;
    return this;
  }

   /**
   * Get totalActivityProfit
   * @return totalActivityProfit
  **/
  @ApiModelProperty(value = "")
  public Double getTotalActivityProfit() {
    return totalActivityProfit;
  }

  public void setTotalActivityProfit(Double totalActivityProfit) {
    this.totalActivityProfit = totalActivityProfit;
  }

  public TradeByVenueDTO totalAmount(Double totalAmount) {
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

  public TradeByVenueDTO tradeType(String tradeType) {
    this.tradeType = tradeType;
    return this;
  }

   /**
   * Get tradeType
   * @return tradeType
  **/
  @ApiModelProperty(value = "")
  public String getTradeType() {
    return tradeType;
  }

  public void setTradeType(String tradeType) {
    this.tradeType = tradeType;
  }

  public TradeByVenueDTO venue(String venue) {
    this.venue = venue;
    return this;
  }

   /**
   * Get venue
   * @return venue
  **/
  @ApiModelProperty(value = "")
  public String getVenue() {
    return venue;
  }

  public void setVenue(String venue) {
    this.venue = venue;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TradeByVenueDTO tradeByVenueDTO = (TradeByVenueDTO) o;
    return Objects.equals(this.avgRate, tradeByVenueDTO.avgRate) &&
        Objects.equals(this.loanStatus, tradeByVenueDTO.loanStatus) &&
        Objects.equals(this.totalActivityLoss, tradeByVenueDTO.totalActivityLoss) &&
        Objects.equals(this.totalActivityPAndL, tradeByVenueDTO.totalActivityPAndL) &&
        Objects.equals(this.totalActivityProfit, tradeByVenueDTO.totalActivityProfit) &&
        Objects.equals(this.totalAmount, tradeByVenueDTO.totalAmount) &&
        Objects.equals(this.tradeType, tradeByVenueDTO.tradeType) &&
        Objects.equals(this.venue, tradeByVenueDTO.venue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(avgRate, loanStatus, totalActivityLoss, totalActivityPAndL, totalActivityProfit, totalAmount, tradeType, venue);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TradeByVenueDTO {\n");
    
    sb.append("    avgRate: ").append(toIndentedString(avgRate)).append("\n");
    sb.append("    loanStatus: ").append(toIndentedString(loanStatus)).append("\n");
    sb.append("    totalActivityLoss: ").append(toIndentedString(totalActivityLoss)).append("\n");
    sb.append("    totalActivityPAndL: ").append(toIndentedString(totalActivityPAndL)).append("\n");
    sb.append("    totalActivityProfit: ").append(toIndentedString(totalActivityProfit)).append("\n");
    sb.append("    totalAmount: ").append(toIndentedString(totalAmount)).append("\n");
    sb.append("    tradeType: ").append(toIndentedString(tradeType)).append("\n");
    sb.append("    venue: ").append(toIndentedString(venue)).append("\n");
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

