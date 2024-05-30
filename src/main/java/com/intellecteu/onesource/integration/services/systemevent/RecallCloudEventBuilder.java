package com.intellecteu.onesource.integration.services.systemevent;

import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_CONTRACT;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_LOAN_CONTRACT;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_RECALL;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.POSITION;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.SPIRE_RECALL;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.SPIRE_RECALL_INSTRUCTION;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.DataMsg.GET_RECALL_DETAILS_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.DataMsg.PROCESS_SPIRE_RECALL_INSTR_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.DataMsg.RECALL_CANCEL_SUBMITTED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.DataMsg.RECALL_CONFIRMED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.DataMsg.RECALL_SUBMITTED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.DataMsg.SPIRE_RECALL_CANCELLATION_INSTR_ISSUE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.DataMsg.SPIRE_RECALL_CANCELLATION_INSTR_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.Subject.GET_RECALL_DETAILS_SUBJECT;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.Subject.PROCESS_SPIRE_RECALL_INSTR_SUBJECT;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.Subject.RECALL_CANCEL_SUBMITTED_SUBJECT;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.Subject.RECALL_CONFIRMED_SUBJECT;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.Subject.RECALL_CONFIRMED_SUBJECT_NO_POSITION;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.Subject.RECALL_SUBMITTED_SUBJECT;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.Subject.SPIRE_RECALL_CANCELLATION_INSTR_ISSUE_SUBJECT;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.Subject.SPIRE_RECALL_CANCELLATION_INSTR_SUBJECT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RECALL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_RECALL_DETAILS;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_SPIRE_RECALL_CANCELLATION_INSTRUCTION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_SPIRE_RECALL_INSTRUCTION;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_ISSUE_INTEGRATION_TOOLKIT;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.model.ProcessExceptionDetails;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RelatedObject;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Component
public class RecallCloudEventBuilder extends IntegrationCloudEventBuilder {

    public RecallCloudEventBuilder(
        @Value("${cloudevents.specversion}") String specVersion,
        @Value("${cloudevents.source}") String integrationUri) {
        super(specVersion, integrationUri);
    }

    @Override
    public IntegrationProcess getVersion() {
        return RECALL;
    }

