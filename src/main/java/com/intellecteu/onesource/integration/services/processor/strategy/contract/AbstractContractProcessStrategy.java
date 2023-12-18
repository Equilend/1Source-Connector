package com.intellecteu.onesource.integration.services.processor.strategy.contract;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.SKIP_RECONCILIATION_STATUSES;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.LOAN_CONTRACT_PROPOSAL_RECONCILIATION_MSG;
import static com.intellecteu.onesource.integration.enums.FlowStatus.PROCESSED;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_DISCREPANCIES;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_VALIDATED;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.RECONCILED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SPIRE_ISSUE;
import static java.lang.String.format;
import static lombok.AccessLevel.PROTECTED;

import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.SettlementInstructionDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.PositionMapper;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.SettlementTempRepository;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SpireService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PROTECTED)
public abstract class AbstractContractProcessStrategy implements ContractProcessFlowStrategy {

    ContractRepository contractRepository;
    PositionRepository positionRepository;
    SettlementTempRepository settlementTempRepository;
    SpireService spireService;
    CloudEventRecordService cloudEventRecordService;
    ReconcileService<ContractDto, PositionDto> reconcileService;
    EventMapper eventMapper;
    PositionMapper positionMapper;

    PositionDto retrievePositionByVenue(String venueRefId) {
        List<Position> positions = positionRepository.findByVenueRefId(venueRefId);
        return positions.isEmpty() ? null : positionMapper.toPositionDto(positions.get(0));
    }

    void saveContractWithStage(ContractDto contract, FlowStatus status) {
        contract.setFlowStatus(status);
        contractRepository.save(eventMapper.toContractEntity(contract));
    }

    void reconcile(ContractDto contractDto, PositionDto positionDto) {
        try {
            var processingStatus = contractDto.getTrade().getProcessingStatus();
            if (processingStatus != null && SKIP_RECONCILIATION_STATUSES.contains(processingStatus)) {
                log.debug("Skipping reconciliation as trade agreement has status: {}", processingStatus);
                return;
            }
            log.debug("Starting reconciliation for contract {} and position {}",
                contractDto.getContractId(), positionDto.getPositionId());
            reconcileService.reconcile(contractDto, positionDto);
            log.debug(format(LOAN_CONTRACT_PROPOSAL_RECONCILIATION_MSG,
                contractDto.getContractId(), contractDto.getMatchingSpirePositionId()));
            saveContract(contractDto, RECONCILED);
            log.debug("Loan contract proposal {} is reconciled.", contractDto.getContractId());
            createContractInitiationEvent(contractDto.getContractId(), LOAN_CONTRACT_PROPOSAL_VALIDATED,
                contractDto.getMatchingSpirePositionId());
        } catch (ReconcileException e) {
            log.error("Reconciliation fails with message: {} ", e.getMessage());
            e.getErrorList().forEach(msg -> log.debug(msg.getExceptionMessage()));
            saveContract(contractDto, DISCREPANCIES);
            createFailedReconciliationEvent(contractDto, e);
        }
    }

    private void createFailedReconciliationEvent(ContractDto contractDto, ReconcileException e) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(contractDto.getContractId(),
            LOAN_CONTRACT_PROPOSAL_DISCREPANCIES, contractDto.getMatchingSpirePositionId(), e.getErrorList());
        cloudEventRecordService.record(recordRequest);
    }

    private void saveContract(ContractDto contractDto, ProcessingStatus processingStatus) {
        log.debug("Set processing status: {} for loan contract proposal {} ",
            processingStatus, contractDto.getContractId());
        contractDto.setProcessingStatus(processingStatus);
        contractDto.setLastUpdateDatetime(LocalDateTime.now());
        contractRepository.save(eventMapper.toContractEntity(contractDto));
    }

    void updateInstruction(ContractDto contract, PartyRole partyRole, PositionDto position,
        String venueRefId, FlowStatus flowStatus) {
        PartyRole roleForRequest = null;
        if (partyRole == LENDER) { // todo - check whether we need to switch roles
            roleForRequest = BORROWER;
        } else if (partyRole == BORROWER) {
            roleForRequest = LENDER;
        }
        List<SettlementDto> settlementDtos = spireService.retrieveSettlementDetails(position, venueRefId,
            contract.getTrade(), roleForRequest);
        settlementTempRepository.save(eventMapper.toSettlementTempEntity(settlementDtos, contract.getContractId()));
        SettlementInstructionDto settlementInstruction = settlementDtos.get(0).getInstruction();
        saveContractWithStage(contract, flowStatus);
        log.debug("Updating Spire with the counterpart's settlement instruction!");
        spireService.updateInstruction(contract, position, venueRefId, settlementInstruction, partyRole);
    }

    void processMatchingPosition(@NonNull ContractDto contractDto, @NonNull PositionDto positionDto) {
        contractDto.setLastUpdateDatetime(LocalDateTime.now());
        contractDto.setMatchingSpirePositionId(positionDto.getPositionId());

        positionDto.setLastUpdateDateTime(LocalDateTime.now());
        positionDto.setMatching1SourceTradeAgreementId(contractDto.getContractId());

        if (positionDto.getProcessingStatus() == CANCELED) {
            contractDto.setProcessingStatus(MATCHED_CANCELED_POSITION);
            createContractInitiationEvent(contractDto.getContractId(),
                TRADE_AGREEMENT_MATCHED_CANCELED_POSITION, contractDto.getMatchingSpirePositionId());
        } else {
            contractDto.setProcessingStatus(MATCHED_POSITION);
            createContractInitiationEvent(contractDto.getContractId(),
                TRADE_AGREEMENT_MATCHED_POSITION, contractDto.getMatchingSpirePositionId());
        }

        positionRepository.save(positionMapper.toPosition(positionDto));
        contractRepository.save(eventMapper.toContractEntity(contractDto));
    }

    void savePositionRetrievementIssue(ContractDto contract) {
        String venueRefId = contract.getTrade().getExecutionVenue().getVenueRefKey();
        log.warn("Could not retrieve position by venue {} for the contract {}", venueRefId, contract.getContractId());
        contract.setProcessingStatus(SPIRE_ISSUE);
        contract.setFlowStatus(PROCESSED);
        contractRepository.save(eventMapper.toContractEntity(contract));
    }

    void processPartyRoleIssue(String positionType, ContractDto contractDto) {
        log.warn("PositionType: {} has no matches with the loan contract {}",
            positionType, contractDto.getContractId());
        contractDto.setProcessingStatus(ProcessingStatus.ONESOURCE_ISSUE);
    }

    void createContractInitiationEvent(String record, RecordType recordType, String related) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(record, recordType, related);
        cloudEventRecordService.record(recordRequest);
    }
}