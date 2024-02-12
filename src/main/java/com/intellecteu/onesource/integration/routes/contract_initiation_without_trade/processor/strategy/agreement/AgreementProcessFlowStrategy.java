package com.intellecteu.onesource.integration.routes.contract_initiation_without_trade.processor.strategy.agreement;

import com.intellecteu.onesource.integration.model.enums.FlowStatus;
import com.intellecteu.onesource.integration.model.onesource.Agreement;

public interface AgreementProcessFlowStrategy {

    void process(Agreement agreement);

    FlowStatus getProcessFlow();

}
