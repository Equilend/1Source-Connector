package com.intellecteu.onesource.integration.routes.contract_initiation_without_trade.processor.strategy.contract;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.CONTRACT_DECLINE_MSG;
import static com.intellecteu.onesource.integration.model.enums.FlowStatus.POSITION_UPDATED;
import static com.intellecteu.onesource.integration.model.enums.FlowStatus.PROCESSED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_SETTLEMENT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_COUNTERPARTY_SETTLEMENT_INSTRUCTION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_SETTLEMENT_INSTRUCTION_UPDATE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_VALIDATED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_CANCELED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_DECLINED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_OPENED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_PENDING;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_PROPOSED;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.DECLINED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.PROPOSAL_CANCELED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.PROPOSAL_DECLINED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.RECONCILED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.SETTLED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.TO_DECLINE;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.VALIDATED;
import static com.intellecteu.onesource.integration.model.onesource.RoundingMode.ALWAYSUP;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractLenderOrBorrower;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.exception.InstructionRetrievementException;
import com.intellecteu.onesource.integration.mapper.ContractMapper;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.enums.FlowStatus;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.onesource.Collateral;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.SettlementTempRepository;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.reconciliation.ReconcileService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.client.onesource.OneSourceApiClient;
import com.intellecteu.onesource.integration.services.client.spire.dto.AccountDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SwiftbicDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.instruction.InstructionDTO;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

@Component
@Slf4j
public class ContractDataReceived extends AbstractContractProcessStrategy {

    private final AgreementRepository agreementRepository;
    private final OneSourceApiClient oneSourceApiClient;
    private final BackOfficeService borrowerBackOfficeService;
    private final BackOfficeService lenderBackOfficeService;

    @Override
    @Transactional
    public void process(Contract contract) {
        String venueRefId = contract.getTrade().getVenue().getVenueRefKey();
        log.debug("Contract Id {} Contract Datetime {}, venueRefId: {}", contract.getContractId(),
            contract.getLastUpdateDateTime(), venueRefId);
        positionService.findByVenueRefId(venueRefId).stream()
            .findFirst()
            .map(spireMapper::toPositionDto)
            .ifPresentOrElse(
                position -> processContractWithPosition(contract, position),
                () -> savePositionRetrievementIssue(contract));
    }

    private void processContractWithPosition(Contract contract, PositionDto position) {
        extractPartyRole(position.unwrapPositionType())
            .ifPresentOrElse(
                role -> processContractByRole(role, contract, position),
                () -> persistPartyRoleIssue(contract, position.unwrapPositionType()));
    }

    private void processContractByRole(PartyRole partyRole, Contract contract, PositionDto positionDto) {
        String venueRefId = contract.getTrade().getVenue().getVenueRefKey();
        EventType eventType = contract.getEventType();
        log.debug("Processing contractId: {} with position: {} for party: {}", contract.getContractId(),
            positionDto.getPositionId(), partyRole);
        if (eventType == CONTRACT_PROPOSED) {
            processMatchingPosition(contract, positionDto);
            if (partyRole == BORROWER) {
                if (MATCHED_POSITION.equals(contract.getProcessingStatus())) {
                    reconcile(contract, positionDto);
                }
                processContractForBorrower(contract, venueRefId, partyRole);
            }
        }
        if (eventType == CONTRACT_PENDING) {
            processApprovedContract(contract, positionDto);
            processPositionAfterContractApproved(contract, positionDto, partyRole);
        }
        if (eventType == CONTRACT_CANCELED) {
            processCanceledContract(contract, positionDto);
        }
        if (eventType == CONTRACT_DECLINED) {
            processDeclinedContract(contract, positionDto);
        }
        if (eventType == CONTRACT_OPENED) {
            processSettledContract(contract);
        }
        contract.setFlowStatus(PROCESSED);
        contractService.save(contract);
    }

