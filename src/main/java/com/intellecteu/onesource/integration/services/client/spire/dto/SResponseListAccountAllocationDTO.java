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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * SResponseListAccountAllocationDTO
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-03-23T11:29:17.054Z")
public class SResponseListAccountAllocationDTO {
  @JsonProperty("data")
  private List<AccountAllocationDTO> data = null;

  @JsonProperty("errCode")
  private String errCode = null;

  @JsonProperty("error")
  private Boolean error = null;

  @JsonProperty("message")
  private String message = null;

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    ERROR("ERROR"),
    
    SUCCESS("SUCCESS");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("status")
  private StatusEnum status = null;

  @JsonProperty("success")
  private Boolean success = null;

  public SResponseListAccountAllocationDTO data(List<AccountAllocationDTO> data) {
    this.data = data;
    return this;
  }

  public SResponseListAccountAllocationDTO addDataItem(AccountAllocationDTO dataItem) {
    if (this.data == null) {
      this.data = new ArrayList<>();
    }
    this.data.add(dataItem);
    return this;
  }

   /**
   * Get data
   * @return data
  **/
  @ApiModelProperty(value = "")
  public List<AccountAllocationDTO> getData() {
    return data;
  }

  public void setData(List<AccountAllocationDTO> data) {
    this.data = data;
  }

  public SResponseListAccountAllocationDTO errCode(String errCode) {
    this.errCode = errCode;
    return this;
  }

   /**
   * Get errCode
   * @return errCode
  **/
  @ApiModelProperty(value = "")
  public String getErrCode() {
    return errCode;
  }

  public void setErrCode(String errCode) {
    this.errCode = errCode;
  }

  public SResponseListAccountAllocationDTO error(Boolean error) {
    this.error = error;
    return this;
  }

   /**
   * Get error
   * @return error
  **/
  @ApiModelProperty(value = "")
  public Boolean isError() {
    return error;
  }

  public void setError(Boolean error) {
    this.error = error;
  }

  public SResponseListAccountAllocationDTO message(String message) {
    this.message = message;
    return this;
  }

   /**
   * Get message
   * @return message
  **/
  @ApiModelProperty(value = "")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public SResponseListAccountAllocationDTO status(StatusEnum status) {
    this.status = status;
    return this;
  }

   /**
   * Get status
   * @return status
  **/
  @ApiModelProperty(value = "")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public SResponseListAccountAllocationDTO success(Boolean success) {
    this.success = success;
    return this;
  }

   /**
   * Get success
   * @return success
  **/
  @ApiModelProperty(value = "")
  public Boolean isSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SResponseListAccountAllocationDTO sresponseListAccountAllocationDTO = (SResponseListAccountAllocationDTO) o;
    return Objects.equals(this.data, sresponseListAccountAllocationDTO.data) &&
        Objects.equals(this.errCode, sresponseListAccountAllocationDTO.errCode) &&
        Objects.equals(this.error, sresponseListAccountAllocationDTO.error) &&
        Objects.equals(this.message, sresponseListAccountAllocationDTO.message) &&
        Objects.equals(this.status, sresponseListAccountAllocationDTO.status) &&
        Objects.equals(this.success, sresponseListAccountAllocationDTO.success);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, errCode, error, message, status, success);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SResponseListAccountAllocationDTO {\n");
    
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    errCode: ").append(toIndentedString(errCode)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    success: ").append(toIndentedString(success)).append("\n");
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

