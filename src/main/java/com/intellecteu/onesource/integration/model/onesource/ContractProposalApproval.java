package com.intellecteu.onesource.integration.model.onesource;

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
public class ContractProposalApproval {

    private String internalRefId;
    private Integer roundingRule;
    private SettlementInstructionUpdate settlement;

}