    private void processSettledContract(Contract contract) {
        contract.setProcessingStatus(SETTLED);
        contract.setLastUpdateDateTime(LocalDateTime.now());
        recordContractEvent(contract.getContractId(), RecordType.LOAN_CONTRACT_SETTLED,
            contract.getMatchingSpirePositionId(), CONTRACT_SETTLEMENT);
    }

    private void persistPartyRoleIssue(Contract contract, String positionType) {
        processPartyRoleIssue(positionType, contract);
        contract.setFlowStatus(PROCESSED);
        contractService.save(contract);
    }

    private void processDeclinedContract(Contract contract, PositionDto position) {
        String spirePositionId = position.getPositionId();
        log.warn(format(CONTRACT_DECLINE_MSG, contract.getContractId(), spirePositionId));
        contract.setProcessingStatus(DECLINED);
        savePositionStatus(position, PROPOSAL_DECLINED);
        recordContractEvent(contract.getContractId(), RecordType.LOAN_CONTRACT_PROPOSAL_DECLINED,
            contract.getMatchingSpirePositionId(), CONTRACT_INITIATION);
    }

    private void processCanceledContract(Contract contract, PositionDto position) {
        String spirePositionId = position.getPositionId();
        log.info("The loan contract proposal (contract identifier: {}) matching with "
                + "the SPIRE position (position identifier: {}) has been canceled",
            contract.getContractId(), spirePositionId);
        contract.setProcessingStatus(CANCELED);
        savePositionStatus(position, PROPOSAL_CANCELED);
        recordContractEvent(contract.getContractId(), RecordType.LOAN_CONTRACT_PROPOSAL_CANCELED,
            contract.getMatchingSpirePositionId(), CONTRACT_INITIATION);
    }

