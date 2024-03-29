package com.intellecteu.onesource.integration.routes.delegate_flow.processor.strategy.contract;

import com.intellecteu.onesource.integration.model.enums.FlowStatus;
import com.intellecteu.onesource.integration.model.onesource.Contract;

public interface ContractProcessFlowStrategy {

    void process(Contract contract);

    FlowStatus getProcessFlow();

}
