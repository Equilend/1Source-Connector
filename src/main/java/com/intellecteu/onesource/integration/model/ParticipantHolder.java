package com.intellecteu.onesource.integration.model;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "participant_holder")
public class ParticipantHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "participantHolder", orphanRemoval = true)
    private List<Participant> participants;

    public void addParticipant(Participant participant) {
        participants.add(participant);
        participant.setParticipantHolder(this);
    }

    public void removeParticipant(Participant participant) {
        participants.remove(participant);
        participant.setParticipantHolder(null);
    }
}
