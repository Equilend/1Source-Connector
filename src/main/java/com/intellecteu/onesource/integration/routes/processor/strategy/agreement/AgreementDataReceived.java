package com.intellecteu.onesource.integration.routes.processor.strategy.agreement;

import static com.intellecteu.onesource.integration.enums.FlowStatus.POSITION_RETRIEVED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SPIRE_ISSUE;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.exception.PositionRetrievementException;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.services.AgreementService;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

@Component
@Slf4j
public class AgreementDataReceived extends AbstractAgreementProcessStrategy {

    private final BackOfficeService lenderBackOfficeService;
    private final BackOfficeService borrowerBackOfficeService;

    @Override
    @Transactional
    public void process(AgreementDto agreement) {
        retrievePositionForTrade(agreement);
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

    private Position retrievePositionForTrade(AgreementDto agreement) {
        var lenderPosition = retrievePositionForLender(agreement);
        var borrowerPosition = retrievePositionForBorrower(agreement);
        return lenderPosition == null ? borrowerPosition : lenderPosition;
    }

    private Position retrievePositionForLender(AgreementDto agreement) {
        TradeAgreementDto trade = agreement.getTrade();
        String venueRefId = trade.getExecutionVenue().getVenueRefKey();
        try {
            return lenderBackOfficeService.getPositionForTrade(venueRefId).orElse(null);
        } catch (PositionRetrievementException e) {
            handlePositionRetrievementException(agreement, e, venueRefId, trade);
            return null;
        }
    }

    private void handlePositionRetrievementException(AgreementDto agreement, PositionRetrievementException e,
        String venueRefId, TradeAgreementDto trade) {
        var msg = format("The position related to the trade : %s negotiated on %s on %s has not been recorded in SPIRE",
            venueRefId, agreement.getTrade().retrieveVenueName(), agreement.getTrade().getTradeDate());
        log.warn(msg);
        if (e.getCause() instanceof HttpStatusCodeException exception) {
            if (Set.of(NOT_FOUND, UNAUTHORIZED, FORBIDDEN).contains(exception.getStatusCode())) {
                trade.setProcessingStatus(SPIRE_ISSUE);
            }
        }
        saveAgreementWithStage(agreement, FlowStatus.PROCESSED);
    }

    private Position retrievePositionForBorrower(AgreementDto agreement) {
        TradeAgreementDto trade = agreement.getTrade();
        String venueRefId = trade.getExecutionVenue().getVenueRefKey();
        try {
            return borrowerBackOfficeService.getPositionForTrade(venueRefId).orElse(null);
        } catch (PositionRetrievementException e) {
            handlePositionRetrievementException(agreement, e, venueRefId, trade);
            return null;
        }
    }

    @Override
    public FlowStatus getProcessFlow() {
        return FlowStatus.TRADE_DATA_RECEIVED;
    }

    public AgreementDataReceived(OneSourceService oneSourceService, SettlementService settlementService,
        ReconcileService<AgreementDto, PositionDto> agreementReconcileService, AgreementService agreementService,
        PositionService positionService,
        EventMapper eventMapper, SpireMapper spireMapper, CloudEventRecordService cloudEventRecordService,
        BackOfficeService lenderBackOfficeService, BackOfficeService borrowerBackOfficeService) {
        super(oneSourceService, settlementService, agreementReconcileService, agreementService,
            positionService, lenderBackOfficeService, eventMapper, spireMapper, cloudEventRecordService);
        this.lenderBackOfficeService = lenderBackOfficeService;
        this.borrowerBackOfficeService = borrowerBackOfficeService;
    }
}
