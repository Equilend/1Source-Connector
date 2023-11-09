package com.intellecteu.onesource.integration.services.record;

import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.dto.record.RelatedObject;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.LocalDate;
import java.util.List;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.DataMsg.GET_EVENTS_EXCEPTION_1SOURCE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.EventTypeMessage.CONTRACT_APPROVE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.EventTypeMessage.CONTRACT_CANCEL_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.EventTypeMessage.CONTRACT_CREATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.EventTypeMessage.CONTRACT_DECLINE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.Subject.GET_EVENTS_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.Subject.GET_EVENTS_LOAN_CONTRACT_PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.Subject.GET_EVENTS_LOAN_CONTRACT_PROPOSAL_CANCELED;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.Subject.GET_EVENTS_LOAN_CONTRACT_PROPOSAL_CREATED;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.Subject.GET_EVENTS_LOAN_CONTRACT_PROPOSAL_DECLINED;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.GENERIC;
import static com.intellecteu.onesource.integration.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static java.lang.String.format;

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
    return switch (recordType) {
      case LOAN_CONTRACT_PROPOSAL_CREATED -> createLoanContractRequest(recordType, recorded, related,
          format(GET_EVENTS_LOAN_CONTRACT_PROPOSAL_CREATED, recorded, related),
          format(CONTRACT_CREATE_MSG, recorded, related));

      case LOAN_CONTRACT_PROPOSAL_APPROVED -> createLoanContractRequest(recordType, recorded, related,
          format(GET_EVENTS_LOAN_CONTRACT_PROPOSAL_APPROVED, recorded, related),
          format(CONTRACT_APPROVE_MSG, recorded, related));

      case LOAN_CONTRACT_PROPOSAL_DECLINED -> createLoanContractRequest(recordType, recorded, related,
          format(GET_EVENTS_LOAN_CONTRACT_PROPOSAL_DECLINED, recorded, related),
          format(CONTRACT_DECLINE_MSG, recorded, related));

      case LOAN_CONTRACT_PROPOSAL_CANCELED -> createLoanContractRequest(recordType, recorded, related,
          format(GET_EVENTS_LOAN_CONTRACT_PROPOSAL_CANCELED, recorded, related),
          format(CONTRACT_CANCEL_MSG, recorded, related));

      default -> new CloudEventBuildRequest();
    };
  }

  private CloudEventBuildRequest createLoanContractRequest(RecordType recordType, String resourceUri,
      String positionId, String subject, String dataMsg) {
    return createRecordRequest(
        recordType,
        subject,
        IntegrationProcess.GENERIC,
        IntegrationSubProcess.GET_1SOURCE_EVENTS,
        createEventData(dataMsg, getLoanContractProposalRelatedToPosition(resourceUri, positionId))
    );
  }
}
