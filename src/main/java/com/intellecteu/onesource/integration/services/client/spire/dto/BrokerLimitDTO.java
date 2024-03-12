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

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * BrokerLimitDTO
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-02-20T15:00:33.025Z")
public class BrokerLimitDTO {
  @JsonProperty("counterpartyBroker")
  private String counterpartyBroker = null;

  @JsonProperty("lendingLimit")
  private Double lendingLimit = null;

  @JsonProperty("limitUsed")
  private Double limitUsed = null;

  public BrokerLimitDTO counterpartyBroker(String counterpartyBroker) {
    this.counterpartyBroker = counterpartyBroker;
    return this;
  }

   /**
   * Get counterpartyBroker
   * @return counterpartyBroker
  **/
  @ApiModelProperty(value = "")
  public String getCounterpartyBroker() {
    return counterpartyBroker;
  }

  public void setCounterpartyBroker(String counterpartyBroker) {
    this.counterpartyBroker = counterpartyBroker;
  }

  public BrokerLimitDTO lendingLimit(Double lendingLimit) {
    this.lendingLimit = lendingLimit;
    return this;
  }

   /**
   * Get lendingLimit
   * @return lendingLimit
  **/
  @ApiModelProperty(value = "")
  public Double getLendingLimit() {
    return lendingLimit;
  }

  public void setLendingLimit(Double lendingLimit) {
    this.lendingLimit = lendingLimit;
  }

  public BrokerLimitDTO limitUsed(Double limitUsed) {
    this.limitUsed = limitUsed;
    return this;
  }

   /**
   * Get limitUsed
   * @return limitUsed
  **/
  @ApiModelProperty(value = "")
  public Double getLimitUsed() {
    return limitUsed;
  }

  public void setLimitUsed(Double limitUsed) {
    this.limitUsed = limitUsed;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BrokerLimitDTO brokerLimitDTO = (BrokerLimitDTO) o;
    return Objects.equals(this.counterpartyBroker, brokerLimitDTO.counterpartyBroker) &&
        Objects.equals(this.lendingLimit, brokerLimitDTO.lendingLimit) &&
        Objects.equals(this.limitUsed, brokerLimitDTO.limitUsed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(counterpartyBroker, lendingLimit, limitUsed);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BrokerLimitDTO {\n");
    
    sb.append("    counterpartyBroker: ").append(toIndentedString(counterpartyBroker)).append("\n");
    sb.append("    lendingLimit: ").append(toIndentedString(lendingLimit)).append("\n");
    sb.append("    limitUsed: ").append(toIndentedString(limitUsed)).append("\n");
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

