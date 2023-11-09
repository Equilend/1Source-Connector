package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.EventType;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeEventDto {

    @JsonProperty("eventId")
    private Long eventId;
    @JsonProperty("eventType")
    private EventType eventType;
    @JsonProperty("eventDateTime")
    private LocalDateTime eventDatetime;
    @JsonProperty("resourceUri")
    private String resourceUri;
    private ProcessingStatus processingStatus = ProcessingStatus.NEW;
}
