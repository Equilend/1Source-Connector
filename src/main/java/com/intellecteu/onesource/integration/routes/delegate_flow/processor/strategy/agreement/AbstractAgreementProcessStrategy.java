package com.intellecteu.onesource.integration.routes.delegate_flow.processor.strategy.agreement;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.SKIP_RECONCILIATION_STATUSES;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.RECONCILE_TRADE_AGREEMENT_SUCCESS_MSG;
import static com.intellecteu.onesource.integration.exception.LoanContractProcessException.PROCESS_EXCEPTION_MESSAGE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.ONESOURCE_ISSUE;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.RECONCILED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TRADE_DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TRADE_RECONCILED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TRADE_AGREEMENT_DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TRADE_AGREEMENT_MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TRADE_AGREEMENT_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TRADE_AGREEMENT_RECONCILED;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.onesource.RoundingMode.ALWAYSUP;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.exception.InstructionRetrievementException;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.enums.FlowStatus;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.ContractProposal;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.onesource.TradeAgreement;
import com.intellecteu.onesource.integration.services.AgreementService;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.client.onesource.OneSourceApiClient;
import com.intellecteu.onesource.integration.services.reconciliation.ReconcileService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class AbstractAgreementProcessStrategy implements AgreementProcessFlowStrategy {

    OneSourceApiClient oneSourceApiClient;
    SettlementService settlementService;
    ReconcileService<Agreement, PositionDto> reconcileService;
    AgreementService agreementService;
    PositionService positionService;
    BackOfficeService lenderBackOfficeService;
    EventMapper eventMapper;
    SpireMapper spireMapper;
    CloudEventRecordService cloudEventRecordService;

    Agreement saveAgreementWithStage(Agreement agreement, FlowStatus status) {
        agreement.setFlowStatus(status);
        return agreementService.saveAgreement(agreement);
    }

    @Transactional
    public void reconcile(Agreement agreement, PositionDto position) {
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
            position.setProcessingStatus(TRADE_RECONCILED);
            var eventBuilder = cloudEventRecordService.getFactory()
                .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
            var recordRequest = eventBuilder.buildRequest(agreement.getAgreementId(),
                TRADE_AGREEMENT_RECONCILED, agreement.getMatchingSpirePositionId());
            cloudEventRecordService.record(recordRequest);
        } catch (ReconcileException e) {
            log.error("Reconciliation fails with message: {} ", e.getMessage());
            e.getErrorList().forEach(msg -> log.debug(msg.getFieldValue()));
            agreement.getTrade().setProcessingStatus(DISCREPANCIES);
            position.setProcessingStatus(TRADE_DISCREPANCIES);
            var eventBuilder = cloudEventRecordService.getFactory()
                .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
            var recordRequest = eventBuilder.buildRequest(agreement.getAgreementId(), TRADE_AGREEMENT_DISCREPANCIES,
                agreement.getMatchingSpirePositionId(), e.getErrorList());
            cloudEventRecordService.record(recordRequest);
        }
        positionService.savePosition(spireMapper.toPosition(position));
    }

    void processAgreement(Agreement agreement, Position position) {
        extractPartyRole(position.unwrapPositionType())
            .ifPresentOrElse(
                party -> processByParty(party, agreement, position),
                () -> persistIssueStatus(agreement, position)
            );
    }

    private void processByParty(PartyRole partyRole, Agreement agreement, Position position) {
        if (partyRole == LENDER) {
            log.debug("Retrieving Settlement Instruction from Spire as a {}", partyRole);
            try {
                Optional<Settlement> settlement = lenderBackOfficeService.retrieveSettlementInstruction(position,
                    partyRole,
                    position.getPositionAccount().getAccountId());
                settlement.ifPresent(s -> {
                    log.debug("Submitting contract proposal as a {}", partyRole);
                    ContractProposal contractProposal = buildContract(agreement, position, s);
//                    oneSourceApiClient.createContractProposal(agreement, contractProposal, position);
                    log.debug("***** Trade Agreement Id: {} was processed successfully", agreement.getAgreementId());
                });
            } catch (InstructionRetrievementException e) {
                if (e.getCause() instanceof HttpStatusCodeException exception) {
                    final HttpStatusCode statusCode = exception.getStatusCode();
                    log.warn("SPIRE error response for request Instruction: " + statusCode);
                    if (Set.of(UNAUTHORIZED, FORBIDDEN, NOT_FOUND).contains(HttpStatus.valueOf(statusCode.value()))) {
                        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                        var recordRequest = eventBuilder.buildExceptionRequest(
                            position.getMatching1SourceTradeAgreementId(), exception,
                            IntegrationSubProcess.GET_SETTLEMENT_INSTRUCTIONS,
                            String.valueOf(position.getPositionId()));
                        cloudEventRecordService.record(recordRequest);
                    }
                }
            }
        }
    }

    private void persistIssueStatus(Agreement agreement, Position position) {
        log.debug(format(PROCESS_EXCEPTION_MESSAGE, agreement.getAgreementId(), position.getPositionId(),
            position.unwrapPositionStatus()));
        agreement.getTrade().setProcessingStatus(ONESOURCE_ISSUE);
    }

    public void processMatchingPosition(Agreement agreement, PositionDto positionDto) {
        RecordType recordType = null;
        if (positionDto.getProcessingStatus() == CANCELED) {
            agreement.setProcessingStatus(MATCHED_CANCELED_POSITION);
            recordType = TRADE_AGREEMENT_MATCHED_CANCELED_POSITION;
        } else {
            agreement.setProcessingStatus(MATCHED_POSITION);
            recordType = TRADE_AGREEMENT_MATCHED_POSITION;
        }

        agreement.setMatchingSpirePositionId(positionDto.getPositionId());
        positionDto.setMatching1SourceTradeAgreementId(agreement.getAgreementId());

        agreementService.saveAgreement(agreement);
        positionService.savePosition(spireMapper.toPosition(positionDto));

        recordCloudEvent(agreement.getAgreementId(), recordType, positionDto.getPositionId());
    }

    ContractProposal buildContract(Agreement agreement, Position position,
        Settlement settlement) {
        TradeAgreement trade = agreement.getTrade();
        trade.getCollateral().setRoundingRule(position.getCpMarkRoundTo());
        trade.getCollateral().setRoundingMode(ALWAYSUP);
        return ContractProposal.builder()
            .trade(trade)
            .settlementList(List.of(settlement))
            .build();
    }

    void recordCloudEvent(String recorded, RecordType recordType, String related) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(recorded, recordType, related);
        cloudEventRecordService.record(recordRequest);
    }
}
