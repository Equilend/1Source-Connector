package com.intellecteu.onesource.integration.model.backoffice;

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

@Getter
@Setter
public class RerateTrade {

    private Long tradeId;
    private LocalDateTime lastUpdateDatetime;
    private ProcessingStatus processingStatus;
    private String matchingRerateId;
    private Long relatedPositionId;
    private String relatedContractId;
    private TradeOut tradeOut;

}
