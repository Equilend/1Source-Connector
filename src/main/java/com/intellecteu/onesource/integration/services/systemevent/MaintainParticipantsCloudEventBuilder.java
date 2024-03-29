package com.intellecteu.onesource.integration.services.systemevent;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.MaintainOnesourceParticipantsList.DataMsg.GET_PARTICIPANTS_1SOURCE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.MaintainOnesourceParticipantsList.Subject.GET_PARTICIPANTS_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.MAINTAIN_1SOURCE_PARTICIPANTS_LIST;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RelatedObject;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Component
public class MaintainParticipantsCloudEventBuilder extends IntegrationCloudEventBuilder {

    public MaintainParticipantsCloudEventBuilder(
        @Value("${cloudevents.specversion}") String specVersion,
        @Value("${integration-toolkit-uri}") String integrationUri) {
        super(specVersion, integrationUri);
    }

    @Override
    public IntegrationProcess getVersion() {
        return MAINTAIN_1SOURCE_PARTICIPANTS_LIST;
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException e, IntegrationSubProcess subProcess) {
        var exceptionMessage = String.format(GET_PARTICIPANTS_1SOURCE_MSG, e.getStatusText());
        return createRecordRequest(
            TECHNICAL_EXCEPTION_1SOURCE,
            format(GET_PARTICIPANTS_EXCEPTION_1SOURCE, LocalDate.now()),
            MAINTAIN_1SOURCE_PARTICIPANTS_LIST,
            subProcess,
            createEventData(exceptionMessage, List.of(RelatedObject.notApplicable()))
        );
    }

    @Override
    public CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related) {
        return null;
    }

    @Override
    public CloudEventBuildRequest buildToolkitIssueRequest(String recorded, IntegrationSubProcess subProcess) {
        return null;
    }
}
