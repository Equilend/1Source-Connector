package com.intellecteu.onesource.integration.services.systemevent;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractCancellation.DataMsg.POSITION_CANCEL_SUBMITTED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractCancellation.Subject.POSITION_CANCEL_SUBMITTED_SUBJECT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_CANCELLATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.CAPTURE_POSITION_CANCELED;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
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
        return null;
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
            default -> null;
        };
    }

    @Override
    public CloudEventBuildRequest buildToolkitIssueRequest(String recorded, IntegrationSubProcess subProcess) {
        return null;
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

}
