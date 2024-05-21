package com.intellecteu.onesource.integration.services.systemevent;

import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_CONTRACT;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_EVENT;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_LOAN_CONTRACT;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_LOAN_PROPOSAL;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_TRADE_AGREEMENT;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.POSITION;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.SHARED_TRADE_TICKET;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.SPIRE_RECALL;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.SPIRE_TRADE;
import static com.intellecteu.onesource.integration.model.enums.FieldSource.ONE_SOURCE_LOAN_CONTRACT;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import com.intellecteu.onesource.integration.model.ProcessExceptionDetails;
import com.intellecteu.onesource.integration.model.enums.FieldExceptionType;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RelatedObject;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.SystemEventData;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventMetadata;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.IntegrationCloudEvent;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpStatusCodeException;

@Data
@Slf4j
public abstract class IntegrationCloudEventBuilder implements CloudEventBuilder<IntegrationCloudEvent> {

    String specVersion;
    String integrationUri;

    public IntegrationCloudEventBuilder(String specVersion, String integrationUri) {
        this.specVersion = specVersion;
        this.integrationUri = integrationUri;
    }

    public IntegrationCloudEventBuilder() {
    }

    @Override
    public IntegrationCloudEvent build(CloudEventBuildRequest buildRequest) {
        var metadata = createMetadata(buildRequest);
        return new IntegrationCloudEvent(metadata, buildRequest.getData());
    }

