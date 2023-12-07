package com.intellecteu.onesource.integration.services.record;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.DataMsg.GET_EVENTS_EXCEPTION_1SOURCE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.Subject.GET_EVENTS_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.GENERIC;
import static com.intellecteu.onesource.integration.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.dto.record.RelatedObject;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Component
public class GenericRecordCloudEventBuilder extends IntegrationCloudEventBuilder {

    @Override
    public IntegrationProcess getVersion() {
        return GENERIC;
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException e, IntegrationSubProcess subProcess) {
        var exceptionMessage = String.format(GET_EVENTS_EXCEPTION_1SOURCE_MSG, e.getStatusText());
        return createRecordRequest(
            TECHNICAL_EXCEPTION_1SOURCE,
            format(GET_EVENTS_EXCEPTION_1SOURCE, LocalDate.now()),
            GENERIC,
            subProcess,
            createEventData(exceptionMessage, List.of(RelatedObject.notApplicable()))
        );
    }

    @Override
    public CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related) {
        return null;
    }
}
