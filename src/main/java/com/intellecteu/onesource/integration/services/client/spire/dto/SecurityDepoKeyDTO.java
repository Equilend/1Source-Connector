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
 * SecurityDepoKeyDTO
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-01-07T21:15:14.632Z")
public class SecurityDepoKeyDTO {

    @JsonProperty("depoId")
    private Integer depoId = null;

    @JsonProperty("securityId")
    private Long securityId = null;

    public SecurityDepoKeyDTO depoId(Integer depoId) {
        this.depoId = depoId;
        return this;
    }

    /**
     * Get depoId
     *
     * @return depoId
     **/
    @ApiModelProperty(value = "")
    public Integer getDepoId() {
        return depoId;
    }

    public void setDepoId(Integer depoId) {
        this.depoId = depoId;
    }

    public SecurityDepoKeyDTO securityId(Long securityId) {
        this.securityId = securityId;
        return this;
    }

    /**
     * Get securityId
     *
     * @return securityId
     **/
    @ApiModelProperty(value = "")
    public Long getSecurityId() {
        return securityId;
    }

    public void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SecurityDepoKeyDTO securityDepoKeyDTO = (SecurityDepoKeyDTO) o;
        return Objects.equals(this.depoId, securityDepoKeyDTO.depoId) &&
            Objects.equals(this.securityId, securityDepoKeyDTO.securityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(depoId, securityId);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SecurityDepoKeyDTO {\n");

        sb.append("    depoId: ").append(toIndentedString(depoId)).append("\n");
        sb.append("    securityId: ").append(toIndentedString(securityId)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

