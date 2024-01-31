package com.intellecteu.onesource.integration.routes.processor.strategy.agreement;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.SKIP_RECONCILIATION_STATUSES;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.RECONCILE_TRADE_AGREEMENT_SUCCESS_MSG;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_DISCREPANCIES;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_RECONCILED;
import static com.intellecteu.onesource.integration.exception.LoanContractProcessException.PROCESS_EXCEPTION_MESSAGE;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.ONESOURCE_ISSUE;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.RECONCILED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TRADE_DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TRADE_RECONCILED;
import static com.intellecteu.onesource.integration.model.RoundingMode.ALWAYSUP;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractProposalDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.exception.InstructionRetrievementException;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.Settlement;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.services.AgreementService;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class AbstractAgreementProcessStrategy implements AgreementProcessFlowStrategy {

    OneSourceService oneSourceService;
    SettlementService settlementService;
    ReconcileService<AgreementDto, PositionDto> reconcileService;
    AgreementService agreementService;
    PositionService positionService;
    BackOfficeService lenderBackOfficeService;
    EventMapper eventMapper;
    SpireMapper spireMapper;
    CloudEventRecordService cloudEventRecordService;

    Agreement saveAgreementWithStage(AgreementDto agreement, FlowStatus status) {
        agreement.setFlowStatus(status);
        return agreementService.saveAgreement(eventMapper.toAgreementEntity(agreement));
    }

    @Transactional
    public void reconcile(AgreementDto agreement, PositionDto position) {
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
            e.getErrorList().forEach(msg -> log.debug(msg.getExceptionMessage()));
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

    void processAgreement(AgreementDto agreementDto, PositionDto positionDto) {
        extractPartyRole(positionDto.unwrapPositionType())
            .ifPresentOrElse(
                party -> processByParty(party, agreementDto, positionDto),
                () -> persistIssueStatus(agreementDto, positionDto)
            );
    }

    private void processByParty(PartyRole partyRole, AgreementDto agreementDto, PositionDto positionDto) {
        if (partyRole == LENDER) {
            log.debug("Retrieving Settlement Instruction from Spire as a {}", partyRole);
            try {
                Position position = spireMapper.toPosition(positionDto);
                Optional<Settlement> settlement = lenderBackOfficeService.retrieveSettlementInstruction(position,
                    partyRole,
                    position.getPositionAccount().getAccountId());
                settlement.ifPresent(s -> {
                    log.debug("Submitting contract proposal as a {}", partyRole);
                    ContractProposalDto contractProposalDto = buildContract(agreementDto, positionDto,
                        eventMapper.toSettlementDto(s));
                    oneSourceService.createContract(agreementDto, contractProposalDto, positionDto);
                    log.debug("***** Trade Agreement Id: {} was processed successfully", agreementDto.getAgreementId());
                });
            } catch (InstructionRetrievementException e) {
                if (e.getCause() instanceof HttpStatusCodeException exception) {
                    log.warn("SPIRE error response for request Instruction: " + exception.getStatusCode());
                    if (Set.of(UNAUTHORIZED, FORBIDDEN, NOT_FOUND).contains(exception.getStatusCode())) {
                        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                        var recordRequest = eventBuilder.buildExceptionRequest(
                            positionDto.getMatching1SourceTradeAgreementId(), exception,
                            IntegrationSubProcess.GET_SETTLEMENT_INSTRUCTIONS, positionDto.getPositionId());
                        cloudEventRecordService.record(recordRequest);
                    }
                }
            }
        }
    }

    private void persistIssueStatus(AgreementDto agreementDto, PositionDto positionDto) {
        log.debug(format(PROCESS_EXCEPTION_MESSAGE, agreementDto.getAgreementId(), positionDto.getPositionId(),
            positionDto.unwrapPositionStatus()));
        agreementDto.getTrade().setProcessingStatus(ONESOURCE_ISSUE);
    }

    public void processMatchingPosition(AgreementDto agreementDto, PositionDto positionDto) {
        RecordType recordType = null;
        if (positionDto.getProcessingStatus() == CANCELED) {
            agreementDto.setProcessingStatus(MATCHED_CANCELED_POSITION);
            recordType = TRADE_AGREEMENT_MATCHED_CANCELED_POSITION;
        } else {
            agreementDto.setProcessingStatus(MATCHED_POSITION);
            recordType = TRADE_AGREEMENT_MATCHED_POSITION;
        }

        agreementDto.setMatchingSpirePositionId(positionDto.getPositionId());
        positionDto.setMatching1SourceTradeAgreementId(agreementDto.getAgreementId());

        agreementService.saveAgreement(eventMapper.toAgreementEntity(agreementDto));
        positionService.savePosition(spireMapper.toPosition(positionDto));

        recordCloudEvent(agreementDto.getAgreementId(), recordType, positionDto.getPositionId());
    }

    ContractProposalDto buildContract(AgreementDto agreement, PositionDto positionDto,
        SettlementDto settlement) {
        TradeAgreementDto trade = agreement.getTrade();
        trade.getCollateral().setRoundingRule(positionDto.getCpMarkRoundTo());
        trade.getCollateral().setRoundingMode(ALWAYSUP);
        return ContractProposalDto.builder()
            .trade(trade)
            .settlement(List.of(settlement))
            .build();
    }

    void recordCloudEvent(String recorded, RecordType recordType, String related) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(recorded, recordType, related);
        cloudEventRecordService.record(recordRequest);
    }
}
