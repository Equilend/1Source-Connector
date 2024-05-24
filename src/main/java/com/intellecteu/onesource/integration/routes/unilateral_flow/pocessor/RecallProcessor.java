package com.intellecteu.onesource.integration.routes.unilateral_flow.pocessor;

import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RECALL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_RECALL_DETAILS;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_SPIRE_RECALL_INSTRUCTION;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RECALL_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;

import com.intellecteu.onesource.integration.model.backoffice.Recall;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecallStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.onesource.Recall1Source;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.IntegrationDataTransformer;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.RecallService;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RecallProposalDTO;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.utils.IntegrationUtils;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecallProcessor {

    private final IntegrationDataTransformer dataTransformer;
    private final OneSourceService oneSourceService;
    private final RecallService recallService;
    private final CloudEventRecordService cloudEventRecordService;

    @Transactional
    public Recall1Source getRecall1SourceDetails(TradeEvent event) {
        String resourceUri = event.getResourceUri();
        try {
            String recallId = IntegrationUtils.parseIdFrom1SourceResourceUri(resourceUri);
            return oneSourceService.retrieveRecallDetails(recallId);
        } catch (HttpStatusCodeException e) {
            log.debug("The details of the 1Source recall: {} haven't been retrieved from 1Source", resourceUri);
            record1SourceTechnicalEvent(resourceUri, e, GET_RECALL_DETAILS, RECALL);
            return null;
        }
    }

    @Transactional
    public Recall1Source createRecall1Source(@NotNull Recall1Source recall1Source) {
        recall1Source.setProcessingStatus(ProcessingStatus.CREATED);
        recall1Source.setRecallStatus(RecallStatus.OPEN);
        recall1Source.setCreateUpdateDateTime(LocalDateTime.now());
        return recallService.save(recall1Source);
    }

    @Transactional
    public void processRecallInstruction(Recall recall) {
        try {
            final RecallProposalDTO recallProposal = dataTransformer.to1SourceRecallProposal(recall);
            oneSourceService.instructRecall(recall.getRelatedContractId(), recallProposal);
            updateRecallProcessingStatus(recall, ProcessingStatus.SUBMITTED);
            var relatedSequence = String.format("%d,%s", recall.getRelatedPositionId(), recall.getRelatedContractId());
            recordBusinessEvent(recall.getRecallId(), RECALL_SUBMITTED, relatedSequence,
                PROCESS_SPIRE_RECALL_INSTRUCTION, RECALL);
        } catch (HttpStatusCodeException e) {
            record1SourceTechnicalEvent(recall.getRecallId(), e, PROCESS_SPIRE_RECALL_INSTRUCTION, RECALL);
        }
    }

    /**
     * Update recall processing status and save the recall
     *
     * @param recall Recall
     * @param processingStatus Processing status
     * @return Persisted recall with updated processing status
     */
    @Transactional
    public Recall updateRecallProcessingStatus(@NonNull Recall recall, @NonNull ProcessingStatus processingStatus) {
        recall.setProcessingStatus(processingStatus);
        return saveRecall(recall);
    }

    public Recall saveRecall(@NonNull Recall recall) {
        return recallService.save(recall);
    }

    private void record1SourceTechnicalEvent(String record, HttpStatusCodeException exception,
        IntegrationSubProcess subProcess, IntegrationProcess process) {
        record1SourceTechnicalEvent(record, exception, subProcess, null, process);
    }

    private void record1SourceTechnicalEvent(String record, HttpStatusCodeException exception,
        IntegrationSubProcess subProcess, String related, IntegrationProcess process) {
        cloudEventRecordService.getToolkitCloudEventId(record, subProcess, TECHNICAL_EXCEPTION_1SOURCE)
            .ifPresentOrElse(
                cloudEventRecordService::updateTime,
                () -> recordTechnicalEvent(process, record, exception, subProcess, related)
            );
    }

    private void recordTechnicalEvent(IntegrationProcess process, String record,
        HttpStatusCodeException exception, IntegrationSubProcess subProcess, String related) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(process);
        var recordRequest = eventBuilder.buildExceptionRequest(record, exception, subProcess, related);
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
