package com.intellecteu.onesource.integration.model.onesource;

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
public class ContractProposal {

    private TradeAgreement trade;
    private List<Settlement> settlementList;

}
