package com.intellecteu.onesource.integration.services.processor.strategy.contract;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.CONTRACT_DECLINE_MSG;
import static com.intellecteu.onesource.integration.enums.FlowStatus.CTR_INSTRUCTIONS_RETRIEVED;
import static com.intellecteu.onesource.integration.enums.FlowStatus.POSITION_UPDATED;
import static com.intellecteu.onesource.integration.enums.FlowStatus.PROCESSED;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_VALIDATED;
import static com.intellecteu.onesource.integration.model.ContractStatus.APPROVED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_CANCELED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_DECLINED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_OPENED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_PENDING;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_PROPOSED;
import static com.intellecteu.onesource.integration.model.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.DECLINED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.PROPOSAL_CANCELED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.PROPOSAL_DECLINED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SETTLED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TO_CANCEL;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TO_DECLINE;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.VALIDATED;
import static com.intellecteu.onesource.integration.model.RoundingMode.ALWAYSUP;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.intellecteu.onesource.integration.dto.CollateralDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.PositionMapper;
import com.intellecteu.onesource.integration.model.EventType;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.SettlementTempRepository;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SpireService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import com.intellecteu.onesource.integration.utils.IntegrationUtils;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ContractDataReceived extends AbstractContractProcessStrategy {

    private final AgreementRepository agreementRepository;
    private final OneSourceService oneSourceService;

    @Override
    public void process(ContractDto contract) {
        String venueRefId = contract.getTrade().getExecutionVenue().getVenueRefKey();
        log.debug("Contract Id {} Contract Datetime {}, venueRefId: {}", contract.getContractId(),
            contract.getLastUpdateDatetime(), venueRefId);
        positionRepository.findByVenueRefId(venueRefId).stream()
            .findFirst()
            .map(positionMapper::toPositionDto)
            .ifPresentOrElse(
                position -> extractPartyRole(contract, position),
                () -> savePositionRetrievementIssue(contract));
    }

    private void extractPartyRole(ContractDto contract, PositionDto position) {
        IntegrationUtils.extractPartyRole(position.unwrapPositionType())
            .ifPresentOrElse(
                role -> processContractByRole(role, contract, position),
                () -> persistPartyRoleIssue(contract, position.unwrapPositionType()));
    }

    private void processContractByRole(PartyRole partyRole, ContractDto contract, PositionDto positionDto) {
        String venueRefId = contract.getTrade().getExecutionVenue().getVenueRefKey();
        EventType eventType = contract.getEventType();
        log.debug("Processing contractId: {} with position: {} for party: {}", contract.getContractId(),
            positionDto.getPositionId(), partyRole);
        if (contract.getEventType() == CONTRACT_PROPOSED) {
            processMatchingPosition(contract, positionDto);
            if (partyRole == BORROWER) {
                processContractForBorrower(contract, venueRefId, partyRole);
            }
        }
        if ((eventType == CONTRACT_PENDING)
            && contract.getContractStatus() == APPROVED) {
            //TODO remove wrong logic to overwrite statuses
            contract.setProcessingStatus(ProcessingStatus.APPROVED);
            savePositionStatus(positionDto, PROPOSAL_APPROVED);
            processApprovedContract(contract, venueRefId, positionDto, partyRole);
        }
        if (partyRole == BORROWER && MATCHED_POSITION.equals(contract.getProcessingStatus())) {
            reconcile(contract, positionDto);
        }
        if (eventType == CONTRACT_CANCELED) {
            contract.setProcessingStatus(CANCELED);
            savePositionStatus(positionDto, PROPOSAL_CANCELED);
            if (partyRole == BORROWER) {
                processCanceledContractForBorrower(contract, positionDto);
            }
        }
        if (eventType == CONTRACT_DECLINED) {
            contract.setProcessingStatus(DECLINED);
            savePositionStatus(positionDto, PROPOSAL_DECLINED);
            if (partyRole == LENDER) {
                processDeclinedContractForLender(contract, positionDto);
            }
        }
        if (eventType == CONTRACT_OPENED) {
            contract.setProcessingStatus(SETTLED);
            contract.setLastUpdateDatetime(LocalDateTime.now());
        }
        contract.setFlowStatus(PROCESSED);
        contractRepository.save(eventMapper.toContractEntity(contract));
    }

    private void persistPartyRoleIssue(ContractDto contract, String positionType) {
        processPartyRoleIssue(positionType, contract);
        contract.setFlowStatus(PROCESSED);
        contractRepository.save(eventMapper.toContractEntity(contract));
    }

    private void processDeclinedContractForLender(ContractDto contract, PositionDto position) {
        String spirePositionId = position.getPositionId();
        log.warn(format(CONTRACT_DECLINE_MSG, contract.getContractId(), spirePositionId));
        contract.setProcessingStatus(DECLINED);
        recordContractEvent(contract.getContractId(), RecordType.LOAN_CONTRACT_PROPOSAL_DECLINED,
            contract.getMatchingSpirePositionId());
    }

    private void processCanceledContractForBorrower(ContractDto contract, PositionDto position) {
        String spirePositionId = position.getPositionId();
        log.error("The loan contract proposal (contract identifier: {}) matching with "
                + "the SPIRE position (position identifier: {}) has been canceled by the Lender",
            contract.getContractId(), spirePositionId);
        contract.setProcessingStatus(CANCELED);
        recordContractEvent(contract.getContractId(), RecordType.LOAN_CONTRACT_PROPOSAL_CANCELED,
            contract.getMatchingSpirePositionId());
    }

    private void processApprovedContract(ContractDto contract, String venueRefId, PositionDto position,
        PartyRole partyRole) {
        log.debug("Retrieving Position by venueRefId: {}", venueRefId);
        spireService.updatePosition(contract, position.getPositionId());
        saveContractWithStage(contract, POSITION_UPDATED);
        updateInstruction(contract, partyRole, position, venueRefId, CTR_INSTRUCTIONS_RETRIEVED);
        recordContractEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_APPROVED,
            contract.getMatchingSpirePositionId());
    }

    private void processContractForBorrower(ContractDto contract, String venueRefId, PartyRole partyRole) {
        log.debug("Validating contract: {} with venueRefId: {} for party role = {}",
            contract.getContractId(), venueRefId, partyRole);
//        final PositionDto matchedPosition = matchContract(contract, venueRefId); // todo is this matching still required?
        if (contract.getProcessingStatus() == TO_DECLINE) {
            log.debug("Declining contract: {} as a {}", contract.getContractId(), partyRole);
            oneSourceService.declineContract(contract);
        } else if (contract.getProcessingStatus() == VALIDATED) {
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

    private void recordContractEvent(String recordData, RecordType recordType, String relatedData) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(recordData, recordType, relatedData);
        cloudEventRecordService.record(recordRequest);
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
        } else if (agreementProcessingStatus == TO_CANCEL) {
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

    public ContractDataReceived(ContractRepository contractRepository, PositionRepository positionRepository,
        SettlementTempRepository settlementTempRepository, SpireService spireService,
        CloudEventRecordService cloudEventRecordService,
        ReconcileService<ContractDto, PositionDto> contractReconcileService,
        EventMapper eventMapper, PositionMapper positionMapper,
        AgreementRepository agreementRepository, OneSourceService oneSourceService) {
        super(contractRepository, positionRepository, settlementTempRepository, spireService,
            cloudEventRecordService, contractReconcileService, eventMapper, positionMapper);
        this.agreementRepository = agreementRepository;
        this.oneSourceService = oneSourceService;
    }
}
