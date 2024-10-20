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
import java.util.Objects;
/**
 * CollateralDTO
 */



public class CollateralDTO {
  @JsonProperty("contractPrice")
  private Double contractPrice = null;

  @JsonProperty("contractValue")
  private Double contractValue = null;

  @JsonProperty("collateralValue")
  private Double collateralValue = null;

  @JsonProperty("currency")
  private CurrencyCdDTO currency = null;

  @JsonProperty("type")
  private CollateralTypeDTO type = null;

  @JsonProperty("descriptionCd")
  private CollateralDescriptionDTO descriptionCd = null;

  @JsonProperty("margin")
  private Double margin = null;

  @JsonProperty("roundingRule")
  private Integer roundingRule = null;

  @JsonProperty("roundingMode")
  private RoundingModeDTO roundingMode = null;

  public CollateralDTO contractPrice(Double contractPrice) {
    this.contractPrice = contractPrice;
    return this;
  }

   /**
   * Get contractPrice
   * @return contractPrice
  **/
  @Schema(description = "")
  public Double getContractPrice() {
    return contractPrice;
  }

  public void setContractPrice(Double contractPrice) {
    this.contractPrice = contractPrice;
  }

  public CollateralDTO contractValue(Double contractValue) {
    this.contractValue = contractValue;
    return this;
  }

   /**
   * Get contractValue
   * @return contractValue
  **/
  @Schema(description = "")
  public Double getContractValue() {
    return contractValue;
  }

  public void setContractValue(Double contractValue) {
    this.contractValue = contractValue;
  }

  public CollateralDTO collateralValue(Double collateralValue) {
    this.collateralValue = collateralValue;
    return this;
  }

   /**
   * Get collateralValue
   * @return collateralValue
  **/
  @Schema(required = true, description = "")
  public Double getCollateralValue() {
    return collateralValue;
  }

  public void setCollateralValue(Double collateralValue) {
    this.collateralValue = collateralValue;
  }

  public CollateralDTO currency(CurrencyCdDTO currency) {
    this.currency = currency;
    return this;
  }

   /**
   * Get currency
   * @return currency
  **/
  @Schema(required = true, description = "")
  public CurrencyCdDTO getCurrency() {
    return currency;
  }

  public void setCurrency(CurrencyCdDTO currency) {
    this.currency = currency;
  }

  public CollateralDTO type(CollateralTypeDTO type) {
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  @Schema(required = true, description = "")
  public CollateralTypeDTO getType() {
    return type;
  }

  public void setType(CollateralTypeDTO type) {
    this.type = type;
  }

  public CollateralDTO descriptionCd(CollateralDescriptionDTO descriptionCd) {
    this.descriptionCd = descriptionCd;
    return this;
  }

   /**
   * Get descriptionCd
   * @return descriptionCd
  **/
  @Schema(description = "")
  public CollateralDescriptionDTO getDescriptionCd() {
    return descriptionCd;
  }

  public void setDescriptionCd(CollateralDescriptionDTO descriptionCd) {
    this.descriptionCd = descriptionCd;
  }

  public CollateralDTO margin(Double margin) {
    this.margin = margin;
    return this;
  }

   /**
   * Get margin
   * @return margin
  **/
  @Schema(description = "")
  public Double getMargin() {
    return margin;
  }

  public void setMargin(Double margin) {
    this.margin = margin;
  }

  public CollateralDTO roundingRule(Integer roundingRule) {
    this.roundingRule = roundingRule;
    return this;
  }

   /**
   * Get roundingRule
   * @return roundingRule
  **/
  @Schema(description = "")
  public Integer getRoundingRule() {
    return roundingRule;
  }

  public void setRoundingRule(Integer roundingRule) {
    this.roundingRule = roundingRule;
  }

  public CollateralDTO roundingMode(RoundingModeDTO roundingMode) {
    this.roundingMode = roundingMode;
    return this;
  }

   /**
   * Get roundingMode
   * @return roundingMode
  **/
  @Schema(description = "")
  public RoundingModeDTO getRoundingMode() {
    return roundingMode;
  }

  public void setRoundingMode(RoundingModeDTO roundingMode) {
    this.roundingMode = roundingMode;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CollateralDTO collateral = (CollateralDTO) o;
    return Objects.equals(this.contractPrice, collateral.contractPrice) &&
        Objects.equals(this.contractValue, collateral.contractValue) &&
        Objects.equals(this.collateralValue, collateral.collateralValue) &&
        Objects.equals(this.currency, collateral.currency) &&
        Objects.equals(this.type, collateral.type) &&
        Objects.equals(this.descriptionCd, collateral.descriptionCd) &&
        Objects.equals(this.margin, collateral.margin) &&
        Objects.equals(this.roundingRule, collateral.roundingRule) &&
        Objects.equals(this.roundingMode, collateral.roundingMode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contractPrice, contractValue, collateralValue, currency, type, descriptionCd, margin, roundingRule, roundingMode);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CollateralDTO {\n");
    
    sb.append("    contractPrice: ").append(toIndentedString(contractPrice)).append("\n");
    sb.append("    contractValue: ").append(toIndentedString(contractValue)).append("\n");
    sb.append("    collateralValue: ").append(toIndentedString(collateralValue)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    descriptionCd: ").append(toIndentedString(descriptionCd)).append("\n");
    sb.append("    margin: ").append(toIndentedString(margin)).append("\n");
    sb.append("    roundingRule: ").append(toIndentedString(roundingRule)).append("\n");
    sb.append("    roundingMode: ").append(toIndentedString(roundingMode)).append("\n");
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
