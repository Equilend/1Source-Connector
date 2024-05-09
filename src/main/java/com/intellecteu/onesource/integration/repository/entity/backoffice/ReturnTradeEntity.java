package com.intellecteu.onesource.integration.repository.entity.backoffice;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "return_trade")
public class ReturnTradeEntity {

    @Id
    private Long tradeId;
    private Long cancelingTradeId;
    private LocalDateTime creationDatetime;
    private LocalDateTime lastUpdateDatetime;
    private String matching1SourceReturnId;
    private Long relatedPositionId;
    private String relatedContractId;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private TradeOutEntity tradeOut;
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;
}
