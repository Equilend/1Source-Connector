package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractProposalDto {

    @JsonProperty("trade")
    private TradeAgreementDto trade;
    @JsonProperty("settlement")
    private List<SettlementDto> settlement;

    @JsonCreator
    public ContractProposalDto(@JsonProperty("trade") TradeAgreementDto trade,
        @JsonProperty("settlement") List<SettlementDto> settlement) {
        this.trade = trade;
        this.settlement = settlement;
    }
}
