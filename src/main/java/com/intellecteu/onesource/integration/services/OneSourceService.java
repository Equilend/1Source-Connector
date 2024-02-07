package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.TradeEventDto;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.client.onesource.OneSourceApiClient;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class OneSourceService {

    private final OneSourceApiClient oneSourceApiClient;
    private final EventMapper eventMapper;
    private final OneSourceMapper oneSourceMapper;

    @Autowired
    public OneSourceService(OneSourceApiClient oneSourceApiClient, EventMapper eventMapper,
        OneSourceMapper oneSourceMapper) {
        this.oneSourceApiClient = oneSourceApiClient;
        this.eventMapper = eventMapper;
        this.oneSourceMapper = oneSourceMapper;
    }

    public List<TradeEvent> retrieveEvents(LocalDateTime lastEventDatetime) {
        List<TradeEventDto> tradeEventDtos = oneSourceApiClient.retrieveEvents(lastEventDatetime);
        tradeEventDtos = findNewEvents(tradeEventDtos, lastEventDatetime);
        return tradeEventDtos.stream().map(eventMapper::toEventEntity).collect(Collectors.toList());
    }

    public Optional<Agreement> retrieveTradeAgreement(String eventUri, EventType eventType) {
        Optional<AgreementDto> tradeAgreementDtos = oneSourceApiClient.findTradeAgreement(eventUri, eventType);
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
        return oneSourceApiClient.retrieveContract(eventUri);
    }

    public Optional<Rerate> retrieveRerate(String eventUri) {
        RerateDTO rerateDTO = oneSourceApiClient.retrieveRerate(eventUri);
        return Optional.of(oneSourceMapper.toModel(rerateDTO));
    }

    @Transactional
    public void cancelContract(Contract contract, String positionId) {
        oneSourceApiClient.cancelContract(contract, positionId);
    }

}
