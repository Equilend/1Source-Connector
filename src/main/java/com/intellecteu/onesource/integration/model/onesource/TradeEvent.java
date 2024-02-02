package com.intellecteu.onesource.integration.model.onesource;

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

    private Long id;
    private Long eventId;
    private EventType eventType;
    private LocalDateTime eventDatetime;
    private String resourceUri;
    private ProcessingStatus processingStatus;

}
