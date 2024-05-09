package com.intellecteu.onesource.integration.model.backoffice;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnTrade {
    private Long tradeId;
    private Long cancelingTradeId;
    private LocalDateTime creationDatetime;
    private LocalDateTime lastUpdateDatetime;
    private String matching1SourceReturnId;
    private Long relatedPositionId;
    private String relatedContractId;
    private TradeOut tradeOut;
    private ProcessingStatus processingStatus;
}
