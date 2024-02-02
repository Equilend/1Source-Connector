package com.intellecteu.onesource.integration.services.systemevent;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.DataMsg.GET_EVENTS_EXCEPTION_1SOURCE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.DataMsg.GET_TRADE_EVENTS_PENDING_CONFIRMATION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.Subject.GET_EVENTS_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.Subject.GET_TRADE_EVENTS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.GENERIC;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_SPIRE;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.dto.record.RelatedObject;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
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
    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException exception, IntegrationSubProcess subProcess) {
        return switch (subProcess) {
            case GET_1SOURCE_EVENTS -> createOneSourceExceptionCloudRequest(subProcess, exception);
            case GET_TRADE_EVENTS_PENDING_CONFIRMATION ->
                createTradeEventsPendingConfirmationCloudRequest(subProcess, exception);
            default -> null;
        };

    }

    private CloudEventBuildRequest createOneSourceExceptionCloudRequest(IntegrationSubProcess subProcess,
        HttpStatusCodeException exception) {
        var exceptionMessage = String.format(GET_EVENTS_EXCEPTION_1SOURCE_MSG, exception.getStatusText());
        return createRecordRequest(
            TECHNICAL_EXCEPTION_1SOURCE,
            format(GET_EVENTS_EXCEPTION_1SOURCE, LocalDate.now()),
            GENERIC,
            subProcess,
            createEventData(exceptionMessage, List.of(RelatedObject.notApplicable()))
        );
    }

    private CloudEventBuildRequest createTradeEventsPendingConfirmationCloudRequest(IntegrationSubProcess subProcess,
        HttpStatusCodeException exception) {
        var exceptionMessage = String.format(GET_TRADE_EVENTS_PENDING_CONFIRMATION_MSG, exception.getStatusText());
        return createRecordRequest(
            TECHNICAL_EXCEPTION_SPIRE,
            format(GET_TRADE_EVENTS_PENDING_CONFIRMATION, LocalDate.now()),
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
