package com.intellecteu.onesource.integration.routes.delegate_flow.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    private static void updateFixedRate(Position initialPosition, TradeOut tradeUpdateRequest) {
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

    private static void updateNotFixedRate(Position initialPosition, TradeOut tradeUpdateRequest) {
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

    public Contract executeCancelRequest(Position position) {
        return contractService.findByPositionId(position.getPositionId())
            .map(contract -> {
                final String matchedContractId = position.getMatching1SourceLoanContractId();
                executeCancelRequest(position.getPositionId(), contract, matchedContractId);
                return contract;
            })
            .orElse(null); // temporary solution until new flow will be implemented
    }

    public void cancelContractForCancelLoanTrade(Position position) {
        final Optional<Contract> contractOptional = contractService.findByPositionId(position.getPositionId());
        contractOptional.ifPresent(contract -> {
            final String matchedContractId = position.getMatching1SourceLoanContractId();
            if (contract.getProcessingStatus() != DECLINED && matchedContractId.equals(contract.getContractId())) {
                var canceled = executeCancelRequest(position.getPositionId(), contract, matchedContractId);
                if (canceled) {
                    recordPositionCancelSubmittedEvent(contract);
                    contractService.save(contract);
                }
            }
        });
    }

    private boolean executeCancelRequest(Long positionId, Contract contract, String matchedContractId) {
        log.debug("Sending cancel request for contract Id:{}, position Id:{}", matchedContractId, positionId);
        return oneSourceService.instructCancelLoanContract(contract.getContractId());
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
        final Optional<Contract> contractOpt = contractService.findByPositionId(cancelBorrowPosition.getPositionId());
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
