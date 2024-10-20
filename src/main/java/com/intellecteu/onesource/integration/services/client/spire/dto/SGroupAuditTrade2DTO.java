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
 * SGroupAuditTrade2DTO
 */

public class SGroupAuditTrade2DTO {
  @JsonProperty("avg")
  private AuditTrade2DTO avg = null;

  @JsonProperty("childrenGroupKeys")
  private List<String> childrenGroupKeys = null;

  @JsonProperty("endIndex")
  private Integer endIndex = null;

  @JsonProperty("groupLevel")
  private Integer groupLevel = null;

  @JsonProperty("groups")
  private List<SGroupAuditTrade2DTO> groups = null;

  @JsonProperty("key")
  private String key = null;

  @JsonProperty("max")
  private AuditTrade2DTO max = null;

  @JsonProperty("min")
  private AuditTrade2DTO min = null;

  @JsonProperty("startIndex")
  private Integer startIndex = null;

  @JsonProperty("sum")
  private AuditTrade2DTO sum = null;

  @JsonProperty("value")
  private Object value = null;

  public SGroupAuditTrade2DTO avg(AuditTrade2DTO avg) {
    this.avg = avg;
    return this;
  }

   /**
   * Get avg
   * @return avg
  **/
  @ApiModelProperty(value = "")
  public AuditTrade2DTO getAvg() {
    return avg;
  }

  public void setAvg(AuditTrade2DTO avg) {
    this.avg = avg;
  }

  public SGroupAuditTrade2DTO childrenGroupKeys(List<String> childrenGroupKeys) {
    this.childrenGroupKeys = childrenGroupKeys;
    return this;
  }

  public SGroupAuditTrade2DTO addChildrenGroupKeysItem(String childrenGroupKeysItem) {
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

  public SGroupAuditTrade2DTO endIndex(Integer endIndex) {
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

  public SGroupAuditTrade2DTO groupLevel(Integer groupLevel) {
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

  public SGroupAuditTrade2DTO groups(List<SGroupAuditTrade2DTO> groups) {
    this.groups = groups;
    return this;
  }

  public SGroupAuditTrade2DTO addGroupsItem(SGroupAuditTrade2DTO groupsItem) {
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
  public List<SGroupAuditTrade2DTO> getGroups() {
    return groups;
  }

  public void setGroups(List<SGroupAuditTrade2DTO> groups) {
    this.groups = groups;
  }

  public SGroupAuditTrade2DTO key(String key) {
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

  public SGroupAuditTrade2DTO max(AuditTrade2DTO max) {
    this.max = max;
    return this;
  }

   /**
   * Get max
   * @return max
  **/
  @ApiModelProperty(value = "")
  public AuditTrade2DTO getMax() {
    return max;
  }

  public void setMax(AuditTrade2DTO max) {
    this.max = max;
  }

  public SGroupAuditTrade2DTO min(AuditTrade2DTO min) {
    this.min = min;
    return this;
  }

   /**
   * Get min
   * @return min
  **/
  @ApiModelProperty(value = "")
  public AuditTrade2DTO getMin() {
    return min;
  }

  public void setMin(AuditTrade2DTO min) {
    this.min = min;
  }

  public SGroupAuditTrade2DTO startIndex(Integer startIndex) {
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

  public SGroupAuditTrade2DTO sum(AuditTrade2DTO sum) {
    this.sum = sum;
    return this;
  }

   /**
   * Get sum
   * @return sum
  **/
  @ApiModelProperty(value = "")
  public AuditTrade2DTO getSum() {
    return sum;
  }

  public void setSum(AuditTrade2DTO sum) {
    this.sum = sum;
  }

  public SGroupAuditTrade2DTO value(Object value) {
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
    SGroupAuditTrade2DTO sgroupAuditTrade2DTO = (SGroupAuditTrade2DTO) o;
    return Objects.equals(this.avg, sgroupAuditTrade2DTO.avg) &&
        Objects.equals(this.childrenGroupKeys, sgroupAuditTrade2DTO.childrenGroupKeys) &&
        Objects.equals(this.endIndex, sgroupAuditTrade2DTO.endIndex) &&
        Objects.equals(this.groupLevel, sgroupAuditTrade2DTO.groupLevel) &&
        Objects.equals(this.groups, sgroupAuditTrade2DTO.groups) &&
        Objects.equals(this.key, sgroupAuditTrade2DTO.key) &&
        Objects.equals(this.max, sgroupAuditTrade2DTO.max) &&
        Objects.equals(this.min, sgroupAuditTrade2DTO.min) &&
        Objects.equals(this.startIndex, sgroupAuditTrade2DTO.startIndex) &&
        Objects.equals(this.sum, sgroupAuditTrade2DTO.sum) &&
        Objects.equals(this.value, sgroupAuditTrade2DTO.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(avg, childrenGroupKeys, endIndex, groupLevel, groups, key, max, min, startIndex, sum, value);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SGroupAuditTrade2DTO {\n");
    
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

