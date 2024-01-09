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
 * OpenRecallsDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-01-07T21:15:14.632Z")
public class OpenRecallsDTO {
  @JsonProperty("__qualifiedName")
  private String qualifiedName = null;

  @JsonProperty("accountId")
  private Long accountId = null;

  @JsonProperty("openRecalls")
  private Integer openRecalls = null;

  @JsonProperty("shortName")
  private String shortName = null;

  public OpenRecallsDTO qualifiedName(String qualifiedName) {
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

  public OpenRecallsDTO accountId(Long accountId) {
    this.accountId = accountId;
    return this;
  }

   /**
   * Get accountId
   * @return accountId
  **/
  @ApiModelProperty(value = "")
  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public OpenRecallsDTO openRecalls(Integer openRecalls) {
    this.openRecalls = openRecalls;
    return this;
  }

   /**
   * Get openRecalls
   * @return openRecalls
  **/
  @ApiModelProperty(value = "")
  public Integer getOpenRecalls() {
    return openRecalls;
  }

  public void setOpenRecalls(Integer openRecalls) {
    this.openRecalls = openRecalls;
  }

  public OpenRecallsDTO shortName(String shortName) {
    this.shortName = shortName;
    return this;
  }

   /**
   * Get shortName
   * @return shortName
  **/
  @ApiModelProperty(value = "")
  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OpenRecallsDTO openRecallsDTO = (OpenRecallsDTO) o;
    return Objects.equals(this.qualifiedName, openRecallsDTO.qualifiedName) &&
        Objects.equals(this.accountId, openRecallsDTO.accountId) &&
        Objects.equals(this.openRecalls, openRecallsDTO.openRecalls) &&
        Objects.equals(this.shortName, openRecallsDTO.shortName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(qualifiedName, accountId, openRecalls, shortName);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OpenRecallsDTO {\n");
    
    sb.append("    qualifiedName: ").append(toIndentedString(qualifiedName)).append("\n");
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
    sb.append("    openRecalls: ").append(toIndentedString(openRecalls)).append("\n");
    sb.append("    shortName: ").append(toIndentedString(shortName)).append("\n");
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

