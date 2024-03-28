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
import java.time.LocalDateTime;
import java.util.Objects;
/**
 * EventDTO
 */



public class EventDTO {
  @JsonProperty("eventId")
  private Integer eventId = null;

  @JsonProperty("eventType")
  private EventTypeDTO eventType = null;

  @JsonProperty("eventDatetime")
  private LocalDateTime eventDatetime = null;

  @JsonProperty("resourceUri")
  private String resourceUri = null;

  public EventDTO eventId(Integer eventId) {
    this.eventId = eventId;
    return this;
  }

   /**
   * Get eventId
   * @return eventId
  **/
  @Schema(required = true, description = "")
  public Integer getEventId() {
    return eventId;
  }

  public void setEventId(Integer eventId) {
    this.eventId = eventId;
  }

  public EventDTO eventType(EventTypeDTO eventType) {
    this.eventType = eventType;
    return this;
  }

   /**
   * Get eventType
   * @return eventType
  **/
  @Schema(required = true, description = "")
  public EventTypeDTO getEventType() {
    return eventType;
  }

  public void setEventType(EventTypeDTO eventType) {
    this.eventType = eventType;
  }

  public EventDTO eventDatetime(LocalDateTime eventDatetime) {
    this.eventDatetime = eventDatetime;
    return this;
  }

   /**
   * Get eventDatetime
   * @return eventDatetime
  **/
  @Schema(description = "")
  public LocalDateTime getEventDatetime() {
    return eventDatetime;
  }

  public void setEventDatetime(LocalDateTime eventDatetime) {
    this.eventDatetime = eventDatetime;
  }

  public EventDTO resourceUri(String resourceUri) {
    this.resourceUri = resourceUri;
    return this;
  }

   /**
   * Get resourceUri
   * @return resourceUri
  **/
  @Schema(description = "")
  public String getResourceUri() {
    return resourceUri;
  }

  public void setResourceUri(String resourceUri) {
    this.resourceUri = resourceUri;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EventDTO event = (EventDTO) o;
    return Objects.equals(this.eventId, event.eventId) &&
        Objects.equals(this.eventType, event.eventType) &&
        Objects.equals(this.eventDatetime, event.eventDatetime) &&
        Objects.equals(this.resourceUri, event.resourceUri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventId, eventType, eventDatetime, resourceUri);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EventDTO {\n");
    
    sb.append("    eventId: ").append(toIndentedString(eventId)).append("\n");
    sb.append("    eventType: ").append(toIndentedString(eventType)).append("\n");
    sb.append("    eventDatetime: ").append(toIndentedString(eventDatetime)).append("\n");
    sb.append("    resourceUri: ").append(toIndentedString(resourceUri)).append("\n");
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
