package com.intellecteu.onesource.integration.routes.processor.strategy.contract;

import static com.intellecteu.onesource.integration.enums.FlowStatus.CTR_INSTRUCTIONS_RETRIEVED;
import static com.intellecteu.onesource.integration.enums.FlowStatus.PROCESSED;
import static com.intellecteu.onesource.integration.model.ContractStatus.APPROVED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_PENDING;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;

import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.SettlementInstructionDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.SettlementTemp;
import com.intellecteu.onesource.integration.repository.SettlementTempRepository;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.SpireService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ContractInstructionsRetrieved extends AbstractContractProcessStrategy {

    @Override
    public void process(ContractDto contract) {
        //todo remove fault tolerance case
        String venueRefId = contract.getTrade().getExecutionVenue().getVenueRefKey();
        PositionDto position = retrievePositionByVenue(venueRefId).orElse(null);
        if (position == null) {
            savePositionRetrievementIssue(contract);
            return;
        }
        extractPartyRole(position.unwrapPositionType())
            .ifPresentOrElse(
                role -> processByRole(role, contract, position),
                () -> processPartyRoleIssue(position.unwrapPositionType(), contract)
            );
        contract.setFlowStatus(PROCESSED);
        contractService.save(eventMapper.toContractEntity(contract));
    }

    private void processByRole(PartyRole partyRole, ContractDto contractDto, PositionDto positionDto) {
        String venueRefId = contractDto.getTrade().getExecutionVenue().getVenueRefKey();
        if (contractDto.getEventType().equals(CONTRACT_PENDING) && (contractDto.getContractStatus() == APPROVED)) {
            log.debug("Retrieving Position by venueRefId: {}", venueRefId);
            settlementTempRepository.findByContractId(contractDto.getContractId()).stream()
                .findFirst()
                .ifPresent(settlementTemp -> update(contractDto, settlementTemp, positionDto, venueRefId, partyRole));
            saveContractWithStage(contractDto, CTR_INSTRUCTIONS_RETRIEVED);
        }
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

    public ContractInstructionsRetrieved(ContractService contractService, PositionService positionService,
        SettlementTempRepository settlementTempRepository, SettlementService settlementService,
        SpireService spireService, CloudEventRecordService cloudEventRecordService,
        ReconcileService<ContractDto, PositionDto> contractReconcileService, EventMapper eventMapper,
        SpireMapper spireMapper) {
        super(contractService, positionService, settlementTempRepository, settlementService, spireService,
            cloudEventRecordService, contractReconcileService, eventMapper, spireMapper);
    }
}
