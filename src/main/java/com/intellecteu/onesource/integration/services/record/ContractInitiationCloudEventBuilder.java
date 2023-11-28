package com.intellecteu.onesource.integration.services.record;

import com.intellecteu.onesource.integration.dto.ExceptionMessageDto;
import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.dto.record.RelatedObject;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_TRADE_AGREEMENT;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.POSITION;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.APPROVE_LOAN_PROPOSAL_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.CANCEL_LOAN_PROPOSAL_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.DECLINE_LOAN_PROPOSAL_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.GET_LOAN_CONTRACT_PROPOSAL_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.GET_POSITION_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.GET_SETTLEMENT_INSTRUCTIONS_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.GET_TRADE_AGREEMENT_EXCEPTION_1SOURCE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.MATCHED_CANCELED_POSITION_TRADE_AGREEMENT_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.MATCHED_POSITION_LOAN_CONTRACT_PROPOSAL_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.MATCHED_POSITION_TRADE_AGREEMENT_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.POST_LOAN_CONTRACT_PROPOSAL_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.POST_LOAN_CONTRACT_PROPOSAL_UPDATE_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.POST_POSITION_UPDATE_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.POST_SETTLEMENT_INSTRUCTION_UPDATE_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.RECONCILE_TRADE_AGREEMENT_FAIL_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.RECONCILE_TRADE_AGREEMENT_SUCCESS_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.TRADE_AGREEMENT_CANCELED_EVENT_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.TRADE_AGREEMENT_CREATE_EVENT_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.TRADE_AGREEMENT_MATCHED_CANCELED_EVENT_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.VALIDATE_LOAN_CONTRACT_PROPOSAL_CANCELED_POSITION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.VALIDATE_LOAN_CONTRACT_PROPOSAL_VALIDATED_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.APPROVE_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.CANCEL_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.DECLINE_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.GET_AGREEMENT_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.GET_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.GET_POSITION_CONFIRMATION_EXCEPTION_SPIRE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.GET_SETTLEMENT_INSTR_EXCEPTION_SPIRE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.LOAN_CONTRACT_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.POST_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.POST_LOAN_CONTRACT_PROPOSAL_UPDATE_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.POST_POSITION_UPDATE_EXCEPTION_SPIRE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.POST_SETTLEMENT_INSTRUCTION_UPDATE_EXCEPTION_SPIRE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.TRADE_AGREEMENT_CANCELED;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.TRADE_AGREEMENT_CREATED;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.TRADE_AGREEMENT_MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.TRADE_AGREEMENT_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.TRADE_AGREEMENT_RECONCILED;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.VALIDATE_LOAN_CONTRACT_PROPOSAL_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.VALIDATE_LOAN_CONTRACT_PROPOSAL_VALIDATED;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.CANCEL_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_POSITIONS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_SETTLEMENT_INSTRUCTIONS;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_TRADE_AGREEMENT;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_TRADE_CANCELATION;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.POST_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.RECONCILE_TRADE_AGREEMENT;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.VALIDATE_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.enums.RecordType.TECHNICAL_EXCEPTION_SPIRE;
import static java.lang.String.format;

@Component
public class ContractInitiationCloudEventBuilder extends IntegrationCloudEventBuilder {

  @Override
  public IntegrationProcess getVersion() {
    return CONTRACT_INITIATION;
  }

