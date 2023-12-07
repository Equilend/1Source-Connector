package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.dto.TradeEventDto;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.model.Timestamp;
import com.intellecteu.onesource.integration.repository.TimestampRepository;
import com.intellecteu.onesource.integration.repository.TradeEventRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OneSourceEventConsumer implements EventConsumer {

    private final TimestampRepository timestampRepository;
    private final OneSourceService oneSourceService;
    private final EventMapper eventMapper;
    private final TradeEventRepository tradeEventRepository;

    @Value("${camel.timestamp}")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime timeStamp;

    @Override
    public void consumeEvents() {
        log.debug(">>>>> Pulling events!");
        Optional<Timestamp> maxTimestamp = timestampRepository.findFirstByOrderByTimestampDesc();
        maxTimestamp.ifPresent(timestamp -> timeStamp = timestamp.getTimestamp());
        log.debug("Timestamp: " + timeStamp);
        List<TradeEventDto> events = oneSourceService.retrieveEvents(timeStamp);
        events.forEach(i -> log.debug("Event Id: {}, Type: {}, Uri: {}, Event Datetime {}",
            i.getEventId(), i.getEventType(), i.getResourceUri(), i.getEventDatetime()));
        List<TradeEventDto> newEvents = findNewEvents(events);
        newEvents.forEach(i -> tradeEventRepository.save(eventMapper.toEventEntity(i))); //make batch insert later
        log.debug("<<<<< Retrieved {} new events!", newEvents.size());
    }

    private List<TradeEventDto> findNewEvents(List<TradeEventDto> events) {
        return events.stream()
            .filter(p -> p.getEventDatetime().isAfter(timeStamp))
            .peek(i -> log.debug("New event Id: {}, Type: {}, Uri: {}, Event Datetime {}",
                i.getEventId(), i.getEventType(), i.getResourceUri(), i.getEventDatetime()))
            .toList();
    }
}