    @Override
    public CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related,
        List<ProcessExceptionDetails> exceptionData) {
        return null;
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException e, IntegrationSubProcess subProcess,
        String record) {
        return buildExceptionRequest(record, e, subProcess, null);
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(String record, HttpStatusCodeException e,
        IntegrationSubProcess subProcess, String related) {
        return switch (subProcess) {
            case GET_RECALL_DETAILS -> getRecallDetailsRequest(record, e, subProcess);
            case PROCESS_SPIRE_RECALL_INSTRUCTION -> processSpireRecallInstructionExceptionRequest(record, e);
            case PROCESS_SPIRE_RECALL_CANCELLATION_INSTRUCTION -> spireRecallCancellation(record, e, related);
            default -> null;
        };
    }

    @Override
    public CloudEventBuildRequest buildRequest(String record, RecordType recordType, String related) {
        return switch (recordType) {
            case RECALL_SUBMITTED -> recallSubmitted(record, recordType, related);
            default -> null;
        };
    }

    @Override
    public CloudEventBuildRequest buildRequest(IntegrationSubProcess subProcess, RecordType recordType,
        Map<String, String> data, List<FieldImpacted> fieldsImpacted) {
        return switch (recordType) {
            case RECALL_CONFIRMED -> recallConfirmed(data, recordType, fieldsImpacted);
            case RECALL_CANCEL_SUBMITTED -> recallCancelSubmitted(data, recordType);
            default -> null;
        };
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException exception,
        IntegrationSubProcess subProcess) {
        return null;
    }

    @Override
    public CloudEventBuildRequest buildToolkitIssueRequest(String recorded, IntegrationSubProcess subProcess) {
        return null;
    }

    @Override
    public CloudEventBuildRequest buildToolkitIssueRequest(IntegrationSubProcess subProcess, Map<String, String> data) {
        return switch (subProcess) {
            case PROCESS_SPIRE_RECALL_CANCELLATION_INSTRUCTION -> recallCancellationIssueRequest(subProcess, data);
            default -> null;
        };
    }

    private CloudEventBuildRequest recallCancellationIssueRequest(IntegrationSubProcess subProcess,
        Map<String, String> data) {
        String recallSpireInstructionId = data.get(SPIRE_RECALL_INSTRUCTION);
        String recallSpireId = data.get(SPIRE_RECALL);
        String spirePositionId = data.get(POSITION);
        String contractId = data.get(ONESOURCE_LOAN_CONTRACT);
        String subject = format(SPIRE_RECALL_CANCELLATION_INSTR_ISSUE_SUBJECT, recallSpireId, spirePositionId);
        String message = format(SPIRE_RECALL_CANCELLATION_INSTR_ISSUE_MSG, recallSpireInstructionId);
        List<RelatedObject> relatedObjects = new ArrayList<>();
        if (recallSpireInstructionId != null) {
            relatedObjects.add(new RelatedObject(recallSpireInstructionId, SPIRE_RECALL_INSTRUCTION));
        }
        if (recallSpireId != null) {
            relatedObjects.add(new RelatedObject(recallSpireId, SPIRE_RECALL));
        }
        if (spirePositionId != null) {
            relatedObjects.add(new RelatedObject(spirePositionId, POSITION));
        }
        if (contractId != null) {
            relatedObjects.add(new RelatedObject(contractId, ONESOURCE_LOAN_CONTRACT));
        }
        return createRecordRequest(
            TECHNICAL_ISSUE_INTEGRATION_TOOLKIT,
            subject,
            RECALL,
            subProcess,
            createEventData(message, relatedObjects)
        );
    }

    private CloudEventBuildRequest getRecallDetailsRequest(String record, HttpStatusCodeException e,
        IntegrationSubProcess subProcess) {
        String message = format(GET_RECALL_DETAILS_MSG, record, e.getStatusText());
        return createRecordRequest(
            RecordType.TECHNICAL_EXCEPTION_1SOURCE,
            format(GET_RECALL_DETAILS_SUBJECT, record),
            RECALL,
            subProcess,
            createEventData(message, List.of(new RelatedObject(record, ONESOURCE_RECALL)))
        );
    }

    private CloudEventBuildRequest recallConfirmed(Map<String, String> data, RecordType recordType,
        List<FieldImpacted> fieldsImpacted) {
        String recall1SourceId = data.get(ONESOURCE_RECALL);
        String contractId = data.get(ONESOURCE_LOAN_CONTRACT);
        String spireRecallId = data.get(SPIRE_RECALL);
        String spirePositionId = data.get(POSITION);
        String subject;
        if (spirePositionId == null) {
            subject = format(RECALL_CONFIRMED_SUBJECT_NO_POSITION, recall1SourceId, contractId);
        } else {
            subject = format(RECALL_CONFIRMED_SUBJECT, spireRecallId, spirePositionId);
        }
        String message = format(RECALL_CONFIRMED_MSG, recall1SourceId);
        return createRecordRequest(
            recordType,
            subject,
            RECALL,
            GET_RECALL_DETAILS,
            createEventData(
                message,
                getRecall1SourceRelatedToRecallSpire(recall1SourceId, spireRecallId, spirePositionId, contractId),
                fieldsImpacted)
        );
    }

    private CloudEventBuildRequest recallCancelSubmitted(Map<String, String> data, RecordType recordType) {
        String instructionId = data.get(SPIRE_RECALL_INSTRUCTION);
        String recall1SourceId = data.get(ONESOURCE_RECALL);
        String contractId = data.get(ONESOURCE_LOAN_CONTRACT);
        String spireRecallId = data.get(SPIRE_RECALL);
        String spirePositionId = data.get(POSITION);
        String subject = format(RECALL_CANCEL_SUBMITTED_SUBJECT, spireRecallId, spirePositionId);
        String message = format(RECALL_CANCEL_SUBMITTED_MSG, recall1SourceId, spireRecallId);
        List<RelatedObject> relatedObjects = new ArrayList<>();
        if (instructionId != null) {
            relatedObjects.add(new RelatedObject(instructionId, SPIRE_RECALL_INSTRUCTION));
        }
        relatedObjects.addAll(getRecall1SourceRelatedToRecallSpire(
            recall1SourceId, spireRecallId, spirePositionId, contractId));
        return createRecordRequest(
            recordType,
            subject,
            RECALL,
            PROCESS_SPIRE_RECALL_CANCELLATION_INSTRUCTION,
            createEventData(message, relatedObjects)
        );
    }

    /*
     * Expected relatedSequence in format: "positionId,contractId"
     */
    private CloudEventBuildRequest recallSubmitted(String record, RecordType recordType, String relatedSequence) {
        String[] positionAndContractIds = relatedSequence.split(",");
        String relatedPositionId = positionAndContractIds[0];
        String message = format(RECALL_SUBMITTED_MSG, record);
        String subject = format(RECALL_SUBMITTED_SUBJECT, record, relatedPositionId);
        return createRecordRequest(
            recordType,
            subject,
            RECALL,
            PROCESS_SPIRE_RECALL_INSTRUCTION,
            createEventData(message, getRecallRelatedToPositionAndContract(record, relatedSequence))
        );
    }

    private CloudEventBuildRequest processSpireRecallInstructionExceptionRequest(String record,
        HttpStatusCodeException e) {
        String message = format(PROCESS_SPIRE_RECALL_INSTR_MSG, record, e.getStatusText());
        return createRecordRequest(
            RecordType.TECHNICAL_EXCEPTION_1SOURCE,
            format(PROCESS_SPIRE_RECALL_INSTR_SUBJECT, record),
            RECALL,
            PROCESS_SPIRE_RECALL_INSTRUCTION,
            createEventData(message, List.of(new RelatedObject(record, SPIRE_RECALL)))
        );
    }

    /*
     * related sequence expected as '1sourceRecall,spireRecallId,relatedPositionId,relatedContractId'
     */
    private CloudEventBuildRequest spireRecallCancellation(String record, HttpStatusCodeException e, String related) {
        String[] relatedSequence = related.split(",");
        String oneSourceRecallId;
        String spireRecallId;
        String positionId;
        String contractId;
        try {
            oneSourceRecallId = relatedSequence[0];
            spireRecallId = relatedSequence[1];
            positionId = relatedSequence[2];
            contractId = relatedSequence[3];
        } catch (Exception exception) {
            oneSourceRecallId = "";
            spireRecallId = "";
            positionId = "";
            contractId = "";
        }
        List<RelatedObject> relatedObjects = new ArrayList<>();
        if (record != null) {
            relatedObjects.add(new RelatedObject(record, SPIRE_RECALL_INSTRUCTION));
        }
        if (oneSourceRecallId != null) {
            relatedObjects.add(new RelatedObject(oneSourceRecallId, ONESOURCE_RECALL));
        }
        if (spireRecallId != null) {
            relatedObjects.add(new RelatedObject(spireRecallId, SPIRE_RECALL));
        }
        if (positionId != null) {
            relatedObjects.add(new RelatedObject(positionId, POSITION));
        }
        if (contractId != null) {
            relatedObjects.add(new RelatedObject(contractId, ONESOURCE_CONTRACT));
        }
        String message = format(SPIRE_RECALL_CANCELLATION_INSTR_MSG, spireRecallId, e.getStatusText());
        return createRecordRequest(
            RecordType.TECHNICAL_EXCEPTION_1SOURCE,
            format(SPIRE_RECALL_CANCELLATION_INSTR_SUBJECT, spireRecallId, positionId),
            RECALL,
            PROCESS_SPIRE_RECALL_CANCELLATION_INSTRUCTION,
            createEventData(message, relatedObjects)
        );
    }
}
