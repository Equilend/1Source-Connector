package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.MATCHED_POSITION;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ContractStatus;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jakarta.transaction.Transactional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ContractService {

    private final ContractRepository contractRepository;
    private final CloudEventRecordService cloudEventRecordService;
    private final OneSourceMapper oneSourceMapper;

    @Autowired
    public ContractService(ContractRepository contractRepository, CloudEventRecordService cloudEventRecordService,
        OneSourceMapper oneSourceMapper) {
        this.contractRepository = contractRepository;
        this.cloudEventRecordService = cloudEventRecordService;
        this.oneSourceMapper = oneSourceMapper;
    }

    @Transactional
    public Contract save(Contract contract) {
        ContractEntity contractEntity = contractRepository.save(oneSourceMapper.toEntity(contract));
        return oneSourceMapper.toModel(contractEntity);
    }

    public Optional<Contract> findByVenueRefId(String venueRefId) {
        return contractRepository.findByVenueRefId(venueRefId).stream().findFirst().map(oneSourceMapper::toModel);
    }

    public Optional<Contract> findByPositionId(String positionId){
        return contractRepository.findByMatchingSpirePositionId(positionId).stream().findFirst().map(oneSourceMapper::toModel);
    }

    public List<Contract> findAllByContractId(String contractId) {
        return contractRepository.findAllByContractId(contractId).stream().map(oneSourceMapper::toModel).collect(
            Collectors.toList());
    }

    public List<Contract> findAllByContractStatus(ContractStatus status) {
        return contractRepository.findAllByContractStatus(status).stream().map(oneSourceMapper::toModel).collect(
            Collectors.toList());
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
