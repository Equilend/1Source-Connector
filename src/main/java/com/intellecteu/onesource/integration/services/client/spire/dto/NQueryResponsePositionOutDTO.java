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
 * NQueryResponsePositionOutDTO
 */

public class NQueryResponsePositionOutDTO {
  @JsonProperty("beans")
  private List<PositionOutDTO> beans = null;

  @JsonProperty("groups")
  private List<SGroupPositionOutDTO> groups = null;

  @JsonProperty("totalRows")
  private Integer totalRows = null;

  public NQueryResponsePositionOutDTO beans(List<PositionOutDTO> beans) {
    this.beans = beans;
    return this;
  }

  public NQueryResponsePositionOutDTO addBeansItem(PositionOutDTO beansItem) {
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
  public List<PositionOutDTO> getBeans() {
    return beans;
  }

  public void setBeans(List<PositionOutDTO> beans) {
    this.beans = beans;
  }

  public NQueryResponsePositionOutDTO groups(List<SGroupPositionOutDTO> groups) {
    this.groups = groups;
    return this;
  }

  public NQueryResponsePositionOutDTO addGroupsItem(SGroupPositionOutDTO groupsItem) {
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
  public List<SGroupPositionOutDTO> getGroups() {
    return groups;
  }

  public void setGroups(List<SGroupPositionOutDTO> groups) {
    this.groups = groups;
  }

  public NQueryResponsePositionOutDTO totalRows(Integer totalRows) {
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
    NQueryResponsePositionOutDTO nqueryResponsePositionOutDTO = (NQueryResponsePositionOutDTO) o;
    return Objects.equals(this.beans, nqueryResponsePositionOutDTO.beans) &&
        Objects.equals(this.groups, nqueryResponsePositionOutDTO.groups) &&
        Objects.equals(this.totalRows, nqueryResponsePositionOutDTO.totalRows);
  }

  @Override
  public int hashCode() {
    return Objects.hash(beans, groups, totalRows);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NQueryResponsePositionOutDTO {\n");
    
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

