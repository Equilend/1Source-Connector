package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.model.enums.RecordType.TRADE_AGREEMENT_DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TRADE_AGREEMENT_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TRADE_AGREEMENT_RECONCILED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.RECONCILED;

import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.entity.onesource.AgreementEntity;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AgreementService {

    private final AgreementRepository agreementRepository;
    private final CloudEventRecordService cloudEventRecordService;
    private final SpireMapper spireMapper;
    private final ReconcileService<Agreement, PositionDto> reconcileService;
    private final OneSourceMapper oneSourceMapper;

    @Autowired
    public AgreementService(AgreementRepository agreementRepository, CloudEventRecordService cloudEventRecordService,
        SpireMapper spireMapper, ReconcileService<Agreement, PositionDto> reconcileService,
        OneSourceMapper oneSourceMapper) {
        this.agreementRepository = agreementRepository;
        this.cloudEventRecordService = cloudEventRecordService;
        this.spireMapper = spireMapper;
        this.reconcileService = reconcileService;
        this.oneSourceMapper = oneSourceMapper;
    }

    public Agreement saveAgreement(Agreement agreement) {
        agreement.setLastUpdateDateTime(LocalDateTime.now());
        AgreementEntity agreementEntity = agreementRepository.save(oneSourceMapper.toEntity(agreement));
        return oneSourceMapper.toModel(agreementEntity);
    }

    public Optional<Agreement> findByVenueRefId(String venueRefId) {
        return agreementRepository.findByVenueRefId(venueRefId).stream()
            .findFirst().map(oneSourceMapper::toModel);
    }

    public Optional<Agreement> findByAgreementId(String agreementId) {
        return agreementRepository.findByAgreementId(agreementId).stream().findFirst().map(oneSourceMapper::toModel);
    }

    public Agreement markAgreementAsMatched(Agreement agreement, String positionId) {
        agreement.setMatchingSpirePositionId(positionId);
        agreement.setProcessingStatus(MATCHED_POSITION);
        createContractInitiationCloudEvent(agreement.getAgreementId(),
            TRADE_AGREEMENT_MATCHED_POSITION, agreement.getMatchingSpirePositionId());
        return saveAgreement(agreement);
    }

    public Agreement reconcile(Agreement agreement, Position position) {
        try {
            log.debug("Starting reconciliation for agreement {} and position {}",
                agreement.getAgreementId(), position.getPositionId());
            reconcileService.reconcile(agreement, spireMapper.toPositionDto(position));
            log.debug("Agreement {} is reconciled with position {}", agreement.getAgreementId(),
                position.getPositionId());
            agreement.setProcessingStatus(RECONCILED);
            log.debug("Agreement {} changed processing status to {}", agreement.getAgreementId(),
                agreement.getProcessingStatus());
            recordSuccessReconciliationCloudEvent(agreement);
        } catch (ReconcileException e) {
            log.error("Reconciliation fails with message: {} ", e.getMessage());
            agreement.setProcessingStatus(DISCREPANCIES);
            recordFailReconciliationCloudEvent(agreement, e);
        }
        return agreement;
    }

    private void createContractInitiationCloudEvent(String recordData, RecordType recordType, String relatedData) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(IntegrationProcess.CONTRACT_INITIATION);
        var recordRequest = eventBuilder.buildRequest(recordData, recordType, relatedData);
        cloudEventRecordService.record(recordRequest);
    }

    private void recordFailReconciliationCloudEvent(Agreement agreement, ReconcileException exception) {
        exception.getErrorList().forEach(msg -> log.debug(msg.getExceptionMessage()));
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

}
