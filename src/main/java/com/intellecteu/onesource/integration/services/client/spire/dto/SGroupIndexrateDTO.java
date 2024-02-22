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
import com.intellecteu.onesource.integration.services.client.spire.dto.IndexrateDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SGroupIndexrateDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * SGroupIndexrateDTO
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-02-20T15:00:33.025Z")
public class SGroupIndexrateDTO {
  @JsonProperty("avg")
  private IndexrateDTO avg = null;

  @JsonProperty("childrenGroupKeys")
  private List<String> childrenGroupKeys = null;

  @JsonProperty("endIndex")
  private Integer endIndex = null;

  @JsonProperty("groupLevel")
  private Integer groupLevel = null;

  @JsonProperty("groups")
  private List<SGroupIndexrateDTO> groups = null;

  @JsonProperty("key")
  private String key = null;

  @JsonProperty("max")
  private IndexrateDTO max = null;

  @JsonProperty("min")
  private IndexrateDTO min = null;

  @JsonProperty("startIndex")
  private Integer startIndex = null;

  @JsonProperty("sum")
  private IndexrateDTO sum = null;

  @JsonProperty("value")
  private Object value = null;

  public SGroupIndexrateDTO avg(IndexrateDTO avg) {
    this.avg = avg;
    return this;
  }

   /**
   * Get avg
   * @return avg
  **/
  @ApiModelProperty(value = "")
  public IndexrateDTO getAvg() {
    return avg;
  }

  public void setAvg(IndexrateDTO avg) {
    this.avg = avg;
  }

  public SGroupIndexrateDTO childrenGroupKeys(List<String> childrenGroupKeys) {
    this.childrenGroupKeys = childrenGroupKeys;
    return this;
  }

  public SGroupIndexrateDTO addChildrenGroupKeysItem(String childrenGroupKeysItem) {
    if (this.childrenGroupKeys == null) {
      this.childrenGroupKeys = new ArrayList<>();
    }
    this.childrenGroupKeys.add(childrenGroupKeysItem);
    return this;
  }

   /**
   * Get childrenGroupKeys
   * @return childrenGroupKeys
  **/
  @ApiModelProperty(value = "")
  public List<String> getChildrenGroupKeys() {
    return childrenGroupKeys;
  }

  public void setChildrenGroupKeys(List<String> childrenGroupKeys) {
    this.childrenGroupKeys = childrenGroupKeys;
  }

  public SGroupIndexrateDTO endIndex(Integer endIndex) {
    this.endIndex = endIndex;
    return this;
  }

   /**
   * Get endIndex
   * @return endIndex
  **/
  @ApiModelProperty(value = "")
  public Integer getEndIndex() {
    return endIndex;
  }

  public void setEndIndex(Integer endIndex) {
    this.endIndex = endIndex;
  }

  public SGroupIndexrateDTO groupLevel(Integer groupLevel) {
    this.groupLevel = groupLevel;
    return this;
  }

   /**
   * Get groupLevel
   * @return groupLevel
  **/
  @ApiModelProperty(value = "")
  public Integer getGroupLevel() {
    return groupLevel;
  }

  public void setGroupLevel(Integer groupLevel) {
    this.groupLevel = groupLevel;
  }

  public SGroupIndexrateDTO groups(List<SGroupIndexrateDTO> groups) {
    this.groups = groups;
    return this;
  }

  public SGroupIndexrateDTO addGroupsItem(SGroupIndexrateDTO groupsItem) {
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
  public List<SGroupIndexrateDTO> getGroups() {
    return groups;
  }

  public void setGroups(List<SGroupIndexrateDTO> groups) {
    this.groups = groups;
  }

  public SGroupIndexrateDTO key(String key) {
    this.key = key;
    return this;
  }

   /**
   * Get key
   * @return key
  **/
  @ApiModelProperty(value = "")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public SGroupIndexrateDTO max(IndexrateDTO max) {
    this.max = max;
    return this;
  }

   /**
   * Get max
   * @return max
  **/
  @ApiModelProperty(value = "")
  public IndexrateDTO getMax() {
    return max;
  }

  public void setMax(IndexrateDTO max) {
    this.max = max;
  }

  public SGroupIndexrateDTO min(IndexrateDTO min) {
    this.min = min;
    return this;
  }

   /**
   * Get min
   * @return min
  **/
  @ApiModelProperty(value = "")
  public IndexrateDTO getMin() {
    return min;
  }

  public void setMin(IndexrateDTO min) {
    this.min = min;
  }

  public SGroupIndexrateDTO startIndex(Integer startIndex) {
    this.startIndex = startIndex;
    return this;
  }

   /**
   * Get startIndex
   * @return startIndex
  **/
  @ApiModelProperty(value = "")
  public Integer getStartIndex() {
    return startIndex;
  }

  public void setStartIndex(Integer startIndex) {
    this.startIndex = startIndex;
  }

  public SGroupIndexrateDTO sum(IndexrateDTO sum) {
    this.sum = sum;
    return this;
  }

   /**
   * Get sum
   * @return sum
  **/
  @ApiModelProperty(value = "")
  public IndexrateDTO getSum() {
    return sum;
  }

  public void setSum(IndexrateDTO sum) {
    this.sum = sum;
  }

  public SGroupIndexrateDTO value(Object value) {
    this.value = value;
    return this;
  }

   /**
   * Get value
   * @return value
  **/
  @ApiModelProperty(value = "")
  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SGroupIndexrateDTO sgroupIndexrateDTO = (SGroupIndexrateDTO) o;
    return Objects.equals(this.avg, sgroupIndexrateDTO.avg) &&
        Objects.equals(this.childrenGroupKeys, sgroupIndexrateDTO.childrenGroupKeys) &&
        Objects.equals(this.endIndex, sgroupIndexrateDTO.endIndex) &&
        Objects.equals(this.groupLevel, sgroupIndexrateDTO.groupLevel) &&
        Objects.equals(this.groups, sgroupIndexrateDTO.groups) &&
        Objects.equals(this.key, sgroupIndexrateDTO.key) &&
        Objects.equals(this.max, sgroupIndexrateDTO.max) &&
        Objects.equals(this.min, sgroupIndexrateDTO.min) &&
        Objects.equals(this.startIndex, sgroupIndexrateDTO.startIndex) &&
        Objects.equals(this.sum, sgroupIndexrateDTO.sum) &&
        Objects.equals(this.value, sgroupIndexrateDTO.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(avg, childrenGroupKeys, endIndex, groupLevel, groups, key, max, min, startIndex, sum, value);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SGroupIndexrateDTO {\n");
    
    sb.append("    avg: ").append(toIndentedString(avg)).append("\n");
    sb.append("    childrenGroupKeys: ").append(toIndentedString(childrenGroupKeys)).append("\n");
    sb.append("    endIndex: ").append(toIndentedString(endIndex)).append("\n");
    sb.append("    groupLevel: ").append(toIndentedString(groupLevel)).append("\n");
    sb.append("    groups: ").append(toIndentedString(groups)).append("\n");
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    max: ").append(toIndentedString(max)).append("\n");
    sb.append("    min: ").append(toIndentedString(min)).append("\n");
    sb.append("    startIndex: ").append(toIndentedString(startIndex)).append("\n");
    sb.append("    sum: ").append(toIndentedString(sum)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

