package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.MATCHED;

import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ContractStatus;
import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@Transactional(readOnly = true)
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
        final ContractEntity contractEntity = oneSourceMapper.toEntity(contract);
        ContractEntity savedEntity = contractRepository.save(contractEntity);
        log.debug("Contract with id: {} was saved.", contract.getContractId());
        return oneSourceMapper.toModel(savedEntity);
    }

    @Transactional
    public List<Contract> saveAll(List<Contract> contracts) {
        final List<ContractEntity> entityList = contracts.stream()
            .map(oneSourceMapper::toEntity)
            .toList();
        List<ContractEntity> savedEntities = contractRepository.saveAll(entityList);
        log.debug("{} contracts were saved.", savedEntities.size());
        return savedEntities.stream()
            .map(oneSourceMapper::toModel)
            .toList();
    }

    public Optional<Contract> findByVenueRefId(String venueRefId) {
        return contractRepository.findByVenueRefId(venueRefId).stream().findFirst().map(oneSourceMapper::toModel);
    }

    public Set<Contract> findAllByProcessingStatus(ProcessingStatus processingStatus) {
        return contractRepository.findAllByProcessingStatus(processingStatus).stream()
            .map(oneSourceMapper::toModel)
            .collect(Collectors.toSet());
    }

    public Optional<Contract> findByPositionId(Long positionId) {
        return contractRepository.findByMatchingSpirePositionId(positionId).stream().findFirst()
            .map(oneSourceMapper::toModel);
    }

    public List<Contract> findAllByContractId(String contractId) {
        return contractRepository.findAllByContractId(contractId).stream().map(oneSourceMapper::toModel).collect(
            Collectors.toList());
    }

    public List<Contract> findAllByContractStatus(ContractStatus status) {
        return contractRepository.findAllByContractStatus(status).stream().map(oneSourceMapper::toModel).collect(
            Collectors.toList());
    }

    public List<Contract> findAllNotProcessed() {
        List<ContractEntity> notProcessedEntities = contractRepository.findAllNotProcessed();
        return notProcessedEntities.stream()
            .map(oneSourceMapper::toModel)
            .toList();
    }

    public Optional<Contract> findContractById(String contractId) {
        return contractRepository.findByContractId(contractId).map(oneSourceMapper::toModel);
    }

    public Contract markContractAsMatched(Contract contract, Long positionId) {
        contract.setMatchingSpirePositionId(positionId);
        contract.setProcessingStatus(MATCHED);
        contract.setLastUpdateDateTime(LocalDateTime.now());
        createContractInitiationCloudEvent(contract.getContractId(), LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION,
            String.valueOf(contract.getMatchingSpirePositionId()));
        return save(contract);
    }

    private void createContractInitiationCloudEvent(String recordData, RecordType recordType, String relatedData) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(recordData, recordType, relatedData);
        cloudEventRecordService.record(recordRequest);
    }


}
