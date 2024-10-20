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
import java.util.Objects;

/**
 * NQueryTuple
 */

public class NQueryTuple {
  @JsonProperty("lValue")
  private Object lValue = null;

  /**
   * Gets or Sets operator
   */
  public enum OperatorEnum {
    BETWEEN("BETWEEN"),
    
    EQUALS("EQUALS"),
    
    GREATER_THAN("GREATER_THAN"),
    
    GREATER_THAN_EQUALS("GREATER_THAN_EQUALS"),
    
    IN("IN"),
    
    IS_NULL("IS_NULL"),
    
    LESS_THAN("LESS_THAN"),
    
    LESS_THAN_EQUALS("LESS_THAN_EQUALS"),
    
    LIKE("LIKE"),
    
    NOT_EQUALS("NOT_EQUALS"),
    
    NOT_IN("NOT_IN"),
    
    NOT_LIKE("NOT_LIKE"),
    
    NOT_NULL("NOT_NULL");

    private String value;

    OperatorEnum(String value) {
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
    public static OperatorEnum fromValue(String value) {
      for (OperatorEnum b : OperatorEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("operator")
  private OperatorEnum operator = null;

  @JsonProperty("rValue1")
  private Object rValue1 = null;

  @JsonProperty("rValue2")
  private Object rValue2 = null;

  public NQueryTuple lValue(Object lValue) {
    this.lValue = lValue;
    return this;
  }

   /**
   * Get lValue
   * @return lValue
  **/
  @ApiModelProperty(value = "")
  public Object getLValue() {
    return lValue;
  }

  public void setLValue(Object lValue) {
    this.lValue = lValue;
  }

  public NQueryTuple operator(OperatorEnum operator) {
    this.operator = operator;
    return this;
  }

   /**
   * Get operator
   * @return operator
  **/
  @ApiModelProperty(value = "")
  public OperatorEnum getOperator() {
    return operator;
  }

  public void setOperator(OperatorEnum operator) {
    this.operator = operator;
  }

  public NQueryTuple rValue1(Object rValue1) {
    this.rValue1 = rValue1;
    return this;
  }

   /**
   * Get rValue1
   * @return rValue1
  **/
  @ApiModelProperty(value = "")
  public Object getRValue1() {
    return rValue1;
  }

  public void setRValue1(Object rValue1) {
    this.rValue1 = rValue1;
  }

  public NQueryTuple rValue2(Object rValue2) {
    this.rValue2 = rValue2;
    return this;
  }

   /**
   * Get rValue2
   * @return rValue2
  **/
  @ApiModelProperty(value = "")
  public Object getRValue2() {
    return rValue2;
  }

  public void setRValue2(Object rValue2) {
    this.rValue2 = rValue2;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NQueryTuple nqueryTuple = (NQueryTuple) o;
    return Objects.equals(this.lValue, nqueryTuple.lValue) &&
        Objects.equals(this.operator, nqueryTuple.operator) &&
        Objects.equals(this.rValue1, nqueryTuple.rValue1) &&
        Objects.equals(this.rValue2, nqueryTuple.rValue2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lValue, operator, rValue1, rValue2);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NQueryTuple {\n");
    
    sb.append("    lValue: ").append(toIndentedString(lValue)).append("\n");
    sb.append("    operator: ").append(toIndentedString(operator)).append("\n");
    sb.append("    rValue1: ").append(toIndentedString(rValue1)).append("\n");
    sb.append("    rValue2: ").append(toIndentedString(rValue2)).append("\n");
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

