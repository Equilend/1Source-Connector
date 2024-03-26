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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * LRALSuggestionsDTO
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-03-23T11:29:17.054Z")
public class LRALSuggestionsDTO {
  @JsonProperty("amount")
  private Double amount = null;

  @JsonProperty("rowCount")
  private Integer rowCount = null;

  @JsonProperty("suggestions")
  private List<LRALSuggestionDTO> suggestions = null;

  public LRALSuggestionsDTO amount(Double amount) {
    this.amount = amount;
    return this;
  }

   /**
   * Get amount
   * @return amount
  **/
  @ApiModelProperty(value = "")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public LRALSuggestionsDTO rowCount(Integer rowCount) {
    this.rowCount = rowCount;
    return this;
  }

   /**
   * Get rowCount
   * @return rowCount
  **/
  @ApiModelProperty(value = "")
  public Integer getRowCount() {
    return rowCount;
  }

  public void setRowCount(Integer rowCount) {
    this.rowCount = rowCount;
  }

  public LRALSuggestionsDTO suggestions(List<LRALSuggestionDTO> suggestions) {
    this.suggestions = suggestions;
    return this;
  }

  public LRALSuggestionsDTO addSuggestionsItem(LRALSuggestionDTO suggestionsItem) {
    if (this.suggestions == null) {
      this.suggestions = new ArrayList<>();
    }
    this.suggestions.add(suggestionsItem);
    return this;
  }

   /**
   * Get suggestions
   * @return suggestions
  **/
  @ApiModelProperty(value = "")
  public List<LRALSuggestionDTO> getSuggestions() {
    return suggestions;
  }

  public void setSuggestions(List<LRALSuggestionDTO> suggestions) {
    this.suggestions = suggestions;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LRALSuggestionsDTO lrALSuggestionsDTO = (LRALSuggestionsDTO) o;
    return Objects.equals(this.amount, lrALSuggestionsDTO.amount) &&
        Objects.equals(this.rowCount, lrALSuggestionsDTO.rowCount) &&
        Objects.equals(this.suggestions, lrALSuggestionsDTO.suggestions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, rowCount, suggestions);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LRALSuggestionsDTO {\n");
    
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    rowCount: ").append(toIndentedString(rowCount)).append("\n");
    sb.append("    suggestions: ").append(toIndentedString(suggestions)).append("\n");
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

