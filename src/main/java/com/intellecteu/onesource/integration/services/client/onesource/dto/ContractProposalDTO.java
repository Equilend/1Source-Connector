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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * ContractProposalDTO
 */



public class ContractProposalDTO {
  @JsonProperty("trade")
  private TradeAgreementDTO trade = null;

  @JsonProperty("settlement")
  private List<PartySettlementInstructionDTO> settlement = null;

  public ContractProposalDTO trade(TradeAgreementDTO trade) {
    this.trade = trade;
    return this;
  }

   /**
   * Get trade
   * @return trade
  **/
  @Schema(description = "")
  public TradeAgreementDTO getTrade() {
    return trade;
  }

  public void setTrade(TradeAgreementDTO trade) {
    this.trade = trade;
  }

  public ContractProposalDTO settlement(List<PartySettlementInstructionDTO> settlement) {
    this.settlement = settlement;
    return this;
  }

  public ContractProposalDTO addSettlementItem(PartySettlementInstructionDTO settlementItem) {
    if (this.settlement == null) {
      this.settlement = new ArrayList<>();
    }
    this.settlement.add(settlementItem);
    return this;
  }

   /**
   * Get settlement
   * @return settlement
  **/
  @Schema(description = "")
  public List<PartySettlementInstructionDTO> getSettlement() {
    return settlement;
  }

  public void setSettlement(List<PartySettlementInstructionDTO> settlement) {
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
    ContractProposalDTO contractProposal = (ContractProposalDTO) o;
    return Objects.equals(this.trade, contractProposal.trade) &&
        Objects.equals(this.settlement, contractProposal.settlement);
  }

  @Override
  public int hashCode() {
    return Objects.hash(trade, settlement);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContractProposalDTO {\n");
    
    sb.append("    trade: ").append(toIndentedString(trade)).append("\n");
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
