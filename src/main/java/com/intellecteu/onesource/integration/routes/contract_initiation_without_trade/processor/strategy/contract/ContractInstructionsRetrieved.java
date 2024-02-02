package com.intellecteu.onesource.integration.routes.contract_initiation_without_trade.processor.strategy.contract;

import static com.intellecteu.onesource.integration.model.enums.FlowStatus.CTR_INSTRUCTIONS_RETRIEVED;

import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.model.enums.FlowStatus;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.repository.SettlementTempRepository;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.ReconcileService;
import com.intellecteu.onesource.integration.services.SettlementService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Deprecated(since = "flow II refactoring. Fault tolerance support is moved to routes.", forRemoval = true)
public class ContractInstructionsRetrieved extends AbstractContractProcessStrategy {

    @Override
    public void process(ContractDto contract) {
        log.warn("This is deprecated method! Should be removed soon!");
//        String venueRefId = contract.getTrade().getExecutionVenue().getVenueRefKey();
//        PositionDto position = retrievePositionByVenue(venueRefId).orElse(null);
//        if (position == null) {
//            savePositionRetrievementIssue(contract);
//            return;
//        }
//        extractPartyRole(position.unwrapPositionType())
//            .ifPresentOrElse(
//                role -> processByRole(role, contract, position),
//                () -> processPartyRoleIssue(position.unwrapPositionType(), contract)
//            );
//        contract.setFlowStatus(PROCESSED);
//        contractService.save(eventMapper.toContractEntity(contract));
    }

    @Override
    public FlowStatus getProcessFlow() {
        return CTR_INSTRUCTIONS_RETRIEVED;
    }

    public ContractInstructionsRetrieved(ContractService contractService, PositionService positionService,
        SettlementTempRepository settlementTempRepository, SettlementService settlementService,
        CloudEventRecordService cloudEventRecordService,
        ReconcileService<ContractDto, PositionDto> contractReconcileService, EventMapper eventMapper,
        SpireMapper spireMapper) {
        super(contractService, positionService, settlementTempRepository, settlementService,
            cloudEventRecordService, contractReconcileService, eventMapper, spireMapper);
    }
}
