package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.enums.FlowStatus.TRADE_DATA_RECEIVED;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_CREATED;
import static com.intellecteu.onesource.integration.exception.DataMismatchException.LEI_MISMATCH_MSG;
import static com.intellecteu.onesource.integration.exception.LoanContractCancelException.CANCEL_EXCEPTION_MESSAGE;
import static com.intellecteu.onesource.integration.model.ContractStatus.PROPOSED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_APPROVE;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_DECLINE;
import static com.intellecteu.onesource.integration.model.EventType.TRADE;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.NEW;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.PROCESSED;
import static com.intellecteu.onesource.integration.utils.ApiUtils.createGetPositionNQuery;
import static com.intellecteu.onesource.integration.utils.ApiUtils.createListOfTuplesGetPosition;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.dto.spire.AndOr;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.Participant;
import com.intellecteu.onesource.integration.model.ParticipantHolder;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.TradeEvent;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.ParticipantHolderRepository;
import com.intellecteu.onesource.integration.repository.TimestampRepository;
import com.intellecteu.onesource.integration.repository.TradeEventRepository;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeEventService implements EventService {

    private final TradeEventRepository tradeEventRepository;
    private final AgreementRepository agreementRepository;
    private final ContractRepository contractRepository;
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
        List<TradeEvent> events = tradeEventRepository.findAllByProcessingStatus(NEW);
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
            final String venueRefId = contract.getTrade().getVenue().getPlatform().getVenueRefId();
            ResponseEntity<JsonNode> response = spireService.requestPosition(createGetPositionNQuery(null, AndOr.AND, true, createListOfTuplesGetPosition("customValue2", "EQUALS", venueRefId, null)));
            var positionLei = extractPositionLei(response);
            var positionId = extractPositionId(response);
            if (isToCancelCandidate(contract, positionLei, response)) {
                candidatesForCancelToPositionId.put(contract, positionId);
            }
        }
        log.debug("Executing cancellation for {} contracts", candidatesForCancelToPositionId.size());
        for (var candidateWithPositionId : candidatesForCancelToPositionId.entrySet()) {
            oneSourceService.cancelContract(candidateWithPositionId.getKey(), candidateWithPositionId.getValue());
        }
        log.debug("<<<<< Finishing Cancel contract process.");
    }

    private boolean isToCancelCandidate(Contract contract, String positionLei, ResponseEntity<JsonNode> response) {
        final ContractDto contractDto = eventMapper.toContractDto(contract);
        final PartyRole partyRole = extractPartyRole(contractDto.getTrade().getTransactingParties(), positionLei);
        if (partyRole == null) {
            log.debug(String.format(CANCEL_EXCEPTION_MESSAGE, contract.getContractId(),
                String.format(LEI_MISMATCH_MSG, positionLei)));
            contract.setProcessingStatus(ProcessingStatus.ONESOURCE_ISSUE);
        } else if (partyRole == LENDER) {
            log.debug("Processing Spire position response for contract: {}", contract.getContractId());
            return isToCancelCandidate(contract, response);
        }
        return false;
    }

    private String extractPositionLei(ResponseEntity<JsonNode> response) {
        if (response.getBody() != null && response.getBody().get("data") != null
            && response.getBody().get("data").get("beans") != null
            && response.getBody().get("data").get("beans").get(0) != null) {
            JsonNode jsonNode = response.getBody().get("data").get("beans").get(0);
            JsonNode accountDto = jsonNode.get("accountDTO");
            if (accountDto != null && accountDto.get("lei") != null) {
                return accountDto.get("lei").toString().replace("\"", "");
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

    private static void startParticipantActuality(List<PartyDto> retrievedParties, ParticipantHolder holder,
        List<String> storedIds, String gleiflei) {
        List<Participant> participants = holder.getParticipants();
        if (!storedIds.contains(gleiflei)) {
            PartyDto party = retrievedParties.stream().filter(i -> i.getGleifLei().equals(gleiflei))
                .findFirst().get();

            Participant participant = Participant.builder()
                .partyId(party.getPartyId())
                .partyName(party.getPartyName())
                .gleifLei(party.getGleifLei())
                .internalPartyId(party.getInternalPartyId())
                .participantStartDate(LocalDateTime.now())
                .participantEndDate(null)
                .build();

            holder.addParticipant(participant);
        } else {
            Participant participant = participants.stream().filter(i -> gleiflei.equals(i.getGleifLei()))
                .findFirst().get();
            if (participant.getParticipantEndDate() != null) {
                participant.setParticipantStartDate(LocalDateTime.now());
                participant.setParticipantEndDate(null);
            }
        }
    }

    private static void endParticipantActuality(List<String> retrievedIds, String gleiflei,
        List<Participant> participants) {
        if (!retrievedIds.contains(gleiflei)) {

            Participant participant = participants.stream().filter(i -> gleiflei.equals(i.getGleifLei()))
                .findFirst().get();

            participant.setParticipantEndDate(LocalDateTime.now());
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
            if (event.getEventType().equals(TRADE)) {
                // expected format for resourceUri: /v1/ledger/agreements/93f834ff-66b5-4195-892b-8f316ed77010
                String eventUri = event.getResourceUri();
                AgreementDto agreementDto = oneSourceService.findTradeAgreement(eventUri, event.getEventType());
                if (agreementDto != null) {
                    agreementDto.getTrade().setEventId(event.getEventId());
                    agreementDto.getTrade().setResourceUri(event.getResourceUri());
                    agreementDto.setEventType(event.getEventType());
                    agreementDto.setFlowStatus(TRADE_DATA_RECEIVED);
                    storeAgreement(agreementDto);
                    event.setProcessingStatus(PROCESSED);
                    final TradeEvent savedTradeEvent = tradeEventRepository.save(event);
                    var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                    var recordRequest = eventBuilder.buildRequest(TRADE_AGREEMENT_CREATED,
                        savedTradeEvent.getResourceUri());
                    cloudEventRecordService.record(recordRequest);
                }
            } else if (Set.of(CONTRACT, CONTRACT_APPROVE, CONTRACT_DECLINE).contains(event.getEventType())) {
                // expected format for resourceUri: /v1/ledger/contracts/93f834ff-66b5-4195-892b-8f316ed77006
                String resourceUri = event.getResourceUri();
                ContractDto contractDto = oneSourceService.findContract(resourceUri);
                if (contractDto != null) {
                    contractDto.getTrade().setEventId(event.getEventId());
                    contractDto.getTrade().setResourceUri(event.getResourceUri());
                    contractDto.setEventType(event.getEventType());
                    contractDto.setFlowStatus(TRADE_DATA_RECEIVED);
                    storeContract(contractDto);
                    event.setProcessingStatus(PROCESSED);
                    tradeEventRepository.save(event);
                }
            }
        }
    }

    private Agreement storeAgreement(AgreementDto agreementDto) {
        Agreement agreementEntity = eventMapper.toAgreementEntity(agreementDto);
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
}
