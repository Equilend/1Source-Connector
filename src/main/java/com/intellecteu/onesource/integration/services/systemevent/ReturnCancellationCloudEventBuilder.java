package com.intellecteu.onesource.integration.services.systemevent;

import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_LOAN_CONTRACT;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_RETURN;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.POSITION;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.SPIRE_TRADE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.CAPTURE_RETURN_TRADE_CANCELLATION_TE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.PROCESS_RETURN_TRADE_CANCELED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.PROCESS_RETURN_TRADE_CANCELED_TE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.CAPTURE_RETURN_TRADE_CANCELLATION_TE_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.PROCESS_RETURN_TRADE_CANCELED_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.PROCESS_RETURN_TRADE_CANCELED_TE_SBJ;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RETURN_CANCELLATION;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.toStringNullSafe;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RelatedObject;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Component
public class ReturnCancellationCloudEventBuilder extends IntegrationCloudEventBuilder {

    public static final String TRADE_ID = "tradeId";
    public static final String CONTRACT_ID = "contractId";
    public static final String POSITION_ID = "positionId";
    public static final String RETURN_ID = "returnId";
    public static final String RESOURCE_URI = "resourceURI";
    public static final String HTTP_STATUS_TEXT = "httpStatusText";

    public static class DataBuilder {

        private Map<String, String> data = new HashMap<>();

        public DataBuilder putTradeId(Object value) {
            data.put(TRADE_ID, toStringNullSafe(value));
            return this;
        }

        public DataBuilder putContractId(Object value) {
            data.put(CONTRACT_ID, toStringNullSafe(value));
            return this;
        }

        public DataBuilder putPositionId(Object value) {
            data.put(POSITION_ID, toStringNullSafe(value));
            return this;
        }

        public DataBuilder putReturnId(Object value) {
            data.put(RETURN_ID, toStringNullSafe(value));
            return this;
        }

        public DataBuilder putResourceURI(Object value) {
            data.put(RESOURCE_URI, toStringNullSafe(value));
            return this;
        }

        public DataBuilder putHttpStatusText(Object value) {
            data.put(HTTP_STATUS_TEXT, toStringNullSafe(value));
            return this;
        }

        public Map<String, String> getData() {
            return data;
        }
    }

    @Autowired
    public ReturnCancellationCloudEventBuilder(@Value("${cloudevents.specversion}") String specVersion,
        @Value("${cloudevents.source}") String integrationUri) {
        super(specVersion, integrationUri);
    }

    @Override
    public IntegrationProcess getVersion() {
        return RETURN_CANCELLATION;
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
            case CAPTURE_RETURN_TRADE_CANCELLATION: {
                return switch (recordType) {
                    case TECHNICAL_EXCEPTION_SPIRE ->
                        createReturnCancellationTechnicalExceptionCR(subProcess, recordType, data);
                    default -> null;
                };
            }
            case PROCESS_RETURN_TRADE_CANCELED: {
                return switch (recordType) {
                    case RETURN_TRADE_CANCELED -> createReturnCancelledCR(subProcess, recordType, data);
                    case TECHNICAL_EXCEPTION_1SOURCE ->
                        createReturnCancelledTechnicalExceptionCR(subProcess, recordType, data);
                    default -> null;
                };
            }
        }
        return null;
    }

    private CloudEventBuildRequest createReturnCancellationTechnicalExceptionCR(IntegrationSubProcess subProcess,
        RecordType recordType,
        Map<String, String> data) {
        String dataMessage = format(CAPTURE_RETURN_TRADE_CANCELLATION_TE_MSG, data.get(HTTP_STATUS_TEXT));
        return createRecordRequest(
            recordType,
            format(CAPTURE_RETURN_TRADE_CANCELLATION_TE_SBJ, LocalDateTime.now()),
            RETURN_CANCELLATION,
            subProcess,
            createEventData(dataMessage, List.of()));
    }

    private CloudEventBuildRequest createReturnCancelledCR(IntegrationSubProcess subProcess,
        RecordType recordType,
        Map<String, String> data) {
        String dataMessage = format(PROCESS_RETURN_TRADE_CANCELED_MSG, data.get(TRADE_ID));
        return createRecordRequest(
            recordType,
            format(PROCESS_RETURN_TRADE_CANCELED_SBJ, data.get(TRADE_ID)),
            RETURN_CANCELLATION,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT))));
    }

    private CloudEventBuildRequest createReturnCancelledTechnicalExceptionCR(IntegrationSubProcess subProcess,
        RecordType recordType,
        Map<String, String> data) {
        String dataMessage = format(PROCESS_RETURN_TRADE_CANCELED_TE_MSG, data.get(RETURN_ID), data.get(TRADE_ID),
            data.get(HTTP_STATUS_TEXT));
        return createRecordRequest(
            recordType,
            format(PROCESS_RETURN_TRADE_CANCELED_TE_SBJ, data.get(TRADE_ID)),
            RETURN_CANCELLATION,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RETURN_ID), ONESOURCE_RETURN),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT))));
    }
}
