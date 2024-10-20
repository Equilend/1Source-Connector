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
 * CalendarDTO
 */

public class CalendarDTO {
  @JsonProperty("calendarId")
  private Integer calendarId = null;

  @JsonProperty("calendarName")
  private String calendarName = null;

  @JsonProperty("timezone")
  private String timezone = null;

  public CalendarDTO calendarId(Integer calendarId) {
    this.calendarId = calendarId;
    return this;
  }

   /**
   * Get calendarId
   * @return calendarId
  **/
  @ApiModelProperty(value = "")
  public Integer getCalendarId() {
    return calendarId;
  }

  public void setCalendarId(Integer calendarId) {
    this.calendarId = calendarId;
  }

  public CalendarDTO calendarName(String calendarName) {
    this.calendarName = calendarName;
    return this;
  }

   /**
   * Get calendarName
   * @return calendarName
  **/
  @ApiModelProperty(value = "")
  public String getCalendarName() {
    return calendarName;
  }

  public void setCalendarName(String calendarName) {
    this.calendarName = calendarName;
  }

  public CalendarDTO timezone(String timezone) {
    this.timezone = timezone;
    return this;
  }

   /**
   * Get timezone
   * @return timezone
  **/
  @ApiModelProperty(value = "")
  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CalendarDTO calendarDTO = (CalendarDTO) o;
    return Objects.equals(this.calendarId, calendarDTO.calendarId) &&
        Objects.equals(this.calendarName, calendarDTO.calendarName) &&
        Objects.equals(this.timezone, calendarDTO.timezone);
  }

  @Override
  public int hashCode() {
    return Objects.hash(calendarId, calendarName, timezone);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CalendarDTO {\n");
    
    sb.append("    calendarId: ").append(toIndentedString(calendarId)).append("\n");
    sb.append("    calendarName: ").append(toIndentedString(calendarName)).append("\n");
    sb.append("    timezone: ").append(toIndentedString(timezone)).append("\n");
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

