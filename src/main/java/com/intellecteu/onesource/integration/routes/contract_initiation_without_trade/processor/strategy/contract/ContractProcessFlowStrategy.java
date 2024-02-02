package com.intellecteu.onesource.integration.routes.contract_initiation_without_trade.processor.strategy.contract;

import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.model.enums.FlowStatus;

public interface ContractProcessFlowStrategy {

    void process(ContractDto contract);

    FlowStatus getProcessFlow();

}
