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
 * PricehistDTO
 */

public class PricehistDTO {
  @JsonProperty("askPrice")
  private Double askPrice = null;

  @JsonProperty("bidPrice")
  private Double bidPrice = null;

  @JsonProperty("cleanPrice")
  private Double cleanPrice = null;

  @JsonProperty("comments")
  private String comments = null;

  @JsonProperty("defaultPrice")
  private Long defaultPrice = null;

  @JsonProperty("dirtyPrice")
  private Double dirtyPrice = null;

  @JsonProperty("id")
  private PricehistKeyDTO id = null;

  @JsonProperty("isDirty")
  private Integer isDirty = null;

  @JsonProperty("lastModTs")
  private LocalDateTime lastModTs = null;

  @JsonProperty("lastModUserId")
  private Long lastModUserId = null;

  @JsonProperty("lastPrice")
  private Double lastPrice = null;

  @JsonProperty("price")
  private Double price = null;

  public PricehistDTO askPrice(Double askPrice) {
    this.askPrice = askPrice;
    return this;
  }

   /**
   * Get askPrice
   * @return askPrice
  **/
  @ApiModelProperty(value = "")
  public Double getAskPrice() {
    return askPrice;
  }

  public void setAskPrice(Double askPrice) {
    this.askPrice = askPrice;
  }

  public PricehistDTO bidPrice(Double bidPrice) {
    this.bidPrice = bidPrice;
    return this;
  }

   /**
   * Get bidPrice
   * @return bidPrice
  **/
  @ApiModelProperty(value = "")
  public Double getBidPrice() {
    return bidPrice;
  }

  public void setBidPrice(Double bidPrice) {
    this.bidPrice = bidPrice;
  }

  public PricehistDTO cleanPrice(Double cleanPrice) {
    this.cleanPrice = cleanPrice;
    return this;
  }

   /**
   * Get cleanPrice
   * @return cleanPrice
  **/
  @ApiModelProperty(value = "")
  public Double getCleanPrice() {
    return cleanPrice;
  }

  public void setCleanPrice(Double cleanPrice) {
    this.cleanPrice = cleanPrice;
  }

  public PricehistDTO comments(String comments) {
    this.comments = comments;
    return this;
  }

   /**
   * Get comments
   * @return comments
  **/
  @ApiModelProperty(value = "")
  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public PricehistDTO defaultPrice(Long defaultPrice) {
    this.defaultPrice = defaultPrice;
    return this;
  }

   /**
   * Get defaultPrice
   * @return defaultPrice
  **/
  @ApiModelProperty(value = "")
  public Long getDefaultPrice() {
    return defaultPrice;
  }

  public void setDefaultPrice(Long defaultPrice) {
    this.defaultPrice = defaultPrice;
  }

  public PricehistDTO dirtyPrice(Double dirtyPrice) {
    this.dirtyPrice = dirtyPrice;
    return this;
  }

   /**
   * Get dirtyPrice
   * @return dirtyPrice
  **/
  @ApiModelProperty(value = "")
  public Double getDirtyPrice() {
    return dirtyPrice;
  }

  public void setDirtyPrice(Double dirtyPrice) {
    this.dirtyPrice = dirtyPrice;
  }

  public PricehistDTO id(PricehistKeyDTO id) {
    this.id = id;
    return this;
  }

   /**
   * Get id
   * @return id
  **/
  @ApiModelProperty(value = "")
  public PricehistKeyDTO getId() {
    return id;
  }

  public void setId(PricehistKeyDTO id) {
    this.id = id;
  }

  public PricehistDTO isDirty(Integer isDirty) {
    this.isDirty = isDirty;
    return this;
  }

   /**
   * Get isDirty
   * @return isDirty
  **/
  @ApiModelProperty(value = "")
  public Integer getIsDirty() {
    return isDirty;
  }

  public void setIsDirty(Integer isDirty) {
    this.isDirty = isDirty;
  }

  public PricehistDTO lastModTs(LocalDateTime lastModTs) {
    this.lastModTs = lastModTs;
    return this;
  }

   /**
   * Get lastModTs
   * @return lastModTs
  **/
  @ApiModelProperty(value = "")
  public LocalDateTime getLastModTs() {
    return lastModTs;
  }

  public void setLastModTs(LocalDateTime lastModTs) {
    this.lastModTs = lastModTs;
  }

  public PricehistDTO lastModUserId(Long lastModUserId) {
    this.lastModUserId = lastModUserId;
    return this;
  }

   /**
   * Get lastModUserId
   * @return lastModUserId
  **/
  @ApiModelProperty(value = "")
  public Long getLastModUserId() {
    return lastModUserId;
  }

  public void setLastModUserId(Long lastModUserId) {
    this.lastModUserId = lastModUserId;
  }

  public PricehistDTO lastPrice(Double lastPrice) {
    this.lastPrice = lastPrice;
    return this;
  }

   /**
   * Get lastPrice
   * @return lastPrice
  **/
  @ApiModelProperty(value = "")
  public Double getLastPrice() {
    return lastPrice;
  }

  public void setLastPrice(Double lastPrice) {
    this.lastPrice = lastPrice;
  }

  public PricehistDTO price(Double price) {
    this.price = price;
    return this;
  }

   /**
   * Get price
   * @return price
  **/
  @ApiModelProperty(value = "")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PricehistDTO pricehistDTO = (PricehistDTO) o;
    return Objects.equals(this.askPrice, pricehistDTO.askPrice) &&
        Objects.equals(this.bidPrice, pricehistDTO.bidPrice) &&
        Objects.equals(this.cleanPrice, pricehistDTO.cleanPrice) &&
        Objects.equals(this.comments, pricehistDTO.comments) &&
        Objects.equals(this.defaultPrice, pricehistDTO.defaultPrice) &&
        Objects.equals(this.dirtyPrice, pricehistDTO.dirtyPrice) &&
        Objects.equals(this.id, pricehistDTO.id) &&
        Objects.equals(this.isDirty, pricehistDTO.isDirty) &&
        Objects.equals(this.lastModTs, pricehistDTO.lastModTs) &&
        Objects.equals(this.lastModUserId, pricehistDTO.lastModUserId) &&
        Objects.equals(this.lastPrice, pricehistDTO.lastPrice) &&
        Objects.equals(this.price, pricehistDTO.price);
  }

  @Override
  public int hashCode() {
    return Objects.hash(askPrice, bidPrice, cleanPrice, comments, defaultPrice, dirtyPrice, id, isDirty, lastModTs, lastModUserId, lastPrice, price);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PricehistDTO {\n");
    
    sb.append("    askPrice: ").append(toIndentedString(askPrice)).append("\n");
    sb.append("    bidPrice: ").append(toIndentedString(bidPrice)).append("\n");
    sb.append("    cleanPrice: ").append(toIndentedString(cleanPrice)).append("\n");
    sb.append("    comments: ").append(toIndentedString(comments)).append("\n");
    sb.append("    defaultPrice: ").append(toIndentedString(defaultPrice)).append("\n");
    sb.append("    dirtyPrice: ").append(toIndentedString(dirtyPrice)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    isDirty: ").append(toIndentedString(isDirty)).append("\n");
    sb.append("    lastModTs: ").append(toIndentedString(lastModTs)).append("\n");
    sb.append("    lastModUserId: ").append(toIndentedString(lastModUserId)).append("\n");
    sb.append("    lastPrice: ").append(toIndentedString(lastPrice)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
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

