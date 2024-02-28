/*
 * Spire-Ref-Data-Service
 * Spire-Ref-Data-APIs
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
import lombok.NoArgsConstructor;

/**
 * SwiftbicDTO
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-01-19T17:56:43.469+02:00")
@NoArgsConstructor
public class SwiftbicDTO {

    @JsonProperty("__qualifiedName")
    private String qualifiedName = null;

    @JsonProperty("bic")
    private String bic = null;

    @JsonProperty("branch")
    private String branch = null;

    @JsonProperty("devOrProd")
    private String devOrProd = null;

    @JsonProperty("institution")
    private String institution = null;

    @JsonProperty("swiftBicId")
    private Long swiftBicId = null;

    public SwiftbicDTO qualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
        return this;
    }

    /**
     * Get qualifiedName
     *
     * @return qualifiedName
     **/
    @ApiModelProperty(value = "")
    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public SwiftbicDTO bic(String bic) {
        this.bic = bic;
        return this;
    }

    /**
     * Get bic
     *
     * @return bic
     **/
    @ApiModelProperty(value = "")
    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public SwiftbicDTO branch(String branch) {
        this.branch = branch;
        return this;
    }

    /**
     * Get branch
     *
     * @return branch
     **/
    @ApiModelProperty(value = "")
    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public SwiftbicDTO devOrProd(String devOrProd) {
        this.devOrProd = devOrProd;
        return this;
    }

    /**
     * Get devOrProd
     *
     * @return devOrProd
     **/
    @ApiModelProperty(value = "")
    public String getDevOrProd() {
        return devOrProd;
    }

    public void setDevOrProd(String devOrProd) {
        this.devOrProd = devOrProd;
    }

    public SwiftbicDTO institution(String institution) {
        this.institution = institution;
        return this;
    }

    /**
     * Get institution
     *
     * @return institution
     **/
    @ApiModelProperty(value = "")
    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public SwiftbicDTO swiftBicId(Long swiftBicId) {
        this.swiftBicId = swiftBicId;
        return this;
    }

    /**
     * Get swiftBicId
     *
     * @return swiftBicId
     **/
    @ApiModelProperty(value = "")
    public Long getSwiftBicId() {
        return swiftBicId;
    }

    public void setSwiftBicId(Long swiftBicId) {
        this.swiftBicId = swiftBicId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SwiftbicDTO swiftbicDTO = (SwiftbicDTO) o;
        return Objects.equals(this.qualifiedName, swiftbicDTO.qualifiedName) &&
            Objects.equals(this.bic, swiftbicDTO.bic) &&
            Objects.equals(this.branch, swiftbicDTO.branch) &&
            Objects.equals(this.devOrProd, swiftbicDTO.devOrProd) &&
            Objects.equals(this.institution, swiftbicDTO.institution) &&
            Objects.equals(this.swiftBicId, swiftbicDTO.swiftBicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName, bic, branch, devOrProd, institution, swiftBicId);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SwiftbicDTO {\n");

        sb.append("    qualifiedName: ").append(toIndentedString(qualifiedName)).append("\n");
        sb.append("    bic: ").append(toIndentedString(bic)).append("\n");
        sb.append("    branch: ").append(toIndentedString(branch)).append("\n");
        sb.append("    devOrProd: ").append(toIndentedString(devOrProd)).append("\n");
        sb.append("    institution: ").append(toIndentedString(institution)).append("\n");
        sb.append("    swiftBicId: ").append(toIndentedString(swiftBicId)).append("\n");
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

    public SwiftbicDTO(String bic, String branch) {
        this.bic = bic;
        this.branch = branch;
    }
}

