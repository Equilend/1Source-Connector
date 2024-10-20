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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * OutResolverDTO
 */

public class OutResolverDTO {
  @JsonProperty("data")
  private JsonNode data = null;

  @JsonProperty("includeFields")
  private List<String> includeFields = null;

  @JsonProperty("map")
  private Map<String, List<ResolverOutData>> map = null;

  @JsonProperty("skipInterceptorTrim")
  private Boolean skipInterceptorTrim = null;

  public OutResolverDTO data(JsonNode data) {
    this.data = data;
    return this;
  }

   /**
   * Get data
   * @return data
  **/
  @ApiModelProperty(value = "")
  public JsonNode getData() {
    return data;
  }

  public void setData(JsonNode data) {
    this.data = data;
  }

  public OutResolverDTO includeFields(List<String> includeFields) {
    this.includeFields = includeFields;
    return this;
  }

  public OutResolverDTO addIncludeFieldsItem(String includeFieldsItem) {
    if (this.includeFields == null) {
      this.includeFields = new ArrayList<>();
    }
    this.includeFields.add(includeFieldsItem);
    return this;
  }

   /**
   * Get includeFields
   * @return includeFields
  **/
  @ApiModelProperty(value = "")
  public List<String> getIncludeFields() {
    return includeFields;
  }

  public void setIncludeFields(List<String> includeFields) {
    this.includeFields = includeFields;
  }

  public OutResolverDTO map(Map<String, List<ResolverOutData>> map) {
    this.map = map;
    return this;
  }

  public OutResolverDTO putMapItem(String key, List<ResolverOutData> mapItem) {
    if (this.map == null) {
      this.map = new HashMap<>();
    }
    this.map.put(key, mapItem);
    return this;
  }

   /**
   * Get map
   * @return map
  **/
  @ApiModelProperty(value = "")
  public Map<String, List<ResolverOutData>> getMap() {
    return map;
  }

  public void setMap(Map<String, List<ResolverOutData>> map) {
    this.map = map;
  }

  public OutResolverDTO skipInterceptorTrim(Boolean skipInterceptorTrim) {
    this.skipInterceptorTrim = skipInterceptorTrim;
    return this;
  }

   /**
   * Get skipInterceptorTrim
   * @return skipInterceptorTrim
  **/
  @ApiModelProperty(value = "")
  public Boolean isSkipInterceptorTrim() {
    return skipInterceptorTrim;
  }

  public void setSkipInterceptorTrim(Boolean skipInterceptorTrim) {
    this.skipInterceptorTrim = skipInterceptorTrim;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OutResolverDTO outResolverDTO = (OutResolverDTO) o;
    return Objects.equals(this.data, outResolverDTO.data) &&
        Objects.equals(this.includeFields, outResolverDTO.includeFields) &&
        Objects.equals(this.map, outResolverDTO.map) &&
        Objects.equals(this.skipInterceptorTrim, outResolverDTO.skipInterceptorTrim);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, includeFields, map, skipInterceptorTrim);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OutResolverDTO {\n");
    
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    includeFields: ").append(toIndentedString(includeFields)).append("\n");
    sb.append("    map: ").append(toIndentedString(map)).append("\n");
    sb.append("    skipInterceptorTrim: ").append(toIndentedString(skipInterceptorTrim)).append("\n");
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

