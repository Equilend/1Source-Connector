package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.intellecteu.onesource.integration.model.enums.FlowStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Agreement {

    private Long id;
    private String agreementId;
    private AgreementStatus status;
    private LocalDateTime lastUpdateDatetime;
    private TradeAgreement trade;
    private EventType eventType;
    private String matchingSpirePositionId;
    private String matching1SourceLoanContractId;
    private FlowStatus flowStatus;
    private ProcessingStatus processingStatus;

}
