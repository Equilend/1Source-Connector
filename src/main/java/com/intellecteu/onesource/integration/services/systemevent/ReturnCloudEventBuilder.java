package com.intellecteu.onesource.integration.services.systemevent;

import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_LOAN_CONTRACT;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_RETURN;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.POSITION;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.SPIRE_TRADE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.ACKNOWLEDGE_RETURN_NEGATIVELY_NOT_AUTHORIZED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.ACKNOWLEDGE_RETURN_NEGATIVELY_TE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.ACKNOWLEDGE_RETURN_NEGATIVELY_TI_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.ACKNOWLEDGE_RETURN_POSITIVELY_TE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.CONFIRM_RETURN_TRADE_TE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.GET_NEW_RETURN_PENDING_CONFIRMATION_TE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.GET_RETURN_ACKNOWLEDGEMENT_DETAILS_TE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.GET_RETURN_EXCEPTION_1SOURCE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.POST_RETURN_PENDING_CONFIRMATION_TE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.POST_RETURN_SUBMITTED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.RECONCILE_RETURN_DISCREPANCIES_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.RETURN_MATCHED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.RETURN_NEGATIVELY_ACKNOWLEDGED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.RETURN_PENDING_ACKNOWLEDGEMENT_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.RETURN_POSITIVELY_ACKNOWLEDGED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.DataMsg.RETURN_UNMATCHED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.ACKNOWLEDGE_RETURN_NEGATIVELY_NOT_AUTHORIZED_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.ACKNOWLEDGE_RETURN_NEGATIVELY_TE_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.ACKNOWLEDGE_RETURN_NEGATIVELY_TI_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.ACKNOWLEDGE_RETURN_POSITIVELY_TE_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.CONFIRM_RETURN_TRADE_TE_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.GET_NEW_RETURN_PENDING_CONFIRMATION_TE_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.GET_RETURN_ACKNOWLEDGEMENT_DETAILS_TE_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.GET_RETURN_EXCEPTION_1SOURCE_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.POST_RETURN_PENDING_CONFIRMATION_TE_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.POST_RETURN_SUBMITTED_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.RECONCILE_RETURN_DISCREPANCIES_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.RETURN_MATCHED_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.RETURN_NEGATIVELY_ACKNOWLEDGED_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.RETURN_PENDING_ACKNOWLEDGEMENT_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.RETURN_POSITIVELY_ACKNOWLEDGED_SBJ;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Return.Subject.RETURN_UNMATCHED_SBJ;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RERATE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RETURN;
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
public class ReturnCloudEventBuilder extends IntegrationCloudEventBuilder {

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
            case GET_RETURN: {
                return switch (recordType) {
                    case TECHNICAL_EXCEPTION_1SOURCE ->
                        createGetReturnTechnicalExceptionCR(subProcess, recordType, data);
                    default -> null;
                };
            }
            case MATCH_RETURN: {
                return switch (recordType) {
                    case RETURN_MATCHED -> createReturnMatchedCR(subProcess, recordType, data, fieldImpacteds);
                    case RETURN_UNMATCHED -> createReturnUnmatchedCR(subProcess, recordType, data, fieldImpacteds);
                    case RETURN_PENDING_ACKNOWLEDGEMENT ->
                        createReturnPendingAckCR(subProcess, recordType, data, fieldImpacteds);
                    default -> null;
                };
            }
            case VALIDATE_RETURN: {
                return switch (recordType) {
                    case RETURN_DISCREPANCIES ->
                        createReturnDiscrepanciesCR(subProcess, recordType, data, fieldImpacteds);
                    default -> null;
                };
            }
            case ACKNOWLEDGE_RETURN_POSITIVELY: {
                return switch (recordType) {
                    case TECHNICAL_EXCEPTION_1SOURCE ->
                        createAckReturnPositivelyTechnicalExceptionCR(subProcess, recordType, data);
                    default -> null;
                };
            }
            case ACKNOWLEDGE_RETURN_NEGATIVELY: {
                return switch (recordType) {
                    case TECHNICAL_EXCEPTION_1SOURCE ->
                        createAckReturnNegativelyTechnicalExceptionCR(subProcess, recordType, data);
                    case TECHNICAL_ISSUE_INTEGRATION_TOOLKIT ->
                        createAckReturnNegativelyTechnicalIssueCR(subProcess, recordType, data);
                    case NEGATIVE_ACKNOWLEDGEMENT_NOT_AUTHORIZED ->
                        createAckReturnNegativelyNotAuthorizedCR(subProcess, recordType, data);
                    default -> null;
                };
            }
            case GET_RETURN_ACKNOWLEDGEMENT_DETAILS: {
                return switch (recordType) {
                    case TECHNICAL_EXCEPTION_1SOURCE ->
                        createReturnAckDetailsTechnicalExceptionCR(subProcess, recordType, data);
                    default -> null;
                };
            }
            case CAPTURE_RETURN_ACKNOWLEDGEMENT: {
                return switch (recordType) {
                    case RETURN_POSITIVELY_ACKNOWLEDGED -> createReturnPositivelyAckCR(subProcess, recordType, data);
                    case RETURN_NEGATIVELY_ACKNOWLEDGED -> createReturnNegativelyAckCR(subProcess, recordType, data);
                    default -> null;
                };
            }
            case CONFIRM_RETURN_TRADE: {
                return switch (recordType) {
                    case TECHNICAL_EXCEPTION_SPIRE -> createConfirmReturnTechnicalExceptionCR(subProcess, recordType, data);
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
            format(POST_RETURN_SUBMITTED_SBJ, data.get(TRADE_ID)),
            RETURN,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)))
        );
    }

    private CloudEventBuildRequest createGetReturnTechnicalExceptionCR(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(GET_RETURN_EXCEPTION_1SOURCE_MSG, data.get(RESOURCE_URI),
            data.get(HTTP_STATUS_TEXT));
        return createRecordRequest(
            recordType,
            format(GET_RETURN_EXCEPTION_1SOURCE_SBJ, data.get(RESOURCE_URI)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RESOURCE_URI), SPIRE_TRADE)))
        );
    }

    private CloudEventBuildRequest createReturnMatchedCR(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data, List<FieldImpacted> fieldsImpacted) {
        String dataMessage = format(RETURN_MATCHED_MSG, data.get(RETURN_ID), data.get(TRADE_ID));
        return createRecordRequest(
            recordType,
            format(RETURN_MATCHED_SBJ, data.get(TRADE_ID)),
            RETURN,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RETURN_ID), ONESOURCE_RETURN),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)))
        );
    }

    private CloudEventBuildRequest createReturnUnmatchedCR(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data, List<FieldImpacted> fieldsImpacted) {
        String dataMessage = format(RETURN_UNMATCHED_MSG, data.get(RETURN_ID));
        return createRecordRequest(
            recordType,
            format(RETURN_UNMATCHED_SBJ, data.get(RETURN_ID)),
            RETURN,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RETURN_ID), ONESOURCE_RETURN),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)), fieldsImpacted)
        );
    }

    private CloudEventBuildRequest createReturnPendingAckCR(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data, List<FieldImpacted> fieldsImpacted) {
        String dataMessage = format(RETURN_PENDING_ACKNOWLEDGEMENT_MSG, data.get(RETURN_ID), data.get(TRADE_ID));
        return createRecordRequest(
            recordType,
            format(RETURN_PENDING_ACKNOWLEDGEMENT_SBJ, data.get(TRADE_ID)),
            RETURN,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RETURN_ID), ONESOURCE_RETURN),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)))
        );
    }

    private CloudEventBuildRequest createReturnDiscrepanciesCR(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data, List<FieldImpacted> fieldsImpacted) {
        String dataMessage = format(RECONCILE_RETURN_DISCREPANCIES_MSG, data.get(RETURN_ID), data.get(TRADE_ID));
        return createRecordRequest(
            recordType,
            format(RECONCILE_RETURN_DISCREPANCIES_SBJ, data.get(TRADE_ID)),
            RETURN,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RETURN_ID), ONESOURCE_RETURN),
                    new RelatedObject(data.get(POSITION_ID), POSITION),
                    new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE),
                    new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)),
                fieldsImpacted)
        );
    }

    private CloudEventBuildRequest createAckReturnPositivelyTechnicalExceptionCR(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(ACKNOWLEDGE_RETURN_POSITIVELY_TE_MSG, data.get(RETURN_ID), data.get(TRADE_ID),
            data.get(HTTP_STATUS_TEXT));
        return createRecordRequest(
            recordType,
            format(ACKNOWLEDGE_RETURN_POSITIVELY_TE_SBJ, data.get(TRADE_ID)),
            RETURN,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RETURN_ID), ONESOURCE_RETURN),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)))
        );
    }

    private CloudEventBuildRequest createAckReturnNegativelyTechnicalExceptionCR(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(ACKNOWLEDGE_RETURN_NEGATIVELY_TE_MSG, data.get(RETURN_ID), data.get(TRADE_ID),
            data.get(HTTP_STATUS_TEXT));
        return createRecordRequest(
            recordType,
            format(ACKNOWLEDGE_RETURN_NEGATIVELY_TE_SBJ, data.get(TRADE_ID)),
            RETURN,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RETURN_ID), ONESOURCE_RETURN),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)))
        );
    }

    private CloudEventBuildRequest createAckReturnNegativelyTechnicalIssueCR(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(ACKNOWLEDGE_RETURN_NEGATIVELY_TI_MSG, data.get(RETURN_ID));
        return createRecordRequest(
            recordType,
            format(ACKNOWLEDGE_RETURN_NEGATIVELY_TI_SBJ, data.get(RETURN_ID)),
            RETURN,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RETURN_ID), ONESOURCE_RETURN)))
        );
    }

    private CloudEventBuildRequest createAckReturnNegativelyNotAuthorizedCR(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(ACKNOWLEDGE_RETURN_NEGATIVELY_NOT_AUTHORIZED_MSG, data.get(RETURN_ID));
        return createRecordRequest(
            recordType,
            format(ACKNOWLEDGE_RETURN_NEGATIVELY_NOT_AUTHORIZED_SBJ, data.get(RETURN_ID)),
            RETURN,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RETURN_ID), ONESOURCE_RETURN)))
        );
    }

    private CloudEventBuildRequest createReturnAckDetailsTechnicalExceptionCR(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(GET_RETURN_ACKNOWLEDGEMENT_DETAILS_TE_MSG, data.get(RESOURCE_URI),
            data.get(HTTP_STATUS_TEXT));
        return createRecordRequest(
            recordType,
            format(GET_RETURN_ACKNOWLEDGEMENT_DETAILS_TE_SBJ, data.get(RESOURCE_URI)),
            RETURN,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RESOURCE_URI), ONESOURCE_RETURN)))
        );
    }

    private CloudEventBuildRequest createReturnPositivelyAckCR(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data){
        String dataMessage = format(RETURN_POSITIVELY_ACKNOWLEDGED_MSG, data.get(RETURN_ID), data.get(TRADE_ID));
        return createRecordRequest(
            recordType,
            format(RETURN_POSITIVELY_ACKNOWLEDGED_SBJ, data.get(TRADE_ID)),
            RETURN,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RETURN_ID), ONESOURCE_RETURN),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)))
        );
    }

    private CloudEventBuildRequest createReturnNegativelyAckCR(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data){
        String dataMessage = format(RETURN_NEGATIVELY_ACKNOWLEDGED_MSG, data.get(RETURN_ID), data.get(TRADE_ID), data.get("description"));
        return createRecordRequest(
            recordType,
            format(RETURN_NEGATIVELY_ACKNOWLEDGED_SBJ, data.get(TRADE_ID)),
            RETURN,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RETURN_ID), ONESOURCE_RETURN),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)))
        );
    }

    private CloudEventBuildRequest createConfirmReturnTechnicalExceptionCR(IntegrationSubProcess subProcess, RecordType recordType, Map<String, String> data) {
        String dataMessage = format(CONFIRM_RETURN_TRADE_TE_MSG, data.get(TRADE_ID), data.get(RETURN_ID), data.get(HTTP_STATUS_TEXT));
        return createRecordRequest(
            recordType,
            format(CONFIRM_RETURN_TRADE_TE_SBJ, data.get(TRADE_ID)),
            RETURN,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RETURN_ID), ONESOURCE_RETURN),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)))
        );
    }
}
