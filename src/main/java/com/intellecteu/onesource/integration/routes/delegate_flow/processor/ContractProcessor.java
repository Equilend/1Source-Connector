package com.intellecteu.onesource.integration.routes.delegate_flow.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.MATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_PENDING_APPROVAL;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_UNMATCHED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ContractProposal;
import com.intellecteu.onesource.integration.model.onesource.ContractStatus;
import com.intellecteu.onesource.integration.model.onesource.InternalReference;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.model.onesource.TransactingParty;
import com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.DeclineContractInstructionService;
import com.intellecteu.onesource.integration.services.IntegrationDataTransformer;
import com.intellecteu.onesource.integration.services.MatchingService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.PositionService;
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
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContractProcessor {

    private final ContractService contractService;
    private final OneSourceService oneSourceService;
    private final DeclineContractInstructionService declineContractInstructionService;
    private final CloudEventRecordService cloudEventRecordService;
    private final IntegrationDataTransformer dataTransformer;
    private final PositionService positionService;
    private final MatchingService matchingService;

    @Transactional
    public Contract getLoanContractDetails(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/contracts/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            return oneSourceService.retrieveContract(resourceUri)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        } catch (HttpStatusCodeException e) {
            log.debug("Contract {} was not retrieved. Details: {} ", resourceUri, e.getMessage());
            final HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
            if (Set.of(UNAUTHORIZED, NOT_FOUND, INTERNAL_SERVER_ERROR).contains(status)) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                var recordRequest = eventBuilder.buildExceptionRequest(resourceUri,
                    e, GET_LOAN_CONTRACT_PROPOSAL, String.valueOf(event.getEventId()));
                cloudEventRecordService.record(recordRequest);
            }
            return null;
        }
    }

    public boolean matchLenderPosition(@NonNull Contract contract) {
        final Optional<Position> relatedPosition = retrieveRelatedLenderPosition(contract);
        return relatedPosition.map(position -> {
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
        String positionCustomValue2 = contract.getTrade().getVenue().getVenueRefKey();
        notMatchedPositions.stream()
            .filter(position -> positionCustomValue2.equals(position.getCustomValue2()))
            .findAny()
            .ifPresent(position -> updateAndRecordMatchedSystemEventForBorrower(contract, position));
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
        return contract.getTrade().getVenue() != null && contract.getTrade().getVenue().getVenueRefKey() != null;
    }

    private Position updateMatchedContractAndPosition(Contract contract, Position position) {
        saveContractAsMatched(contract, position);
        position.setMatching1SourceLoanContractId(contract.getContractId());
        return positionService.savePosition(position);
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
            throw new EntityNotFoundException(String.format("Internal ref is null for contractID = %s.", contractId));
        }
        final String internalRefId = internalRef.getInternalRefId();
        if (internalRefId == null) {
            throw new EntityNotFoundException(String.format("Internal refId is null for contractID = %s.", contractId));
        }
        return Long.parseLong(internalRefId);
    }

    private TransactingParty retrieveLenderTransactingParty(Contract contract) {
        List<TransactingParty> contractParties = contract.getTrade().getTransactingParties();
        if (CollectionUtils.isEmpty(contractParties)) {
            throw new EntityNotFoundException(
                String.format("Contract %s has no transacting parties", contract.getContractId()));
        }
        return contractParties.stream()
            .filter(p -> p.getPartyRole() == PartyRole.LENDER)
            .findAny()
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Contract %s does not have LENDER transacting party", contract.getContractId())));
    }

    public Contract updateLoanContractStatus(@NonNull Contract contract, @NonNull ProcessingStatus processingStatus) {
        contract.setProcessingStatus(processingStatus);
        contract.setCreateDateTime(LocalDateTime.now());
        contract.setLastUpdateDateTime(LocalDateTime.now());
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
            declineContractInstructionService.save(declineInstruction);
        });
    }

    private void recordPendingApprovalSystemEvent(Contract contract, Position savedPosition) {
        createContractInitiationCloudEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_PENDING_APPROVAL,
            String.format("%d,%d", savedPosition.getPositionId(), savedPosition.getTradeId()));
    }

    private void recordLoanProposalMatchedSystemEvent(Contract contract, Position savedPosition) {
        createContractInitiationCloudEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_MATCHED,
            String.format("%d,%d", savedPosition.getPositionId(), savedPosition.getTradeId()));
    }

    private void recordLoanProposalUnmatchedSystemEvent(Contract contract) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(LOAN_CONTRACT_PROPOSAL_UNMATCHED, contract);
        cloudEventRecordService.record(recordRequest);
    }

    private void createContractInitiationCloudEvent(String recordData, RecordType recordType, String relatedData) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(recordData, recordType, relatedData);
        cloudEventRecordService.record(recordRequest);
    }

    private Contract saveContractAsMatched(Contract contract, Position position) {
        contract.setMatchingSpirePositionId(position.getPositionId());
        contract.setMatchingSpireTradeId(position.getTradeId());
        contract.setProcessingStatus(MATCHED);
        contract.setLastUpdateDateTime(LocalDateTime.now());
        return contractService.save(contract);
    }

}
