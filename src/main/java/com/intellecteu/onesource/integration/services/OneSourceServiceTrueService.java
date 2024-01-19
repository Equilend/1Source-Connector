package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.TradeEventDto;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.EventType;
import com.intellecteu.onesource.integration.model.TradeEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OneSourceServiceTrueService {

    private final OneSourceService oneSourceService;
    private final EventMapper eventMapper;

    @Autowired
    public OneSourceServiceTrueService(OneSourceService oneSourceService, EventMapper eventMapper) {
        this.oneSourceService = oneSourceService;
        this.eventMapper = eventMapper;
    }

    public List<TradeEvent> retrieveEvents(LocalDateTime lastEventDatetime) {
        List<TradeEventDto> tradeEventDtos = oneSourceService.retrieveEvents(lastEventDatetime);
        tradeEventDtos = findNewEvents(tradeEventDtos, lastEventDatetime);
        return tradeEventDtos.stream().map(eventMapper::toEventEntity).collect(Collectors.toList());
    }

    public Optional<Agreement> retrieveTradeAgreement(String eventUri, EventType eventType) {
        Optional<AgreementDto> tradeAgreementDtos = oneSourceService.findTradeAgreement(eventUri, eventType);
        return tradeAgreementDtos.map(eventMapper::toAgreementEntity);
    }

    //TODO Do we need this logic?
    @Deprecated
    private List<TradeEventDto> findNewEvents(List<TradeEventDto> events, LocalDateTime lastEventDatetime) {
        return events.stream()
            .filter(p -> p.getEventDatetime().isAfter(lastEventDatetime))
            .peek(i -> log.debug("New event Id: {}, Type: {}, Uri: {}, Event Datetime {}",
                i.getEventId(), i.getEventType(), i.getResourceUri(), i.getEventDatetime()))
            .toList();
    }

    public Optional<Contract> retrieveContract(String eventUri) {
        return oneSourceService.retrieveContract(eventUri);
    }

    public void cancelContract(Contract contract, String positionId) {
        oneSourceService.cancelContract(contract, positionId);
    }
}
