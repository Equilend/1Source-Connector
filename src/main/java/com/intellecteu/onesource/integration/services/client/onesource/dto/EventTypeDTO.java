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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets EventType
 */
public enum EventTypeDTO {
  TRADE_AGREED("TRADE_AGREED"),
  TRADE_CANCELED("TRADE_CANCELED"),
  CONTRACT_PROPOSED("CONTRACT_PROPOSED"),
  CONTRACT_PENDING("CONTRACT_PENDING"),
  CONTRACT_CANCELED("CONTRACT_CANCELED"),
  CONTRACT_CANCEL_PENDING("CONTRACT_CANCEL_PENDING"),
  CONTRACT_CLOSED("CONTRACT_CLOSED"),
  CONTRACT_DECLINED("CONTRACT_DECLINED"),
  CONTRACT_OPENED("CONTRACT_OPENED"),
  CONTRACT_ALLOCATION("CONTRACT_ALLOCATION"),
  CONTRACT_SPLIT("CONTRACT_SPLIT"),
  SETTLEMENT_STATUS_UPDATE("SETTLEMENT_STATUS_UPDATE"),
  SETTLEMENT_INSTRUCTION_UPDATE("SETTLEMENT_INSTRUCTION_UPDATE"),
  RERATE_PROPOSED("RERATE_PROPOSED"),
  RERATE_PENDING("RERATE_PENDING"),
  RERATE_CANCELED("RERATE_CANCELED"),
  RERATE_CANCEL_PENDING("RERATE_CANCEL_PENDING"),
  RERATE_DECLINED("RERATE_DECLINED"),
  RERATE_APPLIED("RERATE_APPLIED"),
  RETURN_PENDING("RETURN_PENDING"),
  RETURN_SETTLED("RETURN_SETTLED"),
  RETURN_CANCELED("RETURN_CANCELED"),
  RECALL_OPEN("RECALL_OPEN"),
  RECALL_UPDATED("RECALL_UPDATED"),
  RECALL_CANCELED("RECALL_CANCELED"),
  RECALL_CLOSED("RECALL_CLOSED"),
  BUYIN_OPENED("BUYIN_OPENED"),
  BUYIN_CANCELED("BUYIN_CANCELED"),
  BUYIN_CLOSED("BUYIN_CLOSED"),
  BUYIN_COMPLETED("BUYIN_COMPLETED"),
  BUYIN_PENDING("BUYIN_PENDING"),
  BUYIN_UPDATED("BUYIN_UPDATED"),
  DELEGATION_PROPOSED("DELEGATION_PROPOSED"),
  DELEGATION_APPROVED("DELEGATION_APPROVED"),
  DELEGATION_CANCELED("DELEGATION_CANCELED");

  private String value;

  EventTypeDTO(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static EventTypeDTO fromValue(String input) {
    for (EventTypeDTO b : EventTypeDTO.values()) {
      if (b.value.equals(input)) {
        return b;
      }
    }
    return null;
  }
}
