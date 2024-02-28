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

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * PricehistKeyDTO
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-01-07T21:15:14.632Z")
public class PricehistKeyDTO {

    @JsonProperty("__qualifiedName")
    private String qualifiedName = null;

    @JsonProperty("currencyId")
    private Integer currencyId = null;

    @JsonProperty("priceDate")
    private LocalDateTime priceDate = null;

    @JsonProperty("refId")
    private Integer refId = null;

    @JsonProperty("securityId")
    private Long securityId = null;

    public PricehistKeyDTO qualifiedName(String qualifiedName) {
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

    public PricehistKeyDTO currencyId(Integer currencyId) {
        this.currencyId = currencyId;
        return this;
    }

    /**
     * Get currencyId
     *
     * @return currencyId
     **/
    @ApiModelProperty(value = "")
    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public PricehistKeyDTO priceDate(LocalDateTime priceDate) {
        this.priceDate = priceDate;
        return this;
    }

    /**
     * Get priceDate
     *
     * @return priceDate
     **/
    @ApiModelProperty(value = "")
    public LocalDateTime getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(LocalDateTime priceDate) {
        this.priceDate = priceDate;
    }

    public PricehistKeyDTO refId(Integer refId) {
        this.refId = refId;
        return this;
    }

    /**
     * Get refId
     *
     * @return refId
     **/
    @ApiModelProperty(value = "")
    public Integer getRefId() {
        return refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    public PricehistKeyDTO securityId(Long securityId) {
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
        PricehistKeyDTO pricehistKeyDTO = (PricehistKeyDTO) o;
        return Objects.equals(this.qualifiedName, pricehistKeyDTO.qualifiedName) &&
            Objects.equals(this.currencyId, pricehistKeyDTO.currencyId) &&
            Objects.equals(this.priceDate, pricehistKeyDTO.priceDate) &&
            Objects.equals(this.refId, pricehistKeyDTO.refId) &&
            Objects.equals(this.securityId, pricehistKeyDTO.securityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName, currencyId, priceDate, refId, securityId);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PricehistKeyDTO {\n");

        sb.append("    qualifiedName: ").append(toIndentedString(qualifiedName)).append("\n");
        sb.append("    currencyId: ").append(toIndentedString(currencyId)).append("\n");
        sb.append("    priceDate: ").append(toIndentedString(priceDate)).append("\n");
        sb.append("    refId: ").append(toIndentedString(refId)).append("\n");
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

