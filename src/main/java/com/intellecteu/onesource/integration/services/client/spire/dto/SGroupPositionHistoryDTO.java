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
 * SGroupPositionHistoryDTO
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-02-20T15:00:33.025Z")
public class SGroupPositionHistoryDTO {
  @JsonProperty("avg")
  private PositionHistoryDTO avg = null;

  @JsonProperty("childrenGroupKeys")
  private List<String> childrenGroupKeys = null;

  @JsonProperty("endIndex")
  private Integer endIndex = null;

  @JsonProperty("groupLevel")
  private Integer groupLevel = null;

  @JsonProperty("groups")
  private List<SGroupPositionHistoryDTO> groups = null;

  @JsonProperty("key")
  private String key = null;

  @JsonProperty("max")
  private PositionHistoryDTO max = null;

  @JsonProperty("min")
  private PositionHistoryDTO min = null;

  @JsonProperty("startIndex")
  private Integer startIndex = null;

  @JsonProperty("sum")
  private PositionHistoryDTO sum = null;

  @JsonProperty("value")
  private Object value = null;

  public SGroupPositionHistoryDTO avg(PositionHistoryDTO avg) {
    this.avg = avg;
    return this;
  }

   /**
   * Get avg
   * @return avg
  **/
  @ApiModelProperty(value = "")
  public PositionHistoryDTO getAvg() {
    return avg;
  }

  public void setAvg(PositionHistoryDTO avg) {
    this.avg = avg;
  }

  public SGroupPositionHistoryDTO childrenGroupKeys(List<String> childrenGroupKeys) {
    this.childrenGroupKeys = childrenGroupKeys;
    return this;
  }

  public SGroupPositionHistoryDTO addChildrenGroupKeysItem(String childrenGroupKeysItem) {
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

  public SGroupPositionHistoryDTO endIndex(Integer endIndex) {
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

  public SGroupPositionHistoryDTO groupLevel(Integer groupLevel) {
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

  public SGroupPositionHistoryDTO groups(List<SGroupPositionHistoryDTO> groups) {
    this.groups = groups;
    return this;
  }

  public SGroupPositionHistoryDTO addGroupsItem(SGroupPositionHistoryDTO groupsItem) {
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

  public SGroupPositionHistoryDTO key(String key) {
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

  public SGroupPositionHistoryDTO max(PositionHistoryDTO max) {
    this.max = max;
    return this;
  }

   /**
   * Get max
   * @return max
  **/
  @ApiModelProperty(value = "")
  public PositionHistoryDTO getMax() {
    return max;
  }

  public void setMax(PositionHistoryDTO max) {
    this.max = max;
  }

  public SGroupPositionHistoryDTO min(PositionHistoryDTO min) {
    this.min = min;
    return this;
  }

   /**
   * Get min
   * @return min
  **/
  @ApiModelProperty(value = "")
  public PositionHistoryDTO getMin() {
    return min;
  }

  public void setMin(PositionHistoryDTO min) {
    this.min = min;
  }

  public SGroupPositionHistoryDTO startIndex(Integer startIndex) {
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

  public SGroupPositionHistoryDTO sum(PositionHistoryDTO sum) {
    this.sum = sum;
    return this;
  }

   /**
   * Get sum
   * @return sum
  **/
  @ApiModelProperty(value = "")
  public PositionHistoryDTO getSum() {
    return sum;
  }

  public void setSum(PositionHistoryDTO sum) {
    this.sum = sum;
  }

  public SGroupPositionHistoryDTO value(Object value) {
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
    SGroupPositionHistoryDTO sgroupPositionHistoryDTO = (SGroupPositionHistoryDTO) o;
    return Objects.equals(this.avg, sgroupPositionHistoryDTO.avg) &&
        Objects.equals(this.childrenGroupKeys, sgroupPositionHistoryDTO.childrenGroupKeys) &&
        Objects.equals(this.endIndex, sgroupPositionHistoryDTO.endIndex) &&
        Objects.equals(this.groupLevel, sgroupPositionHistoryDTO.groupLevel) &&
        Objects.equals(this.groups, sgroupPositionHistoryDTO.groups) &&
        Objects.equals(this.key, sgroupPositionHistoryDTO.key) &&
        Objects.equals(this.max, sgroupPositionHistoryDTO.max) &&
        Objects.equals(this.min, sgroupPositionHistoryDTO.min) &&
        Objects.equals(this.startIndex, sgroupPositionHistoryDTO.startIndex) &&
        Objects.equals(this.sum, sgroupPositionHistoryDTO.sum) &&
        Objects.equals(this.value, sgroupPositionHistoryDTO.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(avg, childrenGroupKeys, endIndex, groupLevel, groups, key, max, min, startIndex, sum, value);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SGroupPositionHistoryDTO {\n");
    
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

