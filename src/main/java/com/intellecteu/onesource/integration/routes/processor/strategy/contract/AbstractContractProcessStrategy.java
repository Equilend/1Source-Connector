package com.intellecteu.onesource.integration.routes.processor.strategy.contract;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.SKIP_RECONCILIATION_STATUSES;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.LOAN_CONTRACT_PROPOSAL_RECONCILIATION_MSG;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_CREATED;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_DISCREPANCIES;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_VALIDATED;
import static com.intellecteu.onesource.integration.model.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.PROPOSED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.RECONCILED;
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
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.SettlementTempRepository;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.SpireService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import com.intellecteu.onesource.integration.utils.PositionUtils;
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
    SettlementService settlementService;
    SpireService spireService;
    CloudEventRecordService cloudEventRecordService;
    ReconcileService<ContractDto, PositionDto> reconcileService;
    EventMapper eventMapper;
    SpireMapper spireMapper;

    PositionDto retrievePositionByVenue(String venueRefId) {
        List<Position> positions = positionRepository.findByVenueRefId(venueRefId);
        return positions.isEmpty() ? null : spireMapper.toPositionDto(positions.get(0));
    }

    void saveContractWithStage(ContractDto contract, FlowStatus status) {
        contract.setFlowStatus(status);
        contractRepository.save(eventMapper.toContractEntity(contract));
        log.debug("Contract id: {} was saved with flow status: {}", contract.getContractId(), status);
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
        log.debug("Matching position: {} with a contract: {}.",
            positionDto.getPositionId(), contractDto.getContractId());
        contractDto.setLastUpdateDatetime(LocalDateTime.now());
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

        positionRepository.save(spireMapper.toPosition(positionDto));
    }

    void savePositionRetrievementIssue(ContractDto contract) {
        String venueRefId = contract.getTrade().getExecutionVenue().getVenueRefKey();
        log.warn("Could not retrieve position by venue {} for the contract {}", venueRefId, contract.getContractId());
        contract.setProcessingStatus(PROPOSED);
        contract.setLastUpdateDatetime(LocalDateTime.now());
//        contract.setFlowStatus(PROCESSED); TODO commented for local development. Ask to C-H how to resolve if we don't have position for this contract
        contractRepository.save(eventMapper.toContractEntity(contract));
        recordContractCreatedButNotYetMatchedEvent(contract.getContractId());
    }

    void recordContractCreatedButNotYetMatchedEvent(String contractId) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(LOAN_CONTRACT_PROPOSAL_CREATED, contractId);
        cloudEventRecordService.record(recordRequest);
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

    void savePositionStatus(@NonNull PositionDto position, @NonNull ProcessingStatus status) {
        PositionUtils.updatePositionDtoStatus(position, status);
        positionRepository.save(spireMapper.toPosition(position));
        log.debug("Saved status: {} for Position: {}", status, position.getPositionId());
    }
}
