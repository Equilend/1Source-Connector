package com.intellecteu.onesource.integration.services.record;

import com.intellecteu.onesource.integration.dto.ExceptionMessageDto;
import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.dto.record.CloudEventData;
import com.intellecteu.onesource.integration.dto.record.CloudEventMetadata;
import com.intellecteu.onesource.integration.dto.record.IntegrationCloudEvent;
import com.intellecteu.onesource.integration.dto.record.RelatedObject;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpStatusCodeException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_EVENT;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_LOAN_CONTRACT;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_LOAN_PROPOSAL;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_TRADE_AGREEMENT;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.POSITION;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public abstract class IntegrationCloudEventBuilder implements CloudEventBuilder<IntegrationCloudEvent> {

  @Value("${cloudevents.specversion}")
  String specVersion;

  @Value("${integration-toolkit.uri}")
  String integrationUri;

  @Override
  public IntegrationCloudEvent build(CloudEventBuildRequest buildRequest) {
    var metadata = createMetadata(buildRequest);
    return new IntegrationCloudEvent(metadata, buildRequest.getData());
  }

  public abstract CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException e,
      IntegrationSubProcess subProcess);

  public CloudEventBuildRequest buildExceptionRequest(String recorded, HttpStatusCodeException e,
      IntegrationSubProcess subProcess, String related) {
    return null;
  }

  public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException e,
      IntegrationSubProcess subProcess, String recorded) {
    return null;
  }

  public CloudEventBuildRequest buildRequest(RecordType recordType, String resourceUri) {
    return null;
  }

  public abstract CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related);

  public CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related,
      List<ExceptionMessageDto> exceptionData) {
    return null;
  }

  protected CloudEventData createEventData(String message, List<RelatedObject> relatedObjects) {
    return CloudEventData.builder()
        .message(message)
        .relatedObjects(relatedObjects)
        .build();
  }

  protected CloudEventBuildRequest createRecordRequest(RecordType recordType, String subject,
      IntegrationProcess relatedProcess, IntegrationSubProcess relatedSubProcess, CloudEventData eventData) {
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

  protected List<RelatedObject> getTradeAgreementRelatedToPosition(String agreementInfo, String positionInfo) {
    var relatedContractProposal = new RelatedObject(agreementInfo, ONESOURCE_TRADE_AGREEMENT);
    var relatedPosition = new RelatedObject(positionInfo, POSITION);
    return List.of(relatedContractProposal, relatedPosition);
  }

  protected List<RelatedObject> getLoanProposalRelatedToPosition(String contractInfo, String positionInfo) {
    var relatedContractProposal = new RelatedObject(contractInfo, ONESOURCE_LOAN_PROPOSAL);
    var relatedPosition = new RelatedObject(positionInfo, POSITION);
    return List.of(relatedContractProposal, relatedPosition);
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

}
