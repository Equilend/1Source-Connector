package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.MATCHED_POSITION;

import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.ContractStatus;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ContractService {

    private final ContractRepository contractRepository;
    private final CloudEventRecordService cloudEventRecordService;

    @Autowired
    public ContractService(ContractRepository contractRepository, CloudEventRecordService cloudEventRecordService) {
        this.contractRepository = contractRepository;
        this.cloudEventRecordService = cloudEventRecordService;
    }

    public Contract save(Contract contract) {
        return contractRepository.save(contract);
    }

    public Optional<Contract> findByVenueRefId(String venueRefId) {
        return contractRepository.findByVenueRefId(venueRefId).stream().findFirst();
    }

    public List<Contract> findAllByContractId(String contractId) {
        return contractRepository.findAllByContractId(contractId);
    }

    public List<Contract> findAllByContractStatus(ContractStatus status) {
        return contractRepository.findAllByContractStatus(status);
    }

    public Contract markContractAsMatched(Contract contract, String positionId) {
        contract.setMatchingSpirePositionId(positionId);
        contract.setProcessingStatus(MATCHED_POSITION);
        contract.setLastUpdateDatetime(LocalDateTime.now());
        createContractInitiationCloudEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION,
            contract.getMatchingSpirePositionId());
        return save(contract);
    }

    private void createContractInitiationCloudEvent(String recordData, RecordType recordType, String relatedData) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(recordData, recordType, relatedData);
        cloudEventRecordService.record(recordRequest);
    }


}
