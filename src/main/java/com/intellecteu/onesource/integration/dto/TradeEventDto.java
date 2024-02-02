package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeEventDto {

    private Long id;
    @JsonProperty("eventId")
    private Long eventId;
    @JsonProperty("eventType")
    private EventType eventType;
    @JsonProperty("eventDateTime")
    private LocalDateTime eventDatetime;
    @JsonProperty("resourceUri")
    private String resourceUri;
    private ProcessingStatus processingStatus = ProcessingStatus.CREATED;
}
