package com.intellecteu.onesource.integration.routes.unilateral_flow.pocessor;

import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_LOAN_CONTRACT;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_RECALL;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.POSITION;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.SPIRE_RECALL;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.SPIRE_RECALL_INSTRUCTION;
import static com.intellecteu.onesource.integration.model.enums.FieldExceptionType.INFORMATIONAL;
import static com.intellecteu.onesource.integration.model.enums.FieldSource.ONE_SOURCE_RECALL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RECALL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_RECALL_DETAILS;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_SPIRE_RECALL_CANCELLATION_INSTRUCTION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_SPIRE_RECALL_INSTRUCTION;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CANCEL_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CONFIRMED_BORROWER;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CONFIRMED_LENDER;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RECALL_CANCEL_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RECALL_CONFIRMED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RECALL_SUBMITTED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_ISSUE_INTEGRATION_TOOLKIT;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.model.backoffice.RecallSpire;
import com.intellecteu.onesource.integration.model.backoffice.RecallSpireInstruction;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecallStatus;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.onesource.Recall1Source;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.IntegrationDataTransformer;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.RecallService;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RecallProposalDTO;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.utils.IntegrationUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
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
    public void processRecallInstruction(RecallSpire recallSpire) {
        try {
            final RecallProposalDTO recallProposal = dataTransformer.to1SourceRecallProposal(recallSpire);
            oneSourceService.instructRecall(recallSpire.getRelatedContractId(), recallProposal);
            updateRecallProcessingStatus(recallSpire, ProcessingStatus.SUBMITTED);
            var relatedSequence = format("%d,%s", recallSpire.getRelatedPositionId(),
                recallSpire.getRelatedContractId());
            recordBusinessEvent(String.valueOf(recallSpire.getRecallId()), RECALL_SUBMITTED, relatedSequence,
                PROCESS_SPIRE_RECALL_INSTRUCTION, RECALL);
        } catch (HttpStatusCodeException e) {
            record1SourceTechnicalEvent(String.valueOf(recallSpire.getRecallId()), e,
                PROCESS_SPIRE_RECALL_INSTRUCTION, RECALL);
        }
    }

    @Transactional
    public void matchRecalls(@NotNull Recall1Source recall1Source) {
        try {
            final RecallSpire recallSpire = recallService.getMatchedSpireRecall(recall1Source)
                .orElseThrow(EntityNotFoundException::new);
            recall1Source.setMatchingSpireRecallId(Long.valueOf(recallSpire.getRecallId()));
            recall1Source.setRelatedSpirePositionId(recallSpire.getRelatedPositionId());
            updateRecall1SourceProcessingStatus(recall1Source, CONFIRMED_LENDER);
            recallSpire.setMatching1SourceRecallId(recall1Source.getRecallId());
            saveSpireRecall(recallSpire);
            createRecallConfirmedRecord(recall1Source, recallSpire);
        } catch (RuntimeException e) {
            log.debug("No matching SPIRE recalls were found for Recall 1Source:{}", recall1Source.getRecallId());
            updateRecall1SourceProcessingStatus(recall1Source, CONFIRMED_BORROWER);
            createRecallConfirmedRecord(recall1Source);
        }
    }

    @Transactional
    public RecallSpire getRecallToCancel(RecallSpireInstruction instruction) {
        return recallService.getSpireRecallByIdAndPosition(instruction.getSpireRecallId(),
            instruction.getRelatedPositionId()).orElse(null);
    }

    @Transactional
    public void instructRecallCancellation(@NotNull RecallSpireInstruction instruction,
        @Nullable RecallSpire recallSpire) {
        if (recallSpire == null || recallSpire.getMatching1SourceRecallId() == null) {
            recordRecallCancellationToolkitIssue(instruction);
            return;
        }
        try {
            oneSourceService.instructRecallCancellation(recallSpire.getMatching1SourceRecallId(),
                recallSpire.getRelatedContractId());
            recallSpire.setStatus(RecallStatus.CANCELED);
            updateRecallProcessingStatus(recallSpire, CANCEL_SUBMITTED);
            recordCancelSubmittedEvent(instruction.getInstructionId(), recallSpire);
        } catch (HttpStatusCodeException e) {
            log.debug("Exception on instruct SPIRE recall cancellation, recallId:{}, details:{}",
                recallSpire.getRecallId(), e.getMessage());
            recordCancelSubmittedTechnicalEvent(instruction.getInstructionId(), recallSpire, e);
        }
    }

    @Transactional
    public void updateInstructionStatus(RecallSpireInstruction instruction, ProcessingStatus processingStatus) {
        instruction.setProcessingStatus(processingStatus);
        saveSpireInstruction(instruction);
    }

    private void recordCancelSubmittedTechnicalEvent(String instructionId, RecallSpire recallSpire,
        HttpStatusCodeException e) {
        String oneSourceRecallId = String.valueOf(recallSpire.getMatching1SourceRecallId());
        String spireRecallId = String.valueOf(recallSpire.getRecallId());
        String positionId = String.valueOf(recallSpire.getRelatedPositionId());
        String contractId = recallSpire.getRelatedContractId();
        String relatedSequence = format("%s,%s,%s,%s", oneSourceRecallId, spireRecallId, positionId, contractId);
        record1SourceTechnicalEvent(instructionId, e, PROCESS_SPIRE_RECALL_CANCELLATION_INSTRUCTION,
            relatedSequence, RECALL);
    }

    private void recordRecallCancellationToolkitIssue(@NotNull RecallSpireInstruction instruction) {
        String spireRecallInstructionId = instruction.getInstructionId();
        Map<String, String> data = new HashMap<>();
        data.put(SPIRE_RECALL_INSTRUCTION, spireRecallInstructionId);
        data.put(SPIRE_RECALL, String.valueOf(instruction.getSpireRecallId()));
        data.put(POSITION, String.valueOf(instruction.getRelatedPositionId()));
        data.put(ONESOURCE_LOAN_CONTRACT, instruction.getRelatedContractId());
        recordIntegrationToolkitIssueEvent(spireRecallInstructionId, PROCESS_SPIRE_RECALL_CANCELLATION_INSTRUCTION,
            RECALL, data);
    }

    private void recordCancelSubmittedEvent(@NotNull String instructionId, @NotNull RecallSpire recallSpire) {
        String oneSourceRecall = recallSpire.getMatching1SourceRecallId();
        String spireRecallId = String.valueOf(recallSpire.getRecallId());
        String positionId = String.valueOf(recallSpire.getRelatedPositionId());
        String contractId = recallSpire.getRelatedContractId();
        Map<String, String> data = new HashMap<>();
        data.put(SPIRE_RECALL_INSTRUCTION, instructionId);
        data.put(ONESOURCE_RECALL, oneSourceRecall);
        data.put(SPIRE_RECALL, spireRecallId);
        data.put(POSITION, positionId);
        data.put(ONESOURCE_LOAN_CONTRACT, contractId);
        recordBusinessEvent(PROCESS_SPIRE_RECALL_CANCELLATION_INSTRUCTION, RECALL_CANCEL_SUBMITTED, data, RECALL);
    }

    private void createRecallConfirmedRecord(Recall1Source recall1Source) {
        createRecallConfirmedRecord(recall1Source, null);
    }

    private void createRecallConfirmedRecord(Recall1Source recall1Source, RecallSpire recallSpire) {
        Map<String, String> recordData = new HashMap<>();
        List<FieldImpacted> fieldsImpacted = new ArrayList<>();
        if (recall1Source != null) {
            if (recall1Source.getRecallId() != null) {
                recordData.put(ONESOURCE_RECALL, recall1Source.getRecallId());
            }
            if (recall1Source.getContractId() != null) {
                recordData.put(ONESOURCE_LOAN_CONTRACT, recall1Source.getContractId());
            }
            fieldsImpacted.add(buildFieldImpacted("Quantity",
                String.valueOf(recall1Source.getQuantity())));
            fieldsImpacted.add(buildFieldImpacted("Recall Date",
                String.valueOf(recall1Source.getRecallDate())));
            fieldsImpacted.add(buildFieldImpacted("Recall Due Date",
                String.valueOf(recall1Source.getRecallDueDate())));
        }
        if (recallSpire != null) {
            if (recallSpire.getRecallId() != null) {
                recordData.put(SPIRE_RECALL, String.valueOf(recallSpire.getRecallId()));
            }
            if (recallSpire.getRelatedPositionId() != null) {
                recordData.put(POSITION, String.valueOf(recallSpire.getRelatedPositionId()));
            }
        }
        recordBusinessEvent(GET_RECALL_DETAILS, RECALL_CONFIRMED, recordData, fieldsImpacted, RECALL);
    }

    private FieldImpacted buildFieldImpacted(String fieldName, String fieldValue) {
        return FieldImpacted.builder()
            .fieldSource(ONE_SOURCE_RECALL)
            .fieldName(fieldName)
            .fieldValue(fieldValue)
            .fieldExceptionType(INFORMATIONAL)
            .build();
    }

    /**
     * Update recallSpire processing status and save the recallSpire
     *
     * @param recallSpire RecallSpire
     * @param processingStatus Processing status
     * @return Persisted recallSpire with updated processing status
     */
    @Transactional
    public RecallSpire updateRecallProcessingStatus(@NonNull RecallSpire recallSpire,
        @NonNull ProcessingStatus processingStatus) {
        recallSpire.setProcessingStatus(processingStatus);
        return saveSpireRecall(recallSpire);
    }

    @Transactional
    public Recall1Source updateRecall1SourceProcessingStatus(@NonNull Recall1Source recall1Source,
        @NonNull ProcessingStatus processingStatus) {
        recall1Source.setProcessingStatus(processingStatus);
        return save1SourceRecall(recall1Source);
    }

    public RecallSpire saveSpireRecall(@NonNull RecallSpire recallSpire) {
        return recallService.save(recallSpire);
    }

    public Recall1Source save1SourceRecall(@NonNull Recall1Source recall1Source) {
        return recallService.save(recall1Source);
    }

    public RecallSpireInstruction saveSpireInstruction(RecallSpireInstruction instruction) {
        return recallService.save(instruction);
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

    private void recordIntegrationToolkitIssueEvent(String record, IntegrationSubProcess subProcess,
        IntegrationProcess process, Map<String, String> data) {
        cloudEventRecordService.getToolkitCloudEventId(record, subProcess, TECHNICAL_ISSUE_INTEGRATION_TOOLKIT)
            .ifPresentOrElse(
                cloudEventRecordService::updateTime,
                () -> recordToolkitTechnicalEvent(process, subProcess, data)
            );
    }

    private void recordToolkitTechnicalEvent(IntegrationProcess process,
        IntegrationSubProcess subProcess, Map<String, String> data) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(process);
        var recordRequest = eventBuilder.buildToolkitIssueRequest(subProcess, data);
        cloudEventRecordService.record(recordRequest);
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

    private void recordBusinessEvent(IntegrationSubProcess subProcess, RecordType recordType,
        Map<String, String> data, IntegrationProcess process) {
        recordBusinessEvent(subProcess, recordType, data, null, process);
    }

    private void recordBusinessEvent(IntegrationSubProcess subProcess, RecordType recordType,
        Map<String, String> data, List<FieldImpacted> fieldsImpacted, IntegrationProcess process) {
        try {
            var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(process);
            var recordRequest = eventBuilder.buildRequest(subProcess, recordType, data, fieldsImpacted);
            cloudEventRecordService.record(recordRequest);
        } catch (Exception e) {
            log.warn("Cloud event cannot be recorded for recordType:{}, process:{}, subProcess:{}",
                recordType, process, subProcess);
        }
    }
}
