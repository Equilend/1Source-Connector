package com.intellecteu.onesource.integration.routes.contract_initiation.delegate_flow.processor;

import com.intellecteu.onesource.integration.model.enums.FlowStatus;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ContractStatus;
import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity;
import com.intellecteu.onesource.integration.routes.contract_initiation.delegate_flow.processor.strategy.contract.ContractProcessFlowStrategy;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.DeclineContractInstructionService;
import com.intellecteu.onesource.integration.services.client.onesource.OneSourceApiClient;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContractProcessor implements TradeDataProcessor {

    private final Map<FlowStatus, ContractProcessFlowStrategy> strategyByFlow;
    private final ContractService contractService;
    private final OneSourceApiClient oneSourceClient;
    private final DeclineContractInstructionService declineContractInstructionService;

    @Override
    @Transactional
    public void processTradeData() {
        log.info(">>>>> Starting processing trade data");
        contractService.findAllNotProcessed().forEach(this::processContract);
        log.info("<<<<<< Finished processing trade data");
    }

    private void processContract(Contract contract) {
        log.debug("***** Processing Contract id: {} with status {}, processing status: {} ",
            contract.getContractId(), contract.getContractStatus(), contract.getProcessingStatus());
        var strategy = strategyByFlow.get(contract.getFlowStatus());
        if (strategy == null) {
            throw new RuntimeException(
                "Strategy is not implemented for the flow: " + contract.getFlowStatus());
        }
        strategy.process(contract);
    }

    @Transactional
    public void processDecline(DeclineInstructionEntity declineInstruction) {
        String contractId = declineInstruction.getRelatedProposalId();
        Optional<Contract> contractOptional = contractService.findContractById(contractId);
        contractOptional.ifPresent(contract -> {
            oneSourceClient.declineContract(contract);
            contract.setContractStatus(ContractStatus.DECLINED);
            contract.setProcessingStatus(ProcessingStatus.DECLINED);
            contractService.save(contract);
            declineInstruction.setProcessingStatus(ProcessingStatus.PROCESSED);
            declineContractInstructionService.save(declineInstruction);
        });
    }
}
