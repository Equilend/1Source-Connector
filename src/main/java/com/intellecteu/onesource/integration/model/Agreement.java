package com.intellecteu.onesource.integration.model;

import java.time.LocalDateTime;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.intellecteu.onesource.integration.enums.FlowStatus;
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
@Table(name = "agreement")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Agreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "agreement_id") //ToDo: Shall it be unique?
    private String agreementId;
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private AgreementStatus status;
    @JsonAlias({"lastUpdateDatetime", "lastUpdateDateTime"})
    @Column(name = "last_update_datetime", columnDefinition = "TIMESTAMP")
    private LocalDateTime lastUpdateDatetime;
    @OneToOne(fetch = FetchType.LAZY, cascade= CascadeType.ALL)
    @JoinColumn(name = "trade_id")
    private TradeAgreement trade;
    @Column(name = "event_type")
    @Enumerated(value = EnumType.STRING)
    private EventType eventType;
    @Column(name = "flow_status")
    @Enumerated(value = EnumType.STRING)
    private FlowStatus flowStatus;
}
