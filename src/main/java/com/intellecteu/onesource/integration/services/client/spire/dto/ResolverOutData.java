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
 * ResolverOutData
 */

public class ResolverOutData {
  @JsonProperty("parentField")
  private String parentField = null;

  @JsonProperty("requiredFields")
  private List<String> requiredFields = null;

  @JsonProperty("resolverParameters")
  private BaseDTO resolverParameters = null;

  /**
   * Gets or Sets resolverType
   */
  public enum ResolverTypeEnum {
    ACCOUNT("ACCOUNT"),
    
    ACCOUNTACCOUNT("ACCOUNTACCOUNT"),
    
    ACCOUNT_TYPE("ACCOUNT_TYPE"),
    
    CURRENCY("CURRENCY"),
    
    DEPOSITORY("DEPOSITORY"),
    
    SECURITY("SECURITY"),
    
    SECURITY_REF("SECURITY_REF"),
    
    STATUS("STATUS"),
    
    USER("USER"),
    
    VSECURITY("VSECURITY"),
    
    SECURITYHIST("SECURITYHIST"),
    
    POSITIONTYPE("POSITIONTYPE"),
    
    TRADETYPE("TRADETYPE"),
    
    COLLATERALTYPE("COLLATERALTYPE"),
    
    RECALL_POSITION("RECALL_POSITION"),
    
    RECALL_ALLOCATION("RECALL_ALLOCATION"),
    
    LENDING_TO_OMNI("LENDING_TO_OMNI"),
    
    INDEX("INDEX"),
    
    ACCOUNT_TREE("ACCOUNT_TREE"),
    
    EXPOSURE("EXPOSURE"),
    
    BASIS("BASIS"),
    
    CORPACTIONTYPE("CORPACTIONTYPE"),
    
    CORPACTION("CORPACTION"),
    
    COUNTERPARTY_TO_RECALL("COUNTERPARTY_TO_RECALL"),
    
    POSITONREF_TO_POSITION("POSITONREF_TO_POSITION"),
    
    CALENDAR("CALENDAR"),
    
    COUNTERPARTY_FIRM("COUNTERPARTY_FIRM"),
    
    STRATEGY("STRATEGY"),
    
    ALLOCATION_REASON("ALLOCATION_REASON"),
    
    TRADING_DESK("TRADING_DESK"),
    
    LENDING_ACCOUNT("LENDING_ACCOUNT"),
    
    COLLATERAL_ACCOUNT("COLLATERAL_ACCOUNT"),
    
    CUSTODIAN_ACCOUNT("CUSTODIAN_ACCOUNT"),
    
    ACCOUNT_GROUP_ACCOUNT("ACCOUNT_GROUP_ACCOUNT"),
    
    ACCOUNT_GROUP_ACCOUNT_TO_LENDING_ACCOUNT("ACCOUNT_GROUP_ACCOUNT_TO_LENDING_ACCOUNT"),
    
    MATCH_GROUP("MATCH_GROUP"),
    
    POSITION_MATCH_GROUP("POSITION_MATCH_GROUP"),
    
    CATEGORY_TO_SECURITY("CATEGORY_TO_SECURITY"),
    
    RULE_TYPE("RULE_TYPE"),
    
    CATEGORY("CATEGORY"),
    
    COUNTRY("COUNTRY"),
    
    ISSUERCOUNTRY_TO_SECURITY("ISSUERCOUNTRY_TO_SECURITY"),
    
    CATEGORY_TO_CHILD_CATEGORY("CATEGORY_TO_CHILD_CATEGORY"),
    
    USER_ENTITLEMENT("USER_ENTITLEMENT"),
    
    RULE_STATUS("RULE_STATUS"),
    
    LEVEL2ACCOUNTGROUP("LEVEL2ACCOUNTGROUP"),
    
    STRATEGY_NAME_TO_LENDING_ACCOUNT("STRATEGY_NAME_TO_LENDING_ACCOUNT"),
    
    SUB_ACCOUNT_DETAIL_ACCOUNT("SUB_ACCOUNT_DETAIL_ACCOUNT"),
    
    WASH_AND_LENDING_ACCOUNT("WASH_AND_LENDING_ACCOUNT"),
    
    NOOP("NOOP"),
    
    LENDER_ACCOUNT("LENDER_ACCOUNT"),
    
    COUNTERPARTY_GROUP_ACCOUNT("COUNTERPARTY_GROUP_ACCOUNT"),
    
    MIC("MIC"),
    
