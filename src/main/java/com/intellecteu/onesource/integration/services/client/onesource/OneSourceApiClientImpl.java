package com.intellecteu.onesource.integration.services.client.onesource;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.APPROVE_LOAN_PROPOSAL_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.DECLINE_LOAN_PROPOSAL_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.POST_LOAN_CONTRACT_PROPOSAL_UPDATE_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.MaintainOnesourceParticipantsList.DataMsg.GET_PARTICIPANTS_1SOURCE_MSG;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_SETTLEMENT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.GENERIC;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.MAINTAIN_1SOURCE_PARTICIPANTS_LIST;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.APPROVE_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.CANCEL_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.DECLINE_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_1SOURCE_EVENTS;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_PARTICIPANTS_LIST;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_TRADE_AGREEMENT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_LOAN_CONTRACT_UPDATE;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.ONESOURCE_ISSUE;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.SPIRE_ISSUE;
import static java.lang.String.format;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.ContractProposalDto;
import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ContractProposal;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.onesource.SettlementInstructionUpdate;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.SettlementUpdateRepository;
import com.intellecteu.onesource.integration.repository.TradeEventRepository;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateDTO;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class OneSourceApiClientImpl implements OneSourceApiClient {

    private final ContractRepository contractRepository;
    private final CloudEventRecordService cloudEventRecordService;
    private final RestTemplate restTemplate;
    private final SettlementUpdateRepository settlementUpdateRepository;
    private final TradeEventRepository eventRepository;
    private final EventMapper eventMapper;
    private final OneSourceMapper oneSourceMapper;

    private static final String EVENTS_ENDPOINT = "/ledger/events";
    private static final String CONTRACT_APPROVE_ENDPOINT = "/ledger/contracts/{contractId}/approve";
    private static final String PARTIES_ENDPOINT = "/ledger/parties";
    private static final String CONTRACT_DECLINE_ENDPOINT = "/ledger/contracts/{contractId}/decline";
    private static final String CONTRACT_CANCEL_ENDPOINT = "/ledger/contracts/{contractId}/cancel";
    private static final String CONTRACT_ENDPOINT = "/ledger/contracts/{contractId}";
    private static final String CREATE_CONTRACT_ENDPOINT = "/ledger/contracts";

    @Value("${onesource.baseEndpoint}")
    private String onesourceBaseEndpoint;

    @Value("${onesource.version}")
    private String version;

    public OneSourceApiClientImpl(ContractRepository contractRepository,
        CloudEventRecordService cloudEventRecordService,
        RestTemplate restTemplate, SettlementUpdateRepository settlementUpdateRepository, EventMapper eventMapper,
        TradeEventRepository eventRepository, OneSourceMapper oneSourceMapper) {
        this.contractRepository = contractRepository;
        this.cloudEventRecordService = cloudEventRecordService;
        this.restTemplate = restTemplate;
        this.settlementUpdateRepository = settlementUpdateRepository;
        this.eventMapper = eventMapper;
        this.eventRepository = eventRepository;
        this.oneSourceMapper = oneSourceMapper;
    }

    @Override
    public void createContract(Agreement agreement, ContractProposal contractProposal, Position position) {
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<ContractProposal> request = new HttpEntity<>(contractProposal, headers);

        executeCreateContractRequest(agreement, position, request);
    }

    private void executeCreateContractRequest(Agreement agreement, Position position,
        HttpEntity<ContractProposal> request) {
        String agreementId = agreement == null ? null : agreement.getAgreementId();
        log.debug("Sending POST request to {}", onesourceBaseEndpoint + version + CREATE_CONTRACT_ENDPOINT);
        try {
            restTemplate.exchange(
                onesourceBaseEndpoint + version + CREATE_CONTRACT_ENDPOINT, POST, request, JsonNode.class);
        } catch (HttpStatusCodeException e) {
            log.warn(
                "The loan contract proposal instruction has not been processed by 1Source for the trade agreement: "
                    + "{} (SPIRE Position: {}) for the following reason: {}",
                agreementId, position.getPositionId(), e.getStatusCode());
            final HttpStatusCode statusCode = e.getStatusCode();
            if (Set.of(BAD_REQUEST, UNAUTHORIZED, INTERNAL_SERVER_ERROR)
                .contains(HttpStatus.valueOf(statusCode.value()))) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                var recordRequest = eventBuilder.buildExceptionRequest(e, POST_LOAN_CONTRACT_PROPOSAL,
                    position.getPositionId());
                cloudEventRecordService.record(recordRequest);
            }
        }
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
    public void updateContract(Contract contract, HttpEntity<?> request) {
        String venueRefId = contract.getTrade().getVenue().getVenueRefKey();
        executeUpdateContract(contract, request);
        log.debug("Contract id:{} with venueRefId:{} was updated!", contract.getContractId(), venueRefId);
    }

    @Override
    public RerateDTO retrieveRerate(String rerateUri) {
        log.debug("Retrieving rerate: {}", rerateUri);
        return restTemplate.getForObject(onesourceBaseEndpoint + rerateUri, RerateDTO.class);
    }

    @Override
    public Settlement retrieveSettlementInstruction(Contract contractDto) {
        String venueRefId = contractDto.getTrade().getVenue().getVenueRefKey();
        log.debug("Updating contract by venueRefId: {}", venueRefId);
        var settlementInstructionUpdate = settlementUpdateRepository.findByVenueRefId(venueRefId).stream()
            .findFirst()
            .orElse(null);
        if (settlementInstructionUpdate == null) {
            log.warn("No settlement update was found for venueRefId:{}", venueRefId);
            return null;
        }
        Settlement settlement = Settlement.builder()
            .partyRole(BORROWER)
            .instruction(eventMapper.toInstruction(settlementInstructionUpdate.getInstruction())).build();

        log.debug("Settlement with role {} was created from venueRefId:{}",
            settlement.getPartyRole(), venueRefId);
        return settlement;
    }

    private void executeUpdateContract(Contract contract, HttpEntity<?> request) {
        log.debug("Updating contract. Sending PATCH request to {}/{}",
            onesourceBaseEndpoint + version + CONTRACT_ENDPOINT, contract.getContractId());
        try {
            restTemplate.exchange(onesourceBaseEndpoint + version + CONTRACT_ENDPOINT,
                PATCH, request, JsonNode.class, contract.getContractId());
        } catch (HttpStatusCodeException e) {
            log.error(
                format(POST_LOAN_CONTRACT_PROPOSAL_UPDATE_EXCEPTION_MSG, contract.getContractId(), e.getStatusText()));
            contract.setProcessingStatus(SPIRE_ISSUE);
            final HttpStatusCode statusCode = e.getStatusCode();
            if (Set.of(BAD_REQUEST, UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR)
                .contains(HttpStatus.valueOf(statusCode.value()))) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_SETTLEMENT);
                var recordRequest = eventBuilder.buildExceptionRequest(contract.getContractId(), e,
                    POST_LOAN_CONTRACT_UPDATE,
                    contract.getMatchingSpirePositionId());
                cloudEventRecordService.record(recordRequest);
            }
        }
    }

    @Override
    @Deprecated(since = "1.0.4")
    public void approveContract(ContractDto contract) {
        log.debug("Approving contract: {}", contract.getContractId());
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<ContractProposalDto> request = new HttpEntity<>(headers);
        try {
            restTemplate.exchange(onesourceBaseEndpoint + version + CONTRACT_APPROVE_ENDPOINT, POST,
                request, JsonNode.class, contract.getContractId());
        } catch (HttpStatusCodeException e) {
            var positionId = contract.getMatchingSpirePositionId();
            log.error(
                format(APPROVE_LOAN_PROPOSAL_EXCEPTION_MSG, contract.getContractId(), positionId, e.getStatusCode()));
            contract.setProcessingStatus(ONESOURCE_ISSUE);
            final HttpStatusCode statusCode = e.getStatusCode();
            if (Set.of(BAD_REQUEST, UNAUTHORIZED, NOT_FOUND, CONFLICT, INTERNAL_SERVER_ERROR)
                .contains(HttpStatus.valueOf(statusCode.value()))) {
                makeCloudEventRecord(contract.getContractId(), e, APPROVE_LOAN_CONTRACT_PROPOSAL, positionId);
            }
        }
    }

    @Override
    public void approveContract(Contract contract, Settlement settlement) {
        log.debug("Approving contract: {}", contract.getContractId());
        var settlementInstructionUpdate = new SettlementInstructionUpdate(settlement.getInstruction());
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<SettlementInstructionUpdate> request = new HttpEntity<>(settlementInstructionUpdate, headers);
        try {
            log.debug("Sending {} request to {}:", POST, onesourceBaseEndpoint + version + CONTRACT_APPROVE_ENDPOINT);
            restTemplate.exchange(onesourceBaseEndpoint + version + CONTRACT_APPROVE_ENDPOINT, POST,
                request, JsonNode.class, contract.getContractId());
            log.debug("Contract id: {} approval was sent.", contract.getContractId());
        } catch (HttpStatusCodeException e) {
            var positionId = contract.getMatchingSpirePositionId();
            log.error(
                format(APPROVE_LOAN_PROPOSAL_EXCEPTION_MSG, contract.getContractId(), positionId, e.getStatusCode()));
            contract.setProcessingStatus(ONESOURCE_ISSUE);
            final HttpStatusCode statusCode = e.getStatusCode();
            if (Set.of(BAD_REQUEST, UNAUTHORIZED, NOT_FOUND, CONFLICT, INTERNAL_SERVER_ERROR)
                .contains(HttpStatus.valueOf(statusCode.value()))) {
                makeCloudEventRecord(contract.getContractId(), e, APPROVE_LOAN_CONTRACT_PROPOSAL, positionId);
            }
        }
    }

    @Override
    public void declineContract(Contract contract) {
        try {
            String formattedEndpoint = CONTRACT_DECLINE_ENDPOINT.replace("{contractId}", contract.getContractId());
            log.debug("Sending POST request to {}", onesourceBaseEndpoint + version + formattedEndpoint);
            restTemplate.exchange(onesourceBaseEndpoint + version + CONTRACT_DECLINE_ENDPOINT, POST,
                null, JsonNode.class, contract.getContractId());
            log.debug("The contract: {} was declined!", contract.getContractId());
        } catch (HttpStatusCodeException e) {
            String positionId = contract.getMatchingSpirePositionId();
            log.error(
                format(DECLINE_LOAN_PROPOSAL_EXCEPTION_MSG, contract.getContractId(), positionId, e.getStatusText()));
            contract.setProcessingStatus(SPIRE_ISSUE);
            final HttpStatusCode statusCode = e.getStatusCode();
            if (Set.of(BAD_REQUEST, UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR)
                .contains(HttpStatus.valueOf(statusCode.value()))) {
                makeCloudEventRecord(contract.getContractId(), e, DECLINE_LOAN_CONTRACT_PROPOSAL, positionId);
            }
        }
    }

    @Override
    public void cancelContract(Contract contract, String positionId) {
        log.debug("Sending POST request to {}", onesourceBaseEndpoint + version + CONTRACT_CANCEL_ENDPOINT);
        try {
            restTemplate.exchange(onesourceBaseEndpoint + version + CONTRACT_CANCEL_ENDPOINT, POST,
                null, JsonNode.class, contract.getContractId());
        } catch (HttpStatusCodeException e) {
            saveStatusOnUnsuccessfulRequest(contract, e.getStatusText());
            final HttpStatusCode statusCode = e.getStatusCode();
            if (Set.of(BAD_REQUEST, UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR)
                .contains(HttpStatus.valueOf(statusCode.value()))) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                var recordRequest = eventBuilder.buildExceptionRequest(contract.getContractId(), e,
                    CANCEL_LOAN_CONTRACT_PROPOSAL, positionId);
                cloudEventRecordService.record(recordRequest);
            }
        }
    }

    private void makeCloudEventRecord(String recordData, HttpStatusCodeException exception,
        IntegrationSubProcess subProcess, String positionId) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildExceptionRequest(recordData, exception, subProcess, positionId);
        cloudEventRecordService.record(recordRequest);
    }

    @Override
    public List<PartyDto> retrieveParties() {
        log.debug("Retrieving parties. Sending GET request to {}", onesourceBaseEndpoint + version + PARTIES_ENDPOINT);
        try {
            final ResponseEntity<List<PartyDto>> response = restTemplate.exchange(
                onesourceBaseEndpoint + version + PARTIES_ENDPOINT,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                });
            log.debug("Retrieve: parties: " + response.getStatusCode());
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            log.debug(format(GET_PARTICIPANTS_1SOURCE_MSG, e.getStatusCode()));
            if (INTERNAL_SERVER_ERROR == e.getStatusCode()) {
                var eventBuilder = cloudEventRecordService.getFactory()
                    .eventBuilder(MAINTAIN_1SOURCE_PARTICIPANTS_LIST);
                var recordRequest = eventBuilder.buildExceptionRequest(e, GET_PARTICIPANTS_LIST);
                cloudEventRecordService.record(recordRequest);
            }
            return null;
        }
    }

    @Override
    public List<TradeEvent> retrieveEvents(LocalDateTime timeStamp) {
        var encodedTimestamp = URLEncoder.encode(
            timeStamp.atZone(ZoneOffset.UTC).toString(), StandardCharsets.US_ASCII);
        String url = UriComponentsBuilder
            .fromHttpUrl(onesourceBaseEndpoint + version + EVENTS_ENDPOINT)
            .queryParam("since", encodedTimestamp)
            .build()
            .toUriString();

        log.debug("Sending GET request to {}", url);
        return executeGetEventsRequest(url);
    }

    private List<TradeEvent> executeGetEventsRequest(String url) {
        try {
            final ResponseEntity<List<TradeEvent>> response = restTemplate.exchange(
                url, GET, null, new ParameterizedTypeReference<>() {
                });
            log.debug("Retrieve events response: " + response.getStatusCode());
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            log.warn("Retrieve events response: " + e.getStatusCode());
            if (UNAUTHORIZED == e.getStatusCode() || INTERNAL_SERVER_ERROR == e.getStatusCode()) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(GENERIC);
                cloudEventRecordService.record(eventBuilder.buildExceptionRequest(e, GET_1SOURCE_EVENTS));
            }
            return List.of();
        }
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

    private void saveStatusOnUnsuccessfulRequest(Contract contract, String response) {
        log.error("The loan contract : {} cannot be canceled for the following reason: {}",
            contract.getContractId(), response);
        contract.setProcessingStatus(SPIRE_ISSUE);
        contractRepository.save(oneSourceMapper.toEntity(contract));
    }
}
