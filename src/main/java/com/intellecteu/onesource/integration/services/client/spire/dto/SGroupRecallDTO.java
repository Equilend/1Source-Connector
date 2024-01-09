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
 * SGroupRecallDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-01-07T21:15:14.632Z")
public class SGroupRecallDTO {
  @JsonProperty("avg")
  private RecallDTO avg = null;

  @JsonProperty("childrenGroupKeys")
  private List<String> childrenGroupKeys = null;

  @JsonProperty("endIndex")
  private Integer endIndex = null;

  @JsonProperty("groupLevel")
  private Integer groupLevel = null;

  @JsonProperty("groups")
  private List<SGroupRecallDTO> groups = null;

  @JsonProperty("key")
  private String key = null;

  @JsonProperty("max")
  private RecallDTO max = null;

  @JsonProperty("min")
  private RecallDTO min = null;

  @JsonProperty("startIndex")
  private Integer startIndex = null;

  @JsonProperty("sum")
  private RecallDTO sum = null;

  @JsonProperty("value")
  private Object value = null;

  public SGroupRecallDTO avg(RecallDTO avg) {
    this.avg = avg;
    return this;
  }

   /**
   * Get avg
   * @return avg
  **/
  @ApiModelProperty(value = "")
  public RecallDTO getAvg() {
    return avg;
  }

  public void setAvg(RecallDTO avg) {
    this.avg = avg;
  }

  public SGroupRecallDTO childrenGroupKeys(List<String> childrenGroupKeys) {
    this.childrenGroupKeys = childrenGroupKeys;
    return this;
  }

  public SGroupRecallDTO addChildrenGroupKeysItem(String childrenGroupKeysItem) {
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

  public SGroupRecallDTO endIndex(Integer endIndex) {
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

  public SGroupRecallDTO groupLevel(Integer groupLevel) {
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

  public SGroupRecallDTO groups(List<SGroupRecallDTO> groups) {
    this.groups = groups;
    return this;
  }

  public SGroupRecallDTO addGroupsItem(SGroupRecallDTO groupsItem) {
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
  public List<SGroupRecallDTO> getGroups() {
    return groups;
  }

  public void setGroups(List<SGroupRecallDTO> groups) {
    this.groups = groups;
  }

  public SGroupRecallDTO key(String key) {
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

  public SGroupRecallDTO max(RecallDTO max) {
    this.max = max;
    return this;
  }

   /**
   * Get max
   * @return max
  **/
  @ApiModelProperty(value = "")
  public RecallDTO getMax() {
    return max;
  }

  public void setMax(RecallDTO max) {
    this.max = max;
  }

  public SGroupRecallDTO min(RecallDTO min) {
    this.min = min;
    return this;
  }

   /**
   * Get min
   * @return min
  **/
  @ApiModelProperty(value = "")
  public RecallDTO getMin() {
    return min;
  }

  public void setMin(RecallDTO min) {
    this.min = min;
  }

  public SGroupRecallDTO startIndex(Integer startIndex) {
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

  public SGroupRecallDTO sum(RecallDTO sum) {
    this.sum = sum;
    return this;
  }

   /**
   * Get sum
   * @return sum
  **/
  @ApiModelProperty(value = "")
  public RecallDTO getSum() {
    return sum;
  }

  public void setSum(RecallDTO sum) {
    this.sum = sum;
  }

  public SGroupRecallDTO value(Object value) {
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
    SGroupRecallDTO sgroupRecallDTO = (SGroupRecallDTO) o;
    return Objects.equals(this.avg, sgroupRecallDTO.avg) &&
        Objects.equals(this.childrenGroupKeys, sgroupRecallDTO.childrenGroupKeys) &&
        Objects.equals(this.endIndex, sgroupRecallDTO.endIndex) &&
        Objects.equals(this.groupLevel, sgroupRecallDTO.groupLevel) &&
        Objects.equals(this.groups, sgroupRecallDTO.groups) &&
        Objects.equals(this.key, sgroupRecallDTO.key) &&
        Objects.equals(this.max, sgroupRecallDTO.max) &&
        Objects.equals(this.min, sgroupRecallDTO.min) &&
        Objects.equals(this.startIndex, sgroupRecallDTO.startIndex) &&
        Objects.equals(this.sum, sgroupRecallDTO.sum) &&
        Objects.equals(this.value, sgroupRecallDTO.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(avg, childrenGroupKeys, endIndex, groupLevel, groups, key, max, min, startIndex, sum, value);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SGroupRecallDTO {\n");
    
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

