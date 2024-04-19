package com.intellecteu.onesource.integration.routes.delegate_flow.processor;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.TradeEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessor {

    private final TradeEventService tradeEventService;

    public TradeEvent saveEvent(TradeEvent event) {
        return tradeEventService.saveTradeEvent(event);
    }

    /**
     * Update event status and persist event
     *
     * @param event TradeEvent
     * @param status ProcessingStatus
     * @return persisted TradeEvent model
     */
    public TradeEvent updateEventStatus(TradeEvent event, ProcessingStatus status) {
        event.setProcessingStatus(status);
        return saveEvent(event);
    }

}