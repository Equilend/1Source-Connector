package com.intellecteu.onesource.integration.services.processor;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.services.processor.strategy.agreement.AgreementProcessFlowStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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
        .map(mapper::toAgreementDto)
        .forEach(this::processAgreement);
  }

  private void processAgreement(AgreementDto agreement) {
    log.debug("***** Processing Trade Agreement Id: {}, Agreement last updated datetime: {}",
        agreement.getAgreementId(), agreement.getLastUpdateDatetime());
    var strategy = strategyByFlow.get(agreement.getFlowStatus());
    if (strategy == null) {
      throw new NotYetImplementedException("Strategy is not implemented for the flow: " + agreement.getFlowStatus());
    }
    strategy.process(agreement);
  }
}
