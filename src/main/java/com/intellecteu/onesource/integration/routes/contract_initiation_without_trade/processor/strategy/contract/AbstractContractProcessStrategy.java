package com.intellecteu.onesource.integration.routes.contract_initiation_without_trade.processor.strategy.contract;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.SKIP_RECONCILIATION_STATUSES;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.LOAN_CONTRACT_PROPOSAL_RECONCILIATION_MSG;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_CREATED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_VALIDATED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.PROPOSED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.RECONCILED;
import static java.lang.String.format;
import static lombok.AccessLevel.PROTECTED;

import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.mapper.ContractMapper;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.enums.FlowStatus;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.SettlementTempRepository;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PROTECTED)
public abstract class AbstractContractProcessStrategy implements ContractProcessFlowStrategy {

    ContractService contractService;
    PositionService positionService;
    SettlementTempRepository settlementTempRepository;
    SettlementService settlementService;
    CloudEventRecordService cloudEventRecordService;
    ReconcileService<Contract, PositionDto> reconcileService;
    EventMapper eventMapper;
    SpireMapper spireMapper;
    ContractMapper contractMapper;

    Optional<PositionDto> retrievePositionByVenue(String venueRefId) {
        return positionService.findByVenueRefId(venueRefId).map(spireMapper::toPositionDto);
    }

    void saveContractWithStage(Contract contract, FlowStatus status) {
        contract.setFlowStatus(status);
        contractService.save(contract);
        log.debug("Contract id: {} was saved with flow status: {}", contract.getContractId(), status);
    }

    void reconcile(Contract contract, PositionDto positionDto) {
        try {
            var processingStatus = contract.getTrade().getProcessingStatus();
            if (processingStatus != null && SKIP_RECONCILIATION_STATUSES.contains(processingStatus)) {
                log.debug("Skipping reconciliation as trade agreement has status: {}", processingStatus);
                return;
            }
            log.debug("Starting reconciliation for contract {} and position {}",
                contract.getContractId(), positionDto.getPositionId());
            reconcileService.reconcile(contract, positionDto);
            log.debug(format(LOAN_CONTRACT_PROPOSAL_RECONCILIATION_MSG,
                contract.getContractId(), contract.getMatchingSpirePositionId()));
            saveContract(contract, RECONCILED);
            createContractInitiationEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_VALIDATED,
                contract.getMatchingSpirePositionId());
        } catch (ReconcileException e) {
            log.error("Reconciliation fails with message: {} ", e.getMessage());
            e.getErrorList().forEach(msg -> log.debug(msg.getExceptionMessage()));
            saveContract(contract, DISCREPANCIES);
            createFailedReconciliationEvent(contract, e);
        }
    }

    private void createFailedReconciliationEvent(Contract contract, ReconcileException e) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(contract.getContractId(),
            LOAN_CONTRACT_PROPOSAL_DISCREPANCIES, contract.getMatchingSpirePositionId(), e.getErrorList());
        cloudEventRecordService.record(recordRequest);
    }

    private void saveContract(Contract contract, ProcessingStatus processingStatus) {
        contract.setProcessingStatus(processingStatus);
        contract.setLastUpdateDateTime(LocalDateTime.now());
        contractService.save(contract);
    }

    void processMatchingPosition(@NonNull Contract contractDto, @NonNull PositionDto positionDto) {
        log.debug("Matching position: {} with a contract: {}.",
            positionDto.getPositionId(), contractDto.getContractId());
        contractDto.setLastUpdateDateTime(LocalDateTime.now());
        contractDto.setMatchingSpirePositionId(positionDto.getPositionId());

        positionDto.setLastUpdateDateTime(LocalDateTime.now());
        positionDto.setMatching1SourceTradeAgreementId(contractDto.getContractId());

        if (positionDto.getProcessingStatus() == CANCELED) {
            contractDto.setProcessingStatus(MATCHED_CANCELED_POSITION);
            createContractInitiationEvent(contractDto.getContractId(),
                LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION, contractDto.getMatchingSpirePositionId());
        } else {
            contractDto.setProcessingStatus(MATCHED_POSITION);
            createContractInitiationEvent(contractDto.getContractId(),
                LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION, contractDto.getMatchingSpirePositionId());
        }

        positionService.savePosition(spireMapper.toPosition(positionDto));
    }

    void savePositionRetrievementIssue(Contract contract) {
        String venueRefId = contract.getTrade().getVenue().getVenueRefKey();
        log.warn("Could not retrieve position by venue {} for the contract {}", venueRefId, contract.getContractId());
        contract.setProcessingStatus(PROPOSED);
        contract.setLastUpdateDateTime(LocalDateTime.now());
//        contract.setFlowStatus(PROCESSED); TODO commented for local development. Ask to C-H how to resolve if we don't have position for this contract
        contractService.save(contract);
        recordContractCreatedButNotYetMatchedEvent(contract.getContractId());
    }

    void recordContractCreatedButNotYetMatchedEvent(String contractId) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(contractId, LOAN_CONTRACT_PROPOSAL_CREATED);
        cloudEventRecordService.record(recordRequest);
    }

    void processPartyRoleIssue(String positionType, Contract contractDto) {
        log.warn("PositionType: {} has no matches with the loan contract {}",
            positionType, contractDto.getContractId());
        contractDto.setProcessingStatus(ProcessingStatus.ONESOURCE_ISSUE);
    }

    void createContractInitiationEvent(String record, RecordType recordType, String related) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(record, recordType, related);
        cloudEventRecordService.record(recordRequest);
    }

    void savePositionStatus(@NonNull PositionDto position, @NonNull ProcessingStatus status) {
        position.setProcessingStatus(status);
        positionService.savePosition(spireMapper.toPosition(position));
        log.debug("Saved status: {} for Position: {}", status, position.getPositionId());
    }
}
