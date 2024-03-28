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
 * SettlementInstructionDTO
 */



public class SettlementInstructionDTO implements OneOfSettlementInstructionDTO {
  @JsonProperty("settlementBic")
  private String settlementBic = null;

  @JsonProperty("localAgentBic")
  private String localAgentBic = null;

  @JsonProperty("localAgentName")
  private String localAgentName = null;

  @JsonProperty("localAgentAcct")
  private String localAgentAcct = null;

  @JsonProperty("dtcParticipantNumber")
  private String dtcParticipantNumber = null;

  @JsonProperty("cdsCustomerUnitId")
  private String cdsCustomerUnitId = null;

  @JsonProperty("custodianName")
  private String custodianName = null;

  @JsonProperty("custodianBic")
  private String custodianBic = null;

  @JsonProperty("custodianAcct")
  private String custodianAcct = null;

  public SettlementInstructionDTO settlementBic(String settlementBic) {
    this.settlementBic = settlementBic;
    return this;
  }

   /**
   * Business Identifier Code (BIC) used to identify Place of Settlement (PSET)
   * @return settlementBic
  **/
  @Schema(required = true, description = "Business Identifier Code (BIC) used to identify Place of Settlement (PSET)")
  public String getSettlementBic() {
    return settlementBic;
  }

  public void setSettlementBic(String settlementBic) {
    this.settlementBic = settlementBic;
  }

  public SettlementInstructionDTO localAgentBic(String localAgentBic) {
    this.localAgentBic = localAgentBic;
    return this;
  }

   /**
   * BIC used to identify local agent that will interact with PSET
   * @return localAgentBic
  **/
  @Schema(description = "BIC used to identify local agent that will interact with PSET")
  public String getLocalAgentBic() {
    return localAgentBic;
  }

  public void setLocalAgentBic(String localAgentBic) {
    this.localAgentBic = localAgentBic;
  }

  public SettlementInstructionDTO localAgentName(String localAgentName) {
    this.localAgentName = localAgentName;
    return this;
  }

   /**
   * Name of local agent that will interact with PSET
   * @return localAgentName
  **/
  @Schema(description = "Name of local agent that will interact with PSET")
  public String getLocalAgentName() {
    return localAgentName;
  }

  public void setLocalAgentName(String localAgentName) {
    this.localAgentName = localAgentName;
  }

  public SettlementInstructionDTO localAgentAcct(String localAgentAcct) {
    this.localAgentAcct = localAgentAcct;
    return this;
  }

   /**
   * Account within local agent that will interact with PSET
   * @return localAgentAcct
  **/
  @Schema(required = true, description = "Account within local agent that will interact with PSET")
  public String getLocalAgentAcct() {
    return localAgentAcct;
  }

  public void setLocalAgentAcct(String localAgentAcct) {
    this.localAgentAcct = localAgentAcct;
  }

  public SettlementInstructionDTO dtcParticipantNumber(String dtcParticipantNumber) {
    this.dtcParticipantNumber = dtcParticipantNumber;
    return this;
  }

   /**
   * Get dtcParticipantNumber
   * @return dtcParticipantNumber
  **/
  @Schema(description = "")
  public String getDtcParticipantNumber() {
    return dtcParticipantNumber;
  }

  public void setDtcParticipantNumber(String dtcParticipantNumber) {
    this.dtcParticipantNumber = dtcParticipantNumber;
  }

  public SettlementInstructionDTO cdsCustomerUnitId(String cdsCustomerUnitId) {
    this.cdsCustomerUnitId = cdsCustomerUnitId;
    return this;
  }

   /**
   * Get cdsCustomerUnitId
   * @return cdsCustomerUnitId
  **/
  @Schema(description = "")
  public String getCdsCustomerUnitId() {
    return cdsCustomerUnitId;
  }

  public void setCdsCustomerUnitId(String cdsCustomerUnitId) {
    this.cdsCustomerUnitId = cdsCustomerUnitId;
  }

  public SettlementInstructionDTO custodianName(String custodianName) {
    this.custodianName = custodianName;
    return this;
  }

   /**
   * Custodian Bank Name
   * @return custodianName
  **/
  @Schema(description = "Custodian Bank Name")
  public String getCustodianName() {
    return custodianName;
  }

  public void setCustodianName(String custodianName) {
    this.custodianName = custodianName;
  }

  public SettlementInstructionDTO custodianBic(String custodianBic) {
    this.custodianBic = custodianBic;
    return this;
  }

   /**
   * Custodian Bank BIC
   * @return custodianBic
  **/
  @Schema(description = "Custodian Bank BIC")
  public String getCustodianBic() {
    return custodianBic;
  }

  public void setCustodianBic(String custodianBic) {
    this.custodianBic = custodianBic;
  }

  public SettlementInstructionDTO custodianAcct(String custodianAcct) {
    this.custodianAcct = custodianAcct;
    return this;
  }

   /**
   * Custodian Bank Account
   * @return custodianAcct
  **/
  @Schema(description = "Custodian Bank Account")
  public String getCustodianAcct() {
    return custodianAcct;
  }

  public void setCustodianAcct(String custodianAcct) {
    this.custodianAcct = custodianAcct;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SettlementInstructionDTO settlementInstruction = (SettlementInstructionDTO) o;
    return Objects.equals(this.settlementBic, settlementInstruction.settlementBic) &&
        Objects.equals(this.localAgentBic, settlementInstruction.localAgentBic) &&
        Objects.equals(this.localAgentName, settlementInstruction.localAgentName) &&
        Objects.equals(this.localAgentAcct, settlementInstruction.localAgentAcct) &&
        Objects.equals(this.dtcParticipantNumber, settlementInstruction.dtcParticipantNumber) &&
        Objects.equals(this.cdsCustomerUnitId, settlementInstruction.cdsCustomerUnitId) &&
        Objects.equals(this.custodianName, settlementInstruction.custodianName) &&
        Objects.equals(this.custodianBic, settlementInstruction.custodianBic) &&
        Objects.equals(this.custodianAcct, settlementInstruction.custodianAcct);
  }

  @Override
  public int hashCode() {
    return Objects.hash(settlementBic, localAgentBic, localAgentName, localAgentAcct, dtcParticipantNumber, cdsCustomerUnitId, custodianName, custodianBic, custodianAcct);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SettlementInstructionDTO {\n");
    
    sb.append("    settlementBic: ").append(toIndentedString(settlementBic)).append("\n");
    sb.append("    localAgentBic: ").append(toIndentedString(localAgentBic)).append("\n");
    sb.append("    localAgentName: ").append(toIndentedString(localAgentName)).append("\n");
    sb.append("    localAgentAcct: ").append(toIndentedString(localAgentAcct)).append("\n");
    sb.append("    dtcParticipantNumber: ").append(toIndentedString(dtcParticipantNumber)).append("\n");
    sb.append("    cdsCustomerUnitId: ").append(toIndentedString(cdsCustomerUnitId)).append("\n");
    sb.append("    custodianName: ").append(toIndentedString(custodianName)).append("\n");
    sb.append("    custodianBic: ").append(toIndentedString(custodianBic)).append("\n");
    sb.append("    custodianAcct: ").append(toIndentedString(custodianAcct)).append("\n");
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
