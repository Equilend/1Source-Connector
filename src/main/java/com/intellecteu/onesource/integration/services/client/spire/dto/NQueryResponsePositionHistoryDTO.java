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
import com.intellecteu.onesource.integration.services.client.spire.dto.PositionHistoryDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SGroupPositionHistoryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * NQueryResponsePositionHistoryDTO
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-02-20T15:00:33.025Z")
public class NQueryResponsePositionHistoryDTO {
  @JsonProperty("beans")
  private List<PositionHistoryDTO> beans = null;

  @JsonProperty("groups")
  private List<SGroupPositionHistoryDTO> groups = null;

  @JsonProperty("totalRows")
  private Integer totalRows = null;

  public NQueryResponsePositionHistoryDTO beans(List<PositionHistoryDTO> beans) {
    this.beans = beans;
    return this;
  }

  public NQueryResponsePositionHistoryDTO addBeansItem(PositionHistoryDTO beansItem) {
    if (this.beans == null) {
      this.beans = new ArrayList<>();
    }
    this.beans.add(beansItem);
    return this;
  }

   /**
   * Get beans
   * @return beans
  **/
  @ApiModelProperty(value = "")
  public List<PositionHistoryDTO> getBeans() {
    return beans;
  }

  public void setBeans(List<PositionHistoryDTO> beans) {
    this.beans = beans;
  }

  public NQueryResponsePositionHistoryDTO groups(List<SGroupPositionHistoryDTO> groups) {
    this.groups = groups;
    return this;
  }

  public NQueryResponsePositionHistoryDTO addGroupsItem(SGroupPositionHistoryDTO groupsItem) {
    if (this.groups == null) {
      this.groups = new ArrayList<>();
    }
    this.groups.add(groupsItem);
    return this;
  }

   /**
   * Get groups
   * @return groups
  **/
  @ApiModelProperty(value = "")
  public List<SGroupPositionHistoryDTO> getGroups() {
    return groups;
  }

  public void setGroups(List<SGroupPositionHistoryDTO> groups) {
    this.groups = groups;
  }

  public NQueryResponsePositionHistoryDTO totalRows(Integer totalRows) {
    this.totalRows = totalRows;
    return this;
  }

   /**
   * Get totalRows
   * @return totalRows
  **/
  @ApiModelProperty(value = "")
  public Integer getTotalRows() {
    return totalRows;
  }

  public void setTotalRows(Integer totalRows) {
    this.totalRows = totalRows;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NQueryResponsePositionHistoryDTO nqueryResponsePositionHistoryDTO = (NQueryResponsePositionHistoryDTO) o;
    return Objects.equals(this.beans, nqueryResponsePositionHistoryDTO.beans) &&
        Objects.equals(this.groups, nqueryResponsePositionHistoryDTO.groups) &&
        Objects.equals(this.totalRows, nqueryResponsePositionHistoryDTO.totalRows);
  }

  @Override
  public int hashCode() {
    return Objects.hash(beans, groups, totalRows);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NQueryResponsePositionHistoryDTO {\n");
    
    sb.append("    beans: ").append(toIndentedString(beans)).append("\n");
    sb.append("    groups: ").append(toIndentedString(groups)).append("\n");
    sb.append("    totalRows: ").append(toIndentedString(totalRows)).append("\n");
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

