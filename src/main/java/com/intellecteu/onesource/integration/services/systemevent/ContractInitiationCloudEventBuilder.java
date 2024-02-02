package com.intellecteu.onesource.integration.services.systemevent;

import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_TRADE_AGREEMENT;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.POSITION;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.APPROVE_LOAN_PROPOSAL_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.CANCEL_LOAN_PROPOSAL_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.CONTRACT_CANCEL_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.CONTRACT_CREATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.CONTRACT_DECLINE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.DECLINE_LOAN_PROPOSAL_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.GET_COUNTERPARTY_SETTLEMENT_INSTRUCTIONS_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.GET_LOAN_CONTRACT_PROPOSAL_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.GET_NEW_POSITIONS_PENDING_CONFIRMATION_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.GET_SETTLEMENT_INSTRUCTIONS_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.GET_TRADE_AGREEMENT_EXCEPTION_1SOURCE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.GET_UPDATED_POSITIONS_PENDING_CONFIRMATION_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.LOAN_CONTRACT_PROPOSAL_APPROVE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.LOAN_CONTRACT_PROPOSAL_RECONCILIATION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.MATCHED_CANCELED_POSITION_TRADE_AGREEMENT_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.MATCHED_POSITION_LOAN_CONTRACT_PROPOSAL_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.MATCHED_POSITION_TRADE_AGREEMENT_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.POST_LOAN_CONTRACT_PROPOSAL_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.POST_LOAN_CONTRACT_PROPOSAL_UPDATE_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.POST_POSITION_UPDATE_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.POST_SETTLEMENT_INSTRUCTION_UPDATE_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.RECONCILE_LOAN_CONTRACT_DISCREPANCIES_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.RECONCILE_TRADE_AGREEMENT_DISCREPANCIES_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.RECONCILE_TRADE_AGREEMENT_SUCCESS_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.TRADE_AGREEMENT_CANCELED_EVENT_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.TRADE_AGREEMENT_CREATE_EVENT_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.TRADE_AGREEMENT_MATCHED_CANCELED_EVENT_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.VALIDATE_LOAN_CONTRACT_PROPOSAL_CANCELED_POSITION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.APPROVE_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.CANCEL_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.DECLINE_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.GET_AGREEMENT_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.GET_COUNTERPARTY_SETTLEMENT_INSTRUCTION_SUBJECT;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.GET_EVENTS_LOAN_CONTRACT_PROPOSAL_CANCELED;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.GET_EVENTS_LOAN_CONTRACT_PROPOSAL_CREATED;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.GET_EVENTS_LOAN_CONTRACT_PROPOSAL_DECLINED;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.GET_LOAN_CONTRACT_PROPOSAL_DISCREPANCIES;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.GET_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.GET_POSITION_CONFIRMATION_EXCEPTION_SPIRE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.GET_SETTLEMENT_INSTR_EXCEPTION_SPIRE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.GET_UPDATED_POSITIONS_PENDING_CONFIRMATION_EXCEPTION_SPIRE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.LOAN_CONTRACT_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.LOAN_CONTRACT_PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.POST_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.POST_LOAN_CONTRACT_PROPOSAL_UPDATE_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.POST_POSITION_UPDATE_EXCEPTION_SPIRE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.POST_SETTLEMENT_INSTRUCTION_UPDATE_EXCEPTION_SPIRE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.TRADE_AGREEMENT_CANCELED;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.TRADE_AGREEMENT_CANCELED_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.TRADE_AGREEMENT_CREATED;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.TRADE_AGREEMENT_MATCHED_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.TRADE_AGREEMENT_MATCHED_POSITION;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.TRADE_AGREEMENT_RECONCILED;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.VALIDATE_LOAN_CONTRACT_PROPOSAL_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.Subject.VALIDATE_LOAN_CONTRACT_PROPOSAL_VALIDATED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.CANCEL_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_APPROVED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_CANCELED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_DECLINED;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_NEW_POSITIONS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_SETTLEMENT_INSTRUCTIONS;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_TRADE_AGREEMENT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_TRADE_CANCELLATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_UPDATED_POSITIONS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.RECONCILE_TRADE_AGREEMENT;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.VALIDATE_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.model.enums.RecordType.TECHNICAL_EXCEPTION_SPIRE;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.dto.ExceptionMessageDto;
import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.dto.record.CloudEventData;
import com.intellecteu.onesource.integration.dto.record.RelatedObject;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Component
public class ContractInitiationCloudEventBuilder extends IntegrationCloudEventBuilder {

