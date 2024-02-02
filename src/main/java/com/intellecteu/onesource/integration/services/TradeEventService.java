package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.model.onesource.Timestamp;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.repository.TimestampRepository;
import com.intellecteu.onesource.integration.repository.TradeEventRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TradeEventService {

    private static final String LAST_TRADE_EVENT_DATETIME = "LAST_TRADE_EVENT_DATETIME";
    private final TradeEventRepository tradeEventRepository;
    private final TimestampRepository timestampRepository;
    private LocalDateTime startingTradeEventDatetime;

    @Autowired
    public TradeEventService(TradeEventRepository tradeEventRepository, TimestampRepository timestampRepository,
        @Value("${integration-toolkit.starting-trade-event-datetime}") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX") LocalDateTime startingTradeEventDatetime) {
        this.tradeEventRepository = tradeEventRepository;
        this.timestampRepository = timestampRepository;
        this.startingTradeEventDatetime = startingTradeEventDatetime;
    }

    public TradeEvent saveTradeEvent(TradeEvent tradeEvent) {
        return tradeEventRepository.save(tradeEvent);
    }

    public List<TradeEvent> saveTradeEvents(List<TradeEvent> tradeEvents) {
        return tradeEventRepository.saveAll(tradeEvents);
    }

    public LocalDateTime getLastEventDatetime() {
        Optional<Timestamp> lastEventDatetime = timestampRepository.findById(LAST_TRADE_EVENT_DATETIME);
        return lastEventDatetime.map(timestamp -> timestamp.getTimestamp()).orElse(startingTradeEventDatetime);
    }

    public void updateLastEventDatetime(List<TradeEvent> tradeEventList) {
        if (tradeEventList != null && !tradeEventList.isEmpty()) {
            LocalDateTime lastEventDatetime = tradeEventList.stream()
                .map(TradeEvent::getEventDatetime)
                .max(LocalDateTime::compareTo)
                .get();
            timestampRepository.save(new Timestamp(LAST_TRADE_EVENT_DATETIME, lastEventDatetime));
        }
    }

}
