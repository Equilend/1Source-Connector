package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.model.onesource.FixedRate.FIXED_INDEX_NAME;

import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.client.onesource.OneSourceApiClient;
import com.intellecteu.onesource.integration.services.client.onesource.ReratesApi;
import com.intellecteu.onesource.integration.services.client.onesource.dto.BenchmarkCdDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FixedRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FixedRateDefDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FloatingRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FloatingRateDefDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.LedgerResponseDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RebateRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateProposalDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class OneSourceService {

    private final OneSourceApiClient oneSourceApiClient;
    private final ReratesApi reratesApi;
    private final EventMapper eventMapper;
    private final OneSourceMapper oneSourceMapper;

    @Autowired
    public OneSourceService(OneSourceApiClient oneSourceApiClient, ReratesApi reratesApi, EventMapper eventMapper,
        OneSourceMapper oneSourceMapper, @Value("${onesource.baseEndpoint}") String onesourceBasePath) {
        this.oneSourceApiClient = oneSourceApiClient;
        this.reratesApi = reratesApi;
        this.reratesApi.getApiClient().setBasePath(onesourceBasePath);
        this.eventMapper = eventMapper;
        this.oneSourceMapper = oneSourceMapper;
    }

    public List<TradeEvent> retrieveEvents(LocalDateTime lastEventDatetime) {
        List<TradeEvent> tradeEvents = oneSourceApiClient.retrieveEvents(lastEventDatetime);
        List<TradeEvent> newEvents = findNewEvents(tradeEvents, lastEventDatetime);
        return newEvents;
    }

    public Optional<Agreement> retrieveTradeAgreement(String eventUri, EventType eventType) {
        return oneSourceApiClient.findTradeAgreement(eventUri, eventType);
    }

    //TODO Do we need this logic?
    @Deprecated
    private List<TradeEvent> findNewEvents(List<TradeEvent> events, LocalDateTime lastEventDatetime) {
        return events.stream()
            .filter(p -> p.getEventDateTime().isAfter(lastEventDatetime))
            .peek(i -> log.debug("New event Id: {}, Type: {}, Uri: {}, Event Datetime {}",
                i.getEventId(), i.getEventType(), i.getResourceUri(), i.getEventDateTime()))
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

    public void instructRerate(RerateTrade rerateTrade) {
        RerateProposalDTO body = buildRerateProposal(rerateTrade);
        reratesApi.ledgerContractsContractIdReratesPost(body,
            rerateTrade.getRelatedContractId());
    }

    public void approveRerate(String contractId, String rerateId) {
        ResponseEntity<LedgerResponseDTO> ledgerResponseDTOResponseEntity = reratesApi.ledgerContractsContractIdReratesRerateIdApprovePostWithHttpInfo(
            contractId, rerateId);
    }

    private RerateProposalDTO buildRerateProposal(RerateTrade rerateTrade) {
        RerateProposalDTO rerateProposalDTO = new RerateProposalDTO();
        RebateRateDTO rebateRate = new RebateRateDTO();
        if (FIXED_INDEX_NAME.equals(rerateTrade.getTradeOut().getPosition().getIndex().getIndexName())) {
            FixedRateDTO rebate = new FixedRateDTO();
            rebate.fixed(new FixedRateDefDTO()
                .baseRate(rerateTrade.getTradeOut().getRateOrSpread())
                .effectiveDate(rerateTrade.getTradeOut().getAccrualDate().toLocalDate())
            );
            rebateRate.rebate(rebate);
        } else {
            FloatingRateDTO rebate = new FloatingRateDTO();
            rebate.floating(new FloatingRateDefDTO()
                .benchmark(BenchmarkCdDTO.valueOf(rerateTrade.getTradeOut().getIndex().getIndexName()))
                .spread(rerateTrade.getTradeOut().getRateOrSpread())
                .isAutoRerate(false)
                .effectiveDate(rerateTrade.getTradeOut().getAccrualDate().toLocalDate())
            );
            rebateRate.rebate(rebate);
        }
        rerateProposalDTO.rate(rebateRate);
        return rerateProposalDTO;
    }

}
