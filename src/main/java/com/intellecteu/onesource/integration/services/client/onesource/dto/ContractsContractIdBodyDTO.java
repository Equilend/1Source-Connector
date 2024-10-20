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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
* ContractsContractIdBodyDTO
*/
@JsonTypeInfo(
    use = Id.DEDUCTION)
@JsonSubTypes({
  @JsonSubTypes.Type(value = PriceUpdateDTO.class, name = "PriceUpdateDTO"),
  @JsonSubTypes.Type(value = SettlementStatusUpdateDTO.class, name = "SettlementStatusUpdateDTO"),
  @JsonSubTypes.Type(value = SettlementInstructionUpdateDTO.class, name = "SettlementInstructionUpdateDTO"),
  @JsonSubTypes.Type(value = InternalReferenceUpdateDTO.class, name = "InternalReferenceUpdateDTO")
})
public interface ContractsContractIdBodyDTO {

}
