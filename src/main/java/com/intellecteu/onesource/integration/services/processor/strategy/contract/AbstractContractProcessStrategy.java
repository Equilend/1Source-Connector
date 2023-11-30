package com.intellecteu.onesource.integration.services.processor.strategy.contract;

import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.SettlementInstructionDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.PositionMapper;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.SettlementTempRepository;
import com.intellecteu.onesource.integration.services.SpireService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.intellecteu.onesource.integration.enums.FlowStatus.PROCESSED;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.exception.DataMismatchException.LEI_MISMATCH_MSG;
import static com.intellecteu.onesource.integration.model.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SPIRE_ISSUE;
import static lombok.AccessLevel.PROTECTED;

@Slf4j
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PROTECTED)
public abstract class AbstractContractProcessStrategy implements ContractProcessFlowStrategy {

  ContractRepository contractRepository;
  PositionRepository positionRepository;
  SettlementTempRepository settlementTempRepository;
  SpireService spireService;
  CloudEventRecordService cloudEventRecordService;
  EventMapper eventMapper;
  PositionMapper positionMapper;

  PositionDto retrievePositionByVenue(String venueRefId) {
    List<Position> positions = positionRepository.findByVenueRefId(venueRefId);
    return positions.isEmpty() ? null : positionMapper.toPositionDto(positions.get(0));
  }

  void saveContractWithStage(ContractDto contract, FlowStatus status) {
    contract.setFlowStatus(status);
    contractRepository.save(eventMapper.toContractEntity(contract));
  }

  void updateInstruction(ContractDto contract, PartyRole partyRole, PositionDto position,
      String venueRefId, FlowStatus flowStatus) {
    PartyRole roleForRequest = null;
    if (partyRole == LENDER) { // todo - check whether we need to switch roles
      roleForRequest = BORROWER;
    } else if (partyRole == BORROWER) {
      roleForRequest = LENDER;
    }
    List<SettlementDto> settlementDtos = spireService.retrieveSettlementDetails(position, venueRefId,
        contract.getTrade(), roleForRequest);
    settlementTempRepository.save(eventMapper.toSettlementTempEntity(settlementDtos, contract.getContractId()));
    SettlementInstructionDto settlementInstruction = settlementDtos.get(0).getInstruction();
    saveContractWithStage(contract, flowStatus);
    log.debug("Updating Spire with the counterpart's settlement instruction!");
    spireService.updateInstruction(contract, position, venueRefId, settlementInstruction, partyRole);
  }

  void processMatchingPosition(Contract contract, List<Position> positions) {
    if (!positions.isEmpty()) {
      Position position = positions.get(0);
      if (position.getProcessingStatus() == CANCELED) {
        contract.setLastUpdateDatetime(LocalDateTime.now());
        contract.setProcessingStatus(MATCHED_CANCELED_POSITION);
        contract.setMatchingSpirePositionId(position.getPositionId());

        position.setLastUpdateDateTime(LocalDateTime.now());
        position.setMatching1SourceTradeAgreementId(contract.getContractId());
        createContractInitiationCloudEvent(contract.getContractId(), position,
            TRADE_AGREEMENT_MATCHED_CANCELED_POSITION);
      } else {
        contract.setLastUpdateDatetime(LocalDateTime.now());
        contract.setProcessingStatus(MATCHED_POSITION);
        contract.setMatchingSpirePositionId(position.getPositionId());

        position.setLastUpdateDateTime(LocalDateTime.now());
        position.setMatching1SourceTradeAgreementId(contract.getContractId());
        createContractInitiationCloudEvent(contract.getContractId(), position,
            TRADE_AGREEMENT_MATCHED_POSITION);
      }

      positionRepository.save(position);
      contractRepository.save(contract);
    }
  }

  void savePositionRetrievementIssue(ContractDto contract, String venueRefId) {
    log.warn("Could not retrieve position by venue {} for the contract {}", venueRefId, contract.getContractId());
    contract.setProcessingStatus(SPIRE_ISSUE);
    contract.setFlowStatus(PROCESSED);
    contractRepository.save(eventMapper.toContractEntity(contract));
  }

  void processPartyRoleIssue(ContractDto contract, String positionLei) {
    log.debug(String.format("The loan contract %s cannot be processed, reason: %s", contract.getContractId(),
        String.format(LEI_MISMATCH_MSG, positionLei)));
    contract.setProcessingStatus(ProcessingStatus.ONESOURCE_ISSUE);
  }

  void createContractInitiationCloudEvent(String id, Position position, RecordType recordType) {
    var eventBuilder = cloudEventRecordService.getFactory()
        .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
    var recordRequest = eventBuilder.buildRequest(id,
        recordType, position.getPositionId());
    cloudEventRecordService.record(recordRequest);
  }
}