    public abstract CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException e,
        IntegrationSubProcess subProcess);

    public abstract CloudEventBuildRequest buildToolkitIssueRequest(String recorded, IntegrationSubProcess subProcess);

    public CloudEventBuildRequest buildExceptionRequest(String recorded, HttpStatusCodeException e,
        IntegrationSubProcess subProcess, String related) {
        return null;
    }

    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException e,
        IntegrationSubProcess subProcess, String recorded) {
        return null;
    }

    public CloudEventBuildRequest buildRequest(String recorded, RecordType recordType) {
        return buildRequest(recorded, recordType, null);
    }

    public abstract CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related);

    public CloudEventBuildRequest buildRequest(IntegrationSubProcess subProcess, RecordType recordType,
        Map<String, String> data, List<FieldImpacted> fieldImpacteds) {
        return null;
    }

    public CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related,
        List<ProcessExceptionDetails> exceptionData) {
        return null;
    }

    public CloudEventBuildRequest buildRequest(RecordType recordType, Contract contract) {
        return null;
    }

    protected SystemEventData createEventData(String eventDataId, List<FieldImpacted> fieldsImpacted,
        String message, List<RelatedObject> relatedObjects) {
        return SystemEventData.builder()
            .eventDataId(eventDataId)
            .message(message)
            .fieldsImpacted(fieldsImpacted)
            .relatedObjects(relatedObjects)
            .build();
    }

    protected SystemEventData createEventData(String message, List<RelatedObject> relatedObjects,
        List<FieldImpacted> fieldsImpacted) {
        return createEventData(null, fieldsImpacted, message, relatedObjects);
    }

    protected SystemEventData createEventData(String message, List<RelatedObject> relatedObjects) {
        return createEventData(null, null, message, relatedObjects);
    }

    protected CloudEventBuildRequest createRecordRequest(RecordType recordType, String subject,
        IntegrationProcess relatedProcess, IntegrationSubProcess relatedSubProcess, SystemEventData eventData) {
        return CloudEventBuildRequest.builder()
            .recordType(recordType)
            .subject(subject)
            .relatedProcess(relatedProcess)
            .relatedSubProcess(relatedSubProcess)
            .data(eventData)
            .build();
    }

    protected List<RelatedObject> getTradeAgreementRelatedToOnesourceEvent(String agreementInfo, String eventInfo) {
        var relatedContractProposal = new RelatedObject(agreementInfo, ONESOURCE_TRADE_AGREEMENT);
        var relatedPosition = new RelatedObject(eventInfo, ONESOURCE_EVENT);
        return List.of(relatedContractProposal, relatedPosition);
    }

    protected List<RelatedObject> getLoanProposalRelatedToOnesourceEvent(String contractInfo, String eventInfo) {
        var relatedContractProposal = new RelatedObject(contractInfo, ONESOURCE_LOAN_PROPOSAL);
        var relatedPosition = new RelatedObject(eventInfo, ONESOURCE_EVENT);
        return List.of(relatedContractProposal, relatedPosition);
    }

    protected List<RelatedObject> getTradeAgreementRelatedToPosition(String agreementInfo, String positionInfo) {
        var tradeAgreement = new RelatedObject(agreementInfo, ONESOURCE_TRADE_AGREEMENT);
        var relatedPosition = new RelatedObject(positionInfo, POSITION);
        return List.of(tradeAgreement, relatedPosition);
    }

    protected List<RelatedObject> getTradeAgreementRelatedToSharedTradeTicket(String agreementInfo, String ticket) {
        var tradeAgreement = new RelatedObject(agreementInfo, ONESOURCE_TRADE_AGREEMENT);
        var relatedPosition = new RelatedObject(ticket, SHARED_TRADE_TICKET);
        return List.of(tradeAgreement, relatedPosition);
    }

    protected List<RelatedObject> getTradeAgreementRelatedToPositionAndSharedTradeTicket(String agreementInfo,
        String positionInfo, String venueRefKey) {
        var tradeAgreement = new RelatedObject(agreementInfo, ONESOURCE_TRADE_AGREEMENT);
        var relatedTradeTicket = new RelatedObject(venueRefKey, SHARED_TRADE_TICKET);
        var relatedPosition = new RelatedObject(positionInfo, POSITION);
        return List.of(tradeAgreement, relatedTradeTicket, relatedPosition);
    }

    protected List<RelatedObject> getContractRelatedToPositionWithTrade(String contractInfo, String positionInfo,
        String tradeInfo) {
        var tradeAgreement = new RelatedObject(contractInfo, ONESOURCE_LOAN_CONTRACT);
        var relatedPosition = new RelatedObject(positionInfo, POSITION);
        var relatedTrade = new RelatedObject(tradeInfo, SPIRE_TRADE);
        return List.of(tradeAgreement, relatedPosition, relatedTrade);
    }

    protected List<RelatedObject> getAgreementRelatedToNgtPosition(String agreementInfo, String relatedSequence) {
        String[] relatedIds = relatedSequence.split(",");
        String positionId;
        String ngtTicket;
        try {
            positionId = relatedIds[0];
            ngtTicket = relatedIds[1];
        } catch (IndexOutOfBoundsException e) {
            positionId = "";
            ngtTicket = "";
        }
        return getAgreementRelatedToNgtPosition(agreementInfo, positionId, ngtTicket);
    }

    protected List<RelatedObject> getRecallRelatedToPositionAndContract(String recallInfo, String relatedSequence) {
        String[] relatedIds = relatedSequence.split(",");
        String positionId;
        String contractId;
        try {
            positionId = relatedIds[0];
            contractId = relatedIds[1];
        } catch (IndexOutOfBoundsException e) {
            positionId = "";
            contractId = "";
        }
        return getRecallRelatedToPositionAndContract(recallInfo, positionId, contractId);
    }

    protected List<RelatedObject> getRecallRelatedToPositionAndContract(String recallInfo, String positionInfo,
        String contractInfo) {
        var tradeAgreement = new RelatedObject(recallInfo, SPIRE_RECALL);
        var relatedPosition = new RelatedObject(positionInfo, POSITION);
        var relatedTrade = new RelatedObject(contractInfo, ONESOURCE_LOAN_CONTRACT);
        return List.of(tradeAgreement, relatedPosition, relatedTrade);
    }

    protected List<RelatedObject> getAgreementRelatedToNgtPosition(String agreementInfo, String positionInfo,
        String ngtTicketInfo) {
        var tradeAgreement = new RelatedObject(agreementInfo, ONESOURCE_TRADE_AGREEMENT);
        var relatedPosition = new RelatedObject(positionInfo, POSITION);
        var relatedTrade = new RelatedObject(ngtTicketInfo, SHARED_TRADE_TICKET);
        return List.of(tradeAgreement, relatedPosition, relatedTrade);
    }

    protected List<RelatedObject> getContractRelatedToPositionWithTrade(String contractInfo, String relatedSequence) {
        String[] relatedIds = relatedSequence.split(",");
        String positionId;
        String tradeId;
        try {
            positionId = relatedIds[0];
            tradeId = relatedIds[1];
        } catch (IndexOutOfBoundsException e) {
            positionId = "";
            tradeId = "";
        }
        return getContractRelatedToPositionWithTrade(contractInfo, positionId, tradeId);
    }

    protected List<RelatedObject> getLoanProposalRelatedToPosition(String contractInfo, String positionInfo) {
        var relatedContractProposal = new RelatedObject(contractInfo, ONESOURCE_LOAN_PROPOSAL);
        var relatedPosition = new RelatedObject(positionInfo, POSITION);
        return List.of(relatedContractProposal, relatedPosition);
    }

    protected List<RelatedObject> getLoanContractRelated(String contractInfo) {
        return List.of(new RelatedObject(contractInfo, ONESOURCE_LOAN_CONTRACT));
    }

    protected List<RelatedObject> getLoanContractRelatedToPosition(String contractInfo, String positionInfo) {
        var relatedContractProposal = new RelatedObject(contractInfo, ONESOURCE_LOAN_CONTRACT);
        var relatedPosition = new RelatedObject(positionInfo, POSITION);
        return List.of(relatedContractProposal, relatedPosition);
    }

    protected List<RelatedObject> getLoanContractProposalRelatedToPosition(String proposalInfo, String positionInfo) {
        var relatedContractProposal = new RelatedObject(proposalInfo, ONESOURCE_LOAN_CONTRACT_PROPOSAL);
        var relatedPosition = new RelatedObject(positionInfo, POSITION);
        return List.of(relatedContractProposal, relatedPosition);
    }

    protected List<RelatedObject> getPositionRelated(String positionId) {
        return List.of(new RelatedObject(positionId, POSITION));
    }

    protected List<RelatedObject> getContractRelated(String contractId) {
        return List.of(new RelatedObject(contractId, ONESOURCE_CONTRACT));
    }

    protected CloudEventMetadata createMetadata(CloudEventBuildRequest buildRequest) {
        return CloudEventMetadata.builder()
            .specVersion(specVersion)
            .type(buildRequest.getRecordType().name())
            .source(URI.create(integrationUri))
            .subject(buildRequest.getSubject())
            .id(UUID.randomUUID().toString())
            .time(LocalDateTime.now())
            .relatedProcess(buildRequest.getRelatedProcess().name())
            .relatedSubProcess(buildRequest.getRelatedSubProcess().name())
            .dataContentType(APPLICATION_JSON_VALUE)
            .build();
    }

    protected FieldImpacted buildUnmatchedLoanProposalFieldImpacted(String fieldName, String fieldValue) {
        return FieldImpacted.builder()
            .fieldSource(ONE_SOURCE_LOAN_CONTRACT)
            .fieldName(fieldName)
            .fieldValue(fieldValue)
            .fieldExceptionType(FieldExceptionType.UNMATCHED)
            .build();
    }

}
