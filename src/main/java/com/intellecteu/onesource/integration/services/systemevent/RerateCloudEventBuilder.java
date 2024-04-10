package com.intellecteu.onesource.integration.services.systemevent;

import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.BACKOFFICE_RERATE;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_LOAN_CONTRACT;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_RERATE;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.POSITION;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.SPIRE_TRADE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.RECONCILE_RERATE_DISCREPANCIES_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.RERATE_DISCREPANCIES;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.APPLIED_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.APPLIED_TECHNICAL_EXCEPTION_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.APPROVED_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.APPROVE_EXCEPTION_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.APPROVE_TECHNICAL_EXCEPTION_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.CANCELED_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.CANCELED_RERATE_PROPOSAL_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.CANCELED_TECHNICAL_EXCEPTION_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.CANCEL_EXCEPTION_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.CANCEL_PENDING_TECHNICAL_EXCEPTION_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.CANCEL_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.CONFIRM_EXCEPTION_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.CREATED_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.DECLIED_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.DECLINE_EXCEPTION_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.DECLINE_TECHNICAL_EXCEPTION_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.GET_RERATE_EXCEPTION_1SOURCE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.MATCHED_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.POST_RERATE_EXCEPTION_1SOURCE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.REPLACED_RERATE_TRADE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.REPLACE_RERATE_EXCEPTION_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.RERATE_CANCELED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.RERATE_CANCEL_PENDING_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.UNMATCHED_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.APPLIED_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.APPLIED_TECHNICAL_EXCEPTION_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.APPROVED_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.APPROVE_EXCEPTION_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.APPROVE_TECHNICAL_EXCEPTION_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.CANCELED_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.CANCELED_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.CANCELED_TECHNICAL_EXCEPTION_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.CANCEL_EXCEPTION_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.CANCEL_PENDING_TECHNICAL_EXCEPTION_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.CANCEL_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.CONFIRM_EXCEPTION_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.CREATED_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.DECLIED_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.DECLINE_EXCEPTION_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.DECLINE_TECHNICAL_EXCEPTION_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.GET_RERATE_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.MATCHED_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.POST_RERATE_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.REPLACED_RERATE_TRADE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.REPLACE_EXCEPTION_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.RERATE_CANCELED;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.RERATE_CANCEL_PENDING;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.UNMATCHED_RERATE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.RERATE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.VALIDATE_RERATE_PROPOSAL;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.model.ProcessExceptionDetails;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RelatedObject;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Component
public class RerateCloudEventBuilder extends IntegrationCloudEventBuilder {

    public static final String RERATE_ID = "rerateId";
    public static final String TRADE_ID = "tradeId";
    public static final String CONTRACT_ID = "contractId";
    public static final String POSITION_ID = "positionId";
    public static final String RESOURCE_URI = "resourceURI";
    public static final String HTTP_STATUS_TEXT = "httpStatusText";

    public RerateCloudEventBuilder(
        @Value("${cloudevents.specversion}") String specVersion,
        @Value("${cloudevents.source}") String integrationUri) {
        super(specVersion, integrationUri);
    }

    @Override
    public IntegrationProcess getVersion() {
        return RERATE;
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
        return null;
    }

