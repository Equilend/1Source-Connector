package com.intellecteu.onesource.integration.routes.processor.strategy.agreement;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SpireService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class AgreementInstructionsRetrieved extends AbstractAgreementProcessStrategy {

    @Override
    @Transactional
    public void process(AgreementDto agreement) {
        log.debug("Skipping processing AgreementInstructionsRetrieved as not yet implemented!");
    }

    @Override
    public FlowStatus getProcessFlow() {
        return FlowStatus.AGR_INSTRUCTIONS_RETRIEVED;
    }

    public AgreementInstructionsRetrieved(OneSourceService oneSourceService,
        SpireService spireService,
        ReconcileService<AgreementDto, PositionDto> agreementReconcileService,
        AgreementRepository agreementRepository,
        PositionRepository positionRepository,
        EventMapper eventMapper,
        SpireMapper spireMapper,
        CloudEventRecordService cloudEventRecordService) {
        super(oneSourceService, spireService, agreementReconcileService, agreementRepository, positionRepository,
            eventMapper, spireMapper, cloudEventRecordService);
    }
}
