package com.intellecteu.onesource.integration.services.systemevent;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.GET_RERATE_EXCEPTION_1SOURCE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.MATCHED_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.UNMATCHED_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.GET_RERATE_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.MATCHED_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.UNMATCHED_RERATE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RERATE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_MATCHED_RERATE_TRADE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.RERATE_PROPOSAL_UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static java.lang.String.format;

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
            case GET_RERATE_PROPOSAL -> createGetRerateExceptionCloudRequest(exception, subProcess, recorded);
            default -> null;
        };
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

    @Override
    public CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related) {
        return switch (recordType) {
            case RERATE_PROPOSAL_MATCHED_RERATE_TRADE -> createMatchedRerateRecordRequest(recorded, related);
            case RERATE_PROPOSAL_UNMATCHED -> createUnMatchedRerateRecordRequest(recorded);
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
            RERATE_PROPOSAL_MATCHED_RERATE_TRADE,
            format(MATCHED_RERATE, related),
            RERATE,
            GET_RERATE_PROPOSAL,
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
            GET_RERATE_PROPOSAL,
            createEventData(dataMessage, List.of(RelatedObject.notApplicable()))
        );
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException exception,
        IntegrationSubProcess subProcess) {
        return null;
    }
}
