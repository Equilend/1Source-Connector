package com.intellecteu.onesource.integration.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trade_event")
public class TradeEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "event_type")
    @Enumerated(value = EnumType.STRING)
    private EventType eventType;
    @Column(name = "event_datetime", columnDefinition = "TIMESTAMP")
    private LocalDateTime eventDatetime;
    @Column(name = "resource_uri")
    private String resourceUri;
    @Column(name = "processing_status")
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;
}
