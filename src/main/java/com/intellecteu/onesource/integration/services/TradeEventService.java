package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.repository.TimestampRepository;
import com.intellecteu.onesource.integration.repository.TradeEventRepository;
import com.intellecteu.onesource.integration.repository.entity.onesource.TimestampEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.TradeEventEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TradeEventService {

    private static final String LAST_TRADE_EVENT_DATETIME = "LAST_TRADE_EVENT_DATETIME";
    private final TradeEventRepository tradeEventRepository;
    private final TimestampRepository timestampRepository;
    private final LocalDateTime startingTradeEventDatetime;
    private final OneSourceMapper oneSourceMapper;

    @Autowired
    public TradeEventService(TradeEventRepository tradeEventRepository, TimestampRepository timestampRepository,
        @Value("${starting-trade-event-datetime}") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX") LocalDateTime startingTradeEventDatetime,
        OneSourceMapper oneSourceMapper) {
        this.tradeEventRepository = tradeEventRepository;
        this.timestampRepository = timestampRepository;
        this.startingTradeEventDatetime = startingTradeEventDatetime;
        this.oneSourceMapper = oneSourceMapper;
    }

    @Transactional
    public TradeEvent saveTradeEvent(TradeEvent tradeEvent) {
        TradeEventEntity tradeEventEntity = tradeEventRepository.save(oneSourceMapper.toEntity(tradeEvent));
        return oneSourceMapper.toModel(tradeEventEntity);
    }

    @Transactional
    public List<TradeEvent> saveTradeEvents(List<TradeEvent> tradeEvents) {
        List<TradeEventEntity> tradeEventEntities = tradeEvents.stream()
            .map(oneSourceMapper::toEntity)
            .filter(event -> !tradeEventRepository.existsByEventId(event.getEventId()))
            .map(tradeEventRepository::save)
            .toList();
        return tradeEventEntities.stream().map(oneSourceMapper::toModel).collect(Collectors.toList());
    }

    @Transactional
    public LocalDateTime getLastEventDatetime() {
        Optional<TimestampEntity> lastEventDatetime = timestampRepository.findById(LAST_TRADE_EVENT_DATETIME);
        return lastEventDatetime.map(TimestampEntity::getTimestamp).orElse(startingTradeEventDatetime);
    }

    @Transactional
    public void updateLastEventDatetime(List<TradeEvent> tradeEventList) {
        if (tradeEventList != null && !tradeEventList.isEmpty()) {
            LocalDateTime lastEventDatetime = tradeEventList.stream()
                .map(TradeEvent::getEventDateTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());
            timestampRepository.save(new TimestampEntity(LAST_TRADE_EVENT_DATETIME, lastEventDatetime));
        }
    }

}
