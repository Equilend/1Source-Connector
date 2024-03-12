package com.intellecteu.onesource.integration.repository.entity.backoffice;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "rerate_trade")
public class RerateTradeEntity {

    @Id
    private Long tradeId;
    private LocalDateTime creationDatetime;
    private LocalDateTime lastUpdateDatetime;
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;
    private String matchingRerateId;
    private Long relatedPositionId;
    private String relatedContractId;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private TradeOutEntity tradeOut;
}
