package com.intellecteu.onesource.integration.routes.contract_initiation.delegate_flow.processor.strategy.agreement;

import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.enums.FlowStatus;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.services.AgreementService;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.client.onesource.OneSourceApiClient;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@Deprecated(since = "flow II refactoring. Fault tolerance support is moved to routes.", forRemoval = true)
public class AgreementPositionRetrieved extends AbstractAgreementProcessStrategy {

    private final SpireMapper spireMapper;

    @Override
    @Transactional
    public void process(Agreement agreement) {
        log.warn("This is deprecated method! Should be removed soon!");
//        var venueRefId = agreement.getTrade().getExecutionVenue().getVenueRefKey();
//        positionService.findByVenueRefId(venueRefId)
//            .ifPresent(position -> {
//                var positionDto = spireMapper.toPositionDto(position);
//                var processingStatus = agreement.getTrade().getProcessingStatus();
//                log.debug("Start reconciliation from AgreementPositionRetrieved strategy");
//                reconcile(agreement, positionDto);
//                if (processingStatus == RECONCILED) {
//                    processAgreement(agreement, positionDto);
//                }
//                saveAgreementWithStage(agreement, FlowStatus.PROCESSED);
//            });
    }

    @Override
    public FlowStatus getProcessFlow() {
        return FlowStatus.POSITION_RETRIEVED;
    }

    public AgreementPositionRetrieved(OneSourceApiClient oneSourceApiClient, SettlementService settlementService,
        ReconcileService<Agreement, PositionDto> agreementReconcileService, AgreementService agreementService,
        PositionService positionService, BackOfficeService lenderBackOfficeService, EventMapper eventMapper,
        SpireMapper spireMapper,
        CloudEventRecordService cloudEventRecordService) {
        super(oneSourceApiClient,
            settlementService,
            agreementReconcileService,
            agreementService,
            positionService,
            lenderBackOfficeService,
            eventMapper,
            spireMapper,
            cloudEventRecordService);
        this.spireMapper = spireMapper;
    }
}
