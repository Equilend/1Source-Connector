package com.intellecteu.onesource.integration.model.spire;

import com.intellecteu.onesource.integration.model.ProcessingStatus;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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
    private Integer matchingRerateId;
    private Long relatedPositionId;
    private String relatedContractId;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private TradeOut tradeOut;
}
