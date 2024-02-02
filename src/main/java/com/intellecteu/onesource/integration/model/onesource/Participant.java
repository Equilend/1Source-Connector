package com.intellecteu.onesource.integration.model.onesource;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    private Long id;
    private String partyId;
    private String partyName;
    private String gleifLei;
    private String internalPartyId;
    private LocalDateTime participantStartDate;
    private LocalDateTime participantEndDate;
    private ParticipantHolder participantHolder;

}
