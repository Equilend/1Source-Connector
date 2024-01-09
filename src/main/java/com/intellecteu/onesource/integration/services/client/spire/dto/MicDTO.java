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

import java.util.Objects;

/**
 * MicDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-01-07T21:15:14.632Z")
public class MicDTO {
  @JsonProperty("__qualifiedName")
  private String qualifiedName = null;

  @JsonProperty("countryId")
  private Integer countryId = null;

  @JsonProperty("micId")
  private Long micId = null;

  @JsonProperty("micKey")
  private String micKey = null;

  @JsonProperty("name")
  private String name = null;

  public MicDTO qualifiedName(String qualifiedName) {
    this.qualifiedName = qualifiedName;
    return this;
  }

   /**
   * Get qualifiedName
   * @return qualifiedName
  **/
  @ApiModelProperty(value = "")
  public String getQualifiedName() {
    return qualifiedName;
  }

  public void setQualifiedName(String qualifiedName) {
    this.qualifiedName = qualifiedName;
  }

  public MicDTO countryId(Integer countryId) {
    this.countryId = countryId;
    return this;
  }

   /**
   * Get countryId
   * @return countryId
  **/
  @ApiModelProperty(value = "")
  public Integer getCountryId() {
    return countryId;
  }

  public void setCountryId(Integer countryId) {
    this.countryId = countryId;
  }

  public MicDTO micId(Long micId) {
    this.micId = micId;
    return this;
  }

   /**
   * Get micId
   * @return micId
  **/
  @ApiModelProperty(value = "")
  public Long getMicId() {
    return micId;
  }

  public void setMicId(Long micId) {
    this.micId = micId;
  }

  public MicDTO micKey(String micKey) {
    this.micKey = micKey;
    return this;
  }

   /**
   * Get micKey
   * @return micKey
  **/
  @ApiModelProperty(value = "")
  public String getMicKey() {
    return micKey;
  }

  public void setMicKey(String micKey) {
    this.micKey = micKey;
  }

  public MicDTO name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @ApiModelProperty(value = "")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MicDTO micDTO = (MicDTO) o;
    return Objects.equals(this.qualifiedName, micDTO.qualifiedName) &&
        Objects.equals(this.countryId, micDTO.countryId) &&
        Objects.equals(this.micId, micDTO.micId) &&
        Objects.equals(this.micKey, micDTO.micKey) &&
        Objects.equals(this.name, micDTO.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(qualifiedName, countryId, micId, micKey, name);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MicDTO {\n");
    
    sb.append("    qualifiedName: ").append(toIndentedString(qualifiedName)).append("\n");
    sb.append("    countryId: ").append(toIndentedString(countryId)).append("\n");
    sb.append("    micId: ").append(toIndentedString(micId)).append("\n");
    sb.append("    micKey: ").append(toIndentedString(micKey)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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

