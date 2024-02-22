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
import java.time.LocalDateTime;

/**
 * PositionHistoryKeyDTO
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-02-20T15:00:33.025Z")
public class PositionHistoryKeyDTO {
  @JsonProperty("histDate")
  private LocalDateTime histDate = null;

  @JsonProperty("positionId")
  private Long positionId = null;

  public PositionHistoryKeyDTO histDate(LocalDateTime histDate) {
    this.histDate = histDate;
    return this;
  }

   /**
   * Get histDate
   * @return histDate
  **/
  @ApiModelProperty(value = "")
  public LocalDateTime getHistDate() {
    return histDate;
  }

  public void setHistDate(LocalDateTime histDate) {
    this.histDate = histDate;
  }

  public PositionHistoryKeyDTO positionId(Long positionId) {
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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PositionHistoryKeyDTO positionHistoryKeyDTO = (PositionHistoryKeyDTO) o;
    return Objects.equals(this.histDate, positionHistoryKeyDTO.histDate) &&
        Objects.equals(this.positionId, positionHistoryKeyDTO.positionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(histDate, positionId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PositionHistoryKeyDTO {\n");
    
    sb.append("    histDate: ").append(toIndentedString(histDate)).append("\n");
    sb.append("    positionId: ").append(toIndentedString(positionId)).append("\n");
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

