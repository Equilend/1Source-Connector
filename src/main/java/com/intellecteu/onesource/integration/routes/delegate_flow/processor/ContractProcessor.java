package com.intellecteu.onesource.integration.routes.delegate_flow.processor;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.APPROVE_LOAN_PROPOSAL_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.DECLINE_LOAN_PROPOSAL_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_CANCELLATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_SETTLEMENT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.APPROVE_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.CAPTURE_POSITION_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.DECLINE_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_SETTLED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.INSTRUCT_LOAN_CONTRACT_CANCELLATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.LOAN_CONTRACT_PROPOSAL_CANCEL_PENDING;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DECLINED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DECLINE_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.MATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.PROCESSED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.SETTLED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.VALIDATED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_CANCEL_PENDING;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_DECLINED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_PENDING_APPROVAL;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_VALIDATED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.POSITION_CANCEL_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_ISSUE_INTEGRATION_TOOLKIT;
import static com.intellecteu.onesource.integration.model.onesource.ContractStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.onesource.ContractStatus.OPEN;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.parseContractIdFrom1SourceResourceUri;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.DeclineInstruction;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ContractProposal;
import com.intellecteu.onesource.integration.model.onesource.ContractProposalApproval;
import com.intellecteu.onesource.integration.model.onesource.ContractStatus;
import com.intellecteu.onesource.integration.model.onesource.InternalReference;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.model.onesource.TransactingParty;
import com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.DeclineInstructionService;
import com.intellecteu.onesource.integration.services.IntegrationDataTransformer;
import com.intellecteu.onesource.integration.services.MatchingService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.reconciliation.ReconcileService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContractProcessor {

    private final ContractService contractService;
    private final OneSourceService oneSourceService;
    private final DeclineInstructionService declineInstructionService;
    private final CloudEventRecordService cloudEventRecordService;
    private final IntegrationDataTransformer dataTransformer;
    private final PositionService positionService;
    private final MatchingService matchingService;
    private final ReconcileService<Contract, Position> reconcileService;

    @Transactional
    public Contract getLoanContractDetails(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/contracts/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            String contractId = parseContractIdFrom1SourceResourceUri(resourceUri);
            final Contract contract = oneSourceService.retrieveContractDetails(contractId);
            return contract;
        } catch (HttpStatusCodeException e) {
            log.debug("Contract {} was not retrieved. Details: {} ", resourceUri, e.getMessage());
            final HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
            if (Set.of(UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR).contains(status)) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                var recordRequest = eventBuilder.buildExceptionRequest(resourceUri,
                    e, GET_LOAN_CONTRACT_PROPOSAL, event.getEventId());
                cloudEventRecordService.record(recordRequest);
            }
            return null;
        }
    }

    public void executeCancelUpdate(@NonNull Contract contract) {
        boolean isLoanProposal = ContractStatus.PROPOSED == contract.getContractStatus();
        if (isLoanProposal) {
            executeCancelUpdateForLoanContractProposal(contract);
        }
        contract.setProcessingStatus(ProcessingStatus.CANCELED);
        contract.setContractStatus(CANCELED);
        contractService.save(contract);
        if (!isLoanProposal) {
            createBusinessEvent(contract.getContractId(),
                LOAN_CONTRACT_CANCELED, String.valueOf(contract.getMatchingSpirePositionId()),
                GET_LOAN_CONTRACT_CANCELED, CONTRACT_CANCELLATION);
        }
    }

    private void executeCancelUpdateForLoanContractProposal(Contract contract) {
        if (contract.getMatchingSpirePositionId() != null) {
            positionService.getByPositionIdAndRole(contract.getMatchingSpirePositionId(), PartyRole.BORROWER)
                .ifPresent(position -> {
                    position.setMatching1SourceLoanContractId(null);
                    positionService.savePosition(position);
                    String record = contract.getContractId();
                    String related = String.format("%d,%d", position.getPositionId(), position.getTradeId());
                    createBusinessEvent(record, LOAN_CONTRACT_PROPOSAL_CANCELED, related,
                        GET_LOAN_CONTRACT_CANCELED, CONTRACT_INITIATION);
                });
        }
    }

    public Contract retrieveContractFromEvent(@NonNull TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/contracts/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        String contractId = parseContractIdFrom1SourceResourceUri(resourceUri);
        return contractService.findContractById(contractId).orElse(null);
    }

    public Contract validate(@NonNull Contract contractProposal) {
        return positionService.getByPositionId(contractProposal.getMatchingSpirePositionId())
            .map(position -> reconcileProposalWithPosition(contractProposal, position))
            .orElse(null);
    }

    private Contract reconcileProposalWithPosition(@NonNull Contract contractProposal, @NonNull Position position) {
        try {
            reconcileService.reconcile(contractProposal, position);
            contractProposal.setProcessingStatus(VALIDATED);
            return saveContract(contractProposal);
        } catch (ReconcileException e) {
            log.debug("Reconciliation exception: {}", e.getMessage());
            contractProposal.setProcessingStatus(DISCREPANCIES);
            createFailedReconciliationEvent(contractProposal, e);
            saveContract(contractProposal);
            return null;
        }
    }

    private void createFailedReconciliationEvent(Contract contractProposal, ReconcileException e) {
        String relatedSequence = format("%d,%d", contractProposal.getMatchingSpirePositionId(),
            contractProposal.getMatchingSpireTradeId());
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(contractProposal.getContractId(),
            LOAN_CONTRACT_PROPOSAL_DISCREPANCIES, relatedSequence, e.getErrorList());
        cloudEventRecordService.record(recordRequest);
    }

    public void updateSettledContract(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/contracts/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        String contractId = parseContractIdFrom1SourceResourceUri(resourceUri);
        final Optional<Contract> contractOptional = contractService.findContractById(contractId);
        contractOptional.ifPresentOrElse(
            this::updateAndRecordSystemEvent,
            () -> recordToolkitIssueEvent(CONTRACT_SETTLEMENT, event.getResourceUri(), GET_LOAN_CONTRACT_SETTLED)
        );
    }

    private void updateAndRecordSystemEvent(Contract contract) {
        updateContractStatuses(SETTLED, contract, OPEN);
        recordSettledSystemEvent(contract);
    }

    /*
     * Update Processing status and Contract status for the contract and save it.
     * Return persisted Contract model.
     */
    private Contract updateContractStatuses(ProcessingStatus processingStatus, Contract contract,
        ContractStatus contractStatus) {
        contract.setProcessingStatus(processingStatus);
        contract.setContractStatus(contractStatus);
        return saveContract(contract);
    }

    public Contract declineCapturedContract(TradeEvent event) {
        String contractId = parseContractIdFrom1SourceResourceUri(event.getResourceUri());
        return contractService.findContractById(contractId)
            .map(this::declineContract)
            .orElse(null);
    }

    private Contract declineContract(Contract contract) {
        if (contract.getMatchingSpirePositionId() != null) {
            removeMatchingContractFromPosition(contract.getMatchingSpirePositionId());
        }
        contract.setProcessingStatus(DECLINED);
        contract.setContractStatus(ContractStatus.DECLINED);
        return contractService.save(contract);
    }

    private void removeMatchingContractFromPosition(Long positionId) {
        positionService.getByPositionId(positionId)
            .ifPresent(position -> {
                position.setMatching1SourceLoanContractId(null);
                positionService.savePosition(position);
            });
    }

    /**
     * Find recorded contract and update with required contract details retrieved from third party
     *
     * @param contractDetails Contract
     * @return updated Contract model
     */
    public Contract updateContractWithContractDetails(Contract contractDetails) {
        final Optional<Contract> persistedContract = contractService.findContractById(contractDetails.getContractId());
        return persistedContract.map(contract -> {
            contract.setContractStatus(contractDetails.getContractStatus());
            contract.setTrade(contractDetails.getTrade());
            contract.setSettlement(contractDetails.getSettlement());
            return contract;
        }).orElse(null);
    }

    public boolean matchLenderPosition(@NonNull Contract contract) {
        final Optional<Position> relatedPosition = retrieveRelatedLenderPosition(contract);
        return relatedPosition.map(position -> {
            log.debug("Lender position:{} matches to contract:{}", position.getPositionId(), contract.getContractId());
            final Position savedPosition = updateMatchedContractAndPosition(contract, position);
            recordPendingApprovalSystemEvent(contract, savedPosition);
            return true;
        }).orElse(false);
    }

    public void matchBorrowerPosition(@NonNull Contract contract) {
        final Set<Position> notMatchedPositions = positionService.getNotMatched();
//        if (isNgtTradeContract(contract)) { todo waiting for story update if we still need this logic
//            matchNgtTradeContractForBorrower(contract, notMatchedPositions);
//        } else {
        matchContractForBorrower(contract, notMatchedPositions);
//        }
    }

    private void matchNgtTradeContractForBorrower(Contract contract, Set<Position> notMatchedPositions) {
//        String positionCustomValue2 = contract.getTrade().getVenue().getVenueRefKey();
//        notMatchedPositions.stream()
//            .filter(position -> positionCustomValue2.equals(position.getCustomValue2()))
//            .findAny()
//            .ifPresent(position -> updateAndRecordMatchedSystemEventForBorrower(contract, position));
    }

    private void matchContractForBorrower(Contract contract, Set<Position> notMatchedPositions) {
        matchingService.matchBorrowContractWithPositions(contract, notMatchedPositions)
            .ifPresentOrElse(
                position -> updateAndRecordMatchedSystemEventForBorrower(contract, position),
                () -> updateAndRecordUnmatchedSystemEvent(contract));
    }

    private void updateAndRecordUnmatchedSystemEvent(Contract contract) {
        contract.setProcessingStatus(UNMATCHED);
        log.debug("Contract id={} was NOT matched with position any of positions.", contract.getContractId());
        final Contract savedContract = contractService.save(contract);
        recordLoanProposalUnmatchedSystemEvent(savedContract);
    }

    private void updateAndRecordMatchedSystemEventForBorrower(Contract contract, Position position) {
        final Position savedPosition = updateMatchedContractAndPosition(contract, position);
        recordLoanProposalMatchedSystemEvent(contract, savedPosition);
        log.debug("Contract id={} was matched with position id={}", contract.getContractId(), position.getPositionId());
    }

    private static boolean isNgtTradeContract(Contract contract) {
        return contract.getTrade().getVenues() != null
            && contract.getTrade().getVenues().get(0) != null
            && contract.getTrade().getVenues().get(0).getVenueRefKey() != null;
    }

    private Position updateMatchedContractAndPosition(Contract contract, Position position) {
        saveContractAsMatched(contract, position);
        position.setMatching1SourceLoanContractId(contract.getContractId());
        return positionService.savePosition(position);
    }

    public Contract retrieveContractFromDeclineInstruction(DeclineInstruction declineInstruction) {
        return contractService.findContractById(declineInstruction.getRelatedProposalId())
            .filter(contract -> Set.of(DISCREPANCIES, UNMATCHED).contains(contract.getProcessingStatus()))
            .orElse(null);
    }

    public void instructDeclineLoanContractProposal(@NonNull Contract contract, DeclineInstruction declineInstruction) {
        try {
            oneSourceService.instructDeclineLoanProposal(contract);
            contract.setProcessingStatus(DECLINE_SUBMITTED);
            contractService.save(contract);
            declineInstruction.setProcessingStatus(PROCESSED);
            declineInstructionService.save(declineInstruction);
        } catch (HttpStatusCodeException e) {
            String positionId = String.valueOf(contract.getMatchingSpirePositionId());
            log.debug(
                format(DECLINE_LOAN_PROPOSAL_EXCEPTION_MSG, contract.getContractId(), positionId, e.getStatusText()));
            final HttpStatusCode statusCode = e.getStatusCode();
            if (Set.of(BAD_REQUEST, UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR)
                .contains(HttpStatus.valueOf(statusCode.value()))) {
                recordTechnicalEvent(contract.getContractId(), e, DECLINE_LOAN_CONTRACT_PROPOSAL, positionId);
            }
        }
    }

    public boolean instructCancelLoanContract(@NonNull Position position) {
        try {
            return oneSourceService.instructCancelLoanContract(position.getMatching1SourceLoanContractId());
        } catch (HttpStatusCodeException e) {
            String record = position.getMatching1SourceLoanContractId();
            String related = String.valueOf(position.getPositionId());
            record1SourceTechnicalEvent(record, e, INSTRUCT_LOAN_CONTRACT_CANCELLATION, related, CONTRACT_CANCELLATION);
            return false;
        }
    }

    public void instructContractApprovalAsBorrower(@NonNull Contract contract) {
        Optional<Position> matchedPosition = positionService.getByPositionId(contract.getMatchingSpirePositionId());
        matchedPosition.ifPresent(position -> instructContractApproval(contract, position));
    }

    private void instructContractApproval(@NonNull Contract contract, @NonNull Position position) {
        final ContractProposalApproval borrowerContractProposalApproval = dataTransformer
            .toBorrowerContractProposalApproval(contract, position);
        try {
            oneSourceService.executeContractProposalApproval(borrowerContractProposalApproval, contract);
            contract.setProcessingStatus(ProcessingStatus.APPROVAL_SUBMITTED);
            contractService.save(contract);
        } catch (HttpStatusCodeException e) {
            var positionId = String.valueOf(contract.getMatchingSpirePositionId());
            log.debug(
                format(APPROVE_LOAN_PROPOSAL_EXCEPTION_MSG, contract.getContractId(), positionId, e.getStatusCode()));
            final HttpStatus httpStatus = HttpStatus.valueOf(e.getStatusCode().value());
            if (Set.of(BAD_REQUEST, UNAUTHORIZED, NOT_FOUND, CONFLICT, INTERNAL_SERVER_ERROR).contains(httpStatus)) {
                recordTechnicalEvent(contract.getContractId(), e, APPROVE_LOAN_CONTRACT_PROPOSAL, positionId);
            }
        }
    }

    private Optional<Position> retrieveRelatedLenderPosition(@NonNull Contract contract) {
        try {
            final TransactingParty lenderParty = retrieveLenderTransactingParty(contract);
            final Long relatedPositionId = retrieveContractRelatedPositionId(lenderParty, contract.getContractId());
            return positionService.getNotMatchedByPositionId(relatedPositionId);
        } catch (EntityNotFoundException | NumberFormatException e) {
            log.debug("No related lender position for contract:{}. Details:{}",
                contract.getContractId(), e.getMessage());
            return Optional.empty();
        }
    }

    private Long retrieveContractRelatedPositionId(TransactingParty lenderParty, String contractId) {
        final InternalReference internalRef = lenderParty.getInternalRef();
        if (internalRef == null) {
            throw new EntityNotFoundException(format("Internal ref is null for contractID = %s.", contractId));
        }
        final String internalRefId = internalRef.getInternalRefId();
        if (internalRefId == null) {
            throw new EntityNotFoundException(format("Internal refId is null for contractID = %s.", contractId));
        }
        return Long.parseLong(internalRefId);
    }

    private TransactingParty retrieveLenderTransactingParty(Contract contract) {
        List<TransactingParty> contractParties = contract.getTrade().getTransactingParties();
        if (CollectionUtils.isEmpty(contractParties)) {
            throw new EntityNotFoundException(
                format("Contract %s has no transacting parties", contract.getContractId()));
        }
        return contractParties.stream()
            .filter(p -> p.getPartyRole() == PartyRole.LENDER)
            .findAny()
            .orElseThrow(() -> new EntityNotFoundException(
                format("Contract %s does not have LENDER transacting party", contract.getContractId())));
    }

    public Contract updateContractProcessingStatusAndCreatedTime(@NonNull Contract contract,
        @NonNull ProcessingStatus processingStatus) {
        contract.setProcessingStatus(processingStatus);
        contract.setCreateDateTime(LocalDateTime.now());
        contract.setLastUpdateDateTime(LocalDateTime.now());
        return contract;
    }

    /**
     * Update contract processing status and save the contract
     *
     * @param contract Contract
     * @param processingStatus Processing status
     * @return Contract persisted contract with updated processing status
     */
    @Transactional
    public Contract updateContractProcessingStatus(@NonNull Contract contract,
        @NonNull ProcessingStatus processingStatus) {
        contract.setProcessingStatus(processingStatus);
        return saveContract(contract);
    }

    public Contract updateContractStatus(@NonNull Contract contract, ContractStatus contractStatus) {
        contract.setContractStatus(contractStatus);
        return contract;
    }

    public ContractProposal createProposalFromPosition(@NonNull Position position) {
        return dataTransformer.toLenderContractProposal(position);
    }

    public Contract saveContract(@NonNull Contract contract) {
        return contractService.save(contract);
    }

    @Transactional
    public void processTradeData() {
        log.info(">>>>> Starting processing trade data");
        contractService.findAllNotProcessed().forEach(this::processContract);
        log.info("<<<<<< Finished processing trade data");
    }

    private void processContract(Contract contract) {
        log.debug("***** Processing Contract id: {} with status {}, processing status: {} ",
            contract.getContractId(), contract.getContractStatus(), contract.getProcessingStatus());
//        var strategy = strategyByFlow.get(contract.getFlowStatus());
//        if (strategy == null) {
//            throw new RuntimeException(
//                "Strategy is not implemented for the flow: " + contract.getFlowStatus());
//        }
//        strategy.process(contract);
    }

    @Transactional
    @Deprecated(since = "0.0.5", forRemoval = true)
    public void processDecline(DeclineInstructionEntity declineInstruction) {
        String contractId = declineInstruction.getRelatedProposalId();
        Optional<Contract> contractOptional = contractService.findContractById(contractId);
        contractOptional.ifPresent(contract -> {
//            oneSourceClient.declineContract(contract);
            contract.setContractStatus(ContractStatus.DECLINED);
            contract.setProcessingStatus(ProcessingStatus.DECLINED);
            contractService.save(contract);
            declineInstruction.setProcessingStatus(ProcessingStatus.PROCESSED);
            declineInstructionService.save(declineInstruction);
        });
    }

    public void recordPositionCancelSubmittedSystemEvent(@NonNull Position position) {
        final String positionId = String.valueOf(position.getPositionId());
        createBusinessEvent(position.getMatching1SourceLoanContractId(), POSITION_CANCEL_SUBMITTED,
            positionId, CAPTURE_POSITION_CANCELED, CONTRACT_CANCELLATION);
    }

    private void recordPendingApprovalSystemEvent(Contract contract, Position position) {
        createContractInitiationCloudEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_PENDING_APPROVAL,
            format("%d,%d", position.getPositionId(), position.getTradeId()));
    }

    private void recordLoanProposalMatchedSystemEvent(Contract contract, Position savedPosition) {
        createContractInitiationCloudEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_MATCHED,
            format("%d,%d", savedPosition.getPositionId(), savedPosition.getTradeId()));
    }

    private void recordLoanProposalUnmatchedSystemEvent(Contract contract) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(LOAN_CONTRACT_PROPOSAL_UNMATCHED, contract);
        cloudEventRecordService.record(recordRequest);
    }

    public void recordApprovedSystemEvent(@NonNull Contract contract) {
        String related = format("%d,%d", contract.getMatchingSpirePositionId(), contract.getMatchingSpireTradeId());
        createContractInitiationCloudEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_APPROVED, related);
    }

    public void recordSettledSystemEvent(@NonNull Contract contract) {
        String related = String.valueOf(contract.getMatchingSpirePositionId());
        createContractSettlementCloudEvent(contract.getContractId(), RecordType.LOAN_CONTRACT_SETTLED, related);
    }

    public void recordLoanProposalDeclinedSystemEvent(@NonNull Contract contract) {
        String related = contract.getMatchingSpirePositionId() == null
            ? null
            : format("%d,%d", contract.getMatchingSpirePositionId(), contract.getMatchingSpireTradeId());
        createContractInitiationCloudEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_DECLINED, related);
    }

    public void recordContractProposalValidatedEvent(@NonNull Contract contract) {
        String relatedSequence = format("%d,%d", contract.getMatchingSpirePositionId(),
            contract.getMatchingSpireTradeId());
        createContractInitiationCloudEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_VALIDATED, relatedSequence);
    }

    private void createContractInitiationCloudEvent(String recordData, RecordType recordType, String relatedData) {
        createCloudEvent(recordData, recordType, relatedData, IntegrationProcess.CONTRACT_INITIATION);
    }

    private void createContractSettlementCloudEvent(String recordData, RecordType recordType, String relatedData) {
        createCloudEvent(recordData, recordType, relatedData, IntegrationProcess.CONTRACT_SETTLEMENT);
    }

    private void createCloudEvent(String recordData, RecordType recordType, String relatedData, IntegrationProcess ip) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(ip);
        var recordRequest = eventBuilder.buildRequest(recordData, recordType, relatedData);
        cloudEventRecordService.record(recordRequest);
    }

    /**
     * Record Integration Issue System Event for defined integration sub process
     *
     * @param event TradeEvent details to be recorded
     * @param subProcess IntegrationSubProcess of the integration process
     */
    public void recordIntegrationIssueEvent(TradeEvent event, IntegrationSubProcess subProcess) {
        recordToolkitIssueEvent(CONTRACT_INITIATION, event.getResourceUri(), subProcess);
    }

    private void recordToolkitIssueEvent(IntegrationProcess process, String record,
        IntegrationSubProcess subProcess) {
        cloudEventRecordService.getToolkitCloudEventId(record, subProcess, TECHNICAL_ISSUE_INTEGRATION_TOOLKIT)
            .ifPresentOrElse(
                cloudEventRecordService::updateTime,
                () -> recordToolkitTechnicalEvent(process, record, subProcess));
    }

    private void recordToolkitTechnicalEvent(IntegrationProcess process, String record,
        IntegrationSubProcess subProcess) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(process);
        var recordRequest = eventBuilder.buildToolkitIssueRequest(record, subProcess);
        cloudEventRecordService.record(recordRequest);
    }

    private void recordTechnicalEvent(String recorded, HttpStatusCodeException exception,
        IntegrationSubProcess subProcess, String related) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildExceptionRequest(recorded, exception, subProcess, related);
        cloudEventRecordService.record(recordRequest);
    }

    private void recordTechnicalEvent(IntegrationProcess process, String record,
        HttpStatusCodeException exception, IntegrationSubProcess subProcess, String related) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(process);
        var recordRequest = eventBuilder.buildExceptionRequest(record, exception, subProcess, related);
        cloudEventRecordService.record(recordRequest);

    }

    private void record1SourceTechnicalEvent(String record, HttpStatusCodeException exception,
        IntegrationSubProcess subProcess, String related, IntegrationProcess process) {
        cloudEventRecordService.getToolkitCloudEventId(record, subProcess, TECHNICAL_EXCEPTION_1SOURCE)
            .ifPresentOrElse(
                cloudEventRecordService::updateTime,
                () -> recordTechnicalEvent(process, record, exception, subProcess, related)
            );
    }

    public void recordCancelPendingEvent(Contract contract) {
        final String positionId = String.valueOf(contract.getMatchingSpirePositionId());
        createBusinessEvent(contract.getContractId(), LOAN_CONTRACT_CANCEL_PENDING, positionId,
            LOAN_CONTRACT_PROPOSAL_CANCEL_PENDING, CONTRACT_CANCELLATION);
    }

    private Contract saveContractAsMatched(Contract contract, Position position) {
        contract.setMatchingSpirePositionId(position.getPositionId());
        contract.setMatchingSpireTradeId(position.getTradeId());
        contract.setProcessingStatus(MATCHED);
        contract.setLastUpdateDateTime(LocalDateTime.now());
        return contractService.save(contract);
    }

    private void createBusinessEvent(String record, RecordType recordType,
        String related, IntegrationSubProcess subProcess, IntegrationProcess process) {
        cloudEventRecordService.getToolkitCloudEventId(record, subProcess, recordType)
            .ifPresentOrElse(
                cloudEventRecordService::updateTime,
                () -> recordBusinessEvent(record, recordType, related, subProcess, process)
            );
    }

    private void recordBusinessEvent(String record, RecordType recordType,
        String related, IntegrationSubProcess subProcess, IntegrationProcess process) {
        try {
            var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(process);
            var recordRequest = eventBuilder.buildRequest(record, recordType, related);
            cloudEventRecordService.record(recordRequest);
        } catch (Exception e) {
            log.warn("Cloud event cannot be recorded for recordType:{}, process:{}, subProcess:{}, record:{}",
                recordType, process, subProcess, record);
        }
    }

}
