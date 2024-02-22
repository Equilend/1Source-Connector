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
 * DeliveryStatusResponseDTO
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-02-20T15:00:33.025Z")
public class DeliveryStatusResponseDTO {
  @JsonProperty("SWIFTInboxMessageId")
  private String swIFTInboxMessageId = null;

  @JsonProperty("status")
  private String status = null;

  @JsonProperty("statusCd")
  private Integer statusCd = null;

  @JsonProperty("statusDesc")
  private String statusDesc = null;

  public DeliveryStatusResponseDTO swIFTInboxMessageId(String swIFTInboxMessageId) {
    this.swIFTInboxMessageId = swIFTInboxMessageId;
    return this;
  }

   /**
   * Get swIFTInboxMessageId
   * @return swIFTInboxMessageId
  **/
  @ApiModelProperty(value = "")
  public String getSwIFTInboxMessageId() {
    return swIFTInboxMessageId;
  }

  public void setSwIFTInboxMessageId(String swIFTInboxMessageId) {
    this.swIFTInboxMessageId = swIFTInboxMessageId;
  }

  public DeliveryStatusResponseDTO status(String status) {
    this.status = status;
    return this;
  }

   /**
   * Get status
   * @return status
  **/
  @ApiModelProperty(value = "")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public DeliveryStatusResponseDTO statusCd(Integer statusCd) {
    this.statusCd = statusCd;
    return this;
  }

   /**
   * Get statusCd
   * @return statusCd
  **/
  @ApiModelProperty(value = "")
  public Integer getStatusCd() {
    return statusCd;
  }

  public void setStatusCd(Integer statusCd) {
    this.statusCd = statusCd;
  }

  public DeliveryStatusResponseDTO statusDesc(String statusDesc) {
    this.statusDesc = statusDesc;
    return this;
  }

   /**
   * Get statusDesc
   * @return statusDesc
  **/
  @ApiModelProperty(value = "")
  public String getStatusDesc() {
    return statusDesc;
  }

  public void setStatusDesc(String statusDesc) {
    this.statusDesc = statusDesc;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeliveryStatusResponseDTO deliveryStatusResponseDTO = (DeliveryStatusResponseDTO) o;
    return Objects.equals(this.swIFTInboxMessageId, deliveryStatusResponseDTO.swIFTInboxMessageId) &&
        Objects.equals(this.status, deliveryStatusResponseDTO.status) &&
        Objects.equals(this.statusCd, deliveryStatusResponseDTO.statusCd) &&
        Objects.equals(this.statusDesc, deliveryStatusResponseDTO.statusDesc);
  }

  @Override
  public int hashCode() {
    return Objects.hash(swIFTInboxMessageId, status, statusCd, statusDesc);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryStatusResponseDTO {\n");
    
    sb.append("    swIFTInboxMessageId: ").append(toIndentedString(swIFTInboxMessageId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    statusCd: ").append(toIndentedString(statusCd)).append("\n");
    sb.append("    statusDesc: ").append(toIndentedString(statusDesc)).append("\n");
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

