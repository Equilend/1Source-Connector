package com.intellecteu.onesource.integration.routes.delegate_flow.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_SETTLEMENT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.CAPTURE_POSITION_SETTLEMENT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_LOAN_CONTRACT_UPDATE;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.OPEN;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.MATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.SETTLED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UPDATE_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.POSITION_SETTLED_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.POSITION_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.POSITION_UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.POSITION_UPDATE_SUBMITTED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.exception.ContractNotFoundException;
import com.intellecteu.onesource.integration.exception.InstructionRetrievementException;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.PositionConfirmationRequest;
import com.intellecteu.onesource.integration.model.backoffice.PositionStatus;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ContractProposal;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.onesource.SettlementStatus;
import com.intellecteu.onesource.integration.services.AgreementService;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.IntegrationDataTransformer;
import com.intellecteu.onesource.integration.services.MatchingService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.utils.IntegrationUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionProcessor {

    private final PositionService positionService;
    private final BackOfficeService backOfficeService;
    private final AgreementService agreementService;
    private final ContractService contractService;
    private final OneSourceService oneSourceService;
    private final SettlementService settlementService;
    private final CloudEventRecordService cloudEventRecordService;
    private final MatchingService matchingService;
    private final IntegrationDataTransformer dataTransformer;

    public Position savePosition(Position position) {
        return positionService.savePosition(position);
    }

    /**
     * Update processing status and save position
     *
     * @param position Position
     * @param processingStatus Processing status
     * @return persisted position with updated processing status
     */
    public Position updateProcessingStatus(Position position, ProcessingStatus processingStatus) {
        position.setProcessingStatus(processingStatus);
        return positionService.savePosition(position);
    }

    public Position updatePositionStatus(Position position, String positionStatus) {
        if (position.getPositionStatus() == null) {
            position.setPositionStatus(new PositionStatus());
        }
        position.getPositionStatus().setStatus(positionStatus);
        return position;
    }

    @Transactional
    public Position findByPositionId(Long positionId) {
        return positionService.getByPositionId(positionId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Position> getAllByPositionStatus(String status) {
        return positionService.getAllByPositionStatus(status);
    }

    @Transactional
    public void updateCapturedPositions(List<Position> persistedPositions) {
        final List<Position> updatedPositions = requestUpdatesFromBackoffice(persistedPositions);
        if (CollectionUtils.isNotEmpty(updatedPositions)) {
            Set<Long> updatedIds = updatedPositions.stream().map(Position::getPositionId).collect(Collectors.toSet());
            persistedPositions.stream()
                .filter(persistedPosition -> updatedIds.contains(persistedPosition.getPositionId()))
                .map(this::updateToSettled)
                .forEach(position -> {
                    if (instructUpdateLoanContractSettlementStatus(position, SettlementStatus.SETTLED)) {
                        createContractSettlementCloudEvent(position.getMatching1SourceLoanContractId(),
                            POSITION_SETTLED_SUBMITTED, String.valueOf(position.getPositionId()));
                    }
                });
        }
    }

    private Position updateToSettled(Position position) {
        position.getPositionStatus().setStatus(OPEN.name());
        position.setProcessingStatus(SETTLED);
        return savePosition(position);
    }

    public boolean instructUpdateLoanContractSettlementStatus(Position position, SettlementStatus settlementStatus) {
        try {
            oneSourceService.instructUpdateSettlementStatus(position, settlementStatus);
            return true;
        } catch (RestClientException e) {
            if (e instanceof HttpStatusCodeException exception) {
                final HttpStatusCode statusCode = HttpStatus.valueOf(exception.getStatusCode().value());
                log.debug("Exception on instruct update loan contract settlement status. Details: {}", e.getMessage());
                if (Set.of(HttpStatus.CREATED, UNAUTHORIZED, FORBIDDEN, NOT_FOUND).contains(statusCode)) {
                    var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_SETTLEMENT);
                    var recordRequest = eventBuilder.buildExceptionRequest(position.getMatching1SourceLoanContractId(),
                        exception, POST_LOAN_CONTRACT_UPDATE, String.valueOf(position.getPositionId()));
                    cloudEventRecordService.record(recordRequest);
                }
            }
            return false;
        }
    }

    private List<Position> requestUpdatesFromBackoffice(List<Position> capturedPositions) {
        try {
            return backOfficeService.retrieveUpdatedOpenedPositions(capturedPositions);
        } catch (RestClientException e) {
            if (e instanceof HttpStatusCodeException exception) {
                final HttpStatusCode statusCode = HttpStatus.valueOf(exception.getStatusCode().value());
                if (Set.of(HttpStatus.CREATED, UNAUTHORIZED, FORBIDDEN, NOT_FOUND).contains(statusCode)) {
                    var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_SETTLEMENT);
                    var recordRequest = eventBuilder.buildExceptionRequest(exception, CAPTURE_POSITION_SETTLEMENT);
                    cloudEventRecordService.record(recordRequest);
                }
            }
            return List.of();
        }
    }

    public Position matchTradeAgreement(Position position) {
        agreementService.findByVenueRefId(position.getVenueRefId())
            .ifPresent(agreement -> {
                position.setMatching1SourceTradeAgreementId(agreement.getAgreementId());
                agreementService.markAgreementAsMatched(agreement, String.valueOf(position.getPositionId()));
            });
        return position;
    }

    public Position updatePositionForInstructedProposal(Position position) {
        if (ProcessingStatus.CREATED == position.getProcessingStatus()) {
            position.setProcessingStatus(SUBMITTED);
        }
        if (ProcessingStatus.UPDATED == position.getProcessingStatus()) {
            position.setProcessingStatus(UPDATE_SUBMITTED);
        }
        return position;
    }

    @Deprecated(since = "0.0.5-SNAPSHOT")
    public Position matchContractProposalObsolete(Position position) {
        Optional<Contract> contractOpt = contractService.findByVenueRefId(position.getVenueRefId());
        contractOpt.ifPresent(contract -> {
            position.setMatching1SourceLoanContractId(contract.getContractId());
            saveContractAsMatched(contract, position);
            createContractInitiationCloudEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_MATCHED,
                String.valueOf(contract.getMatchingSpirePositionId()));
        });
        return position;
    }

    public boolean instructUpdatePosition(Position position) {
        try {
            final PositionConfirmationRequest confirmationRequest = dataTransformer.toPositionConfirmationRequest(
                position);
            backOfficeService.instructUpdatePosition(confirmationRequest);
            return true;
        } catch (HttpStatusCodeException exception) {
            final HttpStatusCode statusCode = HttpStatus.valueOf(exception.getStatusCode().value());
            log.warn("SPIRE error response for request Instruction: " + statusCode);
            if (Set.of(HttpStatus.CREATED, UNAUTHORIZED, FORBIDDEN, NOT_FOUND).contains(statusCode)) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                var recordRequest = eventBuilder.buildExceptionRequest(
                    position.getMatching1SourceLoanContractId(), exception,
                    IntegrationSubProcess.CONFIRM_POSITION, String.valueOf(position.getPositionId()));
                cloudEventRecordService.record(recordRequest);
            }
            return false;
        }
    }

    public void matchContractProposalAsBorrower(Position position) {
        try {
            Set<Contract> unmatchedContracts = contractService.findAllByProcessingStatus(UNMATCHED);
            if (unmatchedContracts.isEmpty()) {
                throw new ContractNotFoundException(); // todo should we ignore such cases? A lot of system events will be created
            }
            log.debug("Matching position with contracts received ({})", unmatchedContracts.size());
            Contract matchedContract = retrieveMatchedContract(position, unmatchedContracts);
            updatePosition(matchedContract, position);
            saveContractAsMatched(matchedContract, position);
            recordContractProposalMatched(matchedContract);
        } catch (ContractNotFoundException e) {
            log.debug("No matched contracts found for position Id={}", position.getPositionId());
            recordSystemEvent(position, POSITION_UNMATCHED);
        }
    }

    private void recordSystemEvent(Position position, RecordType recordType) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(String.valueOf(position.getPositionId()), recordType);
        cloudEventRecordService.record(recordRequest);
    }

    private Contract retrieveMatchedContract(Position position, Set<Contract> unmatchedContracts) {
        return matchingService.matchPositionWithContracts(position, unmatchedContracts)
            .orElseThrow(ContractNotFoundException::new);
    }

    private void updatePosition(Contract contract, Position position) {
        position.setMatching1SourceLoanContractId(contract.getContractId());
        savePosition(position);
    }

    public Position fetchSettlementInstruction(Position position) {
        PartyRole partyRole = IntegrationUtils.extractPartyRole(position).get();
        try {
            Optional<Settlement> settlementInstructionOpt = backOfficeService.retrieveSettlementInstruction(
                position, partyRole, position.getPositionAccount().getAccountId());
            settlementInstructionOpt.ifPresent(settlementService::persistSettlement);
        } catch (InstructionRetrievementException e) {
            if (e.getCause() instanceof HttpStatusCodeException exception) {
                final HttpStatusCode statusCode = exception.getStatusCode();
                log.warn("SPIRE error response for request Instruction: " + statusCode);
                if (Set.of(UNAUTHORIZED, FORBIDDEN, NOT_FOUND).contains(HttpStatus.valueOf(statusCode.value()))) {
                    var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                    var recordRequest = eventBuilder.buildExceptionRequest(
                        position.getMatching1SourceTradeAgreementId(), exception,
                        IntegrationSubProcess.GET_SETTLEMENT_INSTRUCTIONS, String.valueOf(position.getPositionId()));
                    cloudEventRecordService.record(recordRequest);
                }
            }
        }
        return position;
    }

    public void recordEventProposalInstructed(Position position) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
        if (SUBMITTED == position.getProcessingStatus()) {
            CloudEventBuildRequest recordRequest = eventBuilder.buildRequest(String.valueOf(position.getPositionId()),
                POSITION_SUBMITTED);
            cloudEventRecordService.record(recordRequest);
        }
        if (UPDATE_SUBMITTED == position.getProcessingStatus()) {
            CloudEventBuildRequest recordRequest = eventBuilder.buildRequest(String.valueOf(position.getPositionId()),
                POSITION_UPDATE_SUBMITTED);
            cloudEventRecordService.record(recordRequest);
        }
    }

    public ProcessingStatus reconcileWithAgreement(Position position) {
//        if (position.getMatching1SourceTradeAgreementId() == null || position.getProcessingStatus() != SI_FETCHED) {
//            return position.getProcessingStatus();
//        }
        return agreementService.findByAgreementId(position.getMatching1SourceTradeAgreementId())
            .map(agreement -> agreementService.reconcile(agreement, position))
            .map(agreementService::saveAgreement)
            .map(agreement -> retrieveProcessingStatus(position, agreement))
            .orElse(position.getProcessingStatus());
    }

    private ProcessingStatus retrieveProcessingStatus(Position position, Agreement agreement) {
//        if (agreement.getProcessingStatus().equals(RECONCILED)) {
//            return TRADE_RECONCILED;
//        }
//        if (agreement.getProcessingStatus().equals(DISCREPANCIES)) {
//            return TRADE_DISCREPANCIES;
//        }
        return position.getProcessingStatus();
    }

    /**
     * Instruct new loan contract proposal
     *
     * @param proposal ContractProposal
     * @param position Position
     * @return true if successfully executed new loan contract proposal request
     */
    public boolean instructLoanContractProposal(@NonNull ContractProposal proposal, @NonNull Position position) {
        try {
            return oneSourceService.instructLoanContractProposal(proposal);
        } catch (HttpStatusCodeException e) {
            log.warn("""
                The loan contract proposal instruction has not been processed by 1Source for the \
                (SPIRE Position: {}) for the following reason: {}""", position.getPositionId(), e.getStatusCode());
            log.debug("Details: {}", e.getMessage());
            var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
            var recordRequest = eventBuilder.buildExceptionRequest(e, POST_LOAN_CONTRACT_PROPOSAL,
                String.valueOf(position.getPositionId()));
            cloudEventRecordService.record(recordRequest);
            return false;
        }
    }

    private Contract saveContractAsMatched(Contract contract, Position position) {
        contract.setMatchingSpirePositionId(position.getPositionId());
        contract.setMatchingSpireTradeId(position.getTradeId());
        contract.setProcessingStatus(MATCHED);
        contract.setLastUpdateDateTime(LocalDateTime.now());
        return contractService.save(contract);
    }

    private void recordContractProposalMatched(Contract matchedContract) {
        createContractInitiationCloudEvent(matchedContract.getContractId(), LOAN_CONTRACT_PROPOSAL_MATCHED,
            String.format("%d,%d", matchedContract.getMatchingSpirePositionId(),
                matchedContract.getMatchingSpireTradeId()));
    }

    private void createContractInitiationCloudEvent(String recordData, RecordType recordType, String relatedData) {
        createCloudEvent(recordData, recordType, relatedData, IntegrationProcess.CONTRACT_INITIATION);
    }

    private void createContractSettlementCloudEvent(String recordData, RecordType recordType, String relatedData) {
        createCloudEvent(recordData, recordType, relatedData, IntegrationProcess.CONTRACT_SETTLEMENT);
    }

    private void createCloudEvent(String recordData, RecordType recordType, String relatedData, IntegrationProcess iP) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(iP);
        var recordRequest = eventBuilder.buildRequest(recordData, recordType, relatedData);
        cloudEventRecordService.record(recordRequest);
    }
}
