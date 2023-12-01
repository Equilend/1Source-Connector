package com.intellecteu.onesource.integration.services.record;

import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractSettlement.DataMsg.POST_LOAN_CONTRACT_UPDATE_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractSettlement.Subject.POST_LOAN_CONTRACT_UPDATE_EXCEPTION;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_SETTLEMENT;
import static com.intellecteu.onesource.integration.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static java.lang.String.format;

@Component
public class ContractSettlementCloudEventBuilder extends IntegrationCloudEventBuilder {

  @Override
  public IntegrationProcess getVersion() {
    return CONTRACT_SETTLEMENT;
  }

  @Override
  public CloudEventBuildRequest buildExceptionRequest(String record, HttpStatusCodeException e,
      IntegrationSubProcess subProcess, String related) {
    return switch (subProcess) {
      case POST_LOAN_CONTRACT_UPDATE -> loanContractUpdate(record, e, subProcess, related);
      default -> null;
    };
  }

  private CloudEventBuildRequest loanContractUpdate(String record, HttpStatusCodeException exception,
      IntegrationSubProcess subProcess, String related) {
    String message = format(POST_LOAN_CONTRACT_UPDATE_EXCEPTION_MSG, record, related, exception.getStatusText());
    return createRecordRequest(
        TECHNICAL_EXCEPTION_1SOURCE,
        format(POST_LOAN_CONTRACT_UPDATE_EXCEPTION, related),
        CONTRACT_SETTLEMENT,
        subProcess,
        createEventData(message, getLoanProposalRelatedToPosition(record, related))
    );
  }

  @Override
  public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException e, IntegrationSubProcess subProcess) {
    return null;
  }

  @Override
  public CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related) {
    return null;
  }

}
