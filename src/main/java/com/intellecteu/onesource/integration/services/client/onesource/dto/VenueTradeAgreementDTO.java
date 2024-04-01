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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
/**
 * VenueTradeAgreementDTO
 */



public class VenueTradeAgreementDTO {
  @JsonProperty("executionVenue")
  private VenueDTO executionVenue = null;

  @JsonProperty("instrument")
  private InstrumentDTO instrument = null;

  @JsonProperty("rate")
  private OneOfVenueTradeAgreementRateDTODTO rate = null;

  @JsonProperty("quantity")
  private BigDecimal quantity = null;

  @JsonProperty("billingCurrency")
  private CurrencyCdDTO billingCurrency = null;

  @JsonProperty("dividendRatePct")
  private Double dividendRatePct = null;

  @JsonProperty("tradeDate")
  private LocalDate tradeDate = null;

  @JsonProperty("termType")
  private TermTypeDTO termType = null;

  @JsonProperty("termDate")
  private LocalDate termDate = null;

  @JsonProperty("settlementDate")
  private LocalDate settlementDate = null;

  @JsonProperty("settlementType")
  private SettlementTypeDTO settlementType = null;

  @JsonProperty("collateral")
  private CollateralDTO collateral = null;

  @JsonProperty("transactingParties")
  private TransactingPartiesDTO transactingParties = null;

  public VenueTradeAgreementDTO executionVenue(VenueDTO executionVenue) {
    this.executionVenue = executionVenue;
    return this;
  }

   /**
   * Get executionVenue
   * @return executionVenue
  **/
  @Schema(description = "")
  public VenueDTO getExecutionVenue() {
    return executionVenue;
  }

  public void setExecutionVenue(VenueDTO executionVenue) {
    this.executionVenue = executionVenue;
  }

  public VenueTradeAgreementDTO instrument(InstrumentDTO instrument) {
    this.instrument = instrument;
    return this;
  }

   /**
   * Get instrument
   * @return instrument
  **/
  @Schema(required = true, description = "")
  public InstrumentDTO getInstrument() {
    return instrument;
  }

  public void setInstrument(InstrumentDTO instrument) {
    this.instrument = instrument;
  }

  public VenueTradeAgreementDTO rate(OneOfVenueTradeAgreementRateDTODTO rate) {
    this.rate = rate;
    return this;
  }

   /**
   * Get rate
   * @return rate
  **/
  @Schema(required = true, description = "")
  public OneOfVenueTradeAgreementRateDTODTO getRate() {
    return rate;
  }

  public void setRate(OneOfVenueTradeAgreementRateDTODTO rate) {
    this.rate = rate;
  }

  public VenueTradeAgreementDTO quantity(BigDecimal quantity) {
    this.quantity = quantity;
    return this;
  }

