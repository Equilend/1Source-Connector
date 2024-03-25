package com.intellecteu.onesource.integration.repository.entity.onesource;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.ContractStatus;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
public class ContractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "contract_id")
    private String contractId;
    @Column(name = "processing_status")
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;
    @Column(name = "matching_1source_trade_agreement_id")
    private String matching1SourceTradeAgreementId;
    @Column(name = "matching_spire_position_id")
    private Long matchingSpirePositionId;
    @Column(name = "matching_spire_trade_id")
    private Long matchingSpireTradeId;
    @Column(name = "contract_status")
    @Enumerated(value = EnumType.STRING)
    private ContractStatus contractStatus;
    @Column(name = "last_update_party_id")
    private String lastUpdatePartyId;
    @JsonAlias({"createDateTime", "createDatetime"})
    @Column(name = "create_datetime", columnDefinition = "TIMESTAMP")
    private LocalDateTime createDateTime;
    @JsonAlias({"lastUpdateDatetime", "lastUpdateDateTime"})
    @Column(name = "last_update_datetime", columnDefinition = "TIMESTAMP")
    private LocalDateTime lastUpdateDateTime;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "trade_id", referencedColumnName = "id")
    private TradeAgreementEntity trade;
    @OneToMany(mappedBy = "contract", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SettlementEntity> settlement = new ArrayList<>();

    public void addSettlement(SettlementEntity settlement) {
        this.settlement.add(settlement);
        settlement.setContract(this);
    }

}
