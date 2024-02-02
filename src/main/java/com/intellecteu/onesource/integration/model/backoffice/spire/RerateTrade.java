package com.intellecteu.onesource.integration.model.backoffice.spire;

import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
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
public class RerateTrade {

    @Id
    private Long tradeId;
    private LocalDateTime lastUpdateDatetime;
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;
    private String matchingRerateId;
    private Long relatedPositionId;
    private String relatedContractId;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private TradeOut tradeOut;
}
