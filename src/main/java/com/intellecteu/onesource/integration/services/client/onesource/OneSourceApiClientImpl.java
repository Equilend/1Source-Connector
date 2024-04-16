package com.intellecteu.onesource.integration.services.client.onesource;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_TRADE_AGREEMENT;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.repository.TradeEventRepository;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateDTO;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class OneSourceApiClientImpl implements OneSourceApiClient {

    private final CloudEventRecordService cloudEventRecordService;
    private final RestTemplate restTemplate;
    private final TradeEventRepository eventRepository;

    @Value("${onesource.base-endpoint}")
    private String onesourceBaseEndpoint;

    public OneSourceApiClientImpl(CloudEventRecordService cloudEventRecordService,
        RestTemplate restTemplate, TradeEventRepository eventRepository) {
        this.cloudEventRecordService = cloudEventRecordService;
        this.restTemplate = restTemplate;
        this.eventRepository = eventRepository;
    }

    @Override
    public Optional<Agreement> findTradeAgreement(String agreementUri, EventType eventType) {
        log.debug("Retrieving Trade Agreement from 1Source.");
        try {
            Agreement agreement = restTemplate.getForObject(onesourceBaseEndpoint + agreementUri, Agreement.class);
            return Optional.ofNullable(agreement);
        } catch (RestClientException e) {
            log.warn("Trade Agreement {} was not found. Details: {} ", agreementUri, e.getMessage());
            if (e instanceof HttpStatusCodeException exception) {
                final HttpStatusCode statusCode = exception.getStatusCode();
                if (Set.of(UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR)
                    .contains(HttpStatus.valueOf(statusCode.value()))) {
                    String eventId = retrieveEventId(agreementUri, eventType);
                    var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                    var recordRequest = eventBuilder.buildExceptionRequest(agreementUri, exception, GET_TRADE_AGREEMENT,
                        eventId);
                    cloudEventRecordService.record(recordRequest);
                }
            }
            return Optional.empty();
        }
    }

    @Override
    public Optional<Contract> retrieveContract(String contractUri) {
        log.debug("Retrieving contract: {}", contractUri);
        return Optional.ofNullable(findContractViaHttpRequest(contractUri));
    }

    private Contract findContractViaHttpRequest(String contractUri) throws HttpStatusCodeException {
        return restTemplate.getForObject(onesourceBaseEndpoint + contractUri, Contract.class);
    }

    @Override
    public RerateDTO retrieveRerate(String rerateUri) {
        log.debug("Retrieving rerate: {}", rerateUri);
        return restTemplate.getForObject(onesourceBaseEndpoint + rerateUri, RerateDTO.class);
    }

    private String retrieveEventId(String agreementUri, EventType eventType) {
        String errorMsg = format("Event Id not found for resource: %s and eventType: %s", agreementUri, eventType);
        try {
            return eventRepository.findEventIdByResourceUriAndEventType(agreementUri, eventType.name())
                .map(String::valueOf)
                .orElse(errorMsg);
        } catch (Exception e) {
            log.debug(errorMsg);
            return errorMsg;
        }
    }
}
