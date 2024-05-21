package com.intellecteu.onesource.integration.services.systemevent;

import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_LOAN_CONTRACT;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.POSITION;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.SPIRE_TRADE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.GET_NEW_RETURN_PENDING_CONFIRMATION_TE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.POST_RETURN_PENDING_CONFIRMATION_TE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.POST_RETURN_SUBMITTED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.GET_NEW_RETURN_PENDING_CONFIRMATION_TE_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.POST_RETURN_PENDING_CONFIRMATION_TE_SBJ;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RETURN;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RelatedObject;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Component
public class ReturnCloudEventBuilder extends IntegrationCloudEventBuilder {

    public static final String TRADE_ID = "tradeId";
    public static final String CONTRACT_ID = "contractId";
    public static final String POSITION_ID = "positionId";
    public static final String RESOURCE_URI = "resourceURI";
    public static final String HTTP_STATUS_TEXT = "httpStatusText";

    @Autowired
    public ReturnCloudEventBuilder(@Value("${cloudevents.specversion}") String specVersion,
        @Value("${cloudevents.source}") String integrationUri) {
        super(specVersion, integrationUri);
    }

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
            case POST_RETURN: {
                return switch (recordType) {
                    case TECHNICAL_EXCEPTION_1SOURCE ->
                        createPostReturnTechnicalExceptionCR(subProcess, recordType, data);
                    case RETURN_TRADE_SUBMITTED ->
                        createReturnSubmittedCR(subProcess, recordType, data, fieldImpacteds);
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

    private CloudEventBuildRequest createPostReturnTechnicalExceptionCR(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(POST_RETURN_PENDING_CONFIRMATION_TE_MSG, data.get(TRADE_ID),
            data.get(HTTP_STATUS_TEXT));
        return createRecordRequest(
            recordType,
            format(POST_RETURN_PENDING_CONFIRMATION_TE_SBJ, data.get(TRADE_ID)),
            RETURN,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE)))
        );
    }

    private CloudEventBuildRequest createReturnSubmittedCR(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data, List<FieldImpacted> fieldsImpacted) {
        String dataMessage = format(POST_RETURN_SUBMITTED_MSG, data.get(TRADE_ID));
        return createRecordRequest(
            recordType,
            format(POST_RETURN_SUBMITTED_MSG, data.get(TRADE_ID)),
            RETURN,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)))
        );
    }
}
