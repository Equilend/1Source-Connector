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

import java.util.Objects;

/**
 * RecallAllocationDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-01-07T21:15:14.632Z")
public class RecallAllocationDTO {
  @JsonProperty("__qualifiedName")
  private String qualifiedName = null;

  @JsonProperty("positionId")
  private Long positionId = null;

  @JsonProperty("recallAllocationAccountName")
  private String recallAllocationAccountName = null;

  @JsonProperty("recallAllocationAccountNo")
  private String recallAllocationAccountNo = null;

  @JsonProperty("recallAllocationAccountNo2")
  private String recallAllocationAccountNo2 = null;

  @JsonProperty("recallAllocationPositionDTO")
  private PositionOutDTO recallAllocationPositionDTO = null;

  @JsonProperty("recallId")
  private Long recallId = null;

  @JsonProperty("recalled")
  private Long recalled = null;

  public RecallAllocationDTO qualifiedName(String qualifiedName) {
    this.qualifiedName = qualifiedName;
    return this;
  }

   /**
   * Get qualifiedName
   * @return qualifiedName
  **/
  @ApiModelProperty(value = "")
  public String getQualifiedName() {
    return qualifiedName;
  }

  public void setQualifiedName(String qualifiedName) {
    this.qualifiedName = qualifiedName;
  }

  public RecallAllocationDTO positionId(Long positionId) {
    this.positionId = positionId;
    return this;
  }

   /**
   * Get positionId
   * @return positionId
  **/
  @ApiModelProperty(value = "")
  public Long getPositionId() {
    return positionId;
  }

  public void setPositionId(Long positionId) {
    this.positionId = positionId;
  }

  public RecallAllocationDTO recallAllocationAccountName(String recallAllocationAccountName) {
    this.recallAllocationAccountName = recallAllocationAccountName;
    return this;
  }

   /**
   * Get recallAllocationAccountName
   * @return recallAllocationAccountName
  **/
  @ApiModelProperty(value = "")
  public String getRecallAllocationAccountName() {
    return recallAllocationAccountName;
  }

  public void setRecallAllocationAccountName(String recallAllocationAccountName) {
    this.recallAllocationAccountName = recallAllocationAccountName;
  }

  public RecallAllocationDTO recallAllocationAccountNo(String recallAllocationAccountNo) {
    this.recallAllocationAccountNo = recallAllocationAccountNo;
    return this;
  }

   /**
   * Get recallAllocationAccountNo
   * @return recallAllocationAccountNo
  **/
  @ApiModelProperty(value = "")
  public String getRecallAllocationAccountNo() {
    return recallAllocationAccountNo;
  }

  public void setRecallAllocationAccountNo(String recallAllocationAccountNo) {
    this.recallAllocationAccountNo = recallAllocationAccountNo;
  }

  public RecallAllocationDTO recallAllocationAccountNo2(String recallAllocationAccountNo2) {
    this.recallAllocationAccountNo2 = recallAllocationAccountNo2;
    return this;
  }

   /**
   * Get recallAllocationAccountNo2
   * @return recallAllocationAccountNo2
  **/
  @ApiModelProperty(value = "")
  public String getRecallAllocationAccountNo2() {
    return recallAllocationAccountNo2;
  }

  public void setRecallAllocationAccountNo2(String recallAllocationAccountNo2) {
    this.recallAllocationAccountNo2 = recallAllocationAccountNo2;
  }

  public RecallAllocationDTO recallAllocationPositionDTO(PositionOutDTO recallAllocationPositionDTO) {
    this.recallAllocationPositionDTO = recallAllocationPositionDTO;
    return this;
  }

   /**
   * Get recallAllocationPositionDTO
   * @return recallAllocationPositionDTO
  **/
  @ApiModelProperty(value = "")
  public PositionOutDTO getRecallAllocationPositionDTO() {
    return recallAllocationPositionDTO;
  }

  public void setRecallAllocationPositionDTO(PositionOutDTO recallAllocationPositionDTO) {
    this.recallAllocationPositionDTO = recallAllocationPositionDTO;
  }

  public RecallAllocationDTO recallId(Long recallId) {
    this.recallId = recallId;
    return this;
  }

   /**
   * Get recallId
   * @return recallId
  **/
  @ApiModelProperty(value = "")
  public Long getRecallId() {
    return recallId;
  }

  public void setRecallId(Long recallId) {
    this.recallId = recallId;
  }

  public RecallAllocationDTO recalled(Long recalled) {
    this.recalled = recalled;
    return this;
  }

   /**
   * Get recalled
   * @return recalled
  **/
  @ApiModelProperty(value = "")
  public Long getRecalled() {
    return recalled;
  }

  public void setRecalled(Long recalled) {
    this.recalled = recalled;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RecallAllocationDTO recallAllocationDTO = (RecallAllocationDTO) o;
    return Objects.equals(this.qualifiedName, recallAllocationDTO.qualifiedName) &&
        Objects.equals(this.positionId, recallAllocationDTO.positionId) &&
        Objects.equals(this.recallAllocationAccountName, recallAllocationDTO.recallAllocationAccountName) &&
        Objects.equals(this.recallAllocationAccountNo, recallAllocationDTO.recallAllocationAccountNo) &&
        Objects.equals(this.recallAllocationAccountNo2, recallAllocationDTO.recallAllocationAccountNo2) &&
        Objects.equals(this.recallAllocationPositionDTO, recallAllocationDTO.recallAllocationPositionDTO) &&
        Objects.equals(this.recallId, recallAllocationDTO.recallId) &&
        Objects.equals(this.recalled, recallAllocationDTO.recalled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(qualifiedName, positionId, recallAllocationAccountName, recallAllocationAccountNo, recallAllocationAccountNo2, recallAllocationPositionDTO, recallId, recalled);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RecallAllocationDTO {\n");
    
    sb.append("    qualifiedName: ").append(toIndentedString(qualifiedName)).append("\n");
    sb.append("    positionId: ").append(toIndentedString(positionId)).append("\n");
    sb.append("    recallAllocationAccountName: ").append(toIndentedString(recallAllocationAccountName)).append("\n");
    sb.append("    recallAllocationAccountNo: ").append(toIndentedString(recallAllocationAccountNo)).append("\n");
    sb.append("    recallAllocationAccountNo2: ").append(toIndentedString(recallAllocationAccountNo2)).append("\n");
    sb.append("    recallAllocationPositionDTO: ").append(toIndentedString(recallAllocationPositionDTO)).append("\n");
    sb.append("    recallId: ").append(toIndentedString(recallId)).append("\n");
    sb.append("    recalled: ").append(toIndentedString(recalled)).append("\n");
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

