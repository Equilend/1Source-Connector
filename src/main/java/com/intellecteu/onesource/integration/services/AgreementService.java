package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_DISCREPANCIES;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.TRADE_AGREEMENT_RECONCILED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.MATCHED_POSITION;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.RECONCILED;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AgreementService {

    private AgreementRepository agreementRepository;
    private CloudEventRecordService cloudEventRecordService;
    private EventMapper eventMapper;
    private SpireMapper spireMapper;
    private ReconcileService<AgreementDto, PositionDto> reconcileService;

    @Autowired
    public AgreementService(AgreementRepository agreementRepository, CloudEventRecordService cloudEventRecordService,
        EventMapper eventMapper, SpireMapper spireMapper,
        ReconcileService<AgreementDto, PositionDto> reconcileService) {
        this.agreementRepository = agreementRepository;
        this.cloudEventRecordService = cloudEventRecordService;
        this.eventMapper = eventMapper;
        this.spireMapper = spireMapper;
        this.reconcileService = reconcileService;
    }

    public Agreement saveAgreement(Agreement agreement) {
        agreement.setLastUpdateDatetime(LocalDateTime.now());
        return agreementRepository.save(agreement);
    }

    public Optional<Agreement> findByVenueRefId(String venueRefId) {
        return agreementRepository.findByVenueRefId(venueRefId).stream()
            .findFirst();
    }

    public Optional<Agreement> findByAgreementId(String agreementId) {
        return agreementRepository.findByAgreementId(agreementId).stream().findFirst();
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
            reconcileService.reconcile(eventMapper.toAgreementDto(agreement), spireMapper.toPositionDto(position));
            log.debug("Agreement {} is reconciled with position {}", agreement.getAgreementId(),
                position.getPositionId());
            agreement.setProcessingStatus(RECONCILED);
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
