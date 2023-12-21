package com.intellecteu.onesource.integration.routes.processor.strategy.contract;

import static com.intellecteu.onesource.integration.enums.FlowStatus.AGR_INSTRUCTIONS_RETRIEVED;
import static com.intellecteu.onesource.integration.enums.FlowStatus.PROCESSED;
import static com.intellecteu.onesource.integration.model.ContractStatus.APPROVED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_PENDING;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;

import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.SettlementTempRepository;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.SpireService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ContractPositionUpdated extends AbstractContractProcessStrategy {

    @Override
    public void process(ContractDto contract) {
        String venueRefId = contract.getTrade().getExecutionVenue().getVenueRefKey();
        if (contract.getEventType().equals(CONTRACT_PENDING) && contract.getContractStatus() == APPROVED) {
            log.debug("Retrieving Position by venueRefId: {}", venueRefId);
            PositionDto position = retrievePositionByVenue(venueRefId);
            if (position == null) {
                savePositionRetrievementIssue(contract);
                return;
            }
            extractPartyRole(position.unwrapPositionType())
                .ifPresentOrElse(
                    role -> updateInstruction(contract, role, position, venueRefId, AGR_INSTRUCTIONS_RETRIEVED),
                    () -> processPartyRoleIssue(position.unwrapPositionType(), contract)
                );
        }
        contract.setFlowStatus(PROCESSED);
        contractRepository.save(eventMapper.toContractEntity(contract));
    }

    @Override
    public FlowStatus getProcessFlow() {
        return FlowStatus.POSITION_UPDATED;
    }

    public ContractPositionUpdated(ContractRepository contractRepository, PositionRepository positionRepository,
        SettlementTempRepository settlementTempRepository, SettlementService settlementService,
        SpireService spireService, CloudEventRecordService cloudEventRecordService,
        ReconcileService<ContractDto, PositionDto> contractReconcileService,
        EventMapper eventMapper, SpireMapper spireMapper) {
        super(contractRepository, positionRepository, settlementTempRepository, settlementService, spireService,
            cloudEventRecordService, contractReconcileService, eventMapper, spireMapper);
    }
}