    @Override
    public IntegrationProcess getVersion() {
        return CONTRACT_INITIATION;
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException e, IntegrationSubProcess subProcess) {
        return buildExceptionRequest(null, e, subProcess, null);
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
            case GET_COUNTERPARTY_SETTLEMENT_INSTRUCTION -> getCpSI(record, exception, subProcess, related);
            case GET_TRADE_AGREEMENT -> tradeAgreementExceptionRequest(exception, record, related);
            case GET_NEW_POSITIONS_PENDING_CONFIRMATION -> newPositionsPendingConfirmationExceptionEvent(exception);
            case GET_UPDATED_POSITIONS_PENDING_CONFIRMATION ->
                updatedPositionsPendingConfirmationExceptionEvent(exception);
            case GET_SETTLEMENT_INSTRUCTIONS -> settlementInstructionExceptionEventRequest(exception, related);
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
            case LOAN_CONTRACT_PROPOSAL_APPROVED -> loanContractProposalApproved(recorded, recordType, related);
            case LOAN_CONTRACT_PROPOSAL_CANCELED -> loanContractProposalCanceled(recorded, recordType, related);
            case LOAN_CONTRACT_PROPOSAL_CREATED -> loanContractProposalCreated(recorded, recordType);
            case LOAN_CONTRACT_PROPOSAL_DECLINED -> loanContractProposalDeclined(recorded, recordType, related);
            case LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION -> loanContractProposalMatchingEvent(recorded,
                recordType, related);
            case LOAN_CONTRACT_PROPOSAL_VALIDATED -> loanContractProposalValidated(recorded, recordType, related);
            case TRADE_AGREEMENT_CREATED -> tradeAgreementCreationEvent(recorded, recordType);
            case TRADE_AGREEMENT_MATCHED_CANCELED_POSITION -> tradeAgreementMatchingCanceledPosition(recorded,
                recordType, related);
            case TRADE_AGREEMENT_RECONCILED -> tradeAgreementReconciledEvent(recorded, recordType, related);
            case TRADE_AGREEMENT_MATCHED_POSITION -> tradeMatchedPositionEvent(recorded, recordType, related);
            case TRADE_AGREEMENT_CANCELED -> tradeCancellationEvent(recorded, recordType, related);
            case LOAN_CONTRACT_PROPOSAL_MATCHED_POSITION ->
                loanProposalMatchedPositionEvent(recorded, recordType, related);
            default -> null;
        };
    }

    @Override
    public CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related,
        List<ExceptionMessageDto> exceptionData) {
        return switch (recordType) {
            case TRADE_AGREEMENT_DISCREPANCIES -> tradeAgreementDiscrepancies(recorded, recordType,
                related, exceptionData);
            case LOAN_CONTRACT_PROPOSAL_DISCREPANCIES -> loanContractProposalDiscrepancies(recorded, recordType,
                related, exceptionData);
            default -> null;
        };
    }

    private CloudEventBuildRequest getCpSI(String recorded, HttpStatusCodeException exception,
        IntegrationSubProcess subProcess, String related) {
        String dataMessage = format(GET_COUNTERPARTY_SETTLEMENT_INSTRUCTIONS_EXCEPTION_MSG, recorded,
            related, exception.getStatusText());
        return createRecordRequest(
            TECHNICAL_EXCEPTION_SPIRE,
            format(GET_COUNTERPARTY_SETTLEMENT_INSTRUCTION_SUBJECT, related),
            CONTRACT_INITIATION,
            subProcess,
            createEventData(dataMessage, getLoanContractProposalRelatedToPosition(recorded, related)));
    }

    private CloudEventBuildRequest loanContractProposalDiscrepancies(String recorded, RecordType recordType,
        String related, List<ExceptionMessageDto> exceptionData) {
        final String formattedExceptions = exceptionData.stream()
            .map(d -> "- " + d.getExceptionMessage())
            .collect(Collectors.joining("\n"));
        String dataMessage = format(RECONCILE_LOAN_CONTRACT_DISCREPANCIES_MSG, recorded, related, formattedExceptions);
        return createRecordRequest(
            recordType,
            format(GET_LOAN_CONTRACT_PROPOSAL_DISCREPANCIES, related),
            CONTRACT_INITIATION,
            VALIDATE_LOAN_CONTRACT_PROPOSAL,
            createEventData(dataMessage, getLoanContractProposalRelatedToPosition(recorded, related))
        );
    }

    private CloudEventBuildRequest tradeAgreementDiscrepancies(String recorded, RecordType recordType,
        String related, List<ExceptionMessageDto> exceptionData) {
        final String formattedExceptions = exceptionData.stream()
            .map(d -> "- " + d.getExceptionMessage())
            .collect(Collectors.joining("\n"));
        String dataMessage = format(RECONCILE_TRADE_AGREEMENT_DISCREPANCIES_MSG, recorded, related,
            formattedExceptions);
        return createRecordRequest(
            recordType,
            format(TRADE_AGREEMENT_RECONCILED, related),
            CONTRACT_INITIATION,
            RECONCILE_TRADE_AGREEMENT,
            createEventData(dataMessage, getTradeAgreementRelatedToPosition(recorded, related))
        );
    }

    private CloudEventBuildRequest loanContractProposalApproved(String recorded, RecordType recordType,
        String related) {
        String dataMessage = format(LOAN_CONTRACT_PROPOSAL_APPROVE_MSG, recorded, related);
        return createRecordRequest(
            recordType,
            format(LOAN_CONTRACT_PROPOSAL_APPROVED, related),
            CONTRACT_INITIATION,
            GET_LOAN_CONTRACT_APPROVED,
            createEventData(dataMessage, getLoanContractProposalRelatedToPosition(recorded, related))
        );
    }

    private CloudEventBuildRequest loanContractProposalCanceled(String recorded, RecordType recordType,
        String related) {
        String dataMessage = format(CONTRACT_CANCEL_MSG, recorded, related, "not yet implemented");
        return createRecordRequest(
            recordType,
            format(GET_EVENTS_LOAN_CONTRACT_PROPOSAL_CANCELED, related),
            CONTRACT_INITIATION,
            GET_LOAN_CONTRACT_CANCELED,
            createEventData(dataMessage, getLoanContractProposalRelatedToPosition(recorded, related))
        );
    }

    private CloudEventBuildRequest loanContractProposalCreated(String recorded, RecordType recordType) {
        String dataMessage = format(CONTRACT_CREATE_MSG, recorded);
        return createRecordRequest(
            recordType,
            format(GET_EVENTS_LOAN_CONTRACT_PROPOSAL_CREATED, recorded),
            CONTRACT_INITIATION,
            GET_LOAN_CONTRACT_PROPOSAL,
            createEventData(dataMessage, List.of(new RelatedObject(recorded, ONESOURCE_LOAN_CONTRACT_PROPOSAL)))
        );
    }

    private CloudEventBuildRequest loanContractProposalDeclined(String recorded, RecordType recordType,
        String related) {
        String dataMessage = format(CONTRACT_DECLINE_MSG, recorded, related);
        return createRecordRequest(
            recordType,
            format(GET_EVENTS_LOAN_CONTRACT_PROPOSAL_DECLINED, related),
            CONTRACT_INITIATION,
            GET_LOAN_CONTRACT_DECLINED,
            createEventData(dataMessage, getLoanContractProposalRelatedToPosition(recorded, related))
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
            createEventData(dataMessage, getLoanContractProposalRelatedToPosition(recorded, related))
        );
    }

    private CloudEventBuildRequest tradeAgreementMatchingCanceledPosition(String recorded, RecordType recordType,
        String related) {
        String dataMessage = format(MATCHED_CANCELED_POSITION_TRADE_AGREEMENT_MSG, recorded, related);
        return createRecordRequest(
            recordType,
            format(TRADE_AGREEMENT_MATCHED_CANCELED_POSITION, related),
            CONTRACT_INITIATION,
            GET_TRADE_AGREEMENT,
            createEventData(dataMessage, getTradeAgreementRelatedToPosition(recorded, related))
        );
    }

    private CloudEventBuildRequest loanContractProposalValidated(String recorded, RecordType recordType,
        String related) {
        String dataMessage = format(LOAN_CONTRACT_PROPOSAL_RECONCILIATION_MSG, recorded, related);
        return createRecordRequest(
            recordType,
            format(VALIDATE_LOAN_CONTRACT_PROPOSAL_VALIDATED, related),
            CONTRACT_INITIATION,
            VALIDATE_LOAN_CONTRACT_PROPOSAL,
            createEventData(dataMessage, getLoanContractProposalRelatedToPosition(recorded, related))
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

    private CloudEventBuildRequest tradeCancellationEvent(String recorded, RecordType recordType,
        String related) {
        String dataMessage = related == null
            ? format(TRADE_AGREEMENT_CANCELED_EVENT_MSG, recorded)
            : format(TRADE_AGREEMENT_MATCHED_CANCELED_EVENT_MSG, recorded, related);
        String subject = related == null
            ? format(TRADE_AGREEMENT_CANCELED, recorded)
            : format(TRADE_AGREEMENT_CANCELED_MATCHED_POSITION, related);
        CloudEventData data = related == null
            ? createEventData(dataMessage, List.of(new RelatedObject(recorded, ONESOURCE_TRADE_AGREEMENT)))
            : createEventData(dataMessage, getTradeAgreementRelatedToPosition(recorded, related));
        return createRecordRequest(
            recordType,
            subject,
            CONTRACT_INITIATION,
            GET_TRADE_CANCELLATION,
            data
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
            createEventData(message, getLoanProposalRelatedToPosition(record, related))
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

    private CloudEventBuildRequest declineLoanContractProposalExceptionEvent(String record,
        HttpStatusCodeException exception, String related, IntegrationSubProcess subProcess) {
        String message = format(DECLINE_LOAN_PROPOSAL_EXCEPTION_MSG, record, related, exception.getStatusText());
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
        String message = format(APPROVE_LOAN_PROPOSAL_EXCEPTION_MSG, record, related, exception.getStatusText());
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

    private CloudEventBuildRequest getLoanContractProposalExceptionEvent(String record,
        HttpStatusCodeException exception,
        String related) {
        String message = format(GET_LOAN_CONTRACT_PROPOSAL_EXCEPTION_MSG, record, exception.getStatusText());
        return createRecordRequest(
            TECHNICAL_EXCEPTION_1SOURCE,
            format(GET_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE, record),
            CONTRACT_INITIATION,
            GET_LOAN_CONTRACT_PROPOSAL,
            createEventData(message, getLoanProposalRelatedToOnesourceEvent(record, related))
        );

    }

    private CloudEventBuildRequest cancelLoanContractProposalExceptionEvent(String record,
        HttpStatusCodeException exception, String related) {
        String message = format(CANCEL_LOAN_PROPOSAL_MSG, record, related, exception.getStatusText());
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

    private CloudEventBuildRequest settlementInstructionExceptionEventRequest(HttpStatusCodeException exception,
        String positionId) {
        String message = format(GET_SETTLEMENT_INSTRUCTIONS_EXCEPTION_MSG, positionId,
            exception.getStatusText());
        return createRecordRequest(
            TECHNICAL_EXCEPTION_SPIRE,
            format(GET_SETTLEMENT_INSTR_EXCEPTION_SPIRE, positionId),
            CONTRACT_INITIATION,
            GET_SETTLEMENT_INSTRUCTIONS,
            createEventData(message, List.of(new RelatedObject(positionId, POSITION)))
        );
    }

    private CloudEventBuildRequest newPositionsPendingConfirmationExceptionEvent(HttpStatusCodeException exception) {
        String message = format(GET_NEW_POSITIONS_PENDING_CONFIRMATION_EXCEPTION_MSG, exception.getStatusText());
        return createRecordRequest(
            TECHNICAL_EXCEPTION_SPIRE,
            format(GET_POSITION_CONFIRMATION_EXCEPTION_SPIRE, LocalDateTime.now()),
            CONTRACT_INITIATION,
            GET_NEW_POSITIONS_PENDING_CONFIRMATION,
            createEventData(message, List.of(RelatedObject.notApplicable()))
        );
    }

    private CloudEventBuildRequest updatedPositionsPendingConfirmationExceptionEvent(
        HttpStatusCodeException exception) {
        String message = format(GET_UPDATED_POSITIONS_PENDING_CONFIRMATION_EXCEPTION_MSG, exception.getStatusText());
        return createRecordRequest(
            TECHNICAL_EXCEPTION_SPIRE,
            format(GET_UPDATED_POSITIONS_PENDING_CONFIRMATION_EXCEPTION_SPIRE, LocalDateTime.now()),
            CONTRACT_INITIATION,
            GET_UPDATED_POSITIONS_PENDING_CONFIRMATION,
            createEventData(message, List.of(RelatedObject.notApplicable()))
        );
    }

    private CloudEventBuildRequest tradeAgreementExceptionRequest(HttpStatusCodeException exception, String resource,
        String event) {
        var exceptionMessage = switch (exception.getStatusCode().value()) {
            case 401 -> format(GET_TRADE_AGREEMENT_EXCEPTION_1SOURCE_MSG, resource,
                "Not Authorized to do this operation");
            case 404 -> format(GET_TRADE_AGREEMENT_EXCEPTION_1SOURCE_MSG, resource,
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
