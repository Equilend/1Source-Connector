package com.intellecteu.onesource.integration.routes.processor.strategy.agreement;

import static com.intellecteu.onesource.integration.model.ProcessingStatus.RECONCILED;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.services.AgreementService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SpireService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class AgreementPositionRetrieved extends AbstractAgreementProcessStrategy {

    private final SpireMapper spireMapper;

    @Override
    @Transactional
    public void process(AgreementDto agreement) {
        var venueRefId = agreement.getTrade().getExecutionVenue().getVenueRefKey();
        positionService.findByVenueRefId(venueRefId)
            .ifPresent(position -> {
                var positionDto = spireMapper.toPositionDto(position);
                var processingStatus = agreement.getTrade().getProcessingStatus();
                log.debug("Start reconciliation from AgreementPositionRetrieved strategy");
                reconcile(agreement, positionDto);
                if (processingStatus == RECONCILED) {
                    processAgreement(agreement, positionDto);
                }
                saveAgreementWithStage(agreement, FlowStatus.PROCESSED);
            });
    }

    @Override
    public FlowStatus getProcessFlow() {
        return FlowStatus.POSITION_RETRIEVED;
    }

    public AgreementPositionRetrieved(OneSourceService oneSourceService, SpireService spireService,
        ReconcileService<AgreementDto, PositionDto> agreementReconcileService, AgreementService agreementService,
        PositionService positionService, EventMapper eventMapper, SpireMapper spireMapper,
        CloudEventRecordService cloudEventRecordService) {
        super(oneSourceService,
            spireService,
            agreementReconcileService,
            agreementService,
            positionService,
            eventMapper,
            spireMapper,
            cloudEventRecordService);
        this.spireMapper = spireMapper;
    }
}