    private void processApprovedContract(Contract contract, PositionDto position) {
        saveContractWithStage(contract, POSITION_UPDATED);
        String spirePositionId = position.getPositionId();
        log.info("The loan contract proposal (contract identifier: {}) matching with "
                + "the SPIRE position (position identifier: {}) has been approved",
            contract.getContractId(), spirePositionId);
        contract.setProcessingStatus(ProcessingStatus.APPROVED);
        savePositionStatus(position, PROPOSAL_APPROVED);
        recordContractEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_APPROVED,
            contract.getMatchingSpirePositionId(), CONTRACT_INITIATION);
    }

    private void processPositionAfterContractApproved(Contract contract, PositionDto position, PartyRole partyRole) {
        updateSettlementInstructionForCounterParty(position, contract);
        if (BORROWER.equals(partyRole)) {
            borrowerBackOfficeService.update1SourceLoanContractIdentifier(spireMapper.toPosition(position));
        } else if (LENDER.equals(partyRole)) {
            lenderBackOfficeService.update1SourceLoanContractIdentifier(spireMapper.toPosition(position));
        } else {
            throw new NotImplementedException("Unsupported role: " + partyRole);
        }
    }

    private void updateSettlementInstructionForCounterParty(PositionDto positionDto, Contract contract) {
        var lenderOrBorrowerRole = extractLenderOrBorrower(positionDto);
        var counterPartyRole = lenderOrBorrowerRole == LENDER ? BORROWER : LENDER;
        log.debug("The current position partyRole is {}. Retrieving Settlement Instruction "
            + "for the counterparty: {}", lenderOrBorrowerRole, counterPartyRole);
        Optional<Settlement> spireCpSIToUpdate = retrieveCpSettlementInstruction(contract, positionDto,
            counterPartyRole);
        Optional<Settlement> contractCpSI = contract.getSettlement().stream()
            .filter(s -> s.getPartyRole() == counterPartyRole)
            .findAny();
        if (spireCpSIToUpdate.isPresent() && contractCpSI.isPresent()) {
            updateSettlementInstructionAndRecordOnFail(spireCpSIToUpdate.get(), contract, contractCpSI.get());
        }
    }

    private void updateSettlementInstructionAndRecordOnFail(Settlement instructionToUpdate, Contract Contract,
        Settlement contractInstruction) {
        try {
            // todo: refine and rework update flow. We need to execute PUT request with an updated instruction.
            // But when we retrieve instruction we map the InstructionDTO into SettlementDto and lose other fields.
            lenderBackOfficeService.updateSettlementInstruction(contractInstruction);
            log.debug("SPIRE Settlement Instruction id:{} was updated.", instructionToUpdate.getInstructionId());
        } catch (RestClientException e) {
            if (e instanceof HttpStatusCodeException exception) {
                final HttpStatusCode statusCode = exception.getStatusCode();
                if (Set.of(UNAUTHORIZED, FORBIDDEN, NOT_FOUND).contains(HttpStatus.valueOf(statusCode.value()))) {
                    recordExceptionEvent(Contract.getContractId(), exception,
                        POST_SETTLEMENT_INSTRUCTION_UPDATE, Contract.getMatchingSpirePositionId());
                }
            }
            log.warn("Unexpected exception during updating SI: {}", e.getMessage());
        }
    }

    private InstructionDTO createInstructionUpdateRequestBody(SettlementDto contractInstruction) {
        try {
            final AccountDTO accountDTO = new AccountDTO();
            accountDTO.setDtc(
                Long.valueOf(contractInstruction.getInstruction().getDtcParticipantNumber()));
            final SwiftbicDTO swiftBic = new SwiftbicDTO();
            swiftBic.setBic(contractInstruction.getInstruction().getSettlementBic());
            swiftBic.setBranch(contractInstruction.getInstruction().getLocalAgentBic());

            return InstructionDTO.builder()
                .agentName(contractInstruction.getInstruction().getLocalAgentName())
                .agentSafe(contractInstruction.getInstruction().getLocalAgentAcct())
                .accountDTO(accountDTO)
                .agentBicDTO(swiftBic)
                .build();
        } catch (NumberFormatException e) {
            log.warn("Parse data exception. Check the data correctness");
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
    }

    private Optional<Settlement> retrieveCpSettlementInstruction(Contract contract,
        PositionDto positionDto, PartyRole cpPartyRole) {
        try {
            return lenderBackOfficeService.retrieveSettlementInstruction(
                spireMapper.toPosition(positionDto), cpPartyRole, positionDto.getCpDto().getAccountId());
        } catch (InstructionRetrievementException e) {
            if (e.getCause() instanceof HttpStatusCodeException exception) {
                recordExceptionEvent(contract.getContractId(), exception,
                    GET_COUNTERPARTY_SETTLEMENT_INSTRUCTION, contract.getMatchingSpirePositionId());
            }
            log.warn("Unexpected exception: {}", e.getMessage());
            return Optional.empty();
        }
    }


    private void processContractForBorrower(Contract contract, String venueRefId, PartyRole partyRole) {
        log.debug("Validating contract: {} with venueRefId: {} for party role = {}",
            contract.getContractId(), venueRefId, partyRole);
//        final PositionDto matchedPosition = matchContract(contract, venueRefId); // todo is this matching still required?
        if (contract.getProcessingStatus() == DISCREPANCIES) {
            log.debug("Declining contract: {} as a {}", contract.getContractId(), partyRole);
            oneSourceApiClient.declineContract(contract);
        } else if (contract.getProcessingStatus() == RECONCILED) {
            //todo check if settlement instruction and update needed
            var settlement = oneSourceApiClient.retrieveSettlementInstruction(contract);
            oneSourceApiClient.updateContract(contract, buildInstructionRequest(settlement));
            oneSourceApiClient.approveContract(contract, settlement);
            log.debug("Contract {} was approved by {}!", contract.getContractId(), partyRole);
        }
    }

    private HttpEntity<Settlement> buildInstructionRequest(Settlement settlement) {
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        return new HttpEntity<>(settlement, headers);
    }

    private void recordContractEvent(String recordData, RecordType recordType, String relatedData,
        IntegrationProcess integrationProcess) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(integrationProcess);
        var recordRequest = eventBuilder.buildRequest(recordData, recordType, relatedData);
        cloudEventRecordService.record(recordRequest);
        log.debug("Recorded event with recordType: {}, recordData: {}, relatedData: {}",
            recordType, recordData, relatedData);
    }

    private void recordExceptionEvent(String recordData, HttpStatusCodeException exception,
        IntegrationSubProcess subProcess, String relatedData) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildExceptionRequest(recordData, exception, subProcess, relatedData);
        cloudEventRecordService.record(recordRequest);
        log.debug("Recorded exception event for subProcess: {}, response status: {}, recordData: {}, relatedData: {}",
            subProcess, exception.getStatusCode(), recordData, relatedData);
    }

    private PositionDto matchContract(Contract contract, String venueRefId) {
        PositionDto position = retrievePositionByVenue(venueRefId).orElse(null);
        ProcessingStatus agreementProcessingStatus = agreementRepository.findByVenueRefId(venueRefId).stream()
            .findFirst()
            .map(agreement -> agreement.getTrade().getProcessingStatus())
            .orElse(null);
        if (agreementProcessingStatus == null) {
            log.error("No trade agreement has been matched with the loan contract proposal {}",
                contract.getContractId());
            contract.setProcessingStatus(TO_DECLINE);
            return position;
        }
        Collateral collateral = contract.getTrade().getCollateral();
        if (position != null && !position.getCpMarkRoundTo().equals(collateral.getRoundingRule())
            && collateral.getRoundingMode() != ALWAYSUP) {
            log.error(
                "The Lender's rounding rules provided it the loan contract proposal {} "
                    + "(contract.trade.collateral.roundingRule = {} and contract.trade.collateral.roundingMode = {}) "
                    + "are not matching with rounding rules recorded in SPIRE "
                    + "(PositionOutDTO.ExposureDTO.cpMarkRoundTo = {} - the rounding being ALWAYSUP",
                contract.getContractId(), collateral.getRoundingRule(), collateral.getRoundingMode(),
                position.getCpMarkRoundTo());
            contract.setProcessingStatus(TO_DECLINE);
        } else if (agreementProcessingStatus == DISCREPANCIES) {
            log.error("The loan contract proposal {} is matching a canceled trade agreement",
                contract.getContractId());
            contract.setProcessingStatus(TO_DECLINE);
            var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
            String positionId = position == null ? "null" : position.getPositionId();
            var recordRequest = eventBuilder.buildRequest(contract.getContractId(),
                LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION, positionId);
            cloudEventRecordService.record(recordRequest);
        } else {
            contract.setProcessingStatus(VALIDATED);
            log.debug("Contract {} is validated.", contract.getContractId());
            var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
            var recordRequest = eventBuilder.buildRequest(contract.getContractId(),
                LOAN_CONTRACT_PROPOSAL_VALIDATED, contract.getMatchingSpirePositionId());
            cloudEventRecordService.record(recordRequest);
        }
        return position;
    }

    @Override
    public FlowStatus getProcessFlow() {
        return FlowStatus.TRADE_DATA_RECEIVED;
    }


    public ContractDataReceived(ContractService contractService, PositionService positionService,
        SettlementTempRepository settlementTempRepository, SettlementService settlementService,
        BackOfficeService borrowerBackOfficeService,
        BackOfficeService lenderBackOfficeService,
        CloudEventRecordService cloudEventRecordService,
        ReconcileService<Contract, PositionDto> contractReconcileService,
        EventMapper eventMapper, SpireMapper spireMapper,
        AgreementRepository agreementRepository, OneSourceApiClient oneSourceApiClient, ContractMapper contractMapper) {
        super(contractService, positionService, settlementTempRepository, settlementService,
            cloudEventRecordService, contractReconcileService, eventMapper, spireMapper, contractMapper);
        this.agreementRepository = agreementRepository;
        this.oneSourceApiClient = oneSourceApiClient;
        this.borrowerBackOfficeService = borrowerBackOfficeService;
        this.lenderBackOfficeService = lenderBackOfficeService;
    }
}
