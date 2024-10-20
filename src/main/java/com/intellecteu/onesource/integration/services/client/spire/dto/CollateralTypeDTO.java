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
 * CollateralTypeDTO
 */

public class CollateralTypeDTO {
  @JsonProperty("categoryId")
  private Integer categoryId = null;

  @JsonProperty("collateralType")
  private String collateralType = null;

  @JsonProperty("collateralTypeId")
  private Integer collateralTypeId = null;

  @JsonProperty("eqlCollateralType")
  private String eqlCollateralType = null;

  @JsonProperty("extCollateralType")
  private String extCollateralType = null;

  @JsonProperty("minRatingValue")
  private Integer minRatingValue = null;

  @JsonProperty("profileNo")
  private String profileNo = null;

  @JsonProperty("statusId")
  private Integer statusId = null;

  @JsonProperty("tripartyAgentCode")
  private String tripartyAgentCode = null;

  public CollateralTypeDTO categoryId(Integer categoryId) {
    this.categoryId = categoryId;
    return this;
  }

   /**
   * Get categoryId
   * @return categoryId
  **/
  @ApiModelProperty(value = "")
  public Integer getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Integer categoryId) {
    this.categoryId = categoryId;
  }

  public CollateralTypeDTO collateralType(String collateralType) {
    this.collateralType = collateralType;
    return this;
  }

   /**
   * Get collateralType
   * @return collateralType
  **/
  @ApiModelProperty(value = "")
  public String getCollateralType() {
    return collateralType;
  }

  public void setCollateralType(String collateralType) {
    this.collateralType = collateralType;
  }

  public CollateralTypeDTO collateralTypeId(Integer collateralTypeId) {
    this.collateralTypeId = collateralTypeId;
    return this;
  }

   /**
   * Get collateralTypeId
   * @return collateralTypeId
  **/
  @ApiModelProperty(value = "")
  public Integer getCollateralTypeId() {
    return collateralTypeId;
  }

  public void setCollateralTypeId(Integer collateralTypeId) {
    this.collateralTypeId = collateralTypeId;
  }

  public CollateralTypeDTO eqlCollateralType(String eqlCollateralType) {
    this.eqlCollateralType = eqlCollateralType;
    return this;
  }

   /**
   * Get eqlCollateralType
   * @return eqlCollateralType
  **/
  @ApiModelProperty(value = "")
  public String getEqlCollateralType() {
    return eqlCollateralType;
  }

  public void setEqlCollateralType(String eqlCollateralType) {
    this.eqlCollateralType = eqlCollateralType;
  }

  public CollateralTypeDTO extCollateralType(String extCollateralType) {
    this.extCollateralType = extCollateralType;
    return this;
  }

   /**
   * Get extCollateralType
   * @return extCollateralType
  **/
  @ApiModelProperty(value = "")
  public String getExtCollateralType() {
    return extCollateralType;
  }

  public void setExtCollateralType(String extCollateralType) {
    this.extCollateralType = extCollateralType;
  }

  public CollateralTypeDTO minRatingValue(Integer minRatingValue) {
    this.minRatingValue = minRatingValue;
    return this;
  }

   /**
   * Get minRatingValue
   * @return minRatingValue
  **/
  @ApiModelProperty(value = "")
  public Integer getMinRatingValue() {
    return minRatingValue;
  }

  public void setMinRatingValue(Integer minRatingValue) {
    this.minRatingValue = minRatingValue;
  }

  public CollateralTypeDTO profileNo(String profileNo) {
    this.profileNo = profileNo;
    return this;
  }

   /**
   * Get profileNo
   * @return profileNo
  **/
  @ApiModelProperty(value = "")
  public String getProfileNo() {
    return profileNo;
  }

  public void setProfileNo(String profileNo) {
    this.profileNo = profileNo;
  }

  public CollateralTypeDTO statusId(Integer statusId) {
    this.statusId = statusId;
    return this;
  }

   /**
   * Get statusId
   * @return statusId
  **/
  @ApiModelProperty(value = "")
  public Integer getStatusId() {
    return statusId;
  }

  public void setStatusId(Integer statusId) {
    this.statusId = statusId;
  }

  public CollateralTypeDTO tripartyAgentCode(String tripartyAgentCode) {
    this.tripartyAgentCode = tripartyAgentCode;
    return this;
  }

   /**
   * Get tripartyAgentCode
   * @return tripartyAgentCode
  **/
  @ApiModelProperty(value = "")
  public String getTripartyAgentCode() {
    return tripartyAgentCode;
  }

  public void setTripartyAgentCode(String tripartyAgentCode) {
    this.tripartyAgentCode = tripartyAgentCode;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CollateralTypeDTO collateralTypeDTO = (CollateralTypeDTO) o;
    return Objects.equals(this.categoryId, collateralTypeDTO.categoryId) &&
        Objects.equals(this.collateralType, collateralTypeDTO.collateralType) &&
        Objects.equals(this.collateralTypeId, collateralTypeDTO.collateralTypeId) &&
        Objects.equals(this.eqlCollateralType, collateralTypeDTO.eqlCollateralType) &&
        Objects.equals(this.extCollateralType, collateralTypeDTO.extCollateralType) &&
        Objects.equals(this.minRatingValue, collateralTypeDTO.minRatingValue) &&
        Objects.equals(this.profileNo, collateralTypeDTO.profileNo) &&
        Objects.equals(this.statusId, collateralTypeDTO.statusId) &&
        Objects.equals(this.tripartyAgentCode, collateralTypeDTO.tripartyAgentCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(categoryId, collateralType, collateralTypeId, eqlCollateralType, extCollateralType, minRatingValue, profileNo, statusId, tripartyAgentCode);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CollateralTypeDTO {\n");
    
    sb.append("    categoryId: ").append(toIndentedString(categoryId)).append("\n");
    sb.append("    collateralType: ").append(toIndentedString(collateralType)).append("\n");
    sb.append("    collateralTypeId: ").append(toIndentedString(collateralTypeId)).append("\n");
    sb.append("    eqlCollateralType: ").append(toIndentedString(eqlCollateralType)).append("\n");
    sb.append("    extCollateralType: ").append(toIndentedString(extCollateralType)).append("\n");
    sb.append("    minRatingValue: ").append(toIndentedString(minRatingValue)).append("\n");
    sb.append("    profileNo: ").append(toIndentedString(profileNo)).append("\n");
    sb.append("    statusId: ").append(toIndentedString(statusId)).append("\n");
    sb.append("    tripartyAgentCode: ").append(toIndentedString(tripartyAgentCode)).append("\n");
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

