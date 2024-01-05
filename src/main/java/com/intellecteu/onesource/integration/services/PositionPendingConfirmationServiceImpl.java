package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.constant.PositionConstant.PositionStatus.CANCEL;
import static com.intellecteu.onesource.integration.constant.PositionConstant.PositionStatus.FAILED;
import static com.intellecteu.onesource.integration.constant.PositionConstant.PositionStatus.FUTURE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.PositionStatus.OPEN;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_UPDATED_POSITIONS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.model.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SETTLED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SI_FETCHED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.UPDATED;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.formattedDateTime;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createGetPositionNQuery;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createListOfTuplesUpdatedPositions;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.SettlementStatusUpdateDto;
import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.dto.spire.AndOr;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.SettlementStatus;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionPendingConfirmationServiceImpl implements PositionPendingConfirmationService {

    private final AgreementRepository agreementRepository;
    private final ContractRepository contractRepository;
    private final SpireMapper spireMapper;
    private final EventMapper eventMapper;
    private final PositionRepository positionRepository;
    private final SpireService spireService;
    private final SettlementService settlementService;
    private final CloudEventRecordService cloudEventRecordService;
    private final OneSourceService oneSourceService;

    @Override
    public void processUpdatedPositions() {
        List<Position> positions = positionRepository.findAllNotCanceledAndSettled();
        final List<PositionDto> updatedPositions = findLastUpdatedDateTime(positions)
            .map(lastUpdated -> requestUpdatedPositions(lastUpdated, positions))
            .orElse(List.of());
        updatedPositions.forEach(this::processUpdatedPosition);
        updatedPositions.forEach(this::processSettlement);
    }

    private List<PositionDto> requestUpdatedPositions(LocalDateTime lastUpdatedDateTime, List<Position> positions) {
        String ids = positions.stream()
            .map(Position::getPositionId)
            .collect(Collectors.joining(", "));
        try {
            ResponseEntity<JsonNode> response = spireService.requestPosition(
                createGetPositionNQuery(null, AndOr.AND, null,
                    createListOfTuplesUpdatedPositions(formattedDateTime(lastUpdatedDateTime), ids)));
            return convertPositionResponse(response);
        } catch (HttpStatusCodeException e) {
            log.warn("SPIRE error response for {} subprocess. Details: {}",
                GET_UPDATED_POSITIONS_PENDING_CONFIRMATION, e.getStatusCode());
            if (Set.of(UNAUTHORIZED, FORBIDDEN).contains(e.getStatusCode())) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                final CloudEventBuildRequest recordRequest = eventBuilder.buildExceptionRequest(e,
                    IntegrationSubProcess.GET_UPDATED_POSITIONS_PENDING_CONFIRMATION);
                cloudEventRecordService.record(recordRequest);
            }
            return List.of();
        }
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

    private void processUpdatedPosition(PositionDto positionDto) {
        updatePosition(positionDto);
        PartyRole partyRole = null;
        positionDto.setVenueRefId(positionDto.getCustomValue2());
        if (extractPartyRole(positionDto.unwrapPositionType()).isPresent()) {
            partyRole = extractPartyRole(positionDto.unwrapPositionType()).get();
        }
        if (partyRole == LENDER) {
            processContractCancel(positionDto);
        } else if (partyRole == BORROWER) {
            processContractDecline(positionDto);
        }
        savePosition(positionDto, positionDto.getProcessingStatus());
    }

    private void processContractCancel(PositionDto positionDto) {
        Contract contract = null;
        //todo change List to Optional after fixing mock data
        List<Contract> contracts = contractRepository.findAllByContractId(
            positionDto.getMatching1SourceLoanContractId());
        if (!contracts.isEmpty()) {
            contract = contracts.get(0);
        }
        if (contract != null && List.of(MATCHED_CANCELED_POSITION, DISCREPANCIES)
            .contains(contract.getProcessingStatus())) {
            oneSourceService.cancelContract(contract, positionDto.getPositionId());
        }
    }

    private void processContractDecline(PositionDto positionDto) {
        Contract contract = null;
        List<Contract> contracts = contractRepository.findAllByContractId(
            positionDto.getMatching1SourceLoanContractId());
        if (!contracts.isEmpty()) {
            contract = contracts.get(0);
        }
        if (contract != null && contract.getProcessingStatus() == DISCREPANCIES) {
            oneSourceService.declineContract(eventMapper.toContractDto(contract));
        }
    }

    private void processSettlement(PositionDto positionDto) {
        if (List.of(CREATED, UPDATED).contains(positionDto.getProcessingStatus())) {
            final List<SettlementDto> settlementDtoList = settlementService.getSettlementInstructions(positionDto);
            settlementDtoList.stream()
                .findFirst()
                .map(settlementService::persistSettlement)
                .ifPresent(s -> {
                    positionDto.setApplicableInstructionId(s.getInstructionId());
                    savePosition(positionDto, SI_FETCHED);
                });
        }
    }

    private PositionDto savePosition(PositionDto positionDto, ProcessingStatus processingStatus) {
        positionDto.setProcessingStatus(processingStatus);
        positionDto.setLastUpdateDateTime(LocalDateTime.now());
        var savedPosition = positionRepository.save(spireMapper.toPosition(positionDto));
        return spireMapper.toPositionDto(savedPosition);
    }

    private void updatePosition(PositionDto positionDto) {
        if (positionDto != null && positionDto.unwrapPositionStatus() != null) {
            String status = positionDto.unwrapPositionStatus();
            if (status.equals(FUTURE)) {
                positionDto.setProcessingStatus(UPDATED);
            } else if (Set.of(CANCEL, FAILED).contains(status)) {
                positionDto.setProcessingStatus(CANCELED);
                matchingCanceledPosition(positionDto.getCustomValue2());
            } else if (status.equals(OPEN)) {
                positionDto.setProcessingStatus(SETTLED);
                List<Contract> contracts = contractRepository.findByVenueRefId(
                    positionDto.getCustomValue2());
                if (!contracts.isEmpty()) {
                    Contract contract = contracts.get(0);
                    contract.setSettlementStatus(SettlementStatus.SETTLED);
                    updateSettlementStatus(contract);

                    contractRepository.save(contract);
                }
            } else {
                positionDto.setProcessingStatus(CREATED);
            }
            positionDto.setLastUpdateDateTime(LocalDateTime.now());
        }
    }

    private void updateSettlementStatus(Contract contract) {
        ContractDto contractDto = eventMapper.toContractDto(contract);
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        oneSourceService.updateContract(contractDto,
            new HttpEntity<>(new SettlementStatusUpdateDto(SettlementStatus.SETTLED), headers));
    }

    private void matchingCanceledPosition(String venueRefId) {
        agreementRepository.findByVenueRefId(venueRefId).stream()
            .findFirst()
            .ifPresent(this::processAgreementMatchedCanceledPosition);

        contractRepository.findByVenueRefId(venueRefId).stream()
            .findFirst()
            .ifPresent(this::processContractMatchedCanceledPosition);
    }

    private void processAgreementMatchedCanceledPosition(Agreement agreement) {
        agreement.setLastUpdateDatetime(LocalDateTime.now());
        agreement.setProcessingStatus(MATCHED_CANCELED_POSITION);
        createContractInitiationCloudEvent(agreement.getAgreementId(),
            TRADE_AGREEMENT_MATCHED_CANCELED_POSITION, agreement.getMatchingSpirePositionId());
        agreementRepository.save(agreement);
    }

    private void processContractMatchedCanceledPosition(Contract contract) {
        contract.setLastUpdateDatetime(LocalDateTime.now());
        contract.setProcessingStatus(MATCHED_CANCELED_POSITION);
        createContractInitiationCloudEvent(contract.getContractId(),
            LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION, contract.getMatchingSpirePositionId());
        contractRepository.save(contract);
    }

    private void createContractInitiationCloudEvent(String recordData, RecordType recordType, String relatedData) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(recordData, recordType, relatedData);
        cloudEventRecordService.record(recordRequest);
    }

    private Optional<LocalDateTime> findLastUpdatedDateTime(List<Position> positions) {
        LocalDateTime localDateTime = null;
        if (positions != null && !positions.isEmpty()) {
            localDateTime = positions.stream()
                .map(Position::getLastUpdateDateTime)
                .max(LocalDateTime::compareTo)
                .get();
        }
        return Optional.ofNullable(localDateTime);
    }
}
