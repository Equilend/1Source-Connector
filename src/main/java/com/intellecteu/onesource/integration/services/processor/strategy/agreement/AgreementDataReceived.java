package com.intellecteu.onesource.integration.services.processor.strategy.agreement;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.model.EventType;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SpireService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.intellecteu.onesource.integration.enums.FlowStatus.POSITION_RETRIEVED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.RECONCILED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SPIRE_ISSUE;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TO_CANCEL;

@Component
@Slf4j
public class AgreementDataReceived extends AbstractAgreementProcessStrategy {

  @Override
  @Transactional
  public void process(AgreementDto agreement) {
    var positionDto = spireService.getTradePosition(agreement);
    if (positionDto == null) {
      log.warn("No position for agreement: {}. Skipping processing", agreement.getAgreementId());
      agreement.getTrade().setProcessingStatus(SPIRE_ISSUE);
      saveAgreementWithStage(agreement, FlowStatus.PROCESSED);
      return;
    }
    saveAgreementWithStage(agreement, POSITION_RETRIEVED);
    log.debug("Start reconciliation from AgreementDataReceived strategy");
    reconcile(agreement, positionDto);
    if (agreement.getTrade().getProcessingStatus() == RECONCILED) {
      processAgreement(agreement, positionDto);
      agreement.setEventType(EventType.TRADE);
      saveAgreementWithStage(agreement, FlowStatus.PROCESSED);
    } else if (agreement.getTrade().getProcessingStatus() == TO_CANCEL) {
      agreement.setEventType(EventType.TRADE);
      saveAgreementWithStage(agreement, FlowStatus.PROCESSED);
    }
  }

  @Override
  public FlowStatus getProcessFlow() {
    return FlowStatus.TRADE_DATA_RECEIVED;
  }

  public AgreementDataReceived(OneSourceService oneSourceService, SpireService spireService,
      ReconcileService reconcileService, AgreementRepository agreementRepository,
      EventMapper eventMapper, CloudEventRecordService cloudEventRecordService) {
    super(oneSourceService, spireService, reconcileService, agreementRepository, eventMapper, cloudEventRecordService);
  }
}
