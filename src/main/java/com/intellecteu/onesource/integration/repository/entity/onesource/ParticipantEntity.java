package com.intellecteu.onesource.integration.repository.entity.onesource;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "participant")
public class ParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "party_id")
    private String partyId;
    @Column(name = "party_name")
    private String partyName;
    @Column(name = "gleifLei")
    private String gleifLei;
    @Column(name = "internal_id")
    private String internalPartyId;
    @Column(name = "participant_start_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime participantStartDate;
    @Column(name = "participant_end_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime participantEndDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_holder_id")
    private ParticipantHolderEntity participantHolder;
}
