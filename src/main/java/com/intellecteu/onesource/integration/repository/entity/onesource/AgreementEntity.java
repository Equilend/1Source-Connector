package com.intellecteu.onesource.integration.repository.entity.onesource;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.intellecteu.onesource.integration.model.enums.FlowStatus;
import com.intellecteu.onesource.integration.model.onesource.AgreementStatus;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
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
import java.time.LocalDateTime;
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
@Table(name = "agreement")
public class AgreementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "agreement_id") //ToDo: Shall it be unique?
    private String agreementId;
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private AgreementStatus status;
    @JsonAlias({"lastUpdateDatetime", "lastUpdateDateTime"})
    @Column(name = "last_update_datetime", columnDefinition = "TIMESTAMP")
    private LocalDateTime lastUpdateDatetime;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "trade_id")
    private TradeAgreementEntity trade;
    @Column(name = "event_type")
    @Enumerated(value = EnumType.STRING)
    private EventType eventType;
    @Column(name = "matching_spire_position_id")
    private String matchingSpirePositionId;
    @Column(name = "matching_1source_loan_contract_id")
    private String matching1SourceLoanContractId;
    @Column(name = "flow_status")
    @Enumerated(value = EnumType.STRING)
    private FlowStatus flowStatus;
    @Column(name = "processing_status")
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;
}