    LEGAL_ENTITY_ACCOUNT_GROUP("LEGAL_ENTITY_ACCOUNT_GROUP"),
    
    CHILD_FIRM_ACCOUNT("CHILD_FIRM_ACCOUNT"),
    
    SECURITY_TO_CORPACTION("SECURITY_TO_CORPACTION"),
    
    SWIFT_BIC("SWIFT_BIC"),
    
    COUNTERPARTY("COUNTERPARTY"),
    
    MARKET_INDEX("MARKET_INDEX"),
    
    LENDABLE_VALUE("LENDABLE_VALUE"),
    
    NEW_OPPORTUNITY_COST("NEW_OPPORTUNITY_COST"),
    
    TOTAL_ONLENT_VALUE("TOTAL_ONLENT_VALUE"),
    
    DXDATA_FIELDS("DXDATA_FIELDS"),
    
    ACCOUNT_TO_COMPRESS_ACCOUNT("ACCOUNT_TO_COMPRESS_ACCOUNT");

    private String value;

    ResolverTypeEnum(String value) {
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
    public static ResolverTypeEnum fromValue(String value) {
      for (ResolverTypeEnum b : ResolverTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("resolverType")
  private ResolverTypeEnum resolverType = null;

  @JsonProperty("targetKey")
  private String targetKey = null;

  public ResolverOutData parentField(String parentField) {
    this.parentField = parentField;
    return this;
  }

   /**
   * Get parentField
   * @return parentField
  **/
  @ApiModelProperty(value = "")
  public String getParentField() {
    return parentField;
  }

  public void setParentField(String parentField) {
    this.parentField = parentField;
  }

  public ResolverOutData requiredFields(List<String> requiredFields) {
    this.requiredFields = requiredFields;
    return this;
  }

  public ResolverOutData addRequiredFieldsItem(String requiredFieldsItem) {
    if (this.requiredFields == null) {
      this.requiredFields = new ArrayList<>();
    }
    this.requiredFields.add(requiredFieldsItem);
    return this;
  }

   /**
   * Get requiredFields
   * @return requiredFields
  **/
  @ApiModelProperty(value = "")
  public List<String> getRequiredFields() {
    return requiredFields;
  }

  public void setRequiredFields(List<String> requiredFields) {
    this.requiredFields = requiredFields;
  }

  public ResolverOutData resolverParameters(BaseDTO resolverParameters) {
    this.resolverParameters = resolverParameters;
    return this;
  }

   /**
   * Get resolverParameters
   * @return resolverParameters
  **/
  @ApiModelProperty(value = "")
  public BaseDTO getResolverParameters() {
    return resolverParameters;
  }

  public void setResolverParameters(BaseDTO resolverParameters) {
    this.resolverParameters = resolverParameters;
  }

  public ResolverOutData resolverType(ResolverTypeEnum resolverType) {
    this.resolverType = resolverType;
    return this;
  }

   /**
   * Get resolverType
   * @return resolverType
  **/
  @ApiModelProperty(value = "")
  public ResolverTypeEnum getResolverType() {
    return resolverType;
  }

  public void setResolverType(ResolverTypeEnum resolverType) {
    this.resolverType = resolverType;
  }

  public ResolverOutData targetKey(String targetKey) {
    this.targetKey = targetKey;
    return this;
  }

   /**
   * Get targetKey
   * @return targetKey
  **/
  @ApiModelProperty(value = "")
  public String getTargetKey() {
    return targetKey;
  }

  public void setTargetKey(String targetKey) {
    this.targetKey = targetKey;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResolverOutData resolverOutData = (ResolverOutData) o;
    return Objects.equals(this.parentField, resolverOutData.parentField) &&
        Objects.equals(this.requiredFields, resolverOutData.requiredFields) &&
        Objects.equals(this.resolverParameters, resolverOutData.resolverParameters) &&
        Objects.equals(this.resolverType, resolverOutData.resolverType) &&
        Objects.equals(this.targetKey, resolverOutData.targetKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parentField, requiredFields, resolverParameters, resolverType, targetKey);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResolverOutData {\n");
    
    sb.append("    parentField: ").append(toIndentedString(parentField)).append("\n");
    sb.append("    requiredFields: ").append(toIndentedString(requiredFields)).append("\n");
    sb.append("    resolverParameters: ").append(toIndentedString(resolverParameters)).append("\n");
    sb.append("    resolverType: ").append(toIndentedString(resolverType)).append("\n");
    sb.append("    targetKey: ").append(toIndentedString(targetKey)).append("\n");
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

