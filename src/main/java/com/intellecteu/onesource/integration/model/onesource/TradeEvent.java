package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeEvent {

    @JsonIgnore
    private Long id;
    private String eventId;
    private EventType eventType;
    @JsonProperty("eventDateTime")
    private LocalDateTime eventDateTime;
    private String resourceUri;
    private ProcessingStatus processingStatus;

}
