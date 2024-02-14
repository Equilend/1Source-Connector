package com.intellecteu.onesource.integration.repository.entity.onesource;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "participant_holder")
public class ParticipantHolderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "participantHolder", orphanRemoval = true)
    private List<ParticipantEntity> participants;

    public void addParticipant(ParticipantEntity participant) {
        participants.add(participant);
        participant.setParticipantHolder(this);
    }

    public void removeParticipant(ParticipantEntity participant) {
        participants.remove(participant);
        participant.setParticipantHolder(null);
    }
}
