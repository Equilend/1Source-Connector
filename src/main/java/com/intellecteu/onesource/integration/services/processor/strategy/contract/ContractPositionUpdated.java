package com.intellecteu.onesource.integration.services.processor.strategy.contract;

import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.dto.record.CloudEventData;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.PositionMapper;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.SettlementTempRepository;
import com.intellecteu.onesource.integration.services.SpireService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.intellecteu.onesource.integration.enums.FlowStatus.AGR_INSTRUCTIONS_RETRIEVED;
import static com.intellecteu.onesource.integration.enums.FlowStatus.PROCESSED;
import static com.intellecteu.onesource.integration.model.ContractStatus.APPROVED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_APPROVE;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;

@Component
@Slf4j
public class ContractPositionUpdated extends AbstractContractProcessStrategy {

  @Override
  public void process(ContractDto contract) {
      String venueRefId = contract.getTrade().getExecutionVenue().getPlatform().getVenueRefId();
      if (contract.getEventType().equals(CONTRACT_APPROVE) && contract.getContractStatus() == APPROVED) {
        log.debug("Retrieving Position by venueRefId: {}", venueRefId);
        PositionDto position = retrievePositionByVenue(venueRefId);
        if (position == null) {
          savePositionRetrievementIssue(contract, venueRefId);
          return;
        }
        var positionLei = position.getAccountLei();
        var partyRole = extractPartyRole(contract.getTrade().getTransactingParties(), positionLei);
        if (partyRole == null) {
          processPartyRoleIssue(contract, positionLei);
        } else {
          updateInstruction(contract, partyRole, position, venueRefId, AGR_INSTRUCTIONS_RETRIEVED);
        }
      }
      contract.setFlowStatus(PROCESSED);
      contractRepository.save(eventMapper.toContractEntity(contract));
    }

  @Override
  public FlowStatus getProcessFlow() {
    return FlowStatus.POSITION_UPDATED;
  }

  public ContractPositionUpdated(ContractRepository contractRepository, PositionRepository positionRepository,
      SettlementTempRepository settlementTempRepository, SpireService spireService,
      CloudEventRecordService cloudEventRecordService, EventMapper eventMapper, PositionMapper positionMapper) {
    super(contractRepository, positionRepository, settlementTempRepository, spireService,
        cloudEventRecordService, eventMapper, positionMapper);
  }
}
