package com.intellecteu.onesource.integration.repository.entity.onesource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "settlement_instruction")
public class SettlementInstructionEntity {

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
