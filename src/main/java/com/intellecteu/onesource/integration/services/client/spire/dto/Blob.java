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
 * Blob
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-01-07T21:15:14.632Z")
public class Blob {
  @JsonProperty("binaryStream")
  private Long binaryStream = null;

  public Blob binaryStream(Long binaryStream) {
    this.binaryStream = binaryStream;
    return this;
  }

   /**
   * Get binaryStream
   * @return binaryStream
  **/
  @ApiModelProperty(value = "")
  public Long getBinaryStream() {
    return binaryStream;
  }

  public void setBinaryStream(Long binaryStream) {
    this.binaryStream = binaryStream;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Blob blob = (Blob) o;
    return Objects.equals(this.binaryStream, blob.binaryStream);
  }

  @Override
  public int hashCode() {
    return Objects.hash(binaryStream);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Blob {\n");
    
    sb.append("    binaryStream: ").append(toIndentedString(binaryStream)).append("\n");
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

