package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ContractDetails {

    private String contractId;
    private TradeEvent lastEvent;
    private ContractStatus contractStatus;
    private String lastUpdatePartyId;
    @JsonAlias({"lastUpdateDatetime", "lastUpdateDateTime"})
    private LocalDateTime lastUpdateDateTime;
    private TradeAgreement trade;
    // Expected one settlement for each party role
    private List<Settlement> settlement;

}
