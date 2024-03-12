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

/**
 * SBeanFieldQueryMeta
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-02-20T15:00:33.025Z")
public class SBeanFieldQueryMeta {
  @JsonProperty("editable")
  private Boolean editable = null;

  @JsonProperty("isEditable")
  private Boolean isEditable = null;

  @JsonProperty("queryable")
  private Boolean queryable = null;

  public SBeanFieldQueryMeta editable(Boolean editable) {
    this.editable = editable;
    return this;
  }

   /**
   * Get editable
   * @return editable
  **/
  @ApiModelProperty(value = "")
  public Boolean isEditable() {
    return editable;
  }

  public void setEditable(Boolean editable) {
    this.editable = editable;
  }

  public SBeanFieldQueryMeta isEditable(Boolean isEditable) {
    this.isEditable = isEditable;
    return this;
  }

   /**
   * Get isEditable
   * @return isEditable
  **/
  @ApiModelProperty(value = "")
  public Boolean isIsEditable() {
    return isEditable;
  }

  public void setIsEditable(Boolean isEditable) {
    this.isEditable = isEditable;
  }

  public SBeanFieldQueryMeta queryable(Boolean queryable) {
    this.queryable = queryable;
    return this;
  }

   /**
   * Get queryable
   * @return queryable
  **/
  @ApiModelProperty(value = "")
  public Boolean isQueryable() {
    return queryable;
  }

  public void setQueryable(Boolean queryable) {
    this.queryable = queryable;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SBeanFieldQueryMeta sbeanFieldQueryMeta = (SBeanFieldQueryMeta) o;
    return Objects.equals(this.editable, sbeanFieldQueryMeta.editable) &&
        Objects.equals(this.isEditable, sbeanFieldQueryMeta.isEditable) &&
        Objects.equals(this.queryable, sbeanFieldQueryMeta.queryable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(editable, isEditable, queryable);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SBeanFieldQueryMeta {\n");
    
    sb.append("    editable: ").append(toIndentedString(editable)).append("\n");
    sb.append("    isEditable: ").append(toIndentedString(isEditable)).append("\n");
    sb.append("    queryable: ").append(toIndentedString(queryable)).append("\n");
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

