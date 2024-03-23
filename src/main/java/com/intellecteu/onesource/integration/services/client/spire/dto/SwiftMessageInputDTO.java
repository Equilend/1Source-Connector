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
 * SwiftMessageInputDTO
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-03-23T11:29:17.054Z")
public class SwiftMessageInputDTO {
  @JsonProperty("SWIFTInboxMessageId")
  private String swIFTInboxMessageId = null;

  @JsonProperty("base64EncodedSWIFTMsg")
  private String base64EncodedSWIFTMsg = null;

  public SwiftMessageInputDTO swIFTInboxMessageId(String swIFTInboxMessageId) {
    this.swIFTInboxMessageId = swIFTInboxMessageId;
    return this;
  }

   /**
   * Get swIFTInboxMessageId
   * @return swIFTInboxMessageId
  **/
  @ApiModelProperty(required = true, value = "")
  public String getSwIFTInboxMessageId() {
    return swIFTInboxMessageId;
  }

  public void setSwIFTInboxMessageId(String swIFTInboxMessageId) {
    this.swIFTInboxMessageId = swIFTInboxMessageId;
  }

  public SwiftMessageInputDTO base64EncodedSWIFTMsg(String base64EncodedSWIFTMsg) {
    this.base64EncodedSWIFTMsg = base64EncodedSWIFTMsg;
    return this;
  }

   /**
   * Get base64EncodedSWIFTMsg
   * @return base64EncodedSWIFTMsg
  **/
  @ApiModelProperty(required = true, value = "")
  public String getBase64EncodedSWIFTMsg() {
    return base64EncodedSWIFTMsg;
  }

  public void setBase64EncodedSWIFTMsg(String base64EncodedSWIFTMsg) {
    this.base64EncodedSWIFTMsg = base64EncodedSWIFTMsg;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SwiftMessageInputDTO swiftMessageInputDTO = (SwiftMessageInputDTO) o;
    return Objects.equals(this.swIFTInboxMessageId, swiftMessageInputDTO.swIFTInboxMessageId) &&
        Objects.equals(this.base64EncodedSWIFTMsg, swiftMessageInputDTO.base64EncodedSWIFTMsg);
  }

  @Override
  public int hashCode() {
    return Objects.hash(swIFTInboxMessageId, base64EncodedSWIFTMsg);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SwiftMessageInputDTO {\n");
    
    sb.append("    swIFTInboxMessageId: ").append(toIndentedString(swIFTInboxMessageId)).append("\n");
    sb.append("    base64EncodedSWIFTMsg: ").append(toIndentedString(base64EncodedSWIFTMsg)).append("\n");
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

