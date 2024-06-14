package com.intellecteu.onesource.integration.routes.delegate_flow.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.CANCEL_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CANCEL_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DECLINED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.PROPOSED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.POSITION_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.POSITION_CANCEL_SUBMITTED;
import static java.lang.String.valueOf;

import com.intellecteu.onesource.integration.model.backoffice.Index;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.PositionStatus;
import com.intellecteu.onesource.integration.model.backoffice.TradeOut;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdatePositionProcessor {

    private final PositionService positionService;
    private final BackOfficeService backOfficeService;
    private final ContractService contractService;
    private final OneSourceService oneSourceService;
    private final CloudEventRecordService cloudEventRecordService;

    public List<TradeOut> fetchUpdatesOnPositions(List<Position> initialPositions) {
        return backOfficeService.fetchUpdatesOnPositions(initialPositions);
    }

    public Position updatePositionForRerateTrade(TradeOut tradeOut, List<Position> initialPositions) {
        final Position updateRequestPosition = tradeOut.getPosition();
        if (updateRequestPosition == null) {
            return null;
        }
        return initialPositions.stream()
            .filter(p -> updateRequestPosition.getPositionId().equals(p.getPositionId()))
            .findAny()
            .map(p -> updateInitialPositionForRerate(p, tradeOut))
            .orElse(null);
    }

    private Position updateInitialPositionForRerate(Position initialPosition, TradeOut tradeUpdateRequest) {
        updateFixedRate(initialPosition, tradeUpdateRequest);
        updateNotFixedRate(initialPosition, tradeUpdateRequest);
        return initialPosition;
    }

    private void updateFixedRate(Position initialPosition, TradeOut tradeUpdateRequest) {
        final Index indexUpdateRequest = tradeUpdateRequest.getPosition().getIndex();
        final Index initialIndex = initialPosition.getIndex();
        if (indexUpdateRequest != null && initialIndex != null) {
            final String updateRequestIndexName = indexUpdateRequest.getIndexName();
            final String initialIndexName = initialIndex.getIndexName();
            if ("Fixed Rate".equals(updateRequestIndexName)) {
                initialPosition.setRate(tradeUpdateRequest.getRateOrSpread());
                initialPosition.setAccrualDate(tradeUpdateRequest.getAccrualDate());

                if (!updateRequestIndexName.equals(initialIndexName)) {
                    initialIndex.setIndexId(indexUpdateRequest.getIndexId());
                    initialIndex.setIndexName(indexUpdateRequest.getIndexName());
                    initialIndex.setSpread(null);
                }
            }
        }
    }

    private void updateNotFixedRate(Position initialPosition, TradeOut tradeUpdateRequest) {
        final Index indexUpdateRequest = tradeUpdateRequest.getPosition().getIndex();
        final Index initialIndex = initialPosition.getIndex();
        if (indexUpdateRequest != null && initialIndex != null) {
            final String updateRequestIndexName = indexUpdateRequest.getIndexName();
            if (!"Fixed Rate".equals(updateRequestIndexName)) {
                initialPosition.setRate(null);
                initialPosition.setAccrualDate(tradeUpdateRequest.getAccrualDate());
                initialIndex.setIndexId(indexUpdateRequest.getIndexId());
                initialIndex.setIndexName(indexUpdateRequest.getIndexName());
                initialIndex.setSpread(tradeUpdateRequest.getRateOrSpread());
            }
        }
    }

    public Position getPositionToUpdateById(Long positionIdToUpdate, List<Position> initialPositions) {
        return initialPositions.stream()
            .filter(position -> position.getPositionId().equals(positionIdToUpdate))
            .findAny()
            .orElse(null);
    }

    public Position updatePositionForRollTrade(TradeOut tradeOut, List<Position> initialPositions) {
        final Position updateRequestPosition = tradeOut.getPosition();
        if (updateRequestPosition == null) {
            return null;
        }
        return initialPositions.stream()
            .filter(p -> updateRequestPosition.getPositionId().equals(p.getPositionId()))
            .findAny()
            .map(p -> updateInitialPositionForRollType(p, updateRequestPosition))
            .orElse(null);
    }

    private Position updateInitialPositionForRollType(Position initialPosition, Position updateRequestPosition) {
        if (updateRequestPosition == null) {
            return null;
        }
        initialPosition.setTermId(updateRequestPosition.getTermId());
        initialPosition.setEndDate(updateRequestPosition.getEndDate());
        return initialPosition;
    }

    public Position updatePositionProcessingStatus(Position position, ProcessingStatus processingStatus) {
        position.setProcessingStatus(processingStatus);
        position.setLastUpdateDateTime(LocalDateTime.now());
        return position;
    }

    public Position updatePositionStatus(Position position, String positionStatus) {
        if (position.getPositionStatus() == null) {
            position.setPositionStatus(new PositionStatus());
        }
        position.getPositionStatus().setStatus(positionStatus);
        position.setLastUpdateDateTime(LocalDateTime.now());
        return position;
    }

    public Position savePosition(Position position) {
        return positionService.savePosition(position);
    }

    /**
     * Instruct the cancellation of the locan contract proposal that has been previously generated from the position
     * before the update if the contract can be found.
     *
     * @param position Position
     * @return persisted contract with processing status CANCEL_SUBMITTED or null if no linked contract was found or
     * contract was not cancelled
     */
    public @Nullable Contract instructProposalCancel(Position position) {
        return contractService.findContractById(position.getMatching1SourceLoanContractId())
            .map(this::submitContractCancel)
            .orElse(null);
    }

    @Transactional
    public Position delinkContract(@NotNull Position position) {
        return positionService.delinkContract(position);
    }

    private Contract submitContractCancel(Contract contract) {
        boolean isCanceled = executeCancelRequest(contract);
        if (isCanceled) {
            log.debug("Contract:{} was canceled", contract.getContractId());
            contract.setProcessingStatus(CANCEL_SUBMITTED);
            return contractService.save(contract);
        }
        log.debug("Contract:{} was not canceled!", contract.getContractId());
        return null;
    }

    public void cancelContractForCancelLoanTrade(Position position) {
        log.debug("Searching for not DECLINED Contract to cancel:{} ", position.getMatching1SourceLoanContractId());
        contractService.findContractByIdExcludeProcessingStatus(position.getMatching1SourceLoanContractId(), DECLINED)
            .map(this::submitContractCancel)
            .ifPresent(this::recordPositionCancelSubmittedEvent);
    }

    private boolean executeCancelRequest(Contract contract) {
        try {
            return oneSourceService.instructCancelLoanContract(contract.getContractId());
        } catch (HttpStatusCodeException e) {
            var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
            var recordRequest = eventBuilder.buildExceptionRequest(contract.getContractId(), e,
                CANCEL_LOAN_CONTRACT_PROPOSAL, contract.getContractId());
            cloudEventRecordService.record(recordRequest);
            return false;
        }
    }

    public void recordPositionCanceledSystemEvent(Position toCancelPosition) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(valueOf(toCancelPosition.getPositionId()), POSITION_CANCELED);
        cloudEventRecordService.record(recordRequest);
    }

    public Optional<Contract> updateLoanContract(Position cancelBorrowPosition) {
        if (cancelBorrowPosition.getMatching1SourceLoanContractId() == null) {
            return Optional.empty();
        }
        final Optional<Contract> contractOpt = contractService.findContractById(
            cancelBorrowPosition.getMatching1SourceLoanContractId());
        return contractOpt
            .map(this::updateCancelBorrowContract)
            .map(contractService::save);
    }

    public void recordPositionCancelSubmittedEvent(Contract contract) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
        String spirePositionId = String.valueOf(contract.getMatchingSpirePositionId());
        String spireTradeId = String.valueOf(contract.getMatchingSpireTradeId());
        String positionIdWithTradeId = String.join(",", spirePositionId, spireTradeId);
        var recordRequest = eventBuilder.buildRequest(String.valueOf(contract.getContractId()),
            POSITION_CANCEL_SUBMITTED, positionIdWithTradeId);
        cloudEventRecordService.record(recordRequest);
    }

    public void recordPositionUnmatchedSystemEvent(Optional<Contract> unmatchedContract) {
        unmatchedContract.ifPresent(contract -> {
            var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
            var recordRequest = eventBuilder.buildRequest(String.valueOf(contract.getContractId()),
                LOAN_CONTRACT_PROPOSAL_UNMATCHED);
            cloudEventRecordService.record(recordRequest);
        });
    }

    private Contract updateCancelBorrowContract(Contract contract) {
        contract.setProcessingStatus(PROPOSED);
        contract.setMatchingSpirePositionId(null);
        contract.setMatchingSpireTradeId(null);
        contract.setLastUpdateDateTime(LocalDateTime.now());
        return contract;
    }
}
