package com.intellecteu.onesource.integration.services.systemevent;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractCancellation.DataMsg.CAPTURE_POSITION_CANCELED_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractCancellation.DataMsg.INSTRUCT_CONTRACT_CANCEL_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractCancellation.DataMsg.LOAN_CONTRACT_CANCELED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractCancellation.DataMsg.LOAN_CONTRACT_CANCEL_PENDING_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractCancellation.DataMsg.POSITION_CANCEL_SUBMITTED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractCancellation.DataMsg.PROCESS_LOAN_CONTRACT_PENDING_CANCELLATION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractCancellation.Subject.CAPTURE_POSITION_CANCELED_EXCEPTION_SUBJECT;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractCancellation.Subject.INSTRUCT_CONTRACT_CANCEL_SUBJECT;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractCancellation.Subject.LOAN_CONTRACT_CANCELED_SUBJECT;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractCancellation.Subject.LOAN_CONTRACT_CANCEL_PENDING_SUBJECT;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractCancellation.Subject.POSITION_CANCEL_SUBMITTED_SUBJECT;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractCancellation.Subject.PROCESS_LOAN_CONTRACT_PENDING_CANCELLATION_SUBJECT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_CANCELLATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.CAPTURE_POSITION_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.INSTRUCT_LOAN_CONTRACT_CANCELLATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.LOAN_CONTRACT_PROPOSAL_CANCEL_PENDING;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_SPIRE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_ISSUE_INTEGRATION_TOOLKIT;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RelatedObject;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Component
public class ContractCancellationCloudEventBuilder extends IntegrationCloudEventBuilder {

    public ContractCancellationCloudEventBuilder(
        @Value("${cloudevents.specversion}") String specVersion,
        @Value("${cloudevents.source}") String integrationUri) {
        super(specVersion, integrationUri);
    }

    @Override
    public IntegrationProcess getVersion() {
        return CONTRACT_CANCELLATION;
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(String record, HttpStatusCodeException e,
        IntegrationSubProcess subProcess, String related) {
        return switch (subProcess) {
            case CAPTURE_POSITION_CANCELED ->  capturePositionCanceledExceptionRequest(e);
            case INSTRUCT_LOAN_CONTRACT_CANCELLATION ->  instructContractCancelExceptionRequest(record, e, related);
            default -> null;
        };
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException e, IntegrationSubProcess subProcess) {
        return buildExceptionRequest(null, e, subProcess, null);
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException e, IntegrationSubProcess subProcess,
        String recorded) {
        return buildExceptionRequest(recorded, e, subProcess, null);
    }

    @Override
    public CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related) {
        return switch (recordType) {
            case POSITION_CANCEL_SUBMITTED -> positionCancelSubmitted(recorded, recordType, related);
            case LOAN_CONTRACT_CANCELED -> loanContractCanceled(recorded, recordType, related);
            case LOAN_CONTRACT_CANCEL_PENDING -> loanContractCancelPending(recorded, recordType, related);
            default -> null;
        };
    }

    @Override
    public CloudEventBuildRequest buildToolkitIssueRequest(String recorded, IntegrationSubProcess subProcess) {
        return switch (subProcess) {
            case PROCESS_LOAN_CONTRACT_PENDING_CANCELLATION -> contractPendingCancellation(recorded, subProcess);
            default -> null;
        };
    }

    private CloudEventBuildRequest contractPendingCancellation(String recorded, IntegrationSubProcess subProcess) {
        String dataMessage = format(PROCESS_LOAN_CONTRACT_PENDING_CANCELLATION_MSG, recorded);
        return createRecordRequest(
            TECHNICAL_ISSUE_INTEGRATION_TOOLKIT,
            format(PROCESS_LOAN_CONTRACT_PENDING_CANCELLATION_SUBJECT, recorded),
            CONTRACT_CANCELLATION,
            subProcess,
            createEventData(dataMessage, getLoanContractRelated(recorded))
        );
    }

    private CloudEventBuildRequest positionCancelSubmitted(String recorded, RecordType recordType, String related) {
        String dataMessage = format(POSITION_CANCEL_SUBMITTED_MSG, related, recorded);
        return createRecordRequest(
            recordType,
            format(POSITION_CANCEL_SUBMITTED_SUBJECT, related),
            CONTRACT_CANCELLATION,
            CAPTURE_POSITION_CANCELED,
            createEventData(dataMessage, getLoanContractRelatedToPosition(recorded, related))
        );
    }

    private CloudEventBuildRequest loanContractCanceled(String recorded, RecordType recordType, String related) {
        String dataMessage = format(LOAN_CONTRACT_CANCELED_MSG, recorded, related);
        return createRecordRequest(
            recordType,
            format(LOAN_CONTRACT_CANCELED_SUBJECT, related),
            CONTRACT_CANCELLATION,
            GET_LOAN_CONTRACT_CANCELED,
            createEventData(dataMessage, getLoanContractRelatedToPosition(recorded, related))
        );
    }

    private CloudEventBuildRequest loanContractCancelPending(String recorded, RecordType recordType, String related) {
        String dataMessage = format(LOAN_CONTRACT_CANCEL_PENDING_MSG, recorded, related);
        return createRecordRequest(
            recordType,
            format(LOAN_CONTRACT_CANCEL_PENDING_SUBJECT, related),
            CONTRACT_CANCELLATION,
            LOAN_CONTRACT_PROPOSAL_CANCEL_PENDING,
            createEventData(dataMessage, getLoanContractRelatedToPosition(recorded, related))
        );
    }

    private CloudEventBuildRequest capturePositionCanceledExceptionRequest(HttpStatusCodeException e) {
        String dataMessage = format(CAPTURE_POSITION_CANCELED_EXCEPTION_MSG, e.getStatusText());
        return createRecordRequest(
            TECHNICAL_EXCEPTION_SPIRE,
            format(CAPTURE_POSITION_CANCELED_EXCEPTION_SUBJECT, LocalDateTime.now()),
            CONTRACT_CANCELLATION,
            CAPTURE_POSITION_CANCELED,
            createEventData(dataMessage, List.of(RelatedObject.notApplicable()))
        );
    }

    private CloudEventBuildRequest instructContractCancelExceptionRequest(String record, HttpStatusCodeException e,
        String related) {
        String dataMessage = format(INSTRUCT_CONTRACT_CANCEL_MSG, record, related, e.getStatusText());
        return createRecordRequest(
            TECHNICAL_EXCEPTION_1SOURCE,
            format(INSTRUCT_CONTRACT_CANCEL_SUBJECT, related),
            CONTRACT_CANCELLATION,
            INSTRUCT_LOAN_CONTRACT_CANCELLATION,
            createEventData(dataMessage, getLoanProposalRelatedToPosition(record, related))
        );
    }

}
