package com.intellecteu.onesource.integration.services.systemevent;

import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_RECALL;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.SPIRE_RECALL;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.DataMsg.GET_RECALL_DETAILS_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.DataMsg.PROCESS_SPIRE_RECALL_INSTR_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.DataMsg.RECALL_SUBMITTED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.Subject.GET_RECALL_DETAILS_SUBJECT;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.Subject.PROCESS_SPIRE_RECALL_INSTR_SUBJECT;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Recall.Subject.RECALL_SUBMITTED_SUBJECT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RECALL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_SPIRE_RECALL_INSTRUCTION;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.model.ProcessExceptionDetails;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RelatedObject;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import java.util.List;
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
    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException exception,
        IntegrationSubProcess subProcess) {
        return null;
    }

    @Override
    public CloudEventBuildRequest buildToolkitIssueRequest(String recorded, IntegrationSubProcess subProcess) {
        return null;
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
}
