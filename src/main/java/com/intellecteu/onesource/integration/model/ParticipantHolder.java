package com.intellecteu.onesource.integration.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
