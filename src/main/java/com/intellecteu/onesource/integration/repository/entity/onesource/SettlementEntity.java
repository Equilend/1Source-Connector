package com.intellecteu.onesource.integration.repository.entity.onesource;

import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.SettlementStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "settlement")
public class SettlementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "instruction_id")
    private Long instructionId;
    @Column(name = "party_role")
    @Enumerated(value = EnumType.STRING)
    private PartyRole partyRole;
    @Column(name = "settlement_status")
    @Enumerated(value = EnumType.STRING)
    private SettlementStatus settlementStatus;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "instruction")
    private SettlementInstructionEntity instruction;
    @Column(name = "internal_acct_cd")
    private String internalAcctCd;
    @Column(name = "dtc_participant_number")
    private String dtcParticipantNumber;
    @Column(name = "cds_customer_unit_id")
    private String cdsCustomerUnitId;
    @Column(name = "custodian_name")
    private String custodianName;
    @Column(name = "custodian_bic")
    private String custodianBic;
    @Column(name = "custodian_acct")
    private String custodianAcct;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "contract_id", referencedColumnName = "id")
//    @ToString.Exclude
//    private ContractEntity contract;

}
