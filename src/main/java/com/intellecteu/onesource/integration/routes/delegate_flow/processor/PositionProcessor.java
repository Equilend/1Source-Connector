package com.intellecteu.onesource.integration.routes.delegate_flow.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_CANCELLATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_SETTLEMENT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.CAPTURE_POSITION_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.CAPTURE_POSITION_SETTLEMENT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_NEW_POSITIONS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_LOAN_CONTRACT_UPDATE;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.FUTURE;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.OPEN;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.MATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.MULTIPLE_FIGI;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.NO_FIGI;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.SETTLED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UPDATE_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.MULTIPLE_FIGI_CODES;
import static com.intellecteu.onesource.integration.model.enums.RecordType.NO_FIGI_CODE_RETRIEVED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.POSITION_SETTLED_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.POSITION_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.POSITION_UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.POSITION_UPDATE_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwExceptionForRedeliveryPolicy;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.exception.ContractNotFoundException;
import com.intellecteu.onesource.integration.exception.FigiRetrievementException;
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
import com.intellecteu.onesource.integration.services.FigiService;
import com.intellecteu.onesource.integration.services.IntegrationDataTransformer;
import com.intellecteu.onesource.integration.services.MatchingService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.utils.IntegrationUtils;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
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
    private final FigiService figiService;

    public Position savePosition(Position position) {
        return positionService.savePosition(position);
    }

    /**
     * Update processing status and save position
     *
     * @param position Position
     * @param processingStatus Processing status
     * @return Position persisted position with updated processing status
     */
    @Transactional
    public Position updateProcessingStatus(Position position, ProcessingStatus processingStatus) {
        position.setProcessingStatus(processingStatus);
        return positionService.savePosition(position);
    }

    @Transactional
    public Position updateCounterparty(@NonNull Position position, @NonNull Contract contract) {
        Long dtc = null;
        if (IntegrationUtils.isLender(position)) {
            dtc = retrievePartyDtc(contract, PartyRole.LENDER);
        }
        if (IntegrationUtils.isBorrower(position)) {
            dtc = retrievePartyDtc(contract, PartyRole.BORROWER);
        }
        position.getPositionCpAccount().setDtc(dtc);
        return position;
    }

    /**
     * Retrieve and persist figi code by the ticker. Stop processing flow and update position processing status if figi
     * couldn't be retrieved.
     *
     * @param position Position
     * @return the initial position if figi was retrieved or null
     */
    @Transactional
    public @Nullable Position manageFigiCode(@NotNull Position position) {
        try {
            final String ticker = position.getPositionSecurityDetail().getTicker();
            figiService.findAndSaveFigi(ticker);
            return position;
        } catch (FigiRetrievementException e) {
            return processFigiException(position, e);
        } catch (Exception e) {
            log.debug("Unexpected exception during retrieving figi code. Details: {}", e.getMessage());
            position.setProcessingStatus(NO_FIGI);
            savePosition(position);
            return null;
        }
    }

    private Position processFigiException(Position position, FigiRetrievementException e) {
        if (e.isFigiRetrieved()) {
            log.debug("Multiple figi code returned for position:{}", position.getPositionId());
            position.setProcessingStatus(MULTIPLE_FIGI);
            createCloudEvent(String.valueOf(position.getPositionId()), MULTIPLE_FIGI_CODES, "",
                CONTRACT_INITIATION);
        } else {
            log.debug("No figi code returned for position:{}", position.getPositionId());
            position.setProcessingStatus(NO_FIGI);
            createCloudEvent(String.valueOf(position.getPositionId()), NO_FIGI_CODE_RETRIEVED, "",
                CONTRACT_INITIATION);
        }
        savePosition(position);
        return null;
    }

    private Long retrievePartyDtc(Contract contract, PartyRole partyRole) {
        try {
            final Settlement settlement = contract.getSettlement().stream()
                .filter(s -> partyRole == s.getPartyRole())
                .findAny()
                .orElseThrow();
            final String dtc = settlement.getInstruction().getDtcParticipantNumber();
            return Long.valueOf(dtc);
        } catch (Exception e) {
            log.debug("Couldn't retrieve party's dtc. Details:{}", e.getMessage());
            return null;
        }
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

    public void instructCancelLoanContract(Position position) {
        // todo instruct 1Source to cancel the loan contract
    }

    @Transactional(readOnly = true)
    public List<Position> getAllByPositionStatus(String status) {
        return positionService.getAllByPositionStatus(status);
    }

    @Transactional
    public List<Position> retrieveCanceledPositions() {
        List<Position> positionsToRequest = positionService.getAllByPositionStatus(FUTURE.getValue());
        if (CollectionUtils.isEmpty(positionsToRequest)) {
            return List.of();
        }
        try {
            final Set<Long> positionIdsToUpdate = backOfficeService.retrieveCancelledPositions(positionsToRequest)
                .stream()
                .map(Position::getPositionId)
                .collect(Collectors.toSet());
            return positionsToRequest.stream()
                .filter(p -> positionIdsToUpdate.contains(p.getPositionId()))
                .toList();
        } catch (HttpStatusCodeException e) {
            createExceptionCloudEvent(e, CONTRACT_CANCELLATION, CAPTURE_POSITION_CANCELED);
            return List.of();
        }
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

    @Transactional
    public void matchContractProposalAsBorrower(Position position) {
        try {
            Set<Contract> unmatchedContracts = contractService.findAllByProcessingStatus(
                UNMATCHED); // todo improve move this from position list loop
            if (unmatchedContracts.isEmpty()) {
                throw new ContractNotFoundException();
            }
            log.debug("Matching position: {} with contracts received ({}). Contract ids:{}",
                position.getPositionId(), unmatchedContracts.size(),
                unmatchedContracts.stream().map(Contract::getContractId).collect(Collectors.joining(",")));
            Contract matchedContract = findMatchedContractForBorrower(position, unmatchedContracts);
            savePositionAsMatched(position, matchedContract);
            saveContractAsMatched(matchedContract, position);
            recordContractProposalMatched(matchedContract);
            if (position.isNgtPosition()) {
                agreementService.matchPosition(position);
            }
        } catch (ContractNotFoundException e) {
            log.debug("No matched contracts found for position Id:{}", position.getPositionId());
            position.setProcessingStatus(UNMATCHED);
            final Position unmatchedPosition = savePosition(position);
            recordSystemEvent(unmatchedPosition, GET_NEW_POSITIONS_PENDING_CONFIRMATION, POSITION_UNMATCHED);
        }
    }

    private void recordSystemEvent(Position position, IntegrationSubProcess subProcess, RecordType recordType) {
        final String positionId = String.valueOf(position.getPositionId());
        cloudEventRecordService.getToolkitCloudEventId(positionId, subProcess, recordType)
            .ifPresentOrElse(
                cloudEventRecordService::updateTime,
                () -> createContractInitiationCloudEvent(positionId, recordType, null));
    }

    private Contract findMatchedContractForBorrower(Position position, Set<Contract> unmatchedContracts) {
        if (position.isNgtPosition()) {
            return matchingService.matchBorrowerNgtPositionWithContract(position, unmatchedContracts)
                .orElseThrow(ContractNotFoundException::new);

        }
        return matchingService.matchBorrowerPositionWithContract(position, unmatchedContracts)
            .orElseThrow(ContractNotFoundException::new);
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
            final String positionId = String.valueOf(position.getPositionId());
            log.warn("""
                The loan contract proposal instruction has not been processed by 1Source for the \
                (SPIRE Position: {}) for the following reason: {}""", positionId, e.getStatusCode());
            log.debug("Details: {}", e.getMessage());
            Optional<String> eventId = cloudEventRecordService.getToolkitCloudEventId(positionId,
                POST_LOAN_CONTRACT_PROPOSAL, TECHNICAL_EXCEPTION_1SOURCE);
            eventId.ifPresentOrElse(
                cloudEventRecordService::updateTime,
                () -> createExceptionCloudEvent(positionId, e, CONTRACT_INITIATION, POST_LOAN_CONTRACT_PROPOSAL));
            throwExceptionForRedeliveryPolicy(e);
            return false;
        }
    }

    private Position savePositionAsMatched(Position position, Contract contract) {
        position.setLastUpdateDateTime(LocalDateTime.now());
        position.setProcessingStatus(MATCHED);
        position.setMatching1SourceLoanContractId(contract.getContractId());
        return savePosition(position);
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

    private void createExceptionCloudEvent(HttpStatusCodeException exception,
        IntegrationProcess iP, IntegrationSubProcess subProcess) {
        createExceptionCloudEvent(null, exception, iP, subProcess);
    }

    private void createExceptionCloudEvent(String recordData, HttpStatusCodeException exception,
        IntegrationProcess iP, IntegrationSubProcess subProcess) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(iP);
        var recordRequest = eventBuilder.buildExceptionRequest(exception, subProcess, recordData);
        cloudEventRecordService.record(recordRequest);
    }

}
