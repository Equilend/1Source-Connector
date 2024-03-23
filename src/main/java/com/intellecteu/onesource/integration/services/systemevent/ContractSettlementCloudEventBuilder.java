package com.intellecteu.onesource.integration.services.systemevent;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractSettlement.DataMsg.CAPTURE_POSITION_SETTLEMENT_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractSettlement.DataMsg.LOAN_CONTRACT_SETTLED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractSettlement.DataMsg.POSITION_SETTLED_SUBMITTED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractSettlement.DataMsg.POST_LOAN_CONTRACT_UPDATE_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractSettlement.Subject.CAPTURE_POSITION_SETTLEMENT_EXCEPTION_SPIRE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractSettlement.Subject.LOAN_CONTRACT_SETTLED;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractSettlement.Subject.POSITION_SETTLED_SUBMITTED_SUBJECT;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractSettlement.Subject.POST_LOAN_CONTRACT_UPDATE_EXCEPTION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_SETTLEMENT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_SETTLED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.UPDATE_LOAN_CONTRACT_SETTL_STATUS;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_SPIRE;
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
public class ContractSettlementCloudEventBuilder extends IntegrationCloudEventBuilder {

    public ContractSettlementCloudEventBuilder(
        @Value("${cloudevents.specversion}") String specVersion,
        @Value("${integration-toolkit-uri}") String integrationUri) {
        super(specVersion, integrationUri);
    }

    @Override
    public IntegrationProcess getVersion() {
        return CONTRACT_SETTLEMENT;
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(String record, HttpStatusCodeException e,
        IntegrationSubProcess subProcess, String related) {
        return switch (subProcess) {
            case POST_LOAN_CONTRACT_UPDATE -> loanContractUpdate(record, e, subProcess, related);
            case CAPTURE_POSITION_SETTLEMENT -> buildCapturePositionRequest(e, subProcess);
            default -> null;
        };
    }

    @Override
    public CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related) {
        return switch (recordType) {
            case LOAN_CONTRACT_SETTLED -> loanContractSettled(recorded, recordType, related);
            case POSITION_SETTLED_SUBMITTED -> positionSettledSubmitted(recorded, recordType, related);
            default -> null;
        };
    }

    private CloudEventBuildRequest loanContractUpdate(String record, HttpStatusCodeException exception,
        IntegrationSubProcess subProcess, String related) {
        String message = format(POST_LOAN_CONTRACT_UPDATE_EXCEPTION_MSG, record, related, exception.getStatusText());
        return createRecordRequest(
            TECHNICAL_EXCEPTION_1SOURCE,
            format(POST_LOAN_CONTRACT_UPDATE_EXCEPTION, related),
            CONTRACT_SETTLEMENT,
            subProcess,
            createEventData(message, getLoanProposalRelatedToPosition(record, related))
        );
    }

    private CloudEventBuildRequest buildCapturePositionRequest(HttpStatusCodeException exception,
        IntegrationSubProcess subProcess) {
        String message = format(CAPTURE_POSITION_SETTLEMENT_EXCEPTION_MSG, exception.getStatusText());
        return createRecordRequest(
            TECHNICAL_EXCEPTION_SPIRE,
            format(CAPTURE_POSITION_SETTLEMENT_EXCEPTION_SPIRE, LocalDateTime.now()),
            CONTRACT_SETTLEMENT,
            subProcess,
            createEventData(message, List.of(RelatedObject.notApplicable()))
        );
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException e, IntegrationSubProcess subProcess) {
        return buildExceptionRequest(null, e, subProcess, null);
    }

    @Override
    public CloudEventBuildRequest buildToolkitIssueRequest(String recorded, IntegrationSubProcess subProcess) {
        return null;
    }

    private CloudEventBuildRequest loanContractSettled(String recorded, RecordType recordType, String related) {
        String dataMessage = format(LOAN_CONTRACT_SETTLED_MSG, recorded);
        return createRecordRequest(
            recordType,
            format(LOAN_CONTRACT_SETTLED, related),
            CONTRACT_SETTLEMENT,
            GET_LOAN_CONTRACT_SETTLED,
            createEventData(dataMessage, getLoanProposalRelatedToPosition(recorded, related))
        );
    }

    private CloudEventBuildRequest positionSettledSubmitted(String recorded, RecordType recordType, String related) {
        String dataMessage = format(POSITION_SETTLED_SUBMITTED_MSG, related, recorded);
        return createRecordRequest(
            recordType,
            format(POSITION_SETTLED_SUBMITTED_SUBJECT, related),
            CONTRACT_SETTLEMENT,
            UPDATE_LOAN_CONTRACT_SETTL_STATUS,
            createEventData(dataMessage, getLoanProposalRelatedToPosition(recorded, related))
        );
    }

}
