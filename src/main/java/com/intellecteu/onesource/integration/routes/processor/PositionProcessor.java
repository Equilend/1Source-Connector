package com.intellecteu.onesource.integration.routes.processor;

import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_DISCREPANCIES;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_RECONCILED;
import static com.intellecteu.onesource.integration.model.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.RECONCILED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SI_FETCHED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TRADE_DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TRADE_RECONCILED;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractLenderOrBorrower;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.ContractProposalDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import com.intellecteu.onesource.integration.utils.PositionUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionProcessor {

    private static final String STARTING_POSITION_ID = "0";

    private final PositionService positionService;
    private final AgreementRepository agreementRepository;
    private final ContractRepository contractRepository;
    private final SpireMapper spireMapper;
    private final EventMapper eventMapper;
    private final PositionRepository positionRepository;
    private final OneSourceService oneSourceService;
    private final SettlementService settlementService;
    private final CloudEventRecordService cloudEventRecordService;
    private final ReconcileService<AgreementDto, PositionDto> reconcileService;

    public void fetchNewPositions() {
        List<Position> newSpirePositions = positionService.getNewSpirePositions();
        newSpirePositions.stream()
            .filter(this::isStatusShouldBeUpdated)
            .forEach(p -> PositionUtils.updatePositionStatus(p, CREATED));
        positionService.savePositions(newSpirePositions);
        if (newSpirePositions.isEmpty()) {
            log.debug("No new positions were found.");
        } else {
            log.debug("Saved {} new positions!", newSpirePositions.size());
        }
    }

    public void startContractInitiation() {
        log.info(">>>>> Starting SPIRE contract initiation process!");
        List<PositionDto> positionDetails = getNewPositions();
        positionDetails.forEach(this::processPositionInformation);
        positionDetails.forEach(this::proceedWithSettlementInstruction);
        log.info("<<<<< SPIRE contract initiation process is finished!");
    }

    private List<PositionDto> getNewPositions() {
        List<Position> positions = positionRepository.findAllByProcessingStatus(ProcessingStatus.CREATED);
        return positions.stream().map(spireMapper::toPositionDto).collect(Collectors.toList());
    }

    private void proceedWithSettlementInstruction(PositionDto positionDto) {
        List<SettlementDto> settlementDtoList = settlementService.getSettlementInstruction(positionDto);
        settlementDtoList.stream()
            .findFirst()
            .map(s -> recordInstructionAndPosition(s, positionDto));

        PartyRole partyRole = extractLenderOrBorrower(positionDto);

        if (partyRole == BORROWER) {
            if (positionDto.getProcessingStatus() == SI_FETCHED
                && positionDto.getMatching1SourceTradeAgreementId() != null) {
                reconcileMatchingTradeAgreement(positionDto);
            }
        }
        if (partyRole == LENDER) {
            reconcileMatchingTradeAgreement(positionDto);
            instructLoanContractProposal(positionDto, settlementDtoList);
        }
    }

    private void reconcileMatchingTradeAgreement(PositionDto positionDto) {
        final List<Agreement> agreementList = agreementRepository.findByAgreementId(
            positionDto.getMatching1SourceTradeAgreementId());
        agreementList.stream()
            .findFirst()
            .ifPresent(a -> reconcile(eventMapper.toAgreementDto(a), positionDto));
    }

    private void reconcile(AgreementDto agreementDto, PositionDto positionDto) {
        try {
            log.debug("Starting reconciliation for agreement {} and position {}",
                agreementDto.getAgreementId(), positionDto.getPositionId());
            reconcileService.reconcile(agreementDto, positionDto);
            log.debug("Agreement {} is reconciled with position {}", agreementDto.getAgreementId(),
                positionDto.getPositionId());
            saveMatchedPositionForAgreement(agreementDto, RECONCILED);
            savePosition(positionDto, TRADE_RECONCILED);
            recordSuccessReconciliationCloudEvent(agreementDto);
        } catch (ReconcileException e) {
            log.error("Reconciliation fails with message: {} ", e.getMessage());
            saveMatchedPositionForAgreement(agreementDto, DISCREPANCIES);
            savePosition(positionDto, TRADE_DISCREPANCIES);
            recordFailReconciliationCloudEvent(agreementDto, e);
        }
    }

    private void recordFailReconciliationCloudEvent(AgreementDto agreementDto, ReconcileException exception) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(agreementDto.getAgreementId(), TRADE_AGREEMENT_DISCREPANCIES,
            agreementDto.getMatchingSpirePositionId(), exception.getErrorList());
        cloudEventRecordService.record(recordRequest);
    }

    private void recordSuccessReconciliationCloudEvent(AgreementDto agreementDto) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(agreementDto.getAgreementId(),
            TRADE_AGREEMENT_RECONCILED, agreementDto.getMatchingSpirePositionId());
        cloudEventRecordService.record(recordRequest);
    }

    private SettlementDto recordInstructionAndPosition(SettlementDto settlementDto, PositionDto positionDto) {
        final SettlementDto recordedSettlementDto = settlementService.persistSettlement(settlementDto);
        positionDto.setApplicableInstructionId(recordedSettlementDto.getInstructionId());
        savePosition(positionDto, SI_FETCHED);
        log.debug("Settlement instruction id {} was saved as {} for position id {}",
            settlementDto.getInstructionId(), SI_FETCHED, positionDto.getPositionId());
        return recordedSettlementDto;
    }

    public void processPositionInformation(PositionDto positionDto) {
        positionDto.setVenueRefId(positionDto.getCustomValue2());
        PartyRole partyRole = extractLenderOrBorrower(positionDto);
        lookupTradeAgreement(positionDto);
        if (partyRole == BORROWER) {
            lookupContractProposal(positionDto);
        }
        positionRepository.save(spireMapper.toPosition(positionDto));
    }

    private void lookupTradeAgreement(PositionDto positionDto) {
        final List<Agreement> res = agreementRepository.findByVenueRefId(positionDto.getCustomValue2());
        res.stream()//todo use service instead
            .findFirst()
            .ifPresent(a -> saveMatchedPositionForAgreement(positionDto, a));
    }

    private void lookupContractProposal(PositionDto positionDto) {
        contractRepository.findByVenueRefId(positionDto.getCustomValue2())//todo use service instead
            .stream()
            .findFirst()
            .map(c -> saveMatchedPositionForContractProposal(positionDto, c));
    }

    public void instructLoanContractProposal(PositionDto positionDto, List<SettlementDto> settlementDtos) {
        if ((positionDto.getMatching1SourceTradeAgreementId() != null
            && positionDto.getProcessingStatus() == TRADE_RECONCILED)
            || (positionDto.getMatching1SourceTradeAgreementId() == null
            && positionDto.getProcessingStatus() == SI_FETCHED)) {
            ContractProposalDto contractProposalDto = buildLoanContractProposal(settlementDtos,
                buildTradeAgreementDto(positionDto));
            oneSourceService.createContract(null, contractProposalDto, positionDto);
        }
    }

    private ContractProposalDto buildLoanContractProposal(List<SettlementDto> settlementDtos,
        TradeAgreementDto tradeAgreementDto) {
        return ContractProposalDto.builder()
            .settlement(settlementDtos)
            .trade(tradeAgreementDto)
            .build();
    }

    private List<PositionDto> convertPositionResponse(ResponseEntity<JsonNode> response) {
        List<PositionDto> convertedPositions = new ArrayList<>();
        if (response.getBody() != null
            && response.getBody().get("data") != null
            && response.getBody().get("data").get("beans") != null
            && response.getBody().get("data").get("beans").get(0) != null) {
            var totalRows = response.getBody().at("/data/totalRows").asText();
            if (!"0".equals(totalRows)) {
                JsonNode jsonNode = response.getBody().get("data").get("beans");
                if (jsonNode.isArray()) {
                    for (JsonNode positionNode : jsonNode) {
                        try {
                            convertedPositions.add(spireMapper.jsonToPositionDto(positionNode));
                        } catch (JsonProcessingException e) {
                            log.warn("Cannot converted positionNode {}", positionNode.asText());
                        }
                    }
                }
            }
        }
        return convertedPositions;
    }

    private TradeAgreementDto buildTradeAgreementDto(PositionDto positionDto) {
        return spireMapper.buildTradeAgreementDto(positionDto);
    }

    private ContractDto saveMatchedPositionForContractProposal(PositionDto positionDto, Contract contract) {
        positionDto.setMatching1SourceLoanContractId(contract.getContractId());
        contract.setMatchingSpirePositionId(positionDto.getPositionId());
        contract.setLastUpdateDatetime(LocalDateTime.now());
        contract.setProcessingStatus(MATCHED_POSITION);
        createContractInitiationCloudEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION,
            contract.getMatchingSpirePositionId());

        log.debug("Contract proposal: {} with matched position: {} was saved!",
            contract.getContractId(), positionDto.getPositionId());
        return eventMapper.toContractDto(contractRepository.save(contract)); // todo use service instead
    }

    private void saveMatchedPositionForAgreement(PositionDto positionDto, Agreement agreement) {
        positionDto.setMatching1SourceTradeAgreementId(agreement.getAgreementId());
        agreement.setMatchingSpirePositionId(positionDto.getPositionId());
        agreement.setLastUpdateDatetime(LocalDateTime.now());
        agreement.setProcessingStatus(MATCHED_POSITION);
        createContractInitiationCloudEvent(agreement.getAgreementId(),
            TRADE_AGREEMENT_MATCHED_POSITION, agreement.getMatchingSpirePositionId());
        agreementRepository.save(agreement); // todo use service instead
        log.debug("Agreement: {} with matched position: {} was saved!", agreement.getAgreementId(),
            positionDto.getPositionId());
    }

    private void savePosition(PositionDto positionDto, ProcessingStatus processingStatus) {
        positionDto.setProcessingStatus(processingStatus);
        positionDto.setLastUpdateDateTime(LocalDateTime.now());
        positionRepository.save(spireMapper.toPosition(positionDto));
        log.debug("Position: {} with the processing status: {} was saved!", positionDto.getPositionId(),
            processingStatus);
    }

    private void saveMatchedPositionForAgreement(AgreementDto agreementDto, ProcessingStatus processingStatus) {
        agreementDto.setProcessingStatus(processingStatus);
        agreementDto.setLastUpdateDatetime(LocalDateTime.now());
        final Agreement agreementEntity = eventMapper.toAgreementEntity(agreementDto);
        agreementRepository.save(agreementEntity);
        log.debug("Agreement {} changed processing status to {}", agreementDto.getAgreementId(), processingStatus);
    }

    private void createContractInitiationCloudEvent(String recordData, RecordType recordType, String relatedData) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(recordData, recordType, relatedData);
        cloudEventRecordService.record(recordRequest);
    }

    private boolean isStatusShouldBeUpdated(Position position) {
        return position.getProcessingStatus() == null;
    }
}
