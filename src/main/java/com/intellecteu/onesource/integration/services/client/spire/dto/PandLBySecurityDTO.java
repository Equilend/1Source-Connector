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
 * PandLBySecurityDTO
 */

public class PandLBySecurityDTO {
  @JsonProperty("netPos")
  private Long netPos = null;

  @JsonProperty("pnL")
  private Long pnL = null;

  @JsonProperty("premiumPayment")
  private Long premiumPayment = null;

  @JsonProperty("rebateReceive")
  private Long rebateReceive = null;

  @JsonProperty("recalled")
  private Long recalled = null;

  @JsonProperty("returns")
  private Long returns = null;

  @JsonProperty("securityId")
  private Long securityId = null;

  @JsonProperty("totalBorrows")
  private Long totalBorrows = null;

  @JsonProperty("totalLoans")
  private Long totalLoans = null;

  @JsonProperty("waB")
  private Long waB = null;

  @JsonProperty("waL")
  private Long waL = null;

  public PandLBySecurityDTO netPos(Long netPos) {
    this.netPos = netPos;
    return this;
  }

   /**
   * Get netPos
   * @return netPos
  **/
  @ApiModelProperty(value = "")
  public Long getNetPos() {
    return netPos;
  }

  public void setNetPos(Long netPos) {
    this.netPos = netPos;
  }

  public PandLBySecurityDTO pnL(Long pnL) {
    this.pnL = pnL;
    return this;
  }

   /**
   * Get pnL
   * @return pnL
  **/
  @ApiModelProperty(value = "")
  public Long getPnL() {
    return pnL;
  }

  public void setPnL(Long pnL) {
    this.pnL = pnL;
  }

  public PandLBySecurityDTO premiumPayment(Long premiumPayment) {
    this.premiumPayment = premiumPayment;
    return this;
  }

   /**
   * Get premiumPayment
   * @return premiumPayment
  **/
  @ApiModelProperty(value = "")
  public Long getPremiumPayment() {
    return premiumPayment;
  }

  public void setPremiumPayment(Long premiumPayment) {
    this.premiumPayment = premiumPayment;
  }

  public PandLBySecurityDTO rebateReceive(Long rebateReceive) {
    this.rebateReceive = rebateReceive;
    return this;
  }

   /**
   * Get rebateReceive
   * @return rebateReceive
  **/
  @ApiModelProperty(value = "")
  public Long getRebateReceive() {
    return rebateReceive;
  }

  public void setRebateReceive(Long rebateReceive) {
    this.rebateReceive = rebateReceive;
  }

  public PandLBySecurityDTO recalled(Long recalled) {
    this.recalled = recalled;
    return this;
  }

   /**
   * Get recalled
   * @return recalled
  **/
  @ApiModelProperty(value = "")
  public Long getRecalled() {
    return recalled;
  }

  public void setRecalled(Long recalled) {
    this.recalled = recalled;
  }

  public PandLBySecurityDTO returns(Long returns) {
    this.returns = returns;
    return this;
  }

   /**
   * Get returns
   * @return returns
  **/
  @ApiModelProperty(value = "")
  public Long getReturns() {
    return returns;
  }

  public void setReturns(Long returns) {
    this.returns = returns;
  }

  public PandLBySecurityDTO securityId(Long securityId) {
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

  public PandLBySecurityDTO totalBorrows(Long totalBorrows) {
    this.totalBorrows = totalBorrows;
    return this;
  }

   /**
   * Get totalBorrows
   * @return totalBorrows
  **/
  @ApiModelProperty(value = "")
  public Long getTotalBorrows() {
    return totalBorrows;
  }

  public void setTotalBorrows(Long totalBorrows) {
    this.totalBorrows = totalBorrows;
  }

  public PandLBySecurityDTO totalLoans(Long totalLoans) {
    this.totalLoans = totalLoans;
    return this;
  }

   /**
   * Get totalLoans
   * @return totalLoans
  **/
  @ApiModelProperty(value = "")
  public Long getTotalLoans() {
    return totalLoans;
  }

  public void setTotalLoans(Long totalLoans) {
    this.totalLoans = totalLoans;
  }

  public PandLBySecurityDTO waB(Long waB) {
    this.waB = waB;
    return this;
  }

   /**
   * Get waB
   * @return waB
  **/
  @ApiModelProperty(value = "")
  public Long getWaB() {
    return waB;
  }

  public void setWaB(Long waB) {
    this.waB = waB;
  }

  public PandLBySecurityDTO waL(Long waL) {
    this.waL = waL;
    return this;
  }

   /**
   * Get waL
   * @return waL
  **/
  @ApiModelProperty(value = "")
  public Long getWaL() {
    return waL;
  }

  public void setWaL(Long waL) {
    this.waL = waL;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PandLBySecurityDTO pandLBySecurityDTO = (PandLBySecurityDTO) o;
    return Objects.equals(this.netPos, pandLBySecurityDTO.netPos) &&
        Objects.equals(this.pnL, pandLBySecurityDTO.pnL) &&
        Objects.equals(this.premiumPayment, pandLBySecurityDTO.premiumPayment) &&
        Objects.equals(this.rebateReceive, pandLBySecurityDTO.rebateReceive) &&
        Objects.equals(this.recalled, pandLBySecurityDTO.recalled) &&
        Objects.equals(this.returns, pandLBySecurityDTO.returns) &&
        Objects.equals(this.securityId, pandLBySecurityDTO.securityId) &&
        Objects.equals(this.totalBorrows, pandLBySecurityDTO.totalBorrows) &&
        Objects.equals(this.totalLoans, pandLBySecurityDTO.totalLoans) &&
        Objects.equals(this.waB, pandLBySecurityDTO.waB) &&
        Objects.equals(this.waL, pandLBySecurityDTO.waL);
  }

  @Override
  public int hashCode() {
    return Objects.hash(netPos, pnL, premiumPayment, rebateReceive, recalled, returns, securityId, totalBorrows, totalLoans, waB, waL);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PandLBySecurityDTO {\n");
    
    sb.append("    netPos: ").append(toIndentedString(netPos)).append("\n");
    sb.append("    pnL: ").append(toIndentedString(pnL)).append("\n");
    sb.append("    premiumPayment: ").append(toIndentedString(premiumPayment)).append("\n");
    sb.append("    rebateReceive: ").append(toIndentedString(rebateReceive)).append("\n");
    sb.append("    recalled: ").append(toIndentedString(recalled)).append("\n");
    sb.append("    returns: ").append(toIndentedString(returns)).append("\n");
    sb.append("    securityId: ").append(toIndentedString(securityId)).append("\n");
    sb.append("    totalBorrows: ").append(toIndentedString(totalBorrows)).append("\n");
    sb.append("    totalLoans: ").append(toIndentedString(totalLoans)).append("\n");
    sb.append("    waB: ").append(toIndentedString(waB)).append("\n");
    sb.append("    waL: ").append(toIndentedString(waL)).append("\n");
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

