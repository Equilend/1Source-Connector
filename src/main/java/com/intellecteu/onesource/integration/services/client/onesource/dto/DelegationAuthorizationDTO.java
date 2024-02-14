/*
 * 1Source Ledger API
 * 1Source Ledger API provides client access to the 1Source Ledger. You can find out more about 1Source at [https://equilend.com](https://equilend.com).  This specification is work in progress. The design is meant to model the securities lending life cycle in as clean a way as possible while being robust enough to easily translate to ISLA CDM workflows and data model.  API specification is the intellectual property of EquiLend LLC and should not be copied or disseminated in any way. 
 *
 * OpenAPI spec version: 1.0.4
 * Contact: 1source_help@equilend.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package com.intellecteu.onesource.integration.services.client.onesource.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
/**
 * DelegationAuthorizationDTO
 */

@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2024-01-22T10:16:28.187392600Z[Europe/London]")

public class DelegationAuthorizationDTO {
  @JsonProperty("authorizationType")
  private DelegationAuthorizationTypeDTO authorizationType = null;

  public DelegationAuthorizationDTO authorizationType(DelegationAuthorizationTypeDTO authorizationType) {
    this.authorizationType = authorizationType;
    return this;
  }

   /**
   * Get authorizationType
   * @return authorizationType
  **/
  @Schema(required = true, description = "")
  public DelegationAuthorizationTypeDTO getAuthorizationType() {
    return authorizationType;
  }

  public void setAuthorizationType(DelegationAuthorizationTypeDTO authorizationType) {
    this.authorizationType = authorizationType;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DelegationAuthorizationDTO delegationAuthorization = (DelegationAuthorizationDTO) o;
    return Objects.equals(this.authorizationType, delegationAuthorization.authorizationType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(authorizationType);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DelegationAuthorizationDTO {\n");
    
    sb.append("    authorizationType: ").append(toIndentedString(authorizationType)).append("\n");
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
