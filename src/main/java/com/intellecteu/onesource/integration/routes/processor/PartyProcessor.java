package com.intellecteu.onesource.integration.routes.processor;

import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.model.Participant;
import com.intellecteu.onesource.integration.model.ParticipantHolder;
import com.intellecteu.onesource.integration.repository.ParticipantHolderRepository;
import com.intellecteu.onesource.integration.services.OneSourceService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PartyProcessor {

    private final ParticipantHolderRepository participantHolderRepository;
    private final OneSourceService oneSourceService;

    @Autowired
    public PartyProcessor(ParticipantHolderRepository participantHolderRepository, OneSourceService oneSourceService) {
        this.participantHolderRepository = participantHolderRepository;
        this.oneSourceService = oneSourceService;
    }

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

}