    @Override
    public CloudEventBuildRequest buildRequest(IntegrationSubProcess subProcess, RecordType recordType,
        Map<String, String> data, List<FieldImpacted> fieldImpacteds) {
        switch (subProcess) {
            case PROCESS_RERATE_PENDING_CONFIRMATION: {
                return switch (recordType) {
                    case RERATE_PROPOSAL_MATCHED -> createMatchedRerateRecordRequest(subProcess, recordType, data);
                    case RERATE_TRADE_CREATED -> createCreatedRerateRecordRequest(subProcess, recordType, data);
                    default -> null;
                };
            }
            case MATCH_LOAN_RERATE_PROPOSAL: {
                return switch (recordType) {
                    case RERATE_PROPOSAL_UNMATCHED -> createUnMatchedRerateRecordRequest(subProcess, recordType, data);
                    case RERATE_PROPOSAL_PENDING_APPROVAL ->
                        createMatchedAndPendingApprovalRecordRequest(subProcess, recordType, data);
                    default -> null;
                };
            }
            case POST_RERATE_PROPOSAL: {
                return switch (recordType) {
                    case TECHNICAL_EXCEPTION_1SOURCE ->
                        createPostHttpExceptionCloudRequest(subProcess, recordType, data);
                    default -> null;
                };
            }
            case APPROVE_RERATE_PROPOSAL: {
                return switch (recordType) {
                    case TECHNICAL_EXCEPTION_1SOURCE ->
                        createApproveRerateExceptionCloudRequest(subProcess, recordType, data);
                    default -> null;
                };
            }
            case POST_RERATE_TRADE_CONFIRMATION: {
                return switch (recordType) {
                    case TECHNICAL_EXCEPTION_SPIRE ->
                        createConfirmRerateExceptionCloudRequest(subProcess, recordType, data);
                    default -> null;
                };
            }
            case DECLINE_RERATE_PROPOSAL: {
                return switch (recordType) {
                    case TECHNICAL_EXCEPTION_1SOURCE ->
                        createDeclineRerateExceptionCloudRequest(subProcess, recordType, data);
                    default -> null;
                };
            }
            case PROCESS_TRADE_UPDATE: {
                return switch (recordType) {
                    case TECHNICAL_EXCEPTION_1SOURCE ->
                        createCancelRerateExceptionCloudRequest(subProcess, recordType, data);
                    case RERATE_TRADE_REPLACED -> createRerateTradeReplacedCloudRequest(subProcess, recordType, data);
                    case RERATE_TRADE_REPLACE_SUBMITTED ->
                        createRerateTradeReplaceSubmittedCloudRequest(subProcess, recordType, data);
                    case TECHNICAL_ISSUE_INTEGRATION_TOOLKIT ->
                        createEntityExceptionCloudRequest(subProcess, recordType, data);
                    default -> null;
                };
            }
            case GET_RERATE_PROPOSAL: {
                return switch (recordType) {
                    case TECHNICAL_EXCEPTION_1SOURCE ->
                        createGetHttpExceptionCloudRequest(subProcess, recordType, data);
                    default -> null;
                };
            }
            case PROCESS_RERATE_APPLIED: {
                return switch (recordType) {
                    case RERATE_PROPOSAL_APPLIED -> createAppliedRecordRequest(subProcess, recordType, data);
                    case TECHNICAL_ISSUE_INTEGRATION_TOOLKIT ->
                        createAppliedTechnicalExceptionRecordRequest(subProcess, recordType, data);
                    default -> null;
                };
            }
            case GET_RERATE_APPROVED: {
                return switch (recordType) {
                    case TECHNICAL_ISSUE_INTEGRATION_TOOLKIT ->
                        createApprovedTechnicalExceptionRecordRequest(subProcess, recordType, data);
                    case RERATE_PROPOSAL_APPROVED -> createApprovedRecordRequest(subProcess, recordType, data);
                    default -> null;
                };
            }
            case PROCESS_RERATE_DECLINED: {
                return switch (recordType) {
                    case RERATE_PROPOSAL_DECLINED -> createDecliedRecordRequest(subProcess, recordType, data);
                    case TECHNICAL_ISSUE_INTEGRATION_TOOLKIT ->
                        createDeclineTechnicalExceptionRecordRequest(subProcess, recordType, data);
                    default -> null;
                };
            }
            case PROCESS_TRADE_CANCELED: {
                return switch (recordType) {
                    case RERATE_TRADE_CANCELED -> createRerateTradeCanceledCloudRequest(subProcess, recordType, data);
                    default -> null;
                };
            }
            case PROCESS_TRADE_CANCEL: {
                return switch (recordType) {
                    case TECHNICAL_EXCEPTION_1SOURCE ->
                        createCancelRerateExceptionCloudRequest(subProcess, recordType, data);
                    case TECHNICAL_ISSUE_INTEGRATION_TOOLKIT ->
                        createEntityExceptionCloudRequest(subProcess, recordType, data);
                    default -> null;
                };
            }
            case PROCESS_RERATE_PROPOSAL_CANCELED: {
                return switch (recordType) {
                    case RERATE_PROPOSAL_CANCELED ->
                        createRerateProposalCanceledRecordRequest(subProcess, recordType, data);
                    case TECHNICAL_ISSUE_INTEGRATION_TOOLKIT ->
                        createRerateProposalCanceledTechnicalExceptionRecordRequest(subProcess, recordType, data);
                    default -> null;
                };
            }
            case PROCESS_RERATE_CANCELED: {
                return switch (recordType) {
                    case RERATE_CANCELED -> createRerateCanceledRecordRequest(subProcess, recordType, data);
                    default -> null;
                };
            }
            case PROCESS_RERATE_CANCEL_PENDING: {
                return switch (recordType) {
                    case RERATE_CANCEL_PENDING_CONFIRMATION ->
                        createRerateCancelPendingRecordRequest(subProcess, recordType, data);
                    case TECHNICAL_ISSUE_INTEGRATION_TOOLKIT ->
                        createRerateCancelPendingTechnicalExceptionRecordRequest(subProcess, recordType, data);
                    default -> null;
                };
            }
        }
        return null;
    }

