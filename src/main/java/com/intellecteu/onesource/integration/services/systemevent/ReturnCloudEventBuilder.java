package com.intellecteu.onesource.integration.services.systemevent;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.GET_NEW_RETURN_PENDING_CONFIRMATION_TE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.GET_NEW_RETURN_PENDING_CONFIRMATION_TE_SBJ;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RETURN;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReturnCloudEventBuilder extends IntegrationCloudEventBuilder {

    public static final String RETURN_ID = "returnId";
    public static final String TRADE_ID = "tradeId";
    public static final String CONTRACT_ID = "contractId";
    public static final String POSITION_ID = "positionId";
    public static final String RESOURCE_URI = "resourceURI";
    public static final String HTTP_STATUS_TEXT = "httpStatusText";

    @Override
    public IntegrationProcess getVersion() {
        return RETURN;
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException e, IntegrationSubProcess subProcess) {
        return null;
    }

    @Override
    public CloudEventBuildRequest buildToolkitIssueRequest(String recorded, IntegrationSubProcess subProcess) {
        return null;
    }

    @Override
    public CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related) {
        return null;
    }

    @Override
    public CloudEventBuildRequest buildRequest(IntegrationSubProcess subProcess, RecordType recordType,
        Map<String, String> data, List<FieldImpacted> fieldImpacteds) {
        switch (subProcess) {
            case GET_NEW_RETURN_PENDING_CONFIRMATION: {
                return switch (recordType) {
                    case TECHNICAL_EXCEPTION_SPIRE ->
                        createGetNewReturnTechnicalExceptionCR(subProcess, recordType, data);
                    default -> null;
                };
            }
        }
        return null;
    }

    private CloudEventBuildRequest createGetNewReturnTechnicalExceptionCR(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(GET_NEW_RETURN_PENDING_CONFIRMATION_TE_MSG, data.get(HTTP_STATUS_TEXT));
        return createRecordRequest(
            recordType,
            format(GET_NEW_RETURN_PENDING_CONFIRMATION_TE_SBJ, LocalDateTime.now()),
            RETURN,
            subProcess,
            createEventData(dataMessage, List.of())
        );
    }
}
