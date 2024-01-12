package com.intellecteu.onesource.integration.routes.processor.strategy.agreement;

import static com.intellecteu.onesource.integration.enums.FlowStatus.POSITION_RETRIEVED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SPIRE_ISSUE;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
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
        if (agreement.getProcessingStatus() == ProcessingStatus.CREATED) {
            String venueRefId = agreement.getTrade().getExecutionVenue().getVenueRefKey();
            positionService.findByVenueRefId(venueRefId)
                .ifPresent(position -> {
                    processMatchingPosition(agreement, spireMapper.toPositionDto(position));
//                    log.debug("Start reconciliation from AgreementDataReceived strategy");
//                    reconcile(agreement, spireMapper.toPositionDto(position));
                });
        }
// todo temporary commented out potentially obsolete flow

//        if (agreement.getTrade().getProcessingStatus() == RECONCILED) {
//            processAgreement(agreement, positionDto);
//            agreement.setEventType(EventType.TRADE_AGREED);
//            saveAgreementWithStage(agreement, FlowStatus.PROCESSED);
//        } else if (agreement.getTrade().getProcessingStatus() == DISCREPANCIES) {
//            agreement.setEventType(EventType.TRADE_AGREED);
//            saveAgreementWithStage(agreement, FlowStatus.PROCESSED);
//        }
    }

    @Override
    public FlowStatus getProcessFlow() {
        return FlowStatus.TRADE_DATA_RECEIVED;
    }

    public AgreementDataReceived(OneSourceService oneSourceService, SpireService spireService,
        ReconcileService<AgreementDto, PositionDto> agreementReconcileService, AgreementService agreementService,
        PositionService positionService,
        EventMapper eventMapper, SpireMapper spireMapper, CloudEventRecordService cloudEventRecordService) {
        super(oneSourceService, spireService, agreementReconcileService, agreementService,
            positionService, eventMapper, spireMapper, cloudEventRecordService);
    }
}
