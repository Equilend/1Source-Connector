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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ReverseRepoTradeInputBulkDTO
 */

public class ReverseRepoTradeInputBulkDTO {
  @JsonProperty("revRepoTradeInputDTOs")
  private List<ReverseRepoTradeInputDTO> revRepoTradeInputDTOs = null;

  public ReverseRepoTradeInputBulkDTO revRepoTradeInputDTOs(List<ReverseRepoTradeInputDTO> revRepoTradeInputDTOs) {
    this.revRepoTradeInputDTOs = revRepoTradeInputDTOs;
    return this;
  }

  public ReverseRepoTradeInputBulkDTO addRevRepoTradeInputDTOsItem(ReverseRepoTradeInputDTO revRepoTradeInputDTOsItem) {
    if (this.revRepoTradeInputDTOs == null) {
      this.revRepoTradeInputDTOs = new ArrayList<>();
    }
    this.revRepoTradeInputDTOs.add(revRepoTradeInputDTOsItem);
    return this;
  }

   /**
   * Get revRepoTradeInputDTOs
   * @return revRepoTradeInputDTOs
  **/
  @ApiModelProperty(value = "")
  public List<ReverseRepoTradeInputDTO> getRevRepoTradeInputDTOs() {
    return revRepoTradeInputDTOs;
  }

  public void setRevRepoTradeInputDTOs(List<ReverseRepoTradeInputDTO> revRepoTradeInputDTOs) {
    this.revRepoTradeInputDTOs = revRepoTradeInputDTOs;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReverseRepoTradeInputBulkDTO reverseRepoTradeInputBulkDTO = (ReverseRepoTradeInputBulkDTO) o;
    return Objects.equals(this.revRepoTradeInputDTOs, reverseRepoTradeInputBulkDTO.revRepoTradeInputDTOs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(revRepoTradeInputDTOs);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReverseRepoTradeInputBulkDTO {\n");
    
    sb.append("    revRepoTradeInputDTOs: ").append(toIndentedString(revRepoTradeInputDTOs)).append("\n");
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

