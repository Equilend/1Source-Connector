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
 * CouponPayment
 */

public class CouponPayment {
  @JsonProperty("coupon")
  private Double coupon = null;

  @JsonProperty("paymentDate")
  private LocalDateTime paymentDate = null;

  @JsonProperty("quantity")
  private Double quantity = null;

  @JsonProperty("securityId")
  private Integer securityId = null;

  public CouponPayment coupon(Double coupon) {
    this.coupon = coupon;
    return this;
  }

   /**
   * Get coupon
   * @return coupon
  **/
  @ApiModelProperty(value = "")
  public Double getCoupon() {
    return coupon;
  }

  public void setCoupon(Double coupon) {
    this.coupon = coupon;
  }

  public CouponPayment paymentDate(LocalDateTime paymentDate) {
    this.paymentDate = paymentDate;
    return this;
  }

   /**
   * Get paymentDate
   * @return paymentDate
  **/
  @ApiModelProperty(value = "")
  public LocalDateTime getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(LocalDateTime paymentDate) {
    this.paymentDate = paymentDate;
  }

  public CouponPayment quantity(Double quantity) {
    this.quantity = quantity;
    return this;
  }

   /**
   * Get quantity
   * @return quantity
  **/
  @ApiModelProperty(value = "")
  public Double getQuantity() {
    return quantity;
  }

  public void setQuantity(Double quantity) {
    this.quantity = quantity;
  }

  public CouponPayment securityId(Integer securityId) {
    this.securityId = securityId;
    return this;
  }

   /**
   * Get securityId
   * @return securityId
  **/
  @ApiModelProperty(value = "")
  public Integer getSecurityId() {
    return securityId;
  }

  public void setSecurityId(Integer securityId) {
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
    CouponPayment couponPayment = (CouponPayment) o;
    return Objects.equals(this.coupon, couponPayment.coupon) &&
        Objects.equals(this.paymentDate, couponPayment.paymentDate) &&
        Objects.equals(this.quantity, couponPayment.quantity) &&
        Objects.equals(this.securityId, couponPayment.securityId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(coupon, paymentDate, quantity, securityId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CouponPayment {\n");
    
    sb.append("    coupon: ").append(toIndentedString(coupon)).append("\n");
    sb.append("    paymentDate: ").append(toIndentedString(paymentDate)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    securityId: ").append(toIndentedString(securityId)).append("\n");
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