    private CloudEventBuildRequest createMatchedRerateRecordRequest(IntegrationSubProcess subProcess,
        RecordType recordType,
        Map<String, String> data) {
        String dataMessage = format(MATCHED_RERATE_MSG, data.get(RERATE_ID), data.get(TRADE_ID));
        return createRecordRequest(
            recordType,
            format(MATCHED_RERATE, data.get(TRADE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RERATE_ID), ONESOURCE_RERATE),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)))
        );
    }

    private CloudEventBuildRequest createMatchedAndPendingApprovalRecordRequest(IntegrationSubProcess subProcess,
        RecordType recordType,
        Map<String, String> data) {
        String dataMessage = format(MATCHED_RERATE_MSG, data.get(RERATE_ID), data.get(TRADE_ID));
        return createRecordRequest(
            recordType,
            format(MATCHED_RERATE, data.get(TRADE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RERATE_ID), ONESOURCE_RERATE),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)))
        );
    }

    private CloudEventBuildRequest createApprovedRecordRequest(IntegrationSubProcess subProcess, RecordType recordType,
        Map<String, String> data) {
        String dataMessage = format(APPROVED_RERATE_MSG, data.get(RERATE_ID), data.get(TRADE_ID));
        return createRecordRequest(
            recordType,
            format(APPROVED_RERATE, data.get(TRADE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RERATE_ID), ONESOURCE_RERATE),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)))
        );
    }

    private CloudEventBuildRequest createAppliedRecordRequest(IntegrationSubProcess subProcess, RecordType recordType,
        Map<String, String> data) {
        String dataMessage = format(APPLIED_RERATE_MSG, data.get(RERATE_ID), data.get(TRADE_ID),
            data.get("contractId"));
        return createRecordRequest(
            recordType,
            format(APPLIED_RERATE, data.get(TRADE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(RelatedObject.notApplicable()))
        );
    }

    private CloudEventBuildRequest createApproveRerateExceptionCloudRequest(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(APPROVE_EXCEPTION_RERATE_MSG, data.get(RERATE_ID), data.get(POSITION_ID),
            data.get(HTTP_STATUS_TEXT));
        return createRecordRequest(
            recordType,
            format(APPROVE_EXCEPTION_RERATE, data.get(TRADE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RERATE_ID), ONESOURCE_RERATE),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE)))
        );
    }

    private CloudEventBuildRequest createConfirmRerateExceptionCloudRequest(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(CONFIRM_EXCEPTION_RERATE_MSG, data.get(TRADE_ID), data.get(RERATE_ID),
            data.get(HTTP_STATUS_TEXT));
        return createRecordRequest(
            recordType,
            format(CONFIRM_EXCEPTION_RERATE, data.get(TRADE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RERATE_ID), ONESOURCE_RERATE),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE)))
        );
    }

    private CloudEventBuildRequest createDeclineRerateExceptionCloudRequest(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(DECLINE_EXCEPTION_RERATE_MSG, data.get(RERATE_ID), data.get(TRADE_ID),
            data.get(HTTP_STATUS_TEXT));
        return createRecordRequest(
            recordType,
            format(DECLINE_EXCEPTION_RERATE, data.get(TRADE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RERATE_ID), ONESOURCE_RERATE),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE)))
        );
    }

    private CloudEventBuildRequest createCancelRerateExceptionCloudRequest(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(CANCEL_EXCEPTION_RERATE_MSG, data.get(RERATE_ID), data.get(TRADE_ID),
            data.get(HTTP_STATUS_TEXT));
        return createRecordRequest(
            recordType,
            format(CANCEL_EXCEPTION_RERATE, data.get(TRADE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RERATE_ID), ONESOURCE_RERATE),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE)))
        );
    }

    private CloudEventBuildRequest createEntityExceptionCloudRequest(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(REPLACE_RERATE_EXCEPTION_RERATE_MSG, data.get(TRADE_ID));
        return createRecordRequest(
            recordType,
            format(REPLACE_EXCEPTION_RERATE, data.get(TRADE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(RelatedObject.notApplicable()))
        );
    }

    private CloudEventBuildRequest createRerateTradeReplacedCloudRequest(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(REPLACED_RERATE_TRADE_MSG, data.get("oldTradeId"), data.get("amendedTradeId"));
        return createRecordRequest(
            recordType,
            format(REPLACED_RERATE_TRADE, data.get(TRADE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get("oldTradeId"), "Initial Trade"),
                new RelatedObject(data.get("amendedTradeId"), "Amended Trade")))
        );
    }

    private CloudEventBuildRequest createRerateTradeReplaceSubmittedCloudRequest(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(CANCEL_RERATE_MSG, data.get("oldTradeId"), data.get(TRADE_ID),
            data.get(RERATE_ID));
        return createRecordRequest(
            recordType,
            format(CANCEL_RERATE, data.get(TRADE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get("oldTradeId"), "Initial Trade"),
                new RelatedObject(data.get("amendedTradeId"), "Amended Trade"),
                new RelatedObject(data.get(RERATE_ID), ONESOURCE_RERATE),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)))
        );
    }

    private CloudEventBuildRequest createRerateTradeCanceledCloudRequest(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(CANCELED_RERATE_MSG, data.get("oldTradeId"), data.get("amendedTradeId"),
            data.get(RERATE_ID));
        return createRecordRequest(
            recordType,
            format(CANCELED_RERATE, data.get("amendedTradeId")),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get("oldTradeId"), "Canceled Trade"),
                new RelatedObject(data.get("amendedTradeId"), "offsetting Trade")))
        );
    }

    private CloudEventBuildRequest createPostHttpExceptionCloudRequest(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(POST_RERATE_EXCEPTION_1SOURCE_MSG, data.get(TRADE_ID),
            data.get(HTTP_STATUS_TEXT));
        return createRecordRequest(
            recordType,
            format(POST_RERATE_EXCEPTION_1SOURCE, data.get(TRADE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE)))
        );
    }

    private CloudEventBuildRequest createGetHttpExceptionCloudRequest(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(GET_RERATE_EXCEPTION_1SOURCE_MSG, data.get(RESOURCE_URI),
            data.get(HTTP_STATUS_TEXT));
        return createRecordRequest(
            recordType,
            format(GET_RERATE_EXCEPTION_1SOURCE, data.get(RESOURCE_URI)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RESOURCE_URI), ONESOURCE_RERATE),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE)))
        );
    }

    private CloudEventBuildRequest createApprovedTechnicalExceptionRecordRequest(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(APPROVE_TECHNICAL_EXCEPTION_RERATE_MSG, data.get(RESOURCE_URI));
        return createRecordRequest(
            recordType,
            format(APPROVE_TECHNICAL_EXCEPTION_RERATE, data.get(RESOURCE_URI)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RESOURCE_URI), ONESOURCE_RERATE)))
        );
    }

    private CloudEventBuildRequest createAppliedTechnicalExceptionRecordRequest(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(APPLIED_TECHNICAL_EXCEPTION_RERATE_MSG, data.get(RESOURCE_URI));
        return createRecordRequest(
            recordType,
            format(APPLIED_TECHNICAL_EXCEPTION_RERATE, data.get(RESOURCE_URI)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RESOURCE_URI), ONESOURCE_RERATE)))
        );
    }

    private CloudEventBuildRequest createDecliedRecordRequest(IntegrationSubProcess subProcess, RecordType recordType,
        Map<String, String> data) {
        String dataMessage = format(DECLIED_RERATE_MSG, data.get(RERATE_ID));
        return createRecordRequest(
            recordType,
            format(DECLIED_RERATE, data.get(RERATE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RERATE_ID), ONESOURCE_RERATE),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)))
        );
    }

    private CloudEventBuildRequest createRerateProposalCanceledRecordRequest(IntegrationSubProcess subProcess,
        RecordType recordType,
        Map<String, String> data) {
        String dataMessage = format(CANCELED_RERATE_PROPOSAL_MSG, data.get(RERATE_ID));
        return createRecordRequest(
            recordType,
            format(CANCELED_RERATE_PROPOSAL, data.get(RERATE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RERATE_ID), ONESOURCE_RERATE),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)))
        );
    }

    private CloudEventBuildRequest createRerateCanceledRecordRequest(IntegrationSubProcess subProcess,
        RecordType recordType,
        Map<String, String> data) {
        String dataMessage = format(RERATE_CANCELED_MSG, data.get(RERATE_ID));
        return createRecordRequest(
            recordType,
            format(RERATE_CANCELED, data.get(RERATE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RERATE_ID), ONESOURCE_RERATE),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)))
        );
    }

    private CloudEventBuildRequest createRerateCancelPendingRecordRequest(IntegrationSubProcess subProcess,
        RecordType recordType,
        Map<String, String> data) {
        String dataMessage = format(RERATE_CANCEL_PENDING_MSG, data.get(RERATE_ID));
        return createRecordRequest(
            recordType,
            format(RERATE_CANCEL_PENDING, data.get(RERATE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RERATE_ID), ONESOURCE_RERATE),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(CONTRACT_ID), ONESOURCE_LOAN_CONTRACT)))
        );
    }

    private CloudEventBuildRequest createRerateCancelPendingTechnicalExceptionRecordRequest(
        IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(CANCEL_PENDING_TECHNICAL_EXCEPTION_RERATE_MSG, data.get(RESOURCE_URI));
        return createRecordRequest(
            recordType,
            format(CANCEL_PENDING_TECHNICAL_EXCEPTION_RERATE, data.get(RESOURCE_URI)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RESOURCE_URI), ONESOURCE_RERATE)))
        );
    }

    private CloudEventBuildRequest createRerateProposalCanceledTechnicalExceptionRecordRequest(
        IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(CANCELED_TECHNICAL_EXCEPTION_RERATE_MSG, data.get(RESOURCE_URI));
        return createRecordRequest(
            recordType,
            format(CANCELED_TECHNICAL_EXCEPTION_RERATE, data.get(RESOURCE_URI)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RESOURCE_URI), ONESOURCE_RERATE)))
        );
    }

    private CloudEventBuildRequest createDeclineTechnicalExceptionRecordRequest(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(DECLINE_TECHNICAL_EXCEPTION_RERATE_MSG, data.get(RESOURCE_URI));
        return createRecordRequest(
            recordType,
            format(DECLINE_TECHNICAL_EXCEPTION_RERATE, data.get(RESOURCE_URI)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RESOURCE_URI), ONESOURCE_RERATE)))
        );
    }

    private CloudEventBuildRequest createCreatedRerateRecordRequest(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(CREATED_RERATE_MSG, data.get(TRADE_ID));
        return createRecordRequest(
            recordType,
            format(CREATED_RERATE, data.get(TRADE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RERATE_ID), ONESOURCE_RERATE),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE)))
        );
    }

    private CloudEventBuildRequest createUnMatchedRerateRecordRequest(IntegrationSubProcess subProcess,
        RecordType recordType, Map<String, String> data) {
        String dataMessage = format(UNMATCHED_RERATE_MSG, data.get(RERATE_ID));
        return createRecordRequest(
            recordType,
            format(UNMATCHED_RERATE, data.get(RERATE_ID)),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(new RelatedObject(data.get(RERATE_ID), ONESOURCE_RERATE),
                new RelatedObject(data.get(POSITION_ID), POSITION),
                new RelatedObject(data.get(TRADE_ID), SPIRE_TRADE)))
        );
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException exception,
        IntegrationSubProcess subProcess) {
        return null;
    }

    @Override
    public CloudEventBuildRequest buildToolkitIssueRequest(String recorded, IntegrationSubProcess subProcess) {
        return null;
    }
}
