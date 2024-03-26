package com.intellecteu.onesource.integration.routes.delegate_flow.processor.strategy.agreement;

import com.intellecteu.onesource.integration.model.enums.FlowStatus;
import com.intellecteu.onesource.integration.model.onesource.Agreement;

public interface AgreementProcessFlowStrategy {

    void process(Agreement agreement);

    FlowStatus getProcessFlow();

}
