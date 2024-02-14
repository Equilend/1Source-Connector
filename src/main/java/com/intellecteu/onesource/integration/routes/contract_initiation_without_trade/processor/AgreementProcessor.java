package com.intellecteu.onesource.integration.routes.contract_initiation_without_trade.processor;

import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.model.enums.FlowStatus;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.routes.contract_initiation_without_trade.processor.strategy.agreement.AgreementProcessFlowStrategy;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgreementProcessor implements TradeDataProcessor {

    private final Map<FlowStatus, AgreementProcessFlowStrategy> strategyByFlow;

    private final AgreementRepository agreementRepository;
    private final EventMapper mapper;

    @Override
    @Transactional
    public void processTradeData() {
        agreementRepository.findAllNotProcessed().stream()
            .map(mapper::toAgreement)
            .forEach(this::processAgreement);
    }

    private void processAgreement(Agreement agreement) {
        log.debug("***** Processing Trade Agreement Id: {}, Agreement last updated datetime: {}",
            agreement.getAgreementId(), agreement.getLastUpdateDateTime());
        var strategy = Optional.ofNullable(strategyByFlow.get(agreement.getFlowStatus())).orElseThrow(
            () -> new RuntimeException(
                "Strategy is not implemented for the flow: " + agreement.getFlowStatus()));
        strategy.process(agreement);
    }
}
