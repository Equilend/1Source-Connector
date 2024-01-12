package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.APPROVE_LOAN_PROPOSAL_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.DECLINE_LOAN_PROPOSAL_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.POST_LOAN_CONTRACT_PROPOSAL_UPDATE_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.MaintainOnesourceParticipantsList.DataMsg.GET_PARTICIPANTS_1SOURCE_MSG;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_SETTLEMENT;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.GENERIC;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.MAINTAIN_1SOURCE_PARTICIPANTS_LIST;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.APPROVE_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.CANCEL_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.DECLINE_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_1SOURCE_EVENTS;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_PARTICIPANTS_LIST;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_TRADE_AGREEMENT;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.POST_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.POST_LOAN_CONTRACT_UPDATE;
import static com.intellecteu.onesource.integration.model.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.ONESOURCE_ISSUE;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SPIRE_ISSUE;
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
import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.ContractProposalDto;
import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.SettlementInstructionUpdateDto;
import com.intellecteu.onesource.integration.dto.TradeEventDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.EventType;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.SettlementUpdateRepository;
import com.intellecteu.onesource.integration.repository.TradeEventRepository;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class OneSourceApiService implements OneSourceService {

    private final ContractRepository contractRepository;
    private final CloudEventRecordService cloudEventRecordService;
    private final RestTemplate restTemplate;
    private final SettlementUpdateRepository settlementUpdateRepository;

    private final TradeEventRepository eventRepository;
    private final EventMapper eventMapper;
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

    public OneSourceApiService(ContractRepository contractRepository, CloudEventRecordService cloudEventRecordService,
        RestTemplate restTemplate, SettlementUpdateRepository settlementUpdateRepository, EventMapper eventMapper,
        TradeEventRepository eventRepository) {
        this.contractRepository = contractRepository;
        this.cloudEventRecordService = cloudEventRecordService;
        this.restTemplate = restTemplate;
        this.settlementUpdateRepository = settlementUpdateRepository;
        this.eventMapper = eventMapper;
        this.eventRepository = eventRepository;
    }

    @Override
    public void createContract(AgreementDto agreement, ContractProposalDto contractProposalDto, PositionDto position) {
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<ContractProposalDto> request = new HttpEntity<>(contractProposalDto, headers);

        executeCreateContractRequest(agreement, position, request);
    }

    private void executeCreateContractRequest(AgreementDto agreement, PositionDto position,
        HttpEntity<ContractProposalDto> request) {
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
            if (Set.of(BAD_REQUEST, UNAUTHORIZED, INTERNAL_SERVER_ERROR).contains(e.getStatusCode())) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                var recordRequest = eventBuilder.buildExceptionRequest(e, POST_LOAN_CONTRACT_PROPOSAL,
                    position.getPositionId());
                cloudEventRecordService.record(recordRequest);
            }
        }
    }

    @Override
    public Optional<AgreementDto> findTradeAgreement(String agreementUri, EventType eventType) {
        log.debug("Retrieving Trade Agreement from 1Source.");
        try {
            Agreement agreement = restTemplate.getForObject(onesourceBaseEndpoint + agreementUri, Agreement.class);
            return Optional.ofNullable(eventMapper.toAgreementDto(agreement));
        } catch (RestClientException e) {
            log.warn("Trade Agreement {} was not found. Details: {} ", agreementUri, e.getMessage());
            if (e instanceof HttpStatusCodeException exception) {
                if (Set.of(UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR).contains(exception.getStatusCode())) {
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
    public Optional<Contract> findContract(String contractUri) {
        log.debug("Retrieving contract: {}", contractUri);
        return Optional.ofNullable(findContractViaHttpRequest(contractUri));
    }

    private Contract findContractViaHttpRequest(String contractUri) throws HttpStatusCodeException {
        return restTemplate.getForObject(onesourceBaseEndpoint + contractUri, Contract.class);
    }

    @Override
    public void updateContract(ContractDto contract, HttpEntity<?> request) {
        String venueRefId = contract.getTrade().getExecutionVenue().getVenueRefKey();
        executeUpdateContract(contract, request);
        log.debug("Contract id:{} with venueRefId:{} was updated!", contract.getContractId(), venueRefId);
    }

    public SettlementDto retrieveSettlementInstruction(ContractDto contractDto) {
        String venueRefId = contractDto.getTrade().getExecutionVenue().getVenueRefKey();
        log.debug("Updating contract by venueRefId: {}", venueRefId);
        var settlementInstructionUpdate = settlementUpdateRepository.findByVenueRefId(venueRefId).stream()
            .findFirst()
            .orElse(null);
        if (settlementInstructionUpdate == null) {
            log.warn("No settlement update was found for venueRefId:{}", venueRefId);
            return null;
        }
        SettlementDto settlementDto = SettlementDto.builder()
            .partyRole(BORROWER)
            .instruction(eventMapper.toInstructionDto(settlementInstructionUpdate.getInstruction())).build();

        log.debug("Settlement with role {} was created from venueRefId:{}",
            settlementDto.getPartyRole(), venueRefId);
        return settlementDto;
    }

    private void executeUpdateContract(ContractDto contract, HttpEntity<?> request) {
        log.debug("Updating contract. Sending PATCH request to {}/{}",
            onesourceBaseEndpoint + version + CONTRACT_ENDPOINT, contract.getContractId());
        try {
            restTemplate.exchange(onesourceBaseEndpoint + version + CONTRACT_ENDPOINT,
                PATCH, request, JsonNode.class, contract.getContractId());
        } catch (HttpStatusCodeException e) {
            log.error(
                format(POST_LOAN_CONTRACT_PROPOSAL_UPDATE_EXCEPTION_MSG, contract.getContractId(), e.getStatusText()));
            contract.setProcessingStatus(SPIRE_ISSUE);
            if (Set.of(BAD_REQUEST, UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR).contains(e.getStatusCode())) {
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
            if (Set.of(BAD_REQUEST, UNAUTHORIZED, NOT_FOUND, CONFLICT, INTERNAL_SERVER_ERROR)
                .contains(e.getStatusCode())) {
                makeCloudEventRecord(contract.getContractId(), e, APPROVE_LOAN_CONTRACT_PROPOSAL, positionId);
            }
        }
    }

    @Override
    public void approveContract(ContractDto contract, SettlementDto settlementDto) {
        log.debug("Approving contract: {}", contract.getContractId());
        var settlementInstructionUpdateDto = new SettlementInstructionUpdateDto(settlementDto);
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<SettlementInstructionUpdateDto> request = new HttpEntity<>(settlementInstructionUpdateDto, headers);
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
            if (Set.of(BAD_REQUEST, UNAUTHORIZED, NOT_FOUND, CONFLICT, INTERNAL_SERVER_ERROR)
                .contains(e.getStatusCode())) {
                makeCloudEventRecord(contract.getContractId(), e, APPROVE_LOAN_CONTRACT_PROPOSAL, positionId);
            }
        }
    }

    @Override
    public void declineContract(ContractDto contract) {
        try {
            log.debug("Sending POST request to {}", onesourceBaseEndpoint + version + CONTRACT_DECLINE_ENDPOINT);
            restTemplate.exchange(onesourceBaseEndpoint + version + CONTRACT_DECLINE_ENDPOINT, POST,
                null, JsonNode.class, contract.getContractId());
            log.debug("The contract: {} was declined!", contract.getContractId());
        } catch (HttpStatusCodeException e) {
            String positionId = contract.getMatchingSpirePositionId();
            log.error(
                format(DECLINE_LOAN_PROPOSAL_EXCEPTION_MSG, contract.getContractId(), positionId, e.getStatusText()));
            contract.setProcessingStatus(SPIRE_ISSUE);
            if (Set.of(BAD_REQUEST, UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR).contains(e.getStatusCode())) {
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
            if (Set.of(BAD_REQUEST, UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR).contains(e.getStatusCode())) {
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
    public List<TradeEventDto> retrieveEvents(LocalDateTime timeStamp) {
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

    private List<TradeEventDto> executeGetEventsRequest(String url) {
        try {
            final ResponseEntity<List<TradeEventDto>> response = restTemplate.exchange(
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
        contractRepository.save(contract);
    }
}
