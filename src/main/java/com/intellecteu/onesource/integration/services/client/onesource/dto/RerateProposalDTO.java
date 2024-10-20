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
 * RerateProposalDTO
 */



public class RerateProposalDTO {
  @JsonProperty("executionVenue")
  private VenueDTO executionVenue = null;

  @JsonProperty("rate")
  private OneOfRerateProposalRateDTODTO rate = null;

  public RerateProposalDTO executionVenue(VenueDTO executionVenue) {
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

  public RerateProposalDTO rate(OneOfRerateProposalRateDTODTO rate) {
    this.rate = rate;
    return this;
  }

   /**
   * Get rate
   * @return rate
  **/
  @Schema(required = true, description = "")
  public OneOfRerateProposalRateDTODTO getRate() {
    return rate;
  }

  public void setRate(OneOfRerateProposalRateDTODTO rate) {
    this.rate = rate;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RerateProposalDTO rerateProposal = (RerateProposalDTO) o;
    return Objects.equals(this.executionVenue, rerateProposal.executionVenue) &&
        Objects.equals(this.rate, rerateProposal.rate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(executionVenue, rate);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RerateProposalDTO {\n");
    
    sb.append("    executionVenue: ").append(toIndentedString(executionVenue)).append("\n");
    sb.append("    rate: ").append(toIndentedString(rate)).append("\n");
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
