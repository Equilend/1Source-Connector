package com.intellecteu.onesource.integration.services.processor.strategy.agreement;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;

public interface AgreementProcessFlowStrategy {

    void process(AgreementDto agreement);

    FlowStatus getProcessFlow();

}
