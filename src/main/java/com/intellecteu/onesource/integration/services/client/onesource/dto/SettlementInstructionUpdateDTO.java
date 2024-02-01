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
 * SettlementInstructionUpdateDTO
 */

@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2024-01-22T10:16:28.187392600Z[Europe/London]")

public class SettlementInstructionUpdateDTO implements ContractsContractIdBodyDTO {
  @JsonProperty("settlement")
  private PartySettlementInstructionDTO settlement = null;

  public SettlementInstructionUpdateDTO settlement(PartySettlementInstructionDTO settlement) {
    this.settlement = settlement;
    return this;
  }

   /**
   * Get settlement
   * @return settlement
  **/
  @Schema(required = true, description = "")
  public PartySettlementInstructionDTO getSettlement() {
    return settlement;
  }

  public void setSettlement(PartySettlementInstructionDTO settlement) {
    this.settlement = settlement;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SettlementInstructionUpdateDTO settlementInstructionUpdate = (SettlementInstructionUpdateDTO) o;
    return Objects.equals(this.settlement, settlementInstructionUpdate.settlement);
  }

  @Override
  public int hashCode() {
    return Objects.hash(settlement);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SettlementInstructionUpdateDTO {\n");
    
    sb.append("    settlement: ").append(toIndentedString(settlement)).append("\n");
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
