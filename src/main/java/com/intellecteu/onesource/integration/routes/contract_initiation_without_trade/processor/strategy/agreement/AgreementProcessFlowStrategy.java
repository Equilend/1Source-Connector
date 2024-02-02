package com.intellecteu.onesource.integration.routes.contract_initiation_without_trade.processor.strategy.agreement;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.model.enums.FlowStatus;

public interface AgreementProcessFlowStrategy {

    void process(AgreementDto agreement);

    FlowStatus getProcessFlow();

}
