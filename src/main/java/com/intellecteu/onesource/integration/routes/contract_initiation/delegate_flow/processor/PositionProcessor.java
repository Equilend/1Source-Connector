package com.intellecteu.onesource.integration.routes.contract_initiation.delegate_flow.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.RecordType.POSITION_UNMATCHED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.RECONCILED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.SI_FETCHED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.TRADE_DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.TRADE_RECONCILED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.UNMATCHED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.exception.ContractNotFoundException;
import com.intellecteu.onesource.integration.exception.InstructionRetrievementException;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ContractProposal;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.onesource.TradeAgreement;
import com.intellecteu.onesource.integration.services.AgreementService;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.MatchingService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.client.onesource.OneSourceApiClient;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.utils.IntegrationUtils;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionProcessor {

    private final PositionService positionService;
    private final BackOfficeService lenderBackOfficeService;
    private final AgreementService agreementService;
    private final ContractService contractService;
    private final OneSourceApiClient oneSourceApiClient;
    private final SettlementService settlementService;
    private final SpireMapper spireMapper;
    private final CloudEventRecordService cloudEventRecordService;
    private final MatchingService matchingService;

    public Position savePosition(Position position) {
        return positionService.savePosition(position);
    }

    public Position updateProcessingStatus(Position position, ProcessingStatus processingStatus) {
        position.setProcessingStatus(processingStatus);
        return position;
    }

    public Position matchTradeAgreement(Position position) {
        agreementService.findByVenueRefId(position.getVenueRefId())
            .ifPresent(agreement -> {
                position.setMatching1SourceTradeAgreementId(agreement.getAgreementId());
                agreementService.markAgreementAsMatched(agreement, String.valueOf(position.getPositionId()));
            });
        return position;
    }

    @Deprecated(since = "0.0.5-SNAPSHOT")
    public Position matchContractProposalObsolete(Position position) {
        Optional<Contract> contractOpt = contractService.findByVenueRefId(position.getVenueRefId());
        contractOpt.ifPresent(contract -> {
            position.setMatching1SourceLoanContractId(contract.getContractId());
            contractService.markContractAsMatched(contract, position.getPositionId());
        });
        return position;
    }

    public void matchContractProposal(Position position) {
        try {
            Set<Contract> unmatchedContracts = findUnmatchedContracts();
            log.debug("Matching position with contracts received ({})", unmatchedContracts.size());
            Contract matchedContract = retrieveMatchedContract(position, unmatchedContracts);
            updatePosition(matchedContract, position);
            contractService.markContractAsMatched(matchedContract, position.getPositionId());
        } catch (ContractNotFoundException e) {
            log.debug("No matched contracts found for position Id={}", position.getPositionId());
            recordSystemEvent(position, POSITION_UNMATCHED);
        }
    }

    private Set<Contract> findUnmatchedContracts() {
        final Set<Contract> unmatchedContracts = contractService.findAllByProcessingStatus(UNMATCHED);
        if (unmatchedContracts.isEmpty()) {
            throw new ContractNotFoundException(); // todo should we ignore such cases? A lot of system events will be created
        }
        return unmatchedContracts;
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
        positionService.savePosition(position);
    }

    public Position fetchSettlementInstruction(Position position) {
        PartyRole partyRole = IntegrationUtils.extractPartyRole(position).get();
        try {
            Optional<Settlement> settlementInstructionOpt = lenderBackOfficeService.retrieveSettlementInstruction(
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

    public ProcessingStatus reconcileWithAgreement(Position position) {
        if (position.getMatching1SourceTradeAgreementId() == null || position.getProcessingStatus() != SI_FETCHED) {
            return position.getProcessingStatus();
        }
        return agreementService.findByAgreementId(position.getMatching1SourceTradeAgreementId())
            .map(agreement -> agreementService.reconcile(agreement, position))
            .map(agreementService::saveAgreement)
            .map(agreement -> retrieveProcessingStatus(position, agreement))
            .orElse(position.getProcessingStatus());
    }

    private ProcessingStatus retrieveProcessingStatus(Position position, Agreement agreement) {
        if (agreement.getProcessingStatus().equals(RECONCILED)) {
            return TRADE_RECONCILED;
        }
        if (agreement.getProcessingStatus().equals(DISCREPANCIES)) {
            return TRADE_DISCREPANCIES;
        }
        return position.getProcessingStatus();
    }

    public Position instructLoanContractProposal(Position position) {
        if ((position.getMatching1SourceTradeAgreementId() != null
            && position.getProcessingStatus() == TRADE_RECONCILED)
            || (position.getMatching1SourceTradeAgreementId() == null
            && position.getProcessingStatus() == SI_FETCHED)) {
            List<Settlement> settlementList = settlementService.getSettlementByInstructionId(
                position.getPositionId()); // we will not fetch Settlement instruction since 0.0.5-SNAPSHOT
            ContractProposal contractProposal = buildLoanContractProposal(settlementList,
                spireMapper.buildTradeAgreement(position));
            oneSourceApiClient.createContract(null, contractProposal, position);
            log.debug("Loan contract proposal was created for position id: {}", position.getPositionId());
        }
        return position;
    }

    private ContractProposal buildLoanContractProposal(List<Settlement> settlements,
        TradeAgreement tradeAgreement) {
        return ContractProposal.builder()
            .settlementList(settlements)
            .trade(tradeAgreement)
            .build();
    }
}