  @Override
  public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException e, IntegrationSubProcess subProcess) {
    return buildExceptionRequest(null, e, subProcess,  null);
  }

  @Override
  public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException e, IntegrationSubProcess subProcess,
      String recorded) {
    return buildExceptionRequest(recorded, e, subProcess, null);
  }

  @Override
  public CloudEventBuildRequest buildExceptionRequest(String record, HttpStatusCodeException exception,
      IntegrationSubProcess subProcess, String related) {
    return switch (subProcess) {
      case GET_TRADE_AGREEMENT -> tradeAgreementExceptionRequest(exception, record, related);
      case GET_POSITIONS_PENDING_CONFIRMATION -> positionExceptionEventRequest(exception, record, related, subProcess);
      case GET_SETTLEMENT_INSTRUCTIONS -> settlementInstructionExceptionEventRequest(record, exception, related);
      case POST_LOAN_CONTRACT_PROPOSAL -> postLoanContractProposalExceptionEvent(record, exception);
      case CANCEL_LOAN_CONTRACT_PROPOSAL -> cancelLoanContractProposalExceptionEvent(record, exception, related);
      case GET_LOAN_CONTRACT_PROPOSAL -> getLoanContractProposalExceptionEvent(record, exception, related);
      case POST_LOAN_CONTRACT_PROPOSAL_UPDATE -> postLoanContractProposalUpdateExceptionEvent(record, exception,
          related, subProcess);
      case APPROVE_LOAN_CONTRACT_PROPOSAL -> approveLoanContractProposalExceptionEvent(record, exception,
          related, subProcess);
      case DECLINE_LOAN_CONTRACT_PROPOSAL -> declineLoanContractProposalExceptionEvent(record, exception,
          related, subProcess);
      case POST_POSITION_UPDATE -> postPositionUpdateExceptionEvent(record, exception, related, subProcess);
      case POST_SETTLEMENT_INSTRUCTION_UPDATE -> postSettlementInstructionUpdateExceptionEvent(record,
          exception, related, subProcess);
      default -> null;
    };
  }

  @Override
  public CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related) {
    return switch (recordType) {
      case LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION -> loanContractProposalMatchingEvent(recorded,
          recordType, related);
      case TRADE_AGREEMENT_MATCHED_CANCELED_POSITION -> tradeAgreementMatchingEvent(recorded,
          recordType, related);
      case LOAN_CONTRACT_PROPOSAL_VALIDATED -> loanContractProposalValidated(recorded, recordType, related);
      case TRADE_AGREEMENT_RECONCILED -> tradeAgreementReconciledEvent(recorded, recordType, related);
      case TRADE_AGREEMENT_MATCHED_POSITION -> tradeMatchedPositionEvent(recorded, recordType, related);
      case TRADE_AGREEMENT_CANCELED -> tradeCancelationEvent(recorded, recordType, related);
      case LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION -> loanProposalMatchedPositionEvent(recorded, recordType, related);
      default -> null;
    };
  }

  @Override
  public CloudEventBuildRequest buildRequest(RecordType recordType, String resourceUri) {
    return switch (recordType) {
      case TRADE_AGREEMENT_CREATED -> tradeAgreementCreationEvent(resourceUri,
          recordType);
      case TRADE_AGREEMENT_CANCELED -> tradeAgreementCancelationEvent(resourceUri,
          recordType);
      default -> null;
    };
  }

  @Override
  public CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related,
      List<ExceptionMessageDto> exceptionData) {
    final String formattedExceptions = exceptionData.stream()
        .map(d -> "- " + d.getExceptionMessage())
        .collect(Collectors.joining("\n"));
    String dataMessage = format(RECONCILE_TRADE_AGREEMENT_FAIL_MSG, recorded, related, formattedExceptions);
    return createRecordRequest(
        recordType,
        format(TRADE_AGREEMENT_RECONCILED, related),
        CONTRACT_INITIATION,
        RECONCILE_TRADE_AGREEMENT,
        createEventData(dataMessage, getTradeAgreementRelatedToPosition(recorded, related))
    );
  }

  private CloudEventBuildRequest loanContractProposalMatchingEvent(String recorded, RecordType recordType,
      String related) {
    String dataMessage = format(VALIDATE_LOAN_CONTRACT_PROPOSAL_CANCELED_POSITION_MSG, recorded, related);
    return createRecordRequest(
        recordType,
        format(VALIDATE_LOAN_CONTRACT_PROPOSAL_CANCELED_POSITION, related),
        CONTRACT_INITIATION,
        VALIDATE_LOAN_CONTRACT_PROPOSAL,
        createEventData(dataMessage, getLoanProposalRelatedToPosition(recorded, related))
    );
  }

  private CloudEventBuildRequest tradeAgreementMatchingEvent(String recorded, RecordType recordType,
      String related) {
    String dataMessage = format(MATCHED_CANCELED_POSITION_TRADE_AGREEMENT_MSG, recorded, related);
    return createRecordRequest(
        recordType,
        format(TRADE_AGREEMENT_MATCHED_CANCELED_POSITION, related),
        CONTRACT_INITIATION,
        GET_TRADE_AGREEMENT,
        createEventData(dataMessage, getLoanProposalRelatedToPosition(recorded, related))
    );
  }

  private CloudEventBuildRequest loanContractProposalValidated(String recorded, RecordType recordType,
      String related) {
    String dataMessage = format(VALIDATE_LOAN_CONTRACT_PROPOSAL_VALIDATED_MSG, recorded, related);
    return createRecordRequest(
        recordType,
        format(VALIDATE_LOAN_CONTRACT_PROPOSAL_VALIDATED, related),
        CONTRACT_INITIATION,
        VALIDATE_LOAN_CONTRACT_PROPOSAL,
        createEventData(dataMessage, getLoanProposalRelatedToPosition(recorded, related))
    );
  }

  private CloudEventBuildRequest tradeAgreementReconciledEvent(String recorded, RecordType recordType,
      String related) {
    String dataMessage = format(RECONCILE_TRADE_AGREEMENT_SUCCESS_MSG, recorded, related);
    return createRecordRequest(
        recordType,
        format(TRADE_AGREEMENT_RECONCILED, related),
        CONTRACT_INITIATION,
        RECONCILE_TRADE_AGREEMENT,
        createEventData(dataMessage, getTradeAgreementRelatedToPosition(recorded, related))
    );
  }

  private CloudEventBuildRequest tradeMatchedPositionEvent(String recorded, RecordType recordType,
      String related) {
    String dataMessage = format(MATCHED_POSITION_TRADE_AGREEMENT_MSG, recorded, related);
    return createRecordRequest(
        recordType,
        format(TRADE_AGREEMENT_MATCHED_POSITION, related),
        CONTRACT_INITIATION,
        GET_TRADE_AGREEMENT,
        createEventData(dataMessage, getTradeAgreementRelatedToPosition(recorded, related))
    );
  }

  private CloudEventBuildRequest tradeCancelationEvent(String recorded, RecordType recordType,
      String related) {
    String dataMessage = format(TRADE_AGREEMENT_MATCHED_CANCELED_EVENT_MSG, recorded, related);
    return createRecordRequest(
        recordType,
        format(TRADE_AGREEMENT_CANCELED, related),
        CONTRACT_INITIATION,
        GET_TRADE_CANCELATION,
        createEventData(dataMessage, getTradeAgreementRelatedToPosition(recorded, related))
    );
  }

  private CloudEventBuildRequest loanProposalMatchedPositionEvent(String recorded, RecordType recordType,
      String related) {
    String dataMessage = format(MATCHED_POSITION_LOAN_CONTRACT_PROPOSAL_MSG, recorded, related);
    return createRecordRequest(
        recordType,
        format(LOAN_CONTRACT_MATCHED_POSITION, related),
        CONTRACT_INITIATION,
        GET_LOAN_CONTRACT_PROPOSAL,
        createEventData(dataMessage, getLoanProposalRelatedToPosition(recorded, related))
    );
  }

  private CloudEventBuildRequest postSettlementInstructionUpdateExceptionEvent(String record,
      HttpStatusCodeException exception, String related, IntegrationSubProcess subProcess) {
    String message = format(POST_SETTLEMENT_INSTRUCTION_UPDATE_EXCEPTION_MSG, record, related,
        exception.getStatusText());
    return createRecordRequest(
        TECHNICAL_EXCEPTION_SPIRE,
        format(POST_SETTLEMENT_INSTRUCTION_UPDATE_EXCEPTION_SPIRE, related),
        CONTRACT_INITIATION,
        subProcess,
        createEventData(message, getLoanContractRelatedToPosition(record, related))
    );
  }

  private CloudEventBuildRequest postPositionUpdateExceptionEvent(String record,
      HttpStatusCodeException exception, String related, IntegrationSubProcess subProcess) {
    String message = format(POST_POSITION_UPDATE_EXCEPTION_MSG, record, related, exception.getStatusText());
    return createRecordRequest(
        TECHNICAL_EXCEPTION_1SOURCE,
        format(POST_POSITION_UPDATE_EXCEPTION_SPIRE, related),
        CONTRACT_INITIATION,
        subProcess,
        createEventData(message, List.of(new RelatedObject(related, POSITION)))
    );
  }

  private CloudEventBuildRequest tradeAgreementCreationEvent(String resourceUri, RecordType recordType) {
    String message = format(TRADE_AGREEMENT_CREATE_EVENT_MSG, resourceUri);
    return createRecordRequest(
        recordType,
        format(TRADE_AGREEMENT_CREATED, resourceUri),
        CONTRACT_INITIATION,
        GET_TRADE_AGREEMENT,
        createEventData(message, List.of(new RelatedObject(resourceUri, ONESOURCE_TRADE_AGREEMENT)))
    );
  }

  private CloudEventBuildRequest tradeAgreementCancelationEvent(String resourceUri, RecordType recordType) {
    String message = format(TRADE_AGREEMENT_CANCELED_EVENT_MSG, resourceUri);
    return createRecordRequest(
        recordType,
        format(TRADE_AGREEMENT_CANCELED, resourceUri),
        CONTRACT_INITIATION,
        GET_TRADE_CANCELATION,
        createEventData(message, List.of(new RelatedObject(resourceUri, ONESOURCE_TRADE_AGREEMENT)))
    );
  }
  private CloudEventBuildRequest declineLoanContractProposalExceptionEvent(String record,
      HttpStatusCodeException exception, String related, IntegrationSubProcess subProcess) {
    String message = format(DECLINE_LOAN_PROPOSAL_EXCEPTION_MSG, record, exception.getStatusText());
    return createRecordRequest(
        TECHNICAL_EXCEPTION_1SOURCE,
        format(DECLINE_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE, related),
        IntegrationProcess.CONTRACT_INITIATION,
        subProcess,
        createEventData(message, getLoanProposalRelatedToPosition(record, related))
    );
  }

  private CloudEventBuildRequest approveLoanContractProposalExceptionEvent(String record,
      HttpStatusCodeException exception, String related, IntegrationSubProcess subProcess) {
    String message = format(APPROVE_LOAN_PROPOSAL_EXCEPTION_MSG, record, exception.getStatusText());
    return createRecordRequest(
        TECHNICAL_EXCEPTION_1SOURCE,
        format(APPROVE_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE, related),
        CONTRACT_INITIATION,
        subProcess,
        createEventData(message, getLoanProposalRelatedToPosition(record, related))
    );
  }

  private CloudEventBuildRequest postLoanContractProposalUpdateExceptionEvent(String record,
      HttpStatusCodeException exception, String related, IntegrationSubProcess subProcess) {
    String message = format(POST_LOAN_CONTRACT_PROPOSAL_UPDATE_EXCEPTION_MSG, record, exception.getStatusText());
    return createRecordRequest(
        TECHNICAL_EXCEPTION_1SOURCE,
        format(POST_LOAN_CONTRACT_PROPOSAL_UPDATE_EXCEPTION_1SOURCE, related),
        CONTRACT_INITIATION,
        subProcess,
        createEventData(message, getLoanProposalRelatedToPosition(record, related))
    );
  }

  private CloudEventBuildRequest getLoanContractProposalExceptionEvent(String record, HttpStatusCodeException exception,
      String related) {
    String message = format(GET_LOAN_CONTRACT_PROPOSAL_EXCEPTION_MSG, record, exception.getStatusText());
    return createRecordRequest(
        TECHNICAL_EXCEPTION_1SOURCE,
        format(GET_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE, related),
        CONTRACT_INITIATION,
        GET_LOAN_CONTRACT_PROPOSAL,
        createEventData(message, getLoanProposalRelatedToPosition(record, related))
    );

  }

  private CloudEventBuildRequest cancelLoanContractProposalExceptionEvent(String record,
      HttpStatusCodeException exception, String related) {
    String message = format(CANCEL_LOAN_PROPOSAL_MSG, record, exception.getStatusText());
    return createRecordRequest(
        TECHNICAL_EXCEPTION_1SOURCE,
        format(CANCEL_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE, related),
        CONTRACT_INITIATION,
        CANCEL_LOAN_CONTRACT_PROPOSAL,
        createEventData(message, getLoanProposalRelatedToPosition(record, related))
    );
  }

  private CloudEventBuildRequest postLoanContractProposalExceptionEvent(String record,
      HttpStatusCodeException exception) {
    String message = format(POST_LOAN_CONTRACT_PROPOSAL_EXCEPTION_MSG, record, exception.getStatusText());
    return createRecordRequest(
        TECHNICAL_EXCEPTION_1SOURCE,
        format(POST_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE, record),
        CONTRACT_INITIATION,
        POST_LOAN_CONTRACT_PROPOSAL,
        createEventData(message, List.of(new RelatedObject(record, POSITION)))
    );
  }

  private CloudEventBuildRequest settlementInstructionExceptionEventRequest(String agreementId,
      HttpStatusCodeException exception, String positionId) {
    String message = format(GET_SETTLEMENT_INSTRUCTIONS_EXCEPTION_MSG,agreementId, positionId,
        exception.getStatusText());
    return createRecordRequest(
        TECHNICAL_EXCEPTION_SPIRE,
        format(GET_SETTLEMENT_INSTR_EXCEPTION_SPIRE, positionId),
        CONTRACT_INITIATION,
        GET_SETTLEMENT_INSTRUCTIONS,
        createEventData(message, List.of(new RelatedObject(positionId, POSITION)))
    );
  }

  private CloudEventBuildRequest positionExceptionEventRequest(HttpStatusCodeException exception, String resource,
      String event, IntegrationSubProcess subProcess) {
    return switch (subProcess) {
      case GET_POSITIONS_PENDING_CONFIRMATION -> createPendingConfirmationExceptionEvent(exception);
      default -> null;
    };
  }

  private CloudEventBuildRequest createPendingConfirmationExceptionEvent(HttpStatusCodeException exception) {
    String message = format(GET_POSITION_EXCEPTION_MSG, exception.getStatusText());
    return createRecordRequest(
        TECHNICAL_EXCEPTION_SPIRE,
        format(GET_POSITION_CONFIRMATION_EXCEPTION_SPIRE, LocalDateTime.now()),
        CONTRACT_INITIATION,
        GET_POSITIONS_PENDING_CONFIRMATION,
        createEventData(message, List.of(RelatedObject.notApplicable()))
    );
  }

  private CloudEventBuildRequest tradeAgreementExceptionRequest(HttpStatusCodeException exception, String resource,
      String event) {
    var exceptionMessage = switch (exception.getStatusCode()) {
      case UNAUTHORIZED -> format(GET_TRADE_AGREEMENT_EXCEPTION_1SOURCE_MSG, resource,
          "Not Authorized to do this operation");
      case NOT_FOUND -> format(GET_TRADE_AGREEMENT_EXCEPTION_1SOURCE_MSG, resource,
          "Resource not found");
      default -> format(GET_TRADE_AGREEMENT_EXCEPTION_1SOURCE_MSG, resource,
          "An error occurred");
    };
    return createRecordRequest(
        TECHNICAL_EXCEPTION_1SOURCE,
        format(GET_AGREEMENT_EXCEPTION_1SOURCE, event),
        CONTRACT_INITIATION,
        GET_TRADE_AGREEMENT,
        createEventData(exceptionMessage, getTradeAgreementRelatedToOnesourceEvent(resource, event))
    );
  }
}
