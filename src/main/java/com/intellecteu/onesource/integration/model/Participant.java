package com.intellecteu.onesource.integration.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "participant")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
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
    private ParticipantHolder participantHolder;
}
