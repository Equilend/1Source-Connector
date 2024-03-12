package com.intellecteu.onesource.integration.services.systemevent;

import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.BACKOFFICE_RERATE;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.RECONCILE_RERATE_DISCREPANCIES_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.RERATE_DISCREPANCIES;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.APPROVE_EXCEPTION_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.CREATED_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.GET_RERATE_EXCEPTION_1SOURCE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.MATCHED_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.POST_RERATE_EXCEPTION_1SOURCE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.UNMATCHED_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.APPROVE_EXCEPTION_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.CREATED_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.GET_RERATE_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.MATCHED_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.POST_RERATE_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.UNMATCHED_RERATE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RERATE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.MATCH_LOAN_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.PROCESS_RERATE_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.VALIDATE_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_MATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_PENDING_APPROVAL;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_TRADE_CREATED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.model.ProcessExceptionDetails;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RelatedObject;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Component
public class RerateCloudEventBuilder extends IntegrationCloudEventBuilder {

    public RerateCloudEventBuilder(
        @Value("${cloudevents.specversion}") String specVersion,
        @Value("${integration-toolkit.uri}") String integrationUri) {
        super(specVersion, integrationUri);
    }

    @Override
    public IntegrationProcess getVersion() {
        return RERATE;
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException exception,
        IntegrationSubProcess subProcess, String recorded) {
        return switch (subProcess) {
            case POST_RERATE_PROPOSAL -> createPostRerateExceptionCloudRequest(exception, subProcess, recorded);
            case GET_RERATE_PROPOSAL -> createGetRerateExceptionCloudRequest(exception, subProcess, recorded);
            default -> null;
        };
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(String record, HttpStatusCodeException e,
        IntegrationSubProcess subProcess, String related) {
        return switch (subProcess) {
            case APPROVE_RERATE_PROPOSAL -> createApproveRerateExceptionCloudRequest(record, e, subProcess, related);
            default -> null;
        };
    }

    private CloudEventBuildRequest createPostRerateExceptionCloudRequest(HttpStatusCodeException exception,
        IntegrationSubProcess subProcess, String recorded) {
        String dataMessage = format(POST_RERATE_EXCEPTION_1SOURCE_MSG, recorded, exception.getStatusText());
        return createRecordRequest(
            TECHNICAL_EXCEPTION_1SOURCE,
            format(POST_RERATE_EXCEPTION_1SOURCE, recorded),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(RelatedObject.notApplicable()))
        );
    }

