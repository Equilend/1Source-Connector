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
import com.intellecteu.onesource.integration.services.client.spire.dto.BalanceByCptyAndSecurityKeyDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BalanceByCptyAndSecurityDTO
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-02-20T15:00:33.025Z")
public class BalanceByCptyAndSecurityDTO {
  @JsonProperty("balanceByPositionType")
  private Map<String, Double> balanceByPositionType = null;

  @JsonProperty("keyDTO")
  private BalanceByCptyAndSecurityKeyDTO keyDTO = null;

  public BalanceByCptyAndSecurityDTO balanceByPositionType(Map<String, Double> balanceByPositionType) {
    this.balanceByPositionType = balanceByPositionType;
    return this;
  }

  public BalanceByCptyAndSecurityDTO putBalanceByPositionTypeItem(String key, Double balanceByPositionTypeItem) {
    if (this.balanceByPositionType == null) {
      this.balanceByPositionType = new HashMap<>();
    }
    this.balanceByPositionType.put(key, balanceByPositionTypeItem);
    return this;
  }

   /**
   * Get balanceByPositionType
   * @return balanceByPositionType
  **/
  @ApiModelProperty(value = "")
  public Map<String, Double> getBalanceByPositionType() {
    return balanceByPositionType;
  }

  public void setBalanceByPositionType(Map<String, Double> balanceByPositionType) {
    this.balanceByPositionType = balanceByPositionType;
  }

  public BalanceByCptyAndSecurityDTO keyDTO(BalanceByCptyAndSecurityKeyDTO keyDTO) {
    this.keyDTO = keyDTO;
    return this;
  }

   /**
   * Get keyDTO
   * @return keyDTO
  **/
  @ApiModelProperty(value = "")
  public BalanceByCptyAndSecurityKeyDTO getKeyDTO() {
    return keyDTO;
  }

  public void setKeyDTO(BalanceByCptyAndSecurityKeyDTO keyDTO) {
    this.keyDTO = keyDTO;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BalanceByCptyAndSecurityDTO balanceByCptyAndSecurityDTO = (BalanceByCptyAndSecurityDTO) o;
    return Objects.equals(this.balanceByPositionType, balanceByCptyAndSecurityDTO.balanceByPositionType) &&
        Objects.equals(this.keyDTO, balanceByCptyAndSecurityDTO.keyDTO);
  }

  @Override
  public int hashCode() {
    return Objects.hash(balanceByPositionType, keyDTO);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BalanceByCptyAndSecurityDTO {\n");
    
    sb.append("    balanceByPositionType: ").append(toIndentedString(balanceByPositionType)).append("\n");
    sb.append("    keyDTO: ").append(toIndentedString(keyDTO)).append("\n");
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

