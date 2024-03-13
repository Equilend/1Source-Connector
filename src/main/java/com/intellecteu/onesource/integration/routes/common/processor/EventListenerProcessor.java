package com.intellecteu.onesource.integration.routes.common.processor;

import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;

import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.TradeEventService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventListenerProcessor {

    private final TradeEventService tradeEventService;
    private final OneSourceService oneSourceService;

    @Autowired
    public EventListenerProcessor(TradeEventService tradeEventService, OneSourceService oneSourceService) {
        this.tradeEventService = tradeEventService;
        this.oneSourceService = oneSourceService;
    }

    public void consumeEvents() {
        log.debug(">>>>> Pulling events!");
        LocalDateTime lastEventDatetime = tradeEventService.getLastEventDatetime();
        log.debug("Timestamp: " + lastEventDatetime);
        List<TradeEvent> newEvents = oneSourceService.retrieveEvents(lastEventDatetime);
        newEvents.forEach(event -> event.setProcessingStatus(CREATED));
        tradeEventService.saveTradeEvents(newEvents);
        tradeEventService.updateLastEventDatetime(newEvents);
        log.debug("<<<<< Retrieved {} new events!", newEvents.size());
    }

}
