package com.intellecteu.onesource.integration.routes.delegate_flow.processor;

import static com.intellecteu.onesource.integration.model.enums.FieldSource.ONE_SOURCE_LOAN_CONTRACT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DECLINED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.PROPOSED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.POSITION_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.POSITION_CANCELED_SUBMITTED;
import static java.lang.String.valueOf;

import com.intellecteu.onesource.integration.model.backoffice.Index;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.TradeOut;
import com.intellecteu.onesource.integration.model.enums.FieldExceptionType;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.client.onesource.OneSourceApiClient;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
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
    private final OneSourceApiClient oneSourceApiClient;
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
            .map(p -> updateInitialPosition(p, updateRequestPosition))
            .orElse(null);
    }

    private Position updateInitialPosition(Position initialPosition, Position updateRequestPosition) {
        initialPosition.setRate(updateRequestPosition.getRate());
        initialPosition.setAccrualDate(updateRequestPosition.getAccrualDate());
        final Index initialIndex = initialPosition.getIndex();
        final Index updateRequestIndex = updateRequestPosition.getIndex();
        if (initialIndex != null && updateRequestIndex != null) {
            initialIndex.setIndexId(updateRequestIndex.getIndexId());
            initialIndex.setIndexName(updateRequestIndex.getIndexName());
            initialIndex.setSpread(updateRequestIndex.getSpread());
        }
        return initialPosition;
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

    public Position savePosition(Position position) {
        return positionService.savePosition(position);
    }

    public void executeCancelRequest(Position position) {
        final Optional<Contract> contractOptional = contractService.findByPositionId(position.getPositionId());
        contractOptional.ifPresent(contract -> {
            final String matchedContractId = position.getMatching1SourceLoanContractId();
            if (matchedContractId != null) {
                executeCancelRequest(position.getPositionId(), contract, matchedContractId);
            }
        });
    }

    public void cancelContractForCancelLoanTrade(Position position) {
        final Optional<Contract> contractOptional = contractService.findByPositionId(position.getPositionId());
        contractOptional.ifPresent(contract -> {
            final String matchedContractId = position.getMatching1SourceLoanContractId();
            if (matchedContractId != null && contract.getProcessingStatus() != DECLINED
                && matchedContractId.equals(contract.getContractId())) {
                executeCancelRequest(position.getPositionId(), contract, matchedContractId);
                recordPositionCancelSubmittedEvent(contract);
            }
        });
    }

    private void executeCancelRequest(Long positionId, Contract contract, String matchedContractId) {
        log.debug("Sending cancel request for contract Id:{}, position Id:{}", matchedContractId, positionId);
        oneSourceApiClient.cancelContract(contract); // todo will be reworked to use modern API
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
        // String spireTradeId = String.valueOf(contract.getMatchingSpireTradeId()); todo add the new field
        String positionIdWithTradeId = String.join(",", spirePositionId, "tradeId");
        var recordRequest = eventBuilder.buildRequest(String.valueOf(contract.getContractId()),
            POSITION_CANCELED_SUBMITTED, positionIdWithTradeId);
        cloudEventRecordService.record(recordRequest);
    }

    public void recordPositionUnmatchedSystemEvent(Optional<Contract> unmatchedContract) {
        unmatchedContract.ifPresent(contract -> {
            var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
            var recordRequest = eventBuilder.buildRequest(String.valueOf(contract.getContractId()),
                LOAN_CONTRACT_PROPOSAL_UNMATCHED);
            recordRequest.getData().setFieldsImpacted(createUnmatchedContractFieldsImpacted(contract));
            cloudEventRecordService.record(recordRequest);
        });
    }

    // todo move FieldImpacted creation to cloudEventBuilder when the requirements will be refined
    /*
     * The predefined list of fields that potentially can be unmatched to show to the end user.
     */
    private List<FieldImpacted> createUnmatchedContractFieldsImpacted(Contract contract) {
        return Stream.of(
                buildUnmatchedFieldImpacted("CUSIP", contract.retrieveCusip()),
                buildUnmatchedFieldImpacted("ISIN", contract.retrieveIsin()),
                buildUnmatchedFieldImpacted("SEDOL", contract.retrieveSedol()),
                buildUnmatchedFieldImpacted("Trade date", String.valueOf(contract.getTrade().getTradeDate())),
                buildUnmatchedFieldImpacted("Quantity", String.valueOf(contract.getTrade().getQuantity())),
                buildUnmatchedFieldImpacted("Lender", contract.retrievePartyId(PartyRole.LENDER)))
            .toList();

    }

    // todo move to cloud event builder
    private FieldImpacted buildUnmatchedFieldImpacted(String fieldName, String fieldValue) {
        return FieldImpacted.builder()
            .fieldSource(ONE_SOURCE_LOAN_CONTRACT)
            .fieldName(fieldName)
            .fieldValue(fieldValue)
            .fieldExceptionType(FieldExceptionType.UNMATCHED)
            .build();
    }

    private Contract updateCancelBorrowContract(Contract contract) {
        contract.setProcessingStatus(PROPOSED);
        contract.setMatchingSpirePositionId(null);
        // contract.setMatchingSpireTradeId(null); todo add this field to a model
        contract.setLastUpdateDateTime(LocalDateTime.now());
        return contract;
    }
}
