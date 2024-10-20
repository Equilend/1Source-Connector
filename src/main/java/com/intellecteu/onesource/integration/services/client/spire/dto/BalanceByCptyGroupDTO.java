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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * BalanceByCptyGroupDTO
 */

public class BalanceByCptyGroupDTO {
  @JsonProperty("balanceByPosType")
  private Map<String, Double> balanceByPosType = null;

  @JsonProperty("counterPartyGroupId")
  private Long counterPartyGroupId = null;

  @JsonProperty("counterPartyGroupIdDTO")
  private AccountDTO counterPartyGroupIdDTO = null;

  @JsonProperty("positionTypeDTOMap")
  private Map<String, PositiontypeDTO> positionTypeDTOMap = null;

  public BalanceByCptyGroupDTO balanceByPosType(Map<String, Double> balanceByPosType) {
    this.balanceByPosType = balanceByPosType;
    return this;
  }

  public BalanceByCptyGroupDTO putBalanceByPosTypeItem(String key, Double balanceByPosTypeItem) {
    if (this.balanceByPosType == null) {
      this.balanceByPosType = new HashMap<>();
    }
    this.balanceByPosType.put(key, balanceByPosTypeItem);
    return this;
  }

   /**
   * Get balanceByPosType
   * @return balanceByPosType
  **/
  @ApiModelProperty(value = "")
  public Map<String, Double> getBalanceByPosType() {
    return balanceByPosType;
  }

  public void setBalanceByPosType(Map<String, Double> balanceByPosType) {
    this.balanceByPosType = balanceByPosType;
  }

  public BalanceByCptyGroupDTO counterPartyGroupId(Long counterPartyGroupId) {
    this.counterPartyGroupId = counterPartyGroupId;
    return this;
  }

   /**
   * Get counterPartyGroupId
   * @return counterPartyGroupId
  **/
  @ApiModelProperty(value = "")
  public Long getCounterPartyGroupId() {
    return counterPartyGroupId;
  }

  public void setCounterPartyGroupId(Long counterPartyGroupId) {
    this.counterPartyGroupId = counterPartyGroupId;
  }

  public BalanceByCptyGroupDTO counterPartyGroupIdDTO(AccountDTO counterPartyGroupIdDTO) {
    this.counterPartyGroupIdDTO = counterPartyGroupIdDTO;
    return this;
  }

   /**
   * Get counterPartyGroupIdDTO
   * @return counterPartyGroupIdDTO
  **/
  @ApiModelProperty(value = "")
  public AccountDTO getCounterPartyGroupIdDTO() {
    return counterPartyGroupIdDTO;
  }

  public void setCounterPartyGroupIdDTO(AccountDTO counterPartyGroupIdDTO) {
    this.counterPartyGroupIdDTO = counterPartyGroupIdDTO;
  }

  public BalanceByCptyGroupDTO positionTypeDTOMap(Map<String, PositiontypeDTO> positionTypeDTOMap) {
    this.positionTypeDTOMap = positionTypeDTOMap;
    return this;
  }

  public BalanceByCptyGroupDTO putPositionTypeDTOMapItem(String key, PositiontypeDTO positionTypeDTOMapItem) {
    if (this.positionTypeDTOMap == null) {
      this.positionTypeDTOMap = new HashMap<>();
    }
    this.positionTypeDTOMap.put(key, positionTypeDTOMapItem);
    return this;
  }

   /**
   * Get positionTypeDTOMap
   * @return positionTypeDTOMap
  **/
  @ApiModelProperty(value = "")
  public Map<String, PositiontypeDTO> getPositionTypeDTOMap() {
    return positionTypeDTOMap;
  }

  public void setPositionTypeDTOMap(Map<String, PositiontypeDTO> positionTypeDTOMap) {
    this.positionTypeDTOMap = positionTypeDTOMap;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BalanceByCptyGroupDTO balanceByCptyGroupDTO = (BalanceByCptyGroupDTO) o;
    return Objects.equals(this.balanceByPosType, balanceByCptyGroupDTO.balanceByPosType) &&
        Objects.equals(this.counterPartyGroupId, balanceByCptyGroupDTO.counterPartyGroupId) &&
        Objects.equals(this.counterPartyGroupIdDTO, balanceByCptyGroupDTO.counterPartyGroupIdDTO) &&
        Objects.equals(this.positionTypeDTOMap, balanceByCptyGroupDTO.positionTypeDTOMap);
  }

  @Override
  public int hashCode() {
    return Objects.hash(balanceByPosType, counterPartyGroupId, counterPartyGroupIdDTO, positionTypeDTOMap);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BalanceByCptyGroupDTO {\n");
    
    sb.append("    balanceByPosType: ").append(toIndentedString(balanceByPosType)).append("\n");
    sb.append("    counterPartyGroupId: ").append(toIndentedString(counterPartyGroupId)).append("\n");
    sb.append("    counterPartyGroupIdDTO: ").append(toIndentedString(counterPartyGroupIdDTO)).append("\n");
    sb.append("    positionTypeDTOMap: ").append(toIndentedString(positionTypeDTOMap)).append("\n");
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

