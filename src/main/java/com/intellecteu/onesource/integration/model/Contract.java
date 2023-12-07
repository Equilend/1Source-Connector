package com.intellecteu.onesource.integration.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
@Table(name = "contract")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "contract_id")
    private String contractId;
    @Column(name = "last_event_id")
    private Long lastEventId;
    @Column(name = "contract_status")
    @Enumerated(value = EnumType.STRING)
    private ContractStatus contractStatus;
    @Column(name = "settlement_status")
    @Enumerated(value = EnumType.STRING)
    private SettlementStatus settlementStatus;
    @Column(name = "last_update_party_id")
    private String lastUpdatePartyId;
    @JsonAlias({"lastUpdateDatetime", "lastUpdateDateTime"})
    @Column(name = "last_update_datetime", columnDefinition = "TIMESTAMP")
    private LocalDateTime lastUpdateDatetime;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "trade_id")
    private TradeAgreement trade;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "settlement_id")
    private List<Settlement> settlement;
    @Column(name = "processing_status")
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;
    @Column(name = "event_type")
    @Enumerated(value = EnumType.STRING)
    private EventType eventType;
    @Column(name = "matching_spire_position_id")
    String matchingSpirePositionId;
    @Column(name = "flow_status")
    @Enumerated(value = EnumType.STRING)
    private FlowStatus flowStatus;
}
