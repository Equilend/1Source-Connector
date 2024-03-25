package com.intellecteu.onesource.integration.routes.common.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_SETTLEMENT;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.CANCELLED;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.FAILED;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.FUTURE;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.OPEN;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UPDATED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_SETTLED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TRADE_AGREEMENT_MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.SETTLED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.SI_FETCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UPDATED;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.dto.SettlementStatusUpdateDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.exception.InstructionRetrievementException;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.onesource.SettlementStatus;
import com.intellecteu.onesource.integration.services.AgreementService;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.client.onesource.OneSourceApiClient;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.utils.IntegrationUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionPendingConfirmationProcessor {

    private final AgreementService agreementService;
    private final ContractService contractService;
    private final SpireMapper spireMapper;
    private final PositionService positionService;
    private final SettlementService settlementService;
    private final CloudEventRecordService cloudEventRecordService;
    private final OneSourceApiClient oneSourceApiClient;
    private final BackOfficeService lenderBackOfficeService;
    private final BackOfficeService borrowerBackOfficeService;

    public void processUpdatedPositions() {
        List<Position> positions = positionService.findAllNotCanceledAndSettled();
        final List<PositionDto> updatedPositions = findLastUpdatedDateTime(positions)
            .map(lastUpdated -> requestUpdatedPositions(lastUpdated, positions))
            .orElse(List.of());
        updatedPositions.forEach(this::processUpdatedPosition);
        updatedPositions.forEach(positionDto -> processSettlement(spireMapper.toPosition(positionDto)));
    }

    private List<PositionDto> requestUpdatedPositions(LocalDateTime lastUpdatedDateTime, List<Position> positions) {
        List<PositionDto> positionDtoList = new ArrayList<>();
        List<Position> newPositionsForLender = lenderBackOfficeService.getNewSpirePositionsObsolete(lastUpdatedDateTime,
            positions);
        List<Position> newPositionsForBorrower = borrowerBackOfficeService.getNewSpirePositionsObsolete(
            lastUpdatedDateTime,
            positions);
        positionDtoList.addAll(newPositionsForLender.stream().map(spireMapper::toPositionDto).toList());
        positionDtoList.addAll(newPositionsForBorrower.stream().map(spireMapper::toPositionDto).toList());
        return positionDtoList;
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
            oneSourceApiClient.cancelContract(contract, positionDto.getPositionId());
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
            oneSourceApiClient.declineContract(contract);
        }
    }

    private void processSettlement(Position position) {
        if (Set.of(CREATED, UPDATED).contains(position.getProcessingStatus())) {
            try {
                PartyRole partyRole = IntegrationUtils.extractPartyRole(position).orElse(null);
                Optional<Settlement> settlementOptional = lenderBackOfficeService.retrieveSettlementInstruction(
                    position, partyRole,
                    position.getPositionAccount().getAccountId());
                settlementOptional.ifPresent(s -> {
//                    position.setProcessingStatus(SI_FETCHED);
                    positionService.savePosition(position);
                    settlementService.persistSettlement(s);
                });
            } catch (InstructionRetrievementException e) {
                if (e.getCause() instanceof HttpStatusCodeException exception) {
                    final HttpStatusCode statusCode = exception.getStatusCode();
                    log.warn("SPIRE error response for request Instruction: " + statusCode);
                    if (Set.of(UNAUTHORIZED, FORBIDDEN, NOT_FOUND).contains(HttpStatus.valueOf(statusCode.value()))) {
                        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                        var recordRequest = eventBuilder.buildExceptionRequest(
                            position.getMatching1SourceTradeAgreementId(), exception,
                            IntegrationSubProcess.GET_SETTLEMENT_INSTRUCTIONS,
                            String.valueOf(position.getPositionId()));
                        cloudEventRecordService.record(recordRequest);
                    }
                }
            }
        }
    }

    private void updatePositionByProcessingStatus(PositionDto positionDto) {
        if (positionDto != null && positionDto.unwrapPositionStatus() != null) {
            String status = positionDto.unwrapPositionStatus();
            if (FUTURE.getValue().equals(status)) {
                positionDto.setProcessingStatus(UPDATED);
            } else if (Set.of(CANCELLED.getValue(), FAILED.getValue()).contains(status)) {
                positionDto.setProcessingStatus(CANCELED);
                matchingCanceledPosition(positionDto.getCustomValue2());
            } else if (OPEN.getValue().equals(status)) {
//                positionDto.setProcessingStatus(SETTLED);
                processSettledStatusForContract(positionDto);
            } else if (positionDto.getProcessingStatus() == null) {
                positionDto.setProcessingStatus(CREATED);
            }
        }
    }

    private void processSettledStatusForContract(PositionDto positionDto) {
        contractService.findByVenueRefId(positionDto.getCustomValue2())
            .ifPresent(contract -> {
//                contract.setSettlementStatus(SettlementStatus.SETTLED);
                executeSettledContractUpdate(contract);
                contractService.save(contract);
                recordCloudEvent(contract.getContractId(), LOAN_CONTRACT_SETTLED,
                    String.valueOf(contract.getMatchingSpirePositionId()), CONTRACT_SETTLEMENT);
            });
    }

    private void executeSettledContractUpdate(Contract contract) {
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        oneSourceApiClient.updateContract(contract,
            new HttpEntity<>(new SettlementStatusUpdateDto(SettlementStatus.SETTLED), headers));
    }

    private void matchingCanceledPosition(String venueRefId) {
        agreementService.findByVenueRefId(venueRefId).stream()
            .findFirst()
            .ifPresent(this::processAgreementMatchedCanceledPosition);

        contractService.findByVenueRefId(venueRefId)
            .ifPresent(this::processContractMatchedCanceledPosition);
    }

    private void processAgreementMatchedCanceledPosition(Agreement agreement) {
        agreement.setLastUpdateDateTime(LocalDateTime.now());
        agreement.setProcessingStatus(MATCHED_CANCELED_POSITION);
        agreementService.saveAgreement(agreement);
        recordCloudEvent(agreement.getAgreementId(), TRADE_AGREEMENT_MATCHED_CANCELED_POSITION,
            agreement.getMatchingSpirePositionId(), CONTRACT_INITIATION);
    }

    private void processContractMatchedCanceledPosition(Contract contract) {
        contract.setLastUpdateDateTime(LocalDateTime.now());
        contract.setProcessingStatus(MATCHED_CANCELED_POSITION);
        contractService.save(contract);
        recordCloudEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION,
            String.valueOf(contract.getMatchingSpirePositionId()), CONTRACT_INITIATION);
    }

    private void recordCloudEvent(String recordData, RecordType recordType, String relatedData,
        IntegrationProcess integrationProcess) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(integrationProcess);
        var recordRequest = eventBuilder.buildRequest(recordData, recordType, relatedData);
        cloudEventRecordService.record(recordRequest);
        log.debug("Recorded event with recordType: {}, recordData: {}, relatedData: {}",
            recordType, recordData, relatedData);
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
