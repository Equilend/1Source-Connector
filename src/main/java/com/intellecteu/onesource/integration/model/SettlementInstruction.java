package com.intellecteu.onesource.integration.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "settlement_instruction")
public class SettlementInstruction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "settlement_bic")
    private String settlementBic;
    @Column(name = "local_agent_bic")
    private String localAgentBic;
    @Column(name = "local_agent_name")
    private String localAgentName;
    @Column(name = "local_agent_acct")
    private String localAgentAcct;
    @Column(name = "dtc_participant_number")
    private String dtcParticipantNumber;
}
