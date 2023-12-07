package com.intellecteu.onesource.integration.services.processor.strategy.contract;

import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;

public interface ContractProcessFlowStrategy {

    void process(ContractDto contract);

    FlowStatus getProcessFlow();

}
