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
import com.intellecteu.onesource.integration.model.SettlementStatus;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionPendingConfirmationServiceImpl implements PositionPendingConfirmationService {

    private final AgreementRepository agreementRepository;
    private final ContractService contractService;
    private final SpireMapper spireMapper;
    private final EventMapper eventMapper;
    private final PositionService positionService;
    private final SpireService spireService;
    private final SettlementService settlementService;
    private final CloudEventRecordService cloudEventRecordService;
    private final OneSourceService oneSourceService;

    @Override
    public void processUpdatedPositions() {
        List<Position> positions = positionService.findAllNotCanceledAndSettled();
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
            if (HttpStatus.CREATED == response.getStatusCode()) {
                // temporal throw an exception to record until requirements will be retrieved how to handle 201 status
                throw new HttpClientErrorException(HttpStatus.CREATED);
            }
            return convertPositionResponse(response);
        } catch (HttpStatusCodeException e) {
            log.warn("SPIRE error response for {} subprocess. Details: {}",
                GET_UPDATED_POSITIONS_PENDING_CONFIRMATION, e.getStatusCode());
            if (Set.of(CREATED, UNAUTHORIZED, FORBIDDEN).contains(e.getStatusCode())) {
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
        positionDto.setVenueRefId(positionDto.getCustomValue2());
        updatePositionByProcessingStatus(positionDto);
        processPositionMatchedContracts(positionDto);

        positionService.savePosition(spireMapper.toPosition(positionDto));
    }

    private void processPositionMatchedContracts(PositionDto positionDto) {
        extractPartyRole(positionDto.unwrapPositionType())
            .ifPresent(role -> processContractsByRole(role, positionDto));
    }

    private void processContractsByRole(PartyRole partyRole, PositionDto positionDto) {
        if (partyRole == LENDER) {
            processContractCancel(positionDto);
        } else if (partyRole == BORROWER) {
            processContractDecline(positionDto);
        }
    }

    private void processContractCancel(PositionDto positionDto) {
        Contract contract = null;
        //todo change List to Optional after fixing mock data
        List<Contract> contracts = contractService.findAllByContractId(
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
        List<Contract> contracts = contractService.findAllByContractId(
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
                    positionDto.setProcessingStatus(SI_FETCHED);
                    positionService.savePosition(spireMapper.toPosition(positionDto));
                });
        }
    }

    private void updatePositionByProcessingStatus(PositionDto positionDto) {
        if (positionDto != null && positionDto.unwrapPositionStatus() != null) {
            String status = positionDto.unwrapPositionStatus();
            if (FUTURE.equals(status)) {
                positionDto.setProcessingStatus(UPDATED);
            } else if (Set.of(CANCEL, FAILED).contains(status)) {
                positionDto.setProcessingStatus(CANCELED);
                matchingCanceledPosition(positionDto.getCustomValue2());
            } else if (OPEN.equals(status)) {
                positionDto.setProcessingStatus(SETTLED);
                processSettledStatusForContract(positionDto);
            } else {
                positionDto.setProcessingStatus(CREATED);
            }
        }
    }

    private void processSettledStatusForContract(PositionDto positionDto) {
        contractService.findByVenueRefId(positionDto.getCustomValue2())
            .ifPresent(contract -> {
                contract.setSettlementStatus(SettlementStatus.SETTLED);
                executeSettledContractUpdate(contract);
                contractService.save(contract);
            });
    }

    private void executeSettledContractUpdate(Contract contract) {
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

        contractService.findByVenueRefId(venueRefId)
            .ifPresent(this::processContractMatchedCanceledPosition);
    }

    private void processAgreementMatchedCanceledPosition(Agreement agreement) {
        agreement.setLastUpdateDatetime(LocalDateTime.now());
        agreement.setProcessingStatus(MATCHED_CANCELED_POSITION);
        agreementRepository.save(agreement);
        createContractInitiationCloudEvent(agreement.getAgreementId(),
            TRADE_AGREEMENT_MATCHED_CANCELED_POSITION, agreement.getMatchingSpirePositionId());
    }

    private void processContractMatchedCanceledPosition(Contract contract) {
        contract.setLastUpdateDatetime(LocalDateTime.now());
        contract.setProcessingStatus(MATCHED_CANCELED_POSITION);
        contractService.save(contract);
        createContractInitiationCloudEvent(contract.getContractId(),
            LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION, contract.getMatchingSpirePositionId());
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
