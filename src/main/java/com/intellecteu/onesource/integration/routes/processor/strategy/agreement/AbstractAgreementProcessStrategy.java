package com.intellecteu.onesource.integration.routes.processor.strategy.agreement;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.SKIP_RECONCILIATION_STATUSES;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.RECONCILE_TRADE_AGREEMENT_SUCCESS_MSG;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_DISCREPANCIES;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_RECONCILED;
import static com.intellecteu.onesource.integration.exception.LoanContractProcessException.PROCESS_EXCEPTION_MESSAGE;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.ONESOURCE_ISSUE;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.RECONCILED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TO_CANCEL;
import static com.intellecteu.onesource.integration.model.RoundingMode.ALWAYSUP;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractProposalDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SpireService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class AbstractAgreementProcessStrategy implements AgreementProcessFlowStrategy {

    OneSourceService oneSourceService;
    SpireService spireService;
    ReconcileService<AgreementDto, PositionDto> reconcileService;
    AgreementRepository agreementRepository;
    PositionRepository positionRepository;
    EventMapper eventMapper;
    CloudEventRecordService cloudEventRecordService;

    Agreement saveAgreementWithStage(AgreementDto agreement, FlowStatus status) {
        agreement.setFlowStatus(status);
        return agreementRepository.save(eventMapper.toAgreementEntity(agreement));
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
                TRADE_AGREEMENT_RECONCILED, agreement.getMatchingSpirePositionId());
            cloudEventRecordService.record(recordRequest);
        } catch (ReconcileException e) {
            log.error("Reconciliation fails with message: {} ", e.getMessage());
            e.getErrorList().forEach(msg -> log.debug(msg.getExceptionMessage()));
            agreement.getTrade().setProcessingStatus(TO_CANCEL);
            var eventBuilder = cloudEventRecordService.getFactory()
                .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
            var recordRequest = eventBuilder.buildRequest(agreement.getAgreementId(), TRADE_AGREEMENT_DISCREPANCIES,
                agreement.getMatchingSpirePositionId(), e.getErrorList());
            cloudEventRecordService.record(recordRequest);
        }
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
            String venueRefId = agreementDto.getTrade().getExecutionVenue().getVenueRefKey();
            log.debug("Retrieving Settlement Instruction from Spire as a {}", partyRole);
            var settlements = spireService.retrieveSettlementDetails(positionDto, venueRefId, agreementDto.getTrade(),
                partyRole);
            if (settlements != null) {
                log.debug("Submitting contract proposal as a {}", partyRole);
                ContractProposalDto contractProposalDto = buildContract(agreementDto, positionDto, settlements);
                oneSourceService.createContract(agreementDto, contractProposalDto, positionDto);
                log.debug("***** Trade Agreement Id: {} was processed successfully", agreementDto.getAgreementId());
            }
        }
    }

    private void persistIssueStatus(AgreementDto agreementDto, PositionDto positionDto) {
        log.debug(format(PROCESS_EXCEPTION_MESSAGE, agreementDto.getAgreementId(), positionDto.getId(),
            positionDto.unwrapPositionStatus()));
        agreementDto.getTrade().setProcessingStatus(ONESOURCE_ISSUE);
    }

    void processMatchingPosition(Agreement agreementEntity, List<Position> positions) {
        if (!positions.isEmpty()) {
            Position position = positions.get(0);
            if (position.getProcessingStatus() == CANCELED) {
                agreementEntity.setLastUpdateDatetime(LocalDateTime.now());
                agreementEntity.setProcessingStatus(MATCHED_CANCELED_POSITION);
                agreementEntity.setMatchingSpirePositionId(position.getPositionId());

                position.setLastUpdateDateTime(LocalDateTime.now());
                position.setMatching1SourceTradeAgreementId(agreementEntity.getAgreementId());
                createContractInitiationCloudEvent(agreementEntity.getAgreementId(), position,
                    TRADE_AGREEMENT_MATCHED_CANCELED_POSITION);
            } else {
                agreementEntity.setLastUpdateDatetime(LocalDateTime.now());
                agreementEntity.setProcessingStatus(MATCHED_POSITION);
                agreementEntity.setMatchingSpirePositionId(position.getPositionId());

                position.setLastUpdateDateTime(LocalDateTime.now());
                position.setMatching1SourceTradeAgreementId(agreementEntity.getAgreementId());
                createContractInitiationCloudEvent(agreementEntity.getAgreementId(), position,
                    TRADE_AGREEMENT_MATCHED_POSITION);
            }

            positionRepository.save(position);
            agreementRepository.save(agreementEntity);
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

    void createContractInitiationCloudEvent(String id, Position position, RecordType recordType) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(id,
            recordType, position.getPositionId());
        cloudEventRecordService.record(recordRequest);
    }
}
