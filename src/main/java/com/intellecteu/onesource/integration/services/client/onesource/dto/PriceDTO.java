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
 * Price is ignored for trade agreements and contract proposals
 */
@Schema(description = "Price is ignored for trade agreements and contract proposals")


public class PriceDTO {
  @JsonProperty("value")
  private Double value = null;

  @JsonProperty("currency")
  private CurrencyCdDTO currency = null;

  @JsonProperty("unit")
  private PriceUnitDTO unit = null;

  @JsonProperty("valueDate")
  private LocalDate valueDate = null;

  public PriceDTO value(Double value) {
    this.value = value;
    return this;
  }

   /**
   * Get value
   * @return value
  **/
  @Schema(description = "")
  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public PriceDTO currency(CurrencyCdDTO currency) {
    this.currency = currency;
    return this;
  }

   /**
   * Get currency
   * @return currency
  **/
  @Schema(description = "")
  public CurrencyCdDTO getCurrency() {
    return currency;
  }

  public void setCurrency(CurrencyCdDTO currency) {
    this.currency = currency;
  }

  public PriceDTO unit(PriceUnitDTO unit) {
    this.unit = unit;
    return this;
  }

   /**
   * Get unit
   * @return unit
  **/
  @Schema(description = "")
  public PriceUnitDTO getUnit() {
    return unit;
  }

  public void setUnit(PriceUnitDTO unit) {
    this.unit = unit;
  }

  public PriceDTO valueDate(LocalDate valueDate) {
    this.valueDate = valueDate;
    return this;
  }

   /**
   * Get valueDate
   * @return valueDate
  **/
  @Schema(description = "")
  public LocalDate getValueDate() {
    return valueDate;
  }

  public void setValueDate(LocalDate valueDate) {
    this.valueDate = valueDate;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PriceDTO price = (PriceDTO) o;
    return Objects.equals(this.value, price.value) &&
        Objects.equals(this.currency, price.currency) &&
        Objects.equals(this.unit, price.unit) &&
        Objects.equals(this.valueDate, price.valueDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, currency, unit, valueDate);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PriceDTO {\n");
    
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    unit: ").append(toIndentedString(unit)).append("\n");
    sb.append("    valueDate: ").append(toIndentedString(valueDate)).append("\n");
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
