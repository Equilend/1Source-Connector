package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_NEW_POSITIONS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_DISCREPANCIES;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_RECONCILED;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.RECONCILED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SI_FETCHED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TRADE_DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TRADE_RECONCILED;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createGetPositionNQuery;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createListOfTuplesGetPositionWithoutTA;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.ContractProposalDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.dto.spire.AndOr;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.PositionMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpireContractInitiationService implements ContractInitiationService {

    private static final String STARTING_POSITION_ID = "0";

    private final AgreementRepository agreementRepository;
    private final ContractRepository contractRepository;
    private final PositionMapper positionMapper;
    private final EventMapper eventMapper;
    private final PositionRepository positionRepository;
    private final SpireService spireService;
    private final OneSourceService oneSourceService;
    private final SettlementService settlementService;
    private final CloudEventRecordService cloudEventRecordService;
    private final ReconcileService<AgreementDto, PositionDto> reconcileService;

    @Override
    public void startContractInitiation() {
        log.debug(">>>>> Starting SPIRE contract initiation process!");
        List<PositionDto> positionDetails = getPositionDetails();
        positionDetails.forEach(this::processPositionInformation);
        log.debug("<<<<< SPIRE contract initiation process is finished!");
        positionDetails.forEach(this::proceedWithSettlementInstruction);
    }

    private void proceedWithSettlementInstruction(PositionDto positionDto) {
        List<SettlementDto> settlementDtoList = settlementService.getSettlementInstruction(positionDto);
        settlementDtoList.stream()
            .findFirst()
            .map(s -> recordInstructionAndPosition(s, positionDto));

        extractPartyRole(positionDto.unwrapPositionType())
            .filter(role -> role == LENDER)
            .ifPresent(role -> instructLoanContractProposal(positionDto, settlementDtoList));

        if (positionDto.getProcessingStatus() == SI_FETCHED
            && positionDto.getMatching1SourceTradeAgreementId() != null) {
            reconcileMatchingTradeAgreement(positionDto);
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
            saveAgreement(agreementDto, RECONCILED);
            savePosition(positionDto, TRADE_RECONCILED);
            recordSuccessReconciliationCloudEvent(agreementDto);
        } catch (ReconcileException e) {
            log.error("Reconciliation fails with message: {} ", e.getMessage());
            saveAgreement(agreementDto, DISCREPANCIES);
            savePosition(positionDto, TRADE_DISCREPANCIES);
            recordFailReconciliationCloudEvent(agreementDto, e);
        }
    }

    private void recordFailReconciliationCloudEvent(AgreementDto agreementDto, ReconcileException exception) {
        exception.getErrorList().forEach(msg -> log.debug(msg.getExceptionMessage()));
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
        final SettlementDto recordedSettlementDto = proceedWithSettlementInstruction(settlementDto);
        positionDto.setApplicableInstructionId(recordedSettlementDto.getInstructionId());
        savePosition(positionDto, SI_FETCHED);
        log.debug("Settlement instruction id {} was saved as {} for position id {}",
            settlementDto.getInstructionId(), SI_FETCHED, positionDto.getPositionId());
        return recordedSettlementDto;
    }

    @Override
    public List<PositionDto> getPositionDetails() {
        List<Position> storedPositions = positionRepository.findAll();
        log.debug("Found {} positions. Creating loan contracts.", storedPositions.size());
        return storedPositions.stream()
            .max(Comparator.comparingInt(p -> Integer.parseInt(p.getPositionId())))
            .map(p -> requestPositionDetails(String.valueOf(p.getPositionId())))
            .orElseGet(() -> requestPositionDetails(STARTING_POSITION_ID));
    }

    @Override
    public void processPositionInformation(PositionDto positionDto) {
        matchPositionWithCapturedAgreement(positionDto);
        positionDto.setVenueRefId(positionDto.getCustomValue2());
        savePosition(positionDto, ProcessingStatus.CREATED);
    }

    private void matchPositionWithCapturedAgreement(PositionDto positionDto) {
        agreementRepository.findByVenueRefId(positionDto.getCustomValue2()).stream()
            .findFirst()
            .ifPresent(a -> saveAgreement(positionDto, a));

        contractRepository.findByVenueRefId(positionDto.getCustomValue2())
            .stream()
            .findFirst()
            .map(c -> saveContract(positionDto, c));
    }

    private List<PositionDto> requestPositionDetails(String positionId) {
        log.debug("Sending POST request for position id: {}", positionId);
        return requestSpire(positionId);
    }

    private List<PositionDto> requestSpire(String positionId) {
        try {
            ResponseEntity<JsonNode> response = spireService.requestPosition(
                createGetPositionNQuery(null, AndOr.AND, null,
                    createListOfTuplesGetPositionWithoutTA(positionId)));
            return convertPositionResponse(response);
        } catch (HttpStatusCodeException e) {
            log.warn("SPIRE error response for {} subprocess. Details: {}",
                GET_NEW_POSITIONS_PENDING_CONFIRMATION, e.getStatusCode());
            if (Set.of(UNAUTHORIZED, FORBIDDEN).contains(e.getStatusCode())) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                final CloudEventBuildRequest recordRequest = eventBuilder.buildExceptionRequest(e,
                    IntegrationSubProcess.GET_NEW_POSITIONS_PENDING_CONFIRMATION);
                cloudEventRecordService.record(recordRequest);
            }
            return List.of();
        }
    }

    private SettlementDto proceedWithSettlementInstruction(SettlementDto settlementDto) {
        return settlementService.persistSettlement(settlementDto);
    }

    public void instructLoanContractProposal(PositionDto positionDto, List<SettlementDto> settlementDtos) {
        if ((positionDto.getMatching1SourceTradeAgreementId() != null
            && positionDto.getProcessingStatus() == TRADE_RECONCILED)
            || (positionDto.getMatching1SourceTradeAgreementId() == null
            && positionDto.getProcessingStatus() == SI_FETCHED)) {
            reconcileMatchingTradeAgreement(positionDto);
            ContractProposalDto contractProposalDto = buildLoanContractProposal(settlementDtos,
                buildTradeAgreementDto(positionDto));
            oneSourceService.createContract(null, contractProposalDto, positionDto);
            log.debug("Loan contract proposal was created for position id: {}", positionDto.getPositionId());
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
            JsonNode jsonNode = response.getBody().get("data").get("beans");
            if (jsonNode.isArray()) {
                for (JsonNode positionNode : jsonNode) {
                    try {
                        convertedPositions.add(positionMapper.jsonToPositionDto(positionNode));
                    } catch (JsonProcessingException e) {
                        log.warn("Cannot converted positionNode {}", positionNode.asText());
                    }
                }
            }
        }
        return convertedPositions;
    }

    private TradeAgreementDto buildTradeAgreementDto(PositionDto positionDto) {
        return positionMapper.buildTradeAgreementDto(positionDto);
    }

    private ContractDto saveContract(PositionDto positionDto, Contract contract) {
        positionDto.setMatching1SourceLoanContractId(contract.getContractId());
        contract.setMatchingSpirePositionId(positionDto.getPositionId());
        contract.setLastUpdateDatetime(LocalDateTime.now());
        contract.setProcessingStatus(MATCHED_POSITION);
        createContractInitiationCloudEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION,
            contract.getMatchingSpirePositionId());

        return eventMapper.toContractDto(contractRepository.save(contract));
    }

    private void saveAgreement(PositionDto positionDto, Agreement agreement) {
        positionDto.setMatching1SourceTradeAgreementId(agreement.getAgreementId());
        agreement.setMatchingSpirePositionId(positionDto.getPositionId());
        agreement.setLastUpdateDatetime(LocalDateTime.now());
        agreement.setProcessingStatus(MATCHED_POSITION);
        createContractInitiationCloudEvent(agreement.getAgreementId(),
            TRADE_AGREEMENT_MATCHED_POSITION, agreement.getMatchingSpirePositionId());
        agreementRepository.save(agreement);
    }

    private void savePosition(PositionDto positionDto, ProcessingStatus processingStatus) {
        positionDto.setProcessingStatus(processingStatus);
        positionDto.setLastUpdateDateTime(LocalDateTime.now());
        positionRepository.save(positionMapper.toPosition(positionDto));
        log.debug("Position {} changed processing status to {}", positionDto.getPositionId(), processingStatus);
    }

    private void saveAgreement(AgreementDto agreementDto, ProcessingStatus processingStatus) {
        agreementDto.setProcessingStatus(processingStatus);
        agreementDto.setLastUpdateDatetime(LocalDateTime.now());
        agreementRepository.save(eventMapper.toAgreementEntity(agreementDto));
        log.debug("Agreement {} changed processing status to {}", agreementDto.getAgreementId(), processingStatus);
    }

    private void createContractInitiationCloudEvent(String recordData, RecordType recordType, String relatedData) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(recordData, recordType, relatedData);
        cloudEventRecordService.record(recordRequest);
    }
}