    private CloudEventBuildRequest createGetRerateExceptionCloudRequest(HttpStatusCodeException exception,
        IntegrationSubProcess subProcess, String recorded) {
        String dataMessage = format(GET_RERATE_EXCEPTION_1SOURCE_MSG, recorded, exception.getStatusText());
        return createRecordRequest(
            TECHNICAL_EXCEPTION_1SOURCE,
            format(GET_RERATE_EXCEPTION_1SOURCE, recorded),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(RelatedObject.notApplicable()))
        );
    }

    private CloudEventBuildRequest createApproveRerateExceptionCloudRequest(String recorded, HttpStatusCodeException exception,
        IntegrationSubProcess subProcess, String related) {
        String dataMessage = format(APPROVE_EXCEPTION_RERATE_MSG, recorded, related, exception.getStatusText());
        return createRecordRequest(
            TECHNICAL_EXCEPTION_1SOURCE,
            format(APPROVE_EXCEPTION_RERATE, related),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(RelatedObject.notApplicable()))
        );
    }

    @Override
    public CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related,
        List<ProcessExceptionDetails> exceptionData) {
        return switch (recordType) {
            case RERATE_PROPOSAL_DISCREPANCIES -> reratetDiscrepancies(recorded, recordType,
                related, exceptionData);
            default -> null;
        };
    }

    private CloudEventBuildRequest reratetDiscrepancies(String recorded, RecordType recordType, String related,
        List<ProcessExceptionDetails> exceptionData) {
        final String formattedExceptions = exceptionData.stream()
            .map(d -> "- " + d.getFieldValue())
            .collect(Collectors.joining("\n"));
        String dataMessage = format(RECONCILE_RERATE_DISCREPANCIES_MSG, recorded, related,
            formattedExceptions);
        List<FieldImpacted> fieldImpacteds = exceptionData.stream().map(
                ed -> new FieldImpacted(ed.getSource(), ed.getFieldName(), ed.getFieldValue(), ed.getFieldExceptionType()))
            .collect(
                Collectors.toList());
        return createRecordRequest(
            recordType,
            format(RERATE_DISCREPANCIES, related),
            RERATE,
            VALIDATE_RERATE_PROPOSAL,
            createEventData(dataMessage, getRerateRelatedToPosition(recorded, related), fieldImpacteds)
        );
    }

    private List<RelatedObject> getRerateRelatedToPosition(String recorded, String related) {
        var related1SourceRerate = new RelatedObject(recorded, ONESOURCE_RERATE);
        var relatedBackOfficeRerate = new RelatedObject(related, BACKOFFICE_RERATE);
        return List.of(related1SourceRerate, relatedBackOfficeRerate);
    }

    @Override
    public CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related) {
        return switch (recordType) {
            case RERATE_TRADE_CREATED -> createCreatedRerateRecordRequest(recorded);
            case RERATE_PROPOSAL_MATCHED -> createMatchedRerateRecordRequest(recorded, related);
            case RERATE_PROPOSAL_UNMATCHED -> createUnMatchedRerateRecordRequest(recorded);
            case RERATE_PROPOSAL_PENDING_APPROVAL -> createMatchedAndPendingApprovalRecordRequest(recorded, related);
            default -> null;
        };
    }


    /**
     * @param recorded - onesource rerate id
     * @param related - backoffice rerate id
     * @return CloudEventBuildRequest build request object
     */
    private CloudEventBuildRequest createMatchedRerateRecordRequest(String recorded, String related) {
        String dataMessage = format(MATCHED_RERATE_MSG, recorded, related);
        return createRecordRequest(
            RERATE_PROPOSAL_MATCHED,
            format(MATCHED_RERATE, related),
            RERATE,
            PROCESS_RERATE_PENDING_CONFIRMATION,
            createEventData(dataMessage, List.of(RelatedObject.notApplicable()))
        );
    }

    /**
     * @param recorded - onesource rerate id
     * @param related - backoffice rerate tradeId
     */
    private CloudEventBuildRequest createMatchedAndPendingApprovalRecordRequest(String recorded, String related) {
        String dataMessage = format(MATCHED_RERATE_MSG, recorded, related);
        return createRecordRequest(
            RERATE_PROPOSAL_PENDING_APPROVAL,
            format(MATCHED_RERATE, related),
            RERATE,
            MATCH_LOAN_RERATE_PROPOSAL,
            createEventData(dataMessage, List.of(RelatedObject.notApplicable()))
        );
    }

    /**
     * @param recorded - backoffice rerate tradeId
     */
    private CloudEventBuildRequest createCreatedRerateRecordRequest(String recorded) {
        String dataMessage = format(CREATED_RERATE_MSG, recorded);
        return createRecordRequest(
            RERATE_TRADE_CREATED,
            format(CREATED_RERATE, recorded),
            RERATE,
            PROCESS_RERATE_PENDING_CONFIRMATION,
            createEventData(dataMessage, List.of(RelatedObject.notApplicable()))
        );
    }

    /**
     * @param recorded - onesource rerate id
     * @return CloudEventBuildRequest build request object
     */
    private CloudEventBuildRequest createUnMatchedRerateRecordRequest(String recorded) {
        String dataMessage = format(UNMATCHED_RERATE_MSG, recorded);
        return createRecordRequest(
            RERATE_PROPOSAL_UNMATCHED,
            format(UNMATCHED_RERATE, recorded),
            RERATE,
            MATCH_LOAN_RERATE_PROPOSAL,
            createEventData(dataMessage, List.of(RelatedObject.notApplicable()))
        );
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException exception,
        IntegrationSubProcess subProcess) {
        return null;
    }
}