   /**
   * Get quantity
   * @return quantity
  **/
  @Schema(required = true, description = "")
  public BigDecimal getQuantity() {
    return quantity;
  }

  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
  }

  public VenueTradeAgreementDTO billingCurrency(CurrencyCdDTO billingCurrency) {
    this.billingCurrency = billingCurrency;
    return this;
  }

   /**
   * Get billingCurrency
   * @return billingCurrency
  **/
  @Schema(description = "")
  public CurrencyCdDTO getBillingCurrency() {
    return billingCurrency;
  }

  public void setBillingCurrency(CurrencyCdDTO billingCurrency) {
    this.billingCurrency = billingCurrency;
  }

  public VenueTradeAgreementDTO dividendRatePct(Double dividendRatePct) {
    this.dividendRatePct = dividendRatePct;
    return this;
  }

   /**
   * Get dividendRatePct
   * @return dividendRatePct
  **/
  @Schema(description = "")
  public Double getDividendRatePct() {
    return dividendRatePct;
  }

  public void setDividendRatePct(Double dividendRatePct) {
    this.dividendRatePct = dividendRatePct;
  }

  public VenueTradeAgreementDTO tradeDate(LocalDate tradeDate) {
    this.tradeDate = tradeDate;
    return this;
  }

   /**
   * Get tradeDate
   * @return tradeDate
  **/
  @Schema(required = true, description = "")
  public LocalDate getTradeDate() {
    return tradeDate;
  }

  public void setTradeDate(LocalDate tradeDate) {
    this.tradeDate = tradeDate;
  }

  public VenueTradeAgreementDTO termType(TermTypeDTO termType) {
    this.termType = termType;
    return this;
  }

   /**
   * Get termType
   * @return termType
  **/
  @Schema(description = "")
  public TermTypeDTO getTermType() {
    return termType;
  }

  public void setTermType(TermTypeDTO termType) {
    this.termType = termType;
  }

  public VenueTradeAgreementDTO termDate(LocalDate termDate) {
    this.termDate = termDate;
    return this;
  }

   /**
   * Get termDate
   * @return termDate
  **/
  @Schema(description = "")
  public LocalDate getTermDate() {
    return termDate;
  }

  public void setTermDate(LocalDate termDate) {
    this.termDate = termDate;
  }

  public VenueTradeAgreementDTO settlementDate(LocalDate settlementDate) {
    this.settlementDate = settlementDate;
    return this;
  }

   /**
   * Get settlementDate
   * @return settlementDate
  **/
  @Schema(description = "")
  public LocalDate getSettlementDate() {
    return settlementDate;
  }

  public void setSettlementDate(LocalDate settlementDate) {
    this.settlementDate = settlementDate;
  }

  public VenueTradeAgreementDTO settlementType(SettlementTypeDTO settlementType) {
    this.settlementType = settlementType;
    return this;
  }

   /**
   * Get settlementType
   * @return settlementType
  **/
  @Schema(description = "")
  public SettlementTypeDTO getSettlementType() {
    return settlementType;
  }

  public void setSettlementType(SettlementTypeDTO settlementType) {
    this.settlementType = settlementType;
  }

  public VenueTradeAgreementDTO collateral(CollateralDTO collateral) {
    this.collateral = collateral;
    return this;
  }

   /**
   * Get collateral
   * @return collateral
  **/
  @Schema(required = true, description = "")
  public CollateralDTO getCollateral() {
    return collateral;
  }

  public void setCollateral(CollateralDTO collateral) {
    this.collateral = collateral;
  }

  public VenueTradeAgreementDTO transactingParties(TransactingPartiesDTO transactingParties) {
    this.transactingParties = transactingParties;
    return this;
  }

   /**
   * Get transactingParties
   * @return transactingParties
  **/
  @Schema(required = true, description = "")
  public TransactingPartiesDTO getTransactingParties() {
    return transactingParties;
  }

  public void setTransactingParties(TransactingPartiesDTO transactingParties) {
    this.transactingParties = transactingParties;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VenueTradeAgreementDTO venueTradeAgreement = (VenueTradeAgreementDTO) o;
    return Objects.equals(this.executionVenue, venueTradeAgreement.executionVenue) &&
        Objects.equals(this.instrument, venueTradeAgreement.instrument) &&
        Objects.equals(this.rate, venueTradeAgreement.rate) &&
        Objects.equals(this.quantity, venueTradeAgreement.quantity) &&
        Objects.equals(this.billingCurrency, venueTradeAgreement.billingCurrency) &&
        Objects.equals(this.dividendRatePct, venueTradeAgreement.dividendRatePct) &&
        Objects.equals(this.tradeDate, venueTradeAgreement.tradeDate) &&
        Objects.equals(this.termType, venueTradeAgreement.termType) &&
        Objects.equals(this.termDate, venueTradeAgreement.termDate) &&
        Objects.equals(this.settlementDate, venueTradeAgreement.settlementDate) &&
        Objects.equals(this.settlementType, venueTradeAgreement.settlementType) &&
        Objects.equals(this.collateral, venueTradeAgreement.collateral) &&
        Objects.equals(this.transactingParties, venueTradeAgreement.transactingParties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(executionVenue, instrument, rate, quantity, billingCurrency, dividendRatePct, tradeDate, termType, termDate, settlementDate, settlementType, collateral, transactingParties);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VenueTradeAgreementDTO {\n");
    
    sb.append("    executionVenue: ").append(toIndentedString(executionVenue)).append("\n");
    sb.append("    instrument: ").append(toIndentedString(instrument)).append("\n");
    sb.append("    rate: ").append(toIndentedString(rate)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    billingCurrency: ").append(toIndentedString(billingCurrency)).append("\n");
    sb.append("    dividendRatePct: ").append(toIndentedString(dividendRatePct)).append("\n");
    sb.append("    tradeDate: ").append(toIndentedString(tradeDate)).append("\n");
    sb.append("    termType: ").append(toIndentedString(termType)).append("\n");
    sb.append("    termDate: ").append(toIndentedString(termDate)).append("\n");
    sb.append("    settlementDate: ").append(toIndentedString(settlementDate)).append("\n");
    sb.append("    settlementType: ").append(toIndentedString(settlementType)).append("\n");
    sb.append("    collateral: ").append(toIndentedString(collateral)).append("\n");
    sb.append("    transactingParties: ").append(toIndentedString(transactingParties)).append("\n");
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
