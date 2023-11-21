package com.intellecteu.onesource.integration.services.processor.strategy.agreement;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractProposalDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SpireService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.SKIP_RECONCILIATION_STATUSES;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.RECONCILE_TRADE_AGREEMENT_SUCCESS_MSG;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_DISCREPANCIES;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_RECONCILED;
import static com.intellecteu.onesource.integration.exception.DataMismatchException.LEI_MISMATCH_MSG;
import static com.intellecteu.onesource.integration.exception.LoanContractProcessException.PROCESS_EXCEPTION_MESSAGE;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.ONESOURCE_ISSUE;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.RECONCILED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TO_CANCEL;
import static com.intellecteu.onesource.integration.model.RoundingMode.ALWAYSUP;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;
import static java.lang.String.format;

@Slf4j
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class AbstractAgreementProcessStrategy implements AgreementProcessFlowStrategy {

    OneSourceService oneSourceService;
    SpireService spireService;
    ReconcileService reconcileService;
    AgreementRepository agreementRepository;
    EventMapper eventMapper;
    CloudEventRecordService cloudEventRecordService;

    void saveAgreementWithStage(AgreementDto agreement, FlowStatus status) {
        agreement.setFlowStatus(status);
        agreementRepository.save(eventMapper.toAgreementEntity(agreement));
    }

    void reconcile(AgreementDto agreement, PositionDto position) {
        try {
            var processingStatus = agreement.getTrade().getProcessingStatus();
            if (processingStatus != null && SKIP_RECONCILIATION_STATUSES.contains(processingStatus)) {
                log.debug("Skipping reconciliation as trade agreement has status: {}", processingStatus);
                return;
            }
            log.debug("Starting reconciliation for agreement {} and position {}",
                agreement.getAgreementId(), position.getPositionId());
            reconcileService.reconcile(agreement, position);
            log.debug(
                format(RECONCILE_TRADE_AGREEMENT_SUCCESS_MSG, agreement.getAgreementId(), position.getPositionId()));
            agreement.getTrade().setProcessingStatus(RECONCILED);
            var eventBuilder = cloudEventRecordService.getFactory()
                .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
            var recordRequest = eventBuilder.buildRequest(agreement.getAgreementId(),
                TRADE_AGREEMENT_RECONCILED, position.getPositionId());
            cloudEventRecordService.record(recordRequest);
        } catch (ReconcileException e) {
            log.error("Reconciliation fails with message: {} ", e.getMessage());
            e.getErrorList().forEach(msg -> log.debug(msg.getExceptionMessage()));
            agreement.getTrade().setProcessingStatus(TO_CANCEL);
            var eventBuilder = cloudEventRecordService.getFactory()
                .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
            var recordRequest = eventBuilder.buildRequest(agreement.getAgreementId(), TRADE_AGREEMENT_DISCREPANCIES,
                position.getPositionId(), e.getErrorList());
            cloudEventRecordService.record(recordRequest);
        }
    }

    void processAgreement(AgreementDto agreement, PositionDto positionDto) {
        var positionLei = positionDto.getAccountLei();
        final PartyRole partyRole = extractPartyRole(agreement.getTrade().getTransactingParties(), positionLei);
        if (partyRole == null) {
            log.debug(format(PROCESS_EXCEPTION_MESSAGE, agreement.getAgreementId(),
                positionDto.getId(), format(LEI_MISMATCH_MSG, positionLei)));
            agreement.getTrade().setProcessingStatus(ONESOURCE_ISSUE);
        } else {
            if (partyRole == LENDER) {
                log.debug("Retrieving Settlement Instruction from Spire as a {}", partyRole);
                var settlements = spireService.retrieveSettlementDetails(positionDto, agreement.getTrade(), partyRole);
                if (settlements != null) {
                    log.debug("Submitting contract proposal as a {}", partyRole);
                    ContractProposalDto contractProposalDto = buildContract(agreement, positionDto, settlements);
                    oneSourceService.createContract(agreement, contractProposalDto, positionDto);
                    log.debug("***** Trade Agreement Id: {} was processed successfully", agreement.getAgreementId());
                }
            }
        }
    }

    ContractProposalDto buildContract(AgreementDto agreement, PositionDto positionDto,
        List<SettlementDto> settlements) {
        TradeAgreementDto trade = agreement.getTrade();
        trade.getCollateral().setRoundingRule(positionDto.getCpMarkRoundTo());
        trade.getCollateral().setRoundingMode(ALWAYSUP);
        return ContractProposalDto.builder()
            .trade(trade)
            .settlement(settlements)
            .build();
    }
}
