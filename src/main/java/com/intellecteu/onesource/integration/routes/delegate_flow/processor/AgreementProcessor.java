package com.intellecteu.onesource.integration.routes.delegate_flow.processor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_TRADE_AGREEMENT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_TRADE_CANCELATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_TRADE_CANCELATION;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_ISSUE_INTEGRATION_TOOLKIT;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TRADE_AGREEMENT_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TRADE_AGREEMENT_UNMATCHED;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.parseAgreementIdFrom1SourceResourceUri;

import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.AgreementService;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.PositionService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgreementProcessor {

    private final AgreementService agreementService;
    private final OneSourceService oneSourceService;
    private final CloudEventRecordService cloudEventRecordService;
    private final PositionService positionService;

    @Transactional
    public Agreement getAgreementDetails(TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/agreements/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            String agreementId = parseAgreementIdFrom1SourceResourceUri(resourceUri);
            return oneSourceService.retrieveTradeAgreementDetails(agreementId);
        } catch (HttpStatusCodeException e) {
            log.debug("Agreement {} was not retrieved. Details: {} ", resourceUri, e.getMessage());
            saveOrUpdate1SourceTechnicalEvent(resourceUri, e, GET_TRADE_AGREEMENT, event.getEventId(), CONTRACT_INITIATION);
            return null;
        }
    }

    @Transactional
    public Agreement retrieveAgreementFromEvent(@NotNull TradeEvent event) {
        // expected format for resourceUri: /v1/ledger/agreements/93f834ff-66b5-4195-892b-8f316ed77006
        String resourceUri = event.getResourceUri();
        try {
            String agreementId = parseAgreementIdFrom1SourceResourceUri(resourceUri);
            return agreementService.findByAgreementId(agreementId).orElse(null);
        } catch (HttpStatusCodeException e) {
            log.debug("Agreement {} was not retrieved. Details: {} ", resourceUri, e.getMessage());
            saveOrUpdateTechnicalEvent(TECHNICAL_ISSUE_INTEGRATION_TOOLKIT, resourceUri, e,
                PROCESS_TRADE_CANCELATION, null, CONTRACT_INITIATION);
            return null;
        }
    }

    @Transactional
    public Agreement createAgreement(@NonNull Agreement agreement) {
        agreement.setCreateDateTime(LocalDateTime.now());
        final Agreement savedAgreement = agreementService
            .updateProcessingStatusAndSave(agreement, ProcessingStatus.CREATED);
        recordAgreementUnmatchedEvent(agreement);
        return savedAgreement;
    }

    @Transactional
    public void matchAgreementWithPosition(@NonNull Agreement agreement) {
        final String venueRefKey = agreement.unwrapVenueRefKey();
        if (venueRefKey == null) {
            agreementService.updateProcessingStatusAndSave(agreement, ProcessingStatus.UNMATCHED);
            return;
        }
        log.debug("Matching agreement: {} with position customValue2:{}", agreement.getAgreementId(), venueRefKey);
        final Optional<Position> matchedPosition = positionService.getByVenueRefKey(venueRefKey);
        matchedPosition.ifPresentOrElse(
            position -> agreementService.matchPosition(agreement, position),
            () -> agreementService.updateProcessingStatusAndSave(agreement, ProcessingStatus.UNMATCHED));
    }

    @Transactional
    public void recordAgreementUnmatchedEvent(@NonNull Agreement agreement) {
        String related = agreement.unwrapVenueRefKey();
        recordBusinessEvent(agreement.getAgreementId(), TRADE_AGREEMENT_UNMATCHED, related,
            GET_TRADE_AGREEMENT, CONTRACT_INITIATION);
    }

    @Transactional
    public void executeCancelUpdate(@NonNull Agreement agreement) {
        updateProcessingStatusAndSave(agreement, ProcessingStatus.CANCELED);
        String related = agreement.getMatchingSpirePositionId() == null
            ? agreement.unwrapVenueRefKey()
            : String.format("%s,%s", agreement.getMatchingSpirePositionId(), agreement.unwrapVenueRefKey());
        recordBusinessEvent(agreement.getAgreementId(), TRADE_AGREEMENT_CANCELED, related,
            GET_TRADE_CANCELATION, CONTRACT_INITIATION);
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
        return agreementService.saveAgreement(agreement);
    }

    @Transactional
    public void recordAgreementCancelIssue(@NonNull TradeEvent event) {
        String resourceUri = event.getResourceUri();
        saveOrUpdateTechnicalEvent(TECHNICAL_ISSUE_INTEGRATION_TOOLKIT, resourceUri, null,
            PROCESS_TRADE_CANCELATION, null, CONTRACT_INITIATION);
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

    private void saveOrUpdate1SourceTechnicalEvent(String record, HttpStatusCodeException exception,
        IntegrationSubProcess subProcess, String related, IntegrationProcess process) {
        saveOrUpdateTechnicalEvent(TECHNICAL_EXCEPTION_1SOURCE, record, exception, subProcess, related, process);
    }

    private void saveOrUpdateTechnicalEvent(RecordType recordType, String record, HttpStatusCodeException exception,
        IntegrationSubProcess subProcess, String related, IntegrationProcess process) {
        try {
            cloudEventRecordService.getToolkitCloudEventId(record, subProcess, recordType)
                .ifPresentOrElse(
                    cloudEventRecordService::updateTime,
                    () -> {
                        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(process);
                        var recordRequest = eventBuilder.buildExceptionRequest(record, exception, subProcess, related);
                        cloudEventRecordService.record(recordRequest);
                    }
                );
        } catch (Exception e) {
            log.warn("Cloud event cannot be recorded for recordType:{}, process:{}, subProcess:{}, record:{}",
                recordType, process, subProcess, record);
        }
    }
}
