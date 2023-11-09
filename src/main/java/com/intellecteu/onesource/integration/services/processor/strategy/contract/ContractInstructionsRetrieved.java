package com.intellecteu.onesource.integration.services.processor.strategy.contract;

import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.SettlementInstructionDto;
import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.dto.record.CloudEventData;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.PositionMapper;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.SettlementTemp;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.SettlementTempRepository;
import com.intellecteu.onesource.integration.services.SpireService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.intellecteu.onesource.integration.enums.FlowStatus.CTR_INSTRUCTIONS_RETRIEVED;
import static com.intellecteu.onesource.integration.enums.FlowStatus.PROCESSED;
import static com.intellecteu.onesource.integration.model.ContractStatus.APPROVED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_APPROVE;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;

@Component
@Slf4j
public class ContractInstructionsRetrieved extends AbstractContractProcessStrategy {

  @Override
  public void process(ContractDto contract) {
    String venueRefId = contract.getTrade().getExecutionVenue().getPlatform().getVenueRefId();
    PositionDto position = retrievePositionByVenue(venueRefId);
    if (position == null) {
      savePositionRetrievementIssue(contract, venueRefId);
      return;
    }
    var positionLei = position.getAccountLei();
    final PartyRole partyRole = extractPartyRole(contract.getTrade().getTransactingParties(), positionLei);
    if (partyRole == null) {
      processPartyRoleIssue(contract, positionLei);
    } else if (contract.getEventType().equals(CONTRACT_APPROVE) && (contract.getContractStatus() == APPROVED)) {
      log.debug("Retrieving Position by venueRefId: {}", venueRefId);
      settlementTempRepository.findByContractId(contract.getContractId()).stream()
          .findFirst()
          .ifPresent(settlementTemp -> update(contract, settlementTemp, position, venueRefId, partyRole));
      saveContractWithStage(contract, CTR_INSTRUCTIONS_RETRIEVED);
    }
    contract.setFlowStatus(PROCESSED);
    contractRepository.save(eventMapper.toContractEntity(contract));
  }

  private void update(ContractDto contract, SettlementTemp settlementTemp,
      PositionDto position, String venueRefId, PartyRole partyRole) {
    List<SettlementDto> settlementDtos = eventMapper.toSettlementDtoList(settlementTemp.getSettlement());
    SettlementInstructionDto settlementInstruction = settlementDtos.get(0).getInstruction();
    log.debug("Updating Spire with the counterpart's settlement instruction!");
    spireService.updateInstruction(contract, position, venueRefId, settlementInstruction, partyRole);
  }

  @Override
  public FlowStatus getProcessFlow() {
    return CTR_INSTRUCTIONS_RETRIEVED;
  }

  public ContractInstructionsRetrieved(ContractRepository contractRepository, PositionRepository positionRepository,
      SettlementTempRepository settlementTempRepository, SpireService spireService,
      CloudEventRecordService cloudEventRecordService, EventMapper eventMapper,
      PositionMapper positionMapper) {
    super(contractRepository, positionRepository, settlementTempRepository, spireService,
        cloudEventRecordService, eventMapper, positionMapper);
  }
}
