package com.intellecteu.onesource.integration.routes.processor.strategy.contract;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.CONTRACT_DECLINE_MSG;
import static com.intellecteu.onesource.integration.enums.FlowStatus.POSITION_UPDATED;
import static com.intellecteu.onesource.integration.enums.FlowStatus.PROCESSED;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_SETTLEMENT;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_COUNTERPARTY_SETTLEMENT_INSTRUCTION;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.POST_SETTLEMENT_INSTRUCTION_UPDATE;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_VALIDATED;
import static com.intellecteu.onesource.integration.exception.NoRequiredPartyRoleException.NO_PARTY_ROLE_EXCEPTION;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_CANCELED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_DECLINED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_OPENED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_PENDING;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_PROPOSED;
import static com.intellecteu.onesource.integration.model.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.DECLINED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.PROPOSAL_CANCELED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.PROPOSAL_DECLINED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.RECONCILED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SETTLED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TO_DECLINE;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.VALIDATED;
import static com.intellecteu.onesource.integration.model.RoundingMode.ALWAYSUP;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.intellecteu.onesource.integration.dto.CollateralDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.exception.NoRequiredPartyRoleException;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.EventType;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.SettlementTempRepository;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.SpireService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

@Component
@Slf4j
public class ContractDataReceived extends AbstractContractProcessStrategy {

    private final AgreementRepository agreementRepository;
    private final OneSourceService oneSourceService;
    private final BackOfficeService borrowerBackOfficeService;
    private final BackOfficeService lenderBackOfficeService;

    @Override
    public void process(ContractDto contract) {
        String venueRefId = contract.getTrade().getExecutionVenue().getVenueRefKey();
        log.debug("Contract Id {} Contract Datetime {}, venueRefId: {}", contract.getContractId(),
            contract.getLastUpdateDatetime(), venueRefId);
        positionService.findByVenueRefId(venueRefId).stream()
            .findFirst()
            .map(spireMapper::toPositionDto)
            .ifPresentOrElse(
                position -> processContractWithPosition(contract, position),
                () -> savePositionRetrievementIssue(contract));
    }

    private void processContractWithPosition(ContractDto contract, PositionDto position) {
        extractPartyRole(position.unwrapPositionType())
            .ifPresentOrElse(
                role -> processContractByRole(role, contract, position),
                () -> persistPartyRoleIssue(contract, position.unwrapPositionType()));
    }

    private void processContractByRole(PartyRole partyRole, ContractDto contract, PositionDto positionDto) {
        String venueRefId = contract.getTrade().getExecutionVenue().getVenueRefKey();
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
        contractService.save(eventMapper.toContractEntity(contract));
    }

    private void processSettledContract(ContractDto contract) {
        contract.setProcessingStatus(SETTLED);
        contract.setLastUpdateDatetime(LocalDateTime.now());
        recordContractEvent(contract.getContractId(), RecordType.LOAN_CONTRACT_SETTLED,
            contract.getMatchingSpirePositionId(), CONTRACT_SETTLEMENT);
    }

    private void persistPartyRoleIssue(ContractDto contract, String positionType) {
        processPartyRoleIssue(positionType, contract);
        contract.setFlowStatus(PROCESSED);
        contractService.save(eventMapper.toContractEntity(contract));
    }

    private void processDeclinedContract(ContractDto contract, PositionDto position) {
        String spirePositionId = position.getPositionId();
        log.warn(format(CONTRACT_DECLINE_MSG, contract.getContractId(), spirePositionId));
        contract.setProcessingStatus(DECLINED);
        savePositionStatus(position, PROPOSAL_DECLINED);
        recordContractEvent(contract.getContractId(), RecordType.LOAN_CONTRACT_PROPOSAL_DECLINED,
            contract.getMatchingSpirePositionId(), CONTRACT_INITIATION);
    }

    private void processCanceledContract(ContractDto contract, PositionDto position) {
        String spirePositionId = position.getPositionId();
        log.info("The loan contract proposal (contract identifier: {}) matching with "
                + "the SPIRE position (position identifier: {}) has been canceled",
            contract.getContractId(), spirePositionId);
        contract.setProcessingStatus(CANCELED);
        savePositionStatus(position, PROPOSAL_CANCELED);
        recordContractEvent(contract.getContractId(), RecordType.LOAN_CONTRACT_PROPOSAL_CANCELED,
            contract.getMatchingSpirePositionId(), CONTRACT_INITIATION);
    }

