package com.intellecteu.onesource.integration.routes.processor.strategy.contract;

import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.repository.SettlementTempRepository;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Deprecated(since = "flow II refactoring. Fault tolerance support is moved to routes.", forRemoval = true)
public class ContractPositionUpdated extends AbstractContractProcessStrategy {

    @Override
    public void process(ContractDto contract) {
        log.warn("This is deprecated method! Should be removed soon!");
//        String venueRefId = contract.getTrade().getExecutionVenue().getVenueRefKey();
//        if (contract.getEventType().equals(CONTRACT_PENDING) && contract.getContractStatus() == APPROVED) {
//            log.debug("Retrieving Position by venueRefId: {}", venueRefId);
//            PositionDto position = retrievePositionByVenue(venueRefId).orElse(null);
//            if (position == null) {
//                savePositionRetrievementIssue(contract);
//                return;
//            }
//            extractPartyRole(position.unwrapPositionType())
//                .ifPresentOrElse(
//                    role -> updateInstruction(contract, role, position, venueRefId, AGR_INSTRUCTIONS_RETRIEVED),
//                    () -> processPartyRoleIssue(position.unwrapPositionType(), contract)
//                );
//        }
//        contract.setFlowStatus(PROCESSED);
//        contractService.save(eventMapper.toContractEntity(contract));
    }

    @Override
    public FlowStatus getProcessFlow() {
        return FlowStatus.POSITION_UPDATED;
    }

    public ContractPositionUpdated(ContractService contractService, PositionService positionService,
        SettlementTempRepository settlementTempRepository, SettlementService settlementService,
        CloudEventRecordService cloudEventRecordService,
        ReconcileService<ContractDto, PositionDto> contractReconcileService,
        EventMapper eventMapper, SpireMapper spireMapper) {
        super(contractService, positionService, settlementTempRepository, settlementService,
            cloudEventRecordService, contractReconcileService, eventMapper, spireMapper);
    }
}
