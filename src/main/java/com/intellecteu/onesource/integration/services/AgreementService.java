package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.MATCH_TRADE_AGREEMENT;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TRADE_AGREEMENT_DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TRADE_AGREEMENT_MATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TRADE_AGREEMENT_RECONCILED;

import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.entity.onesource.AgreementEntity;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class AgreementService {

    private final AgreementRepository agreementRepository;
    private final CloudEventRecordService cloudEventRecordService;
    private final OneSourceMapper oneSourceMapper;

    @Autowired
    public AgreementService(AgreementRepository agreementRepository, CloudEventRecordService cloudEventRecordService,
        OneSourceMapper oneSourceMapper) {
        this.agreementRepository = agreementRepository;
        this.cloudEventRecordService = cloudEventRecordService;
        this.oneSourceMapper = oneSourceMapper;
    }

    /**
     * Update last update time and persist an agreement.
     *
     * @param agreement Agreement
     * @return Agreement persisted model
     */
    @Transactional
    public Agreement saveAgreement(Agreement agreement) {
        agreement.setLastUpdateDateTime(LocalDateTime.now());
        AgreementEntity agreementEntity = agreementRepository.save(oneSourceMapper.toEntity(agreement));
        return oneSourceMapper.toModel(agreementEntity);
    }

    /**
     * Update agreement processing status and save the agreement
     *
     * @param agreement Agreement
     * @param processingStatus Processing status
     * @return Agreement persisted agreement with updated processing status
     */
    @Transactional
    public Agreement updateProcessingStatusAndSave(@NonNull Agreement agreement,
        @NonNull ProcessingStatus processingStatus) {
        agreement.setProcessingStatus(processingStatus);
        return saveAgreement(agreement);
    }

    @Transactional
    public void matchPosition(@NonNull Agreement agreement, @NonNull Position position) {
        agreement.setMatchingSpirePositionId(String.valueOf(position.getPositionId()));
        updateProcessingStatusAndSave(agreement, ProcessingStatus.MATCHED);
        recordBusinessEvent(agreement.getAgreementId(), TRADE_AGREEMENT_MATCHED,
            String.format("%s,%s", agreement.getMatchingSpirePositionId(), position.getCustomValue2()),
            MATCH_TRADE_AGREEMENT, CONTRACT_INITIATION);
    }

    @Transactional
    public void matchPosition(Position position) {
        findByVenueRefKey(position.getCustomValue2()).ifPresent(agreement -> matchPosition(agreement, position));
    }

    public Optional<Agreement> findByVenueRefId(String venueRefId) {
        return agreementRepository.findByVenueRefId(venueRefId).stream()
            .findFirst().map(oneSourceMapper::toModel);
    }

    @Transactional
    public Optional<Agreement> findByVenueRefKey(String venueRefKey) {
        return agreementRepository.findByVenueRefKey(venueRefKey).map(oneSourceMapper::toModel);
    }

    public Optional<Agreement> findByAgreementId(String agreementId) {
        return agreementRepository.findByAgreementId(agreementId).map(oneSourceMapper::toModel);
    }

    private void createContractInitiationCloudEvent(String recordData, RecordType recordType, String relatedData) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(recordData, recordType, relatedData);
        cloudEventRecordService.record(recordRequest);
    }

    private void recordFailReconciliationCloudEvent(Agreement agreement, ReconcileException exception) {
        exception.getErrorList().forEach(msg -> log.debug(msg.getFieldValue()));
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(agreement.getAgreementId(), TRADE_AGREEMENT_DISCREPANCIES,
            agreement.getMatchingSpirePositionId(), exception.getErrorList());
        cloudEventRecordService.record(recordRequest);
    }

    private void recordSuccessReconciliationCloudEvent(Agreement agreement) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(agreement.getAgreementId(),
            TRADE_AGREEMENT_RECONCILED, agreement.getMatchingSpirePositionId());
        cloudEventRecordService.record(recordRequest);
    }


    private void recordBusinessEvent(String record, RecordType recordType,
        String related, IntegrationSubProcess subProcess, IntegrationProcess process) {
        try {
            var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(process);
            var recordRequest = eventBuilder.buildRequest(record, recordType, related);
            cloudEventRecordService.record(recordRequest);
        } catch (Exception e) {
            log.warn("Cloud event cannot be recorded for recordType:{}, process:{}, subProcess:{}, record:{}",
                recordType, process, subProcess, record);
        }
    }

}