    private void processApprovedContract(ContractDto contract, PositionDto position) {
        saveContractWithStage(contract, POSITION_UPDATED);
        /* temporary commented update Instruction logic for FLOW I: */
//        updateInstruction(contract, partyRole, position, venueRefId, CTR_INSTRUCTIONS_RETRIEVED;
        String spirePositionId = position.getPositionId();
        log.info("The loan contract proposal (contract identifier: {}) matching with "
                + "the SPIRE position (position identifier: {}) has been approved",
            contract.getContractId(), spirePositionId);
        contract.setProcessingStatus(ProcessingStatus.APPROVED);
        savePositionStatus(position, PROPOSAL_APPROVED);
        recordContractEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_APPROVED,
            contract.getMatchingSpirePositionId(), CONTRACT_INITIATION);
    }

    private void processPositionAfterContractApproved(ContractDto contract, PositionDto position, PartyRole partyRole) {
        updateSettlementInstructionForCounterParty(position, contract);
        if (BORROWER.equals(partyRole)) {
            borrowerBackOfficeService.update1SourceLoanContractIdentifier(spireMapper.toPosition(position));
        } else if (LENDER.equals(partyRole)) {
            lenderBackOfficeService.update1SourceLoanContractIdentifier(spireMapper.toPosition(position));
        } else {
            throw new NotImplementedException("Unsupported role: " + partyRole);
        }
    }

    private void updateSettlementInstructionForCounterParty(PositionDto positionDto, ContractDto contractDto) {
        var lenderOrBorrowerRole = getLenderOrBorrowerRole(positionDto);
        var counterPartyRole = lenderOrBorrowerRole == LENDER ? BORROWER : LENDER;
        log.debug("The current position partyRole is {}. Retrieving Settlement Instruction "
            + "for the counterparty: {}", lenderOrBorrowerRole, counterPartyRole);
        var spireCpSI = retrieveSettlementInstruction(contractDto, positionDto, counterPartyRole);
        var contractCpSI = contractDto.getSettlement().stream()
            .filter(s -> s.getPartyRole() == counterPartyRole)
            .findAny();
        if (spireCpSI.isPresent() && contractCpSI.isPresent()) {
            updateSettlementInstructionAndRecordOnFail(contractDto, spireCpSI.get(), contractCpSI.get(),
                counterPartyRole);
        }
    }

    private void updateSettlementInstructionAndRecordOnFail(ContractDto contractDto, SettlementDto spireCpSI,
        SettlementDto contractCpSI, PartyRole cpPartyRole) {
        try {
            settlementService.updateSpireInstruction(spireCpSI, contractCpSI, cpPartyRole);
            log.debug("SPIRE Settlement Instruction id:{} was updated.", spireCpSI.getInstructionId());
        } catch (RestClientException e) {
            if (e instanceof HttpStatusCodeException exception) {
                if (Set.of(UNAUTHORIZED, FORBIDDEN, NOT_FOUND).contains(exception.getStatusCode())) {
                    recordExceptionEvent(contractDto.getContractId(), exception,
                        POST_SETTLEMENT_INSTRUCTION_UPDATE, contractDto.getMatchingSpirePositionId());
                }
            }
            log.warn("Unexpected exception during updating SI: {}", e.getMessage());
        }
    }

    private static PartyRole getLenderOrBorrowerRole(PositionDto positionDto) {
        return extractPartyRole(positionDto.getPositionTypeDto().getPositionType())
            .filter(p -> (p == LENDER || p == BORROWER))
            .orElseThrow(() -> new NoRequiredPartyRoleException(
                format(NO_PARTY_ROLE_EXCEPTION, positionDto.getPositionId())));
    }

    private Optional<SettlementDto> retrieveSettlementInstruction(ContractDto contract,
        PositionDto positionDto, PartyRole cpPartyRole) {
        try {
            final ResponseEntity<SettlementDto> response = settlementService
                .retrieveSettlementDetails(positionDto, cpPartyRole, positionDto.getCpDto().getInfo());
            if (HttpStatus.CREATED == response.getStatusCode()) {
                // temporal throw an exception to record until requirements will be retrieved how to handle 201 status
                throw new HttpClientErrorException(HttpStatus.CREATED);
            }
            return Optional.ofNullable(response.getBody());
        } catch (RestClientException e) {
            if (e instanceof HttpStatusCodeException exception) {
                recordExceptionEvent(contract.getContractId(), exception,
                    GET_COUNTERPARTY_SETTLEMENT_INSTRUCTION, contract.getMatchingSpirePositionId());
            }
            log.warn("Unexpected exception: {}", e.getMessage());
            return Optional.empty();
        }
    }


    private void processContractForBorrower(ContractDto contract, String venueRefId, PartyRole partyRole) {
        log.debug("Validating contract: {} with venueRefId: {} for party role = {}",
            contract.getContractId(), venueRefId, partyRole);
//        final PositionDto matchedPosition = matchContract(contract, venueRefId); // todo is this matching still required?
        if (contract.getProcessingStatus() == DISCREPANCIES) {
            log.debug("Declining contract: {} as a {}", contract.getContractId(), partyRole);
            oneSourceService.declineContract(contract);
        } else if (contract.getProcessingStatus() == RECONCILED) {
            //todo check if settlement instruction and update needed
            var settlementDto = oneSourceService.retrieveSettlementInstruction(contract);
            oneSourceService.updateContract(contract, buildInstructionRequest(settlementDto));
            oneSourceService.approveContract(contract, settlementDto);
            if (contract.isProcessedWithoutErrors()) {
                log.debug("Contract {} was approved by {}!", contract.getContractId(), partyRole);
            }
        }
    }

    private HttpEntity<SettlementDto> buildInstructionRequest(SettlementDto settlementDto) {
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        return new HttpEntity<>(settlementDto, headers);
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

    private PositionDto matchContract(ContractDto contract, String venueRefId) {
        PositionDto position = retrievePositionByVenue(venueRefId);
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
        CollateralDto collateral = contract.getTrade().getCollateral();
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
        SpireService spireService, BackOfficeService borrowerBackOfficeService,
        BackOfficeService lenderBackOfficeService,
        CloudEventRecordService cloudEventRecordService,
        ReconcileService<ContractDto, PositionDto> contractReconcileService,
        EventMapper eventMapper, SpireMapper spireMapper,
        AgreementRepository agreementRepository, OneSourceService oneSourceService) {
        super(contractService, positionService, settlementTempRepository, settlementService, spireService,
            cloudEventRecordService, contractReconcileService, eventMapper, spireMapper);
        this.agreementRepository = agreementRepository;
        this.oneSourceService = oneSourceService;
        this.borrowerBackOfficeService = borrowerBackOfficeService;
        this.lenderBackOfficeService = lenderBackOfficeService;
    }
}
