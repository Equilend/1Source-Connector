package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.enums.FlowStatus.TRADE_DATA_RECEIVED;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_CANCELED;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_CREATED;
import static com.intellecteu.onesource.integration.exception.LoanContractCancelException.CANCEL_EXCEPTION_MESSAGE;
import static com.intellecteu.onesource.integration.model.ContractStatus.PROPOSED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_CANCELED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_CANCEL_PENDING;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_DECLINED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_OPENED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_PENDING;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_PROPOSED;
import static com.intellecteu.onesource.integration.model.EventType.TRADE_AGREED;
import static com.intellecteu.onesource.integration.model.EventType.TRADE_CANCELED;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.APPROVED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.DECLINED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.NEW;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.PROCESSED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.PROPOSAL_CANCELED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.PROPOSAL_DECLINED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SETTLED;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createGetPositionNQuery;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createListOfTuplesGetPosition;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.dto.spire.AndOr;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.EventType;
import com.intellecteu.onesource.integration.model.Participant;
import com.intellecteu.onesource.integration.model.ParticipantHolder;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.TradeEvent;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.ParticipantHolderRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.TimestampRepository;
import com.intellecteu.onesource.integration.repository.TradeEventRepository;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeEventService implements EventService {

    private final TradeEventRepository tradeEventRepository;
    private final AgreementRepository agreementRepository;
    private final ContractRepository contractRepository;
    private final PositionRepository positionRepository;
    private final TimestampRepository timestampRepository;
    private final ParticipantHolderRepository participantHolderRepository;
    private final EventMapper eventMapper;
    private final SpireService spireService;
    private final OneSourceService oneSourceService;
    private final CloudEventRecordService cloudEventRecordService;

    @Value("${camel.timestamp}")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime timeStamp;

    @Override
    public void processEventData() {
        log.debug(">>>>> Process event data!");
        List<TradeEvent> events = tradeEventRepository.findAllByProcessingStatus(CREATED);
        timeStamp = findMaxDateTimeOfEvents(events);
        storeTimestamp(timeStamp);
        log.debug("The latest timestamp: {}", timeStamp);
        processData(events);
        log.debug("<<<<< Processed {} events", events.size());
    }

    @Override
    public void cancelContract() {
        log.debug(">>>>> Starting the Cancel contract process.");
        Map<Contract, String> candidatesForCancelToPositionId = new HashMap<>();
        List<Contract> proposedContracts = contractRepository.findAllByContractStatus(PROPOSED);
        log.debug("Retrieved {} candidatesToCancel in Proposed status.", proposedContracts.size());
        for (Contract contract : proposedContracts) {
            log.debug("Requesting Spire position!");
            final String venueRefId = contract.getTrade().getVenue().getVenueRefKey();
            ResponseEntity<JsonNode> response = spireService.requestPosition(
                createGetPositionNQuery(null, AndOr.AND, true,
                    createListOfTuplesGetPosition("customValue2", "EQUALS", venueRefId, null)));
            var positionType = extractPositionType(response);
            var positionId = extractPositionId(response);
            if (isToCancelCandidate(contract, positionType, response)) {
                candidatesForCancelToPositionId.put(contract, positionId);
            }
        }
        log.debug("Executing cancellation for {} contracts", candidatesForCancelToPositionId.size());
        for (var candidateWithPositionId : candidatesForCancelToPositionId.entrySet()) {
            oneSourceService.cancelContract(candidateWithPositionId.getKey(), candidateWithPositionId.getValue());
        }
        log.debug("<<<<< Finishing Cancel contract process.");
    }

    private boolean isToCancelCandidate(Contract contract, String positionType, ResponseEntity<JsonNode> response) {
        Optional<PartyRole> partyRole = extractPartyRole(positionType);
        if (partyRole.isEmpty()) {
            log.debug(String.format(CANCEL_EXCEPTION_MESSAGE, contract.getContractId(), positionType));
            contract.setProcessingStatus(ProcessingStatus.ONESOURCE_ISSUE);
        }
        return partyRole
            .filter(role -> role == LENDER)
            .map(party -> isToCancelCandidate(contract, response))
            .orElse(false);
    }

    private String extractPositionType(ResponseEntity<JsonNode> response) {
        if (response.getBody() != null && response.getBody().get("data") != null
            && response.getBody().get("data").get("beans") != null
            && response.getBody().get("data").get("beans").get(0) != null) {
            JsonNode jsonNode = response.getBody().get("data").get("beans").get(0);
            JsonNode positionTypeDTO = jsonNode.get("positiontypeDTO");
            if (positionTypeDTO != null && positionTypeDTO.get("positionType") != null) {
                return positionTypeDTO.get("positionType").toString().trim();
            }
        }
        return "";
    }

    private String extractPositionId(ResponseEntity<JsonNode> response) {
        String positionId = "";
        if (response.getBody() != null && response.getBody().get("data") != null
            && response.getBody().get("data").get("beans") != null
            && response.getBody().get("data").get("beans").get(0) != null) {
            JsonNode jsonNode = response.getBody().get("data").get("beans").get(0);
            positionId = jsonNode.get("positionId").toString().replace("\"", "");
        }
        return positionId;
    }

    @Override
    public void processParties() {
        List<PartyDto> partyDtos = oneSourceService.retrieveParties();
        if (partyDtos == null) {
            return;
        }
        ParticipantHolder holder = participantHolderRepository.findAll().stream().findAny().orElse(null);
        if (holder == null) {
            List<Participant> participants = new ArrayList<>();
            partyDtos.forEach(i -> mapParticipants(participants, i));

            holder = ParticipantHolder.builder().participants(participants).build();
            ParticipantHolder finalHolder = holder;
            holder.getParticipants().forEach(participant -> participant.setParticipantHolder(finalHolder));
            participantHolderRepository.save(holder);
            return;
        }

        List<String> retrievedIds = partyDtos.stream().map(PartyDto::getGleifLei).toList();
        List<String> storedIds = holder.getParticipants().stream().map(Participant::getGleifLei).toList();

        updateParticipants(partyDtos, holder, retrievedIds, storedIds);
        participantHolderRepository.save(holder);
    }

    private void updateParticipants(List<PartyDto> retrievedParties, ParticipantHolder holder,
        List<String> retrievedIds, List<String> storedIds) {
        List<Participant> participants = holder.getParticipants();

        for (String gleiflei : retrievedIds) {
            startParticipantActuality(retrievedParties, holder, storedIds, gleiflei);
        }

        for (String gleiflei : storedIds) {
            endParticipantActuality(retrievedIds, gleiflei, participants);
        }
    }

    private void startParticipantActuality(List<PartyDto> retrievedParties, ParticipantHolder holder,
        List<String> storedIds, String gleiflei) {
        List<Participant> participants = holder.getParticipants();
        if (!storedIds.contains(gleiflei)) {
            retrievedParties.stream()
                .filter(p -> p.getGleifLei().equals(gleiflei))
                .findFirst()
                .ifPresent(party -> injectParticipantToHolder(party, holder));
        } else {
            participants.stream()
                .filter(p -> gleiflei.equals(p.getGleifLei()))
                .filter(p -> p.getParticipantEndDate() != null)
                .findFirst()
                .ifPresent(participant -> {
                    participant.setParticipantStartDate(LocalDateTime.now());
                    participant.setParticipantEndDate(null);
                });
        }
    }

    private void injectParticipantToHolder(PartyDto party, ParticipantHolder holder) {
        Participant participant = Participant.builder()
            .partyId(party.getPartyId())
            .partyName(party.getPartyName())
            .gleifLei(party.getGleifLei())
            .internalPartyId(party.getInternalPartyId())
            .participantStartDate(LocalDateTime.now())
            .participantEndDate(null)
            .build();

        holder.addParticipant(participant);
    }

    private static void endParticipantActuality(List<String> retrievedIds, String gleiflei,
        List<Participant> participants) {
        if (!retrievedIds.contains(gleiflei)) {
            participants.stream()
                .filter(i -> gleiflei.equals(i.getGleifLei()))
                .findFirst()
                .ifPresent(p -> p.setParticipantEndDate(LocalDateTime.now()));
        }
    }

    private void mapParticipants(List<Participant> participants, PartyDto partyDto) {
        Participant participant = Participant.builder()
            .partyId(partyDto.getPartyId())
            .partyName(partyDto.getPartyName())
            .gleifLei(partyDto.getGleifLei())
            .internalPartyId(partyDto.getInternalPartyId())
            .participantStartDate(LocalDateTime.now())
            .participantEndDate(null)
            .build();

        participants.add(participant);
    }

    private boolean isToCancelCandidate(Contract contract, ResponseEntity<JsonNode> response) {
        if (response.getBody() != null && response.getBody().get("data") != null
            && response.getBody().get("data").get("beans") != null
            && response.getBody().get("data").get("beans").get(0) != null) {
            JsonNode jsonNode = response.getBody().get("data").get("beans").get(0);
            JsonNode statusDTO = jsonNode.get("statusDTO");
            String status = eventMapper.getIfExist(statusDTO, "status");
            if (status.equalsIgnoreCase("CANCELED") || status.equalsIgnoreCase("FAILED")) {
                log.debug("Position has status {}! Submitting cancellation for contract: {}", status,
                    contract.getContractId());
                return true;
            }
        }
        return false;
    }

    private void processData(List<TradeEvent> events) {
        for (TradeEvent event : events) {
            if (Set.of(TRADE_AGREED).contains(event.getEventType())) {
                processTradeEvent(event);
            } else if (event.getEventType().equals(TRADE_CANCELED)) {
                processTradeCanceledEvent(event);
            } else if (Set.of(CONTRACT_PENDING, CONTRACT_CANCEL_PENDING, CONTRACT_DECLINED,
                CONTRACT_PROPOSED, CONTRACT_CANCELED).contains(event.getEventType())) {
                processContractEvent(event);
            }
        }
    }

    private void processTradeEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/agreements/93f834ff-66b5-4195-892b-8f316ed77010
        String eventUri = event.getResourceUri();
        AgreementDto agreementDto = oneSourceService.findTradeAgreement(eventUri, event.getEventType());
        if (agreementDto != null) {
            agreementDto.getTrade().setEventId(event.getEventId());
            agreementDto.getTrade().setResourceUri(event.getResourceUri());
            agreementDto.getTrade().setProcessingStatus(NEW);
            agreementDto.setEventType(event.getEventType());
            agreementDto.setFlowStatus(TRADE_DATA_RECEIVED);
            storeAgreement(agreementDto, event.getEventType());
            event.setProcessingStatus(PROCESSED);
            final TradeEvent savedTradeEvent = tradeEventRepository.save(event);
            var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
            var recordRequest = eventBuilder.buildRequest(TRADE_AGREEMENT_CREATED,
                savedTradeEvent.getResourceUri());
            cloudEventRecordService.record(recordRequest);
        }
    }

    private void processTradeCanceledEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/agreements/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        String agreementId = resourceUri.substring(resourceUri.lastIndexOf('/') + 1);
        List<Agreement> agreements = agreementRepository.findByAgreementId(agreementId);
        if (!agreements.isEmpty()) {
            Agreement agreement = agreements.get(0);
            agreement.setLastUpdateDatetime(LocalDateTime.now());
            agreement.setProcessingStatus(CANCELED);
            agreementRepository.save(agreement);
        }

        List<Position> positions = positionRepository.findByMatching1SourceTradeAgreementId(
            agreementId);
        if (!positions.isEmpty()) {
            Position position = positions.get(0);
            position.setProcessingStatus(CANCELED);
            position.setLastUpdateDateTime(LocalDateTime.now());
            positionRepository.save(position);
        }
        event.setProcessingStatus(PROCESSED);
        final TradeEvent savedTradeEvent = tradeEventRepository.save(event);
        createCloudEvent(agreements, positions, savedTradeEvent);
    }

    private void processContractEvent(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/contracts/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            oneSourceService.findContract(resourceUri)
                .ifPresent(contractDto -> processContract(contractDto, event));
        } catch (HttpStatusCodeException e) {
            log.debug("Contract {} was not found. Details: {} ", resourceUri, e.getMessage());
            if (Set.of(UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR).contains(e.getStatusCode())) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                var recordRequest = eventBuilder.buildExceptionRequest(resourceUri,
                    e, GET_LOAN_CONTRACT_PROPOSAL, String.valueOf(event.getEventId()));
                cloudEventRecordService.record(recordRequest);
            }
        }
    }

    private void processContract(ContractDto contractDto, TradeEvent event) {
        contractDto.getTrade().setEventId(event.getEventId());
        contractDto.getTrade().setResourceUri(event.getResourceUri());
        contractDto.setEventType(event.getEventType());
        contractDto.setProcessingStatus(NEW);
        contractDto.setFlowStatus(TRADE_DATA_RECEIVED);
        processContractByEventType(contractDto, event.getEventType());
        storeContract(contractDto);
        event.setProcessingStatus(PROCESSED);
        tradeEventRepository.save(event);
    }

    private Agreement storeAgreement(AgreementDto agreementDto, EventType eventType) {
        Agreement agreementEntity = eventMapper.toAgreementEntity(agreementDto);
        if (eventType.equals(TRADE_AGREED)) {
            agreementEntity.setLastUpdateDatetime(LocalDateTime.now());
            agreementEntity.setProcessingStatus(CREATED);
        }
        return agreementRepository.save(agreementEntity);
    }

    private Contract storeContract(ContractDto contractDto) {
        Contract contractEntity = eventMapper.toContractEntity(contractDto);
        return contractRepository.save(contractEntity);
    }

    private void storeTimestamp(LocalDateTime timestamp) {
        timestampRepository.save(eventMapper.toTimestampEntity(timestamp));
    }

    private LocalDateTime findMaxDateTimeOfEvents(List<TradeEvent> events) {
        LocalDateTime localDateTime = timeStamp;
        if (events != null && !events.isEmpty()) {
            localDateTime = events.stream()
                .map(TradeEvent::getEventDatetime)
                .max(LocalDateTime::compareTo)
                .get();
        }
        return localDateTime;
    }

    private void createCloudEvent(List<Agreement> agreements, List<Position> positions, TradeEvent savedTradeEvent) {
        if (positions.isEmpty()) {
            var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
            var recordRequest = eventBuilder.buildRequest(TRADE_AGREEMENT_CANCELED, savedTradeEvent.getResourceUri());
            cloudEventRecordService.record(recordRequest);
        } else if (!agreements.isEmpty()) {
            Agreement agreement = agreements.get(0);
            var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
            var recordRequest = eventBuilder.buildRequest(savedTradeEvent.getResourceUri(), TRADE_AGREEMENT_CANCELED,
                agreement.getMatchingSpirePositionId());
            cloudEventRecordService.record(recordRequest);
        }
    }

    private void processContractByEventType(ContractDto contractDto, EventType eventType) {
        contractDto.setLastUpdateDatetime(LocalDateTime.now());
        String venueRefId = contractDto.getTrade().getExecutionVenue().getVenueRefKey();
        Optional<Position> position = positionRepository.findByVenueRefId(venueRefId).stream().findFirst();

        if (eventType == CONTRACT_CANCELED) {
            contractDto.setProcessingStatus(CANCELED);
            position.ifPresent(p -> savePositionStatus(p, PROPOSAL_CANCELED));
        }
        if (Set.of(CONTRACT_CANCEL_PENDING, CONTRACT_DECLINED).contains(eventType)) {
            contractDto.setProcessingStatus(DECLINED);
            position.ifPresent(p -> savePositionStatus(p, PROPOSAL_DECLINED));
        }
        if (eventType == CONTRACT_PENDING) {
            contractDto.setProcessingStatus(APPROVED);
            position.ifPresent(p -> savePositionStatus(p, PROPOSAL_APPROVED));
        }
        if (eventType == CONTRACT_OPENED) {
            contractDto.setProcessingStatus(SETTLED);
            contractDto.setLastUpdateDatetime(LocalDateTime.now());
        }
    }

    private void savePositionStatus(@NonNull Position position, @NonNull ProcessingStatus status) {
        position.setProcessingStatus(status);
        position.setLastUpdateDateTime(LocalDateTime.now());
        positionRepository.save(position);
    }
}
