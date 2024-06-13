package com.intellecteu.onesource.integration.routes.delegate_flow;

import static com.intellecteu.onesource.integration.constant.PositionConstant.BORROWER_POSITION_TYPE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_LOAN_CONTRACT_APPROVED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.MATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.PROPOSED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UPDATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.VALIDATED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_CANCELED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_CANCEL_PENDING;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_DECLINED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_OPENED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_PENDING;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_PROPOSED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.TRADE_AGREED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.TRADE_CANCELED;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.mapper.DeclineInstructionMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.AgreementProcessor;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.ContractProcessor;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.EventProcessor;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.PositionProcessor;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    value = "route.delegate-flow.contract-initiation.enable",
    havingValue = "true",
    matchIfMissing = true)
public class ContractInitiationDelegateFlowRoute extends RouteBuilder {

    private static final String CAMEL_JPA_CONFIG = """
        jpa://%s\
        ?consumeLockEntity=false&consumeDelete=false\
        &sharedEntityManager=true&joinTransaction=false\
        &%s\
        &query=%s""";

    private static final String AGREEMENT_SQL_ENDPOINT = """
        jpa://com.intellecteu.onesource.integration.repository.entity.onesource.AgreementEntity\
        ?consumeLockEntity=false&consumeDelete=false&%s\
        &sharedEntityManager=true&joinTransaction=false\
        &query=SELECT a FROM AgreementEntity a WHERE a.processingStatus IN ('%s')""";

    private static final String POSITION_SQL_ENDPOINT = """
        jpa://com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity\
        ?consumeLockEntity=false&consumeDelete=false&%s\
        &sharedEntityManager=true&joinTransaction=false\
        &query=SELECT p FROM PositionEntity p WHERE p.processingStatus IN ('%s')""";

    private static final String POSITION_BY_PARTICIPANT_SQL_ENDPOINT = """
        jpa://com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity\
        ?consumeLockEntity=false&consumeDelete=false&%s\
        &sharedEntityManager=true&joinTransaction=false\
        &query=SELECT p FROM PositionEntity p WHERE p.processingStatus IN ('%s') \
        AND p.positionType.positionType = '%s'""";

    private static final String NEW_TRADE_EVENT_SQL_ENDPOINT = """
        jpa://com.intellecteu.onesource.integration.repository.entity.onesource.TradeEventEntity\
        ?consumeLockEntity=false&consumeDelete=false&%s\
        &sharedEntityManager=true&joinTransaction=false\
        &query=SELECT e FROM TradeEventEntity e WHERE e.eventType IN ('%s') \
        and (e.processingStatus IS NULL OR e.processingStatus = 'CREATED')""";

    private final long updateTimer;

    private final BackOfficeMapper backOfficeMapper;
    private final OneSourceMapper oneSourceMapper;
    private final DeclineInstructionMapper declineInstructionMapper;
    private final ContractProcessor contractProcessor;
    private final PositionProcessor positionProcessor;
    private final EventProcessor eventProcessor;
    private final AgreementProcessor agreementProcessor;
    private final Integer redeliveryPolicyMaxRedeliveries;
    private final String redeliveryPolicyDelayPattern;

    public ContractInitiationDelegateFlowRoute(
        @Value("${route.delegate-flow.update-position.timer}") long updateTimer, BackOfficeMapper backOfficeMapper,
        OneSourceMapper oneSourceMapper, DeclineInstructionMapper declineInstructionMapper,
        ContractProcessor contractProcessor, PositionProcessor positionProcessor,
        EventProcessor eventProcessor, AgreementProcessor agreementProcessor,
        @Value("${route.delegate-flow.contract-initiation.redelivery-policy.max-redeliveries}") Integer redeliveryPolicyMaxRedeliveries,
        @Value("${route.delegate-flow.contract-initiation.redelivery-policy.delay-pattern}") String redeliveryPolicyDelayPattern) {
        this.updateTimer = updateTimer;
        this.backOfficeMapper = backOfficeMapper;
        this.oneSourceMapper = oneSourceMapper;
        this.declineInstructionMapper = declineInstructionMapper;
        this.contractProcessor = contractProcessor;
        this.positionProcessor = positionProcessor;
        this.eventProcessor = eventProcessor;
        this.agreementProcessor = agreementProcessor;
        this.redeliveryPolicyMaxRedeliveries = redeliveryPolicyMaxRedeliveries;
        this.redeliveryPolicyDelayPattern = redeliveryPolicyDelayPattern;
    }

    @Override
    //@formatter:off
    public void configure() throws Exception {
        onException(Exception.class)
            .maximumRedeliveries(redeliveryPolicyMaxRedeliveries)
            .delayPattern(redeliveryPolicyDelayPattern)
            .handled(true)
            .log("Caught an error: ${exception.message}")
            .end();

        from(buildGetNotProcessedTradeEventQuery(TRADE_AGREED))
            .routeId("GetTradeAgreement")
            .log(">>> Started GET_TRADE_AGREEMENT subprocess")
            .bean(oneSourceMapper, "toModel")
            .setHeader("tradeEvent", body())
            .bean(agreementProcessor, "getAgreementDetails")
                .choice()
                    .when(body().isNotNull())
                        .bean(agreementProcessor, "createAgreement")
                .end()
            .bean(eventProcessor, "updateEventStatus(${header.tradeEvent}, PROCESSED)")
            .log("<<< Finished GET_TRADE_AGREEMENT subprocess "
                + "with expected processing statuses: TradeEvent[PROCESSED], Agreement[CREATED]")
        .end();

        from(buildGetAgreementByStatusQuery(CREATED))
            .routeId("MatchTradeAgreement")
            .log(">>> Started MATCH_TRADE_AGREEMENT subprocess")
            .bean(oneSourceMapper, "toModel")
            .bean(agreementProcessor, "matchAgreementWithPosition")
            .log("<<< Finished MATCH_TRADE_AGREEMENT subprocess with expected processing statuses: Agreement[MATCHED, UNMATCHED]")
        .end();

        from(buildGetAgreementByStatusQuery(MATCHED))
            .routeId("ReconcileTradeAgreement")
            .log(">>> Started RECONCILE_TRADE_AGREEMENT subprocess")
            .bean(oneSourceMapper, "toModel")
            .bean(agreementProcessor, "reconcileAgreementWithPosition")
            .log("<<< Finished RECONCILE_TRADE_AGREEMENT subprocess with expected processing statuses: Agreement[RECONCILED, DISCREPANCIES]")
        .end();

        from(buildGetNotProcessedTradeEventQuery(CONTRACT_PROPOSED))
            .routeId("GetLoanContractDetails")
            .log(">>> Started GET_LOAN_CONTRACT_PROPOSAL subprocess")
            .bean(oneSourceMapper, "toModel")
            .setHeader("tradeEvent", body())
            .bean(contractProcessor, "getLoanContractDetails")
            .filter(body().isNotNull())
            .bean(contractProcessor, "updateContractProcessingStatusAndCreatedTime(${body}, PROPOSED)")
            .bean(contractProcessor, "saveContract")
            .bean(eventProcessor, "updateEventStatus(${header.tradeEvent}, PROCESSED)")
            .log("<<< Finished GET_LOAN_CONTRACT_PROPOSAL subprocess "
                + "with expected processing statuses: TradeEvent[PROCESSED], Contract[PROPOSED]")
        .end();

        from(buildGetPositionByParticipantAndStatusQuery(BORROWER_POSITION_TYPE, CREATED))
            .routeId("GetNewPositionsAndMatchIfBorrower")
            .log(">>> Started GET_NEW_POSITIONS_PENDING_CONFIRMATION process for borrower.")
            .bean(backOfficeMapper, "toModel")
            .bean(positionProcessor, "matchContractProposalAsBorrower")
            .log("<<< Finished GET_NEW_POSITIONS_PENDING_CONFIRMATION process for borrower "
                + "with expected processing statuses: Contract[MATCHED], Agreement[MATCHED]")
            .end();

        from(buildLenderPostLoanContractQuery(CREATED, UPDATED))
            .routeId("PostLoanContractProposal")
            .log(">>> Started POST_LOAN_CONTRACT_PROPOSAL subprocess")
            .bean(backOfficeMapper, "toModel")
            .setHeader("notMatchedPosition", body())
            .bean(positionProcessor, "manageFigiCode")
            .filter(body().isNotNull())
            .bean(contractProcessor, "createProposalFromPosition(${header.notMatchedPosition})")
            .filter(body().isNotNull())
            .filter(method(positionProcessor, "instructLoanContractProposal(${body}, ${header.notMatchedPosition})"))
            .bean(positionProcessor, "updatePositionForInstructedProposal(${header.notMatchedPosition})")
            .bean(positionProcessor, "savePosition")
            .bean(positionProcessor, "recordEventProposalInstructed")
            .log("<<< Finished POST_LOAN_CONTRACT_PROPOSAL subprocess "
                + "with expected processing statuses: Position[SUBMITTED, UPDATE_SUBMITTED]")
        .end();

        from(buildGetContractByStatusQuery(PROPOSED))
            .routeId("MatchLoanContractProposal")
            .log(">>> Started MATCH_LOAN_CONTRACT_PROPOSAL process.")
            .bean(oneSourceMapper, "toModel")
            .setHeader("currentContract", body())
            .bean(contractProcessor, "matchLenderPosition")
                .choice()
                    .when().simple("${body} == false") // if lender was not matched, should execute matching for borrower
                    .bean(contractProcessor, "matchBorrowerPosition(${header.currentContract})")
                .end()
            .log("Finished MATCH_LOAN_CONTRACT_PROPOSAL process"
                + "with expected processing statuses: Contract[MATCHED, UNMATCHED]")
        .end();

        from(buildContractQueryByParticipantAndProcessingStatuses("CASH BORROW", MATCHED))
            .routeId("ValidateLoanContractProposalWithMatchedPosition")
            .log(">>> Started VALIDATE_LOAN_CONTRACT_PROPOSAL process for matched position.")
            .bean(oneSourceMapper, "toModel")
            .bean(contractProcessor, "validate")
            .filter(body().isNotNull())
            .bean(contractProcessor, "recordContractProposalValidatedEvent")
            .log("Finished VALIDATE_LOAN_CONTRACT_PROPOSAL process for matched position "
                + "with expected processing statuses: Contract[VALIDATED, DISCREPANCIES]")
        .end();

        from(buildDiscrepanciesContractQuery())
            .routeId("ValidateLoanContractProposalWithDiscrepancies")
            .log(">>> Started VALIDATE_LOAN_CONTRACT_PROPOSAL process for contracts in DISCREPANCIES.")
            .bean(oneSourceMapper, "toModel")
            .bean(contractProcessor, "validate")
            .filter(body().isNotNull())
            .bean(contractProcessor, "recordContractProposalValidatedEvent")
            .log("Finished VALIDATE_LOAN_CONTRACT_PROPOSAL process for contract in DISCREPANCIES "
                + "with expected processing statuses: Contract[VALIDATED, DISCREPANCIES]")
            .end();

        from(buildGetValidatedContractForBorrowerQuery(VALIDATED))
            .routeId("ApproveLoanContractProposalAsBorrower")
            .log(">>> Started APPROVE_LOAN_CONTRACT_PROPOSAL process.")
            .bean(oneSourceMapper, "toModel")
            .bean(contractProcessor, "instructContractApprovalAsBorrower")
            .log("Finished APPROVE_LOAN_CONTRACT_PROPOSAL process "
                + "with expected processing statuses: Contract[APPROVAL_SUBMITTED]")
        .end();

        from(buildGetDeclineInstructionsQuery())
            .routeId("DeclineLoanContractProposalAsBorrower")
            .log(">>> Started DECLINE_LOAN_CONTRACT_PROPOSAL process.")
            .bean(declineInstructionMapper, "toModel")
            .setHeader("declineInstruction", body())
            .bean(contractProcessor, "retrieveContractFromDeclineInstruction")
            .filter(body().isNotNull())
            .bean(contractProcessor, "instructDeclineLoanContractProposal(${body}, ${header.declineInstruction})")
            .log("Finished DECLINE_LOAN_CONTRACT_PROPOSAL process"
                + "with expected processing statuses: Contract[DECLINE_SUBMITTED], DeclineInstruction[PROCESSED]")
        .end();

        from(buildGetNotProcessedTradeEventQuery(CONTRACT_PENDING))
            .routeId("GetLoanContractApproved")
            .log(">>> Started GET_LOAN_CONTRACT_APPROVED subprocess")
            .bean(oneSourceMapper, "toModel")
            .setHeader("tradeEvent", body())
            .bean(contractProcessor, "getLoanContractDetails")
            .bean(contractProcessor, "updateContractWithContractDetails")
            .choice()
                .when(simple("${body} == null"))
                    .bean(contractProcessor, String.format("recordIntegrationIssueEvent(${header.tradeEvent}, %s)", GET_LOAN_CONTRACT_APPROVED))
                    .bean(eventProcessor, "updateEventStatus(${header.tradeEvent}, PROCESSED)")
                .otherwise()
                    .bean(contractProcessor, "updateContractProcessingStatus(${body}, APPROVED)")
                    .setHeader("contract", body())
                    .bean(positionProcessor, "findByPositionId(${body.matchingSpirePositionId})")
                    .filter(body().isNotNull())
                    .bean(positionProcessor, "updateCounterparty(${body}, ${header.contract})")
                    .bean(positionProcessor, "updateProcessingStatus(${body}, PROPOSAL_APPROVED)")
                    .bean(contractProcessor, "recordApprovedSystemEvent(${header.contract})")
                    .bean(eventProcessor, "updateEventStatus(${header.tradeEvent}, PROCESSED)")
            .end()
            .log("<<< Finished GET_LOAN_CONTRACT_APPROVED subprocess with expected processing statuses: "
                + "TradeEvent[PROCESSED], Contract[APPROVED], Position[PROPOSAL_APPROVED]")
        .end();

        from(buildGetPositionByStatusQuery(PROPOSAL_APPROVED))
            .routeId("PostPositionUpdate")
            .log(">>> Started POST_POSITION_UPDATE subprocess")
            .bean(backOfficeMapper, "toModel")
            .setHeader("position", body())
            .bean(positionProcessor, "instructUpdatePosition")
            .filter(simple("${body} == true"))
                .bean(positionProcessor, "updatePositionStatus(${header.position}, FUTURE)")
                .bean(positionProcessor, "updateProcessingStatus(${body}, CONFIRMED)")
            .log("<<< Finished POST_POSITION_UPDATE subprocess with expected processing statuses: Position[CONFIRMED]")
        .end();

        from(String.format("timer://eventTimer?period=%d", updateTimer))
            .routeId("ProcessPositionCanceled")
            .log(">>> Started PROCESS_POSITION_CANCELED subprocess")
            .bean(positionProcessor, "retrieveCanceledPositions")
            .split(body())
            .bean(positionProcessor, "updatePositionStatus(${body}, CANCELLED)")
            .bean(positionProcessor, "updateProcessingStatus(${body}, CANCELED)")
            .setHeader("position", body())
            .bean(contractProcessor, "instructCancelLoanContract")
            .filter(simple("${body} == true"))
                .bean(contractProcessor, "recordPositionCancelSubmittedSystemEvent(${header.position})")
            .log("<<< Finished PROCESS_POSITION_CANCELED subprocess "
                + "with expected processing statuses: Position[CANCELED]")
            .end();

        from(buildGetNotProcessedTradeEventQuery(CONTRACT_DECLINED))
            .routeId("GetLoanContractDeclined")
            .log(">>> Started GET_LOAN_CONTRACT_DECLINED subprocess")
            .bean(oneSourceMapper, "toModel")
            .setHeader("tradeEvent", body())
            .bean(contractProcessor, "declineCapturedContract")
                .choice()
                    .when(body().isNull())
                        .bean(eventProcessor, "recordContractDeclineIssue(${header.tradeEvent})")
                    .otherwise()
                        .bean(contractProcessor, "recordLoanProposalDeclinedSystemEvent")
                .end()
            .bean(eventProcessor, "updateEventStatus(${header.tradeEvent}, PROCESSED)")
            .log("<<< Finished GET_LOAN_CONTRACT_DECLINED subprocess with expected processing statuses: "
                + "TradeEvent[PROCESSED], Contract[DECLINED]")
        .end();

        from(String.format("timer://eventTimer?period=%d", updateTimer))
            .routeId("UpdateLoanContractSettlementStatus")
            .log(">>> Started UPDATE_LOAN_CONTRACT_SETTL_STATUS process.")
            .bean(positionProcessor, "getAllByPositionStatus(FUTURE)")
            .bean(positionProcessor, "updateCapturedPositions")
            .log("<<< Finished UPDATE_LOAN_CONTRACT_SETTL_STATUS process"
                + "with expected processing statuses: Contract_Settlement[SETTLED]")
        .end();

        from(buildGetNotProcessedTradeEventQuery(CONTRACT_OPENED))
            .routeId("CaptureLoanContractSettled")
            .log(">>> Started GET_LOAN_CONTRACT_SETTLED subprocess")
            .bean(oneSourceMapper, "toModel")
            .setHeader("tradeEvent", body())
            .bean(contractProcessor, "updateSettledContract")
            .bean(eventProcessor, "updateEventStatus(${header.tradeEvent}, PROCESSED)")
            .log("<<< Finished GET_LOAN_CONTRACT_SETTLED subprocess with expected processing statuses: "
                + "TradeEvent[PROCESSED], Contract[SETTLED]")
        .end();

        from(buildGetNotProcessedTradeEventQuery(TRADE_CANCELED))
            .routeId("GetTradeCancelation")
            .log(">>> Started GET_TRADE_CANCELATION subprocess")
            .bean(oneSourceMapper, "toModel")
            .setHeader("tradeEvent", body())
            .bean(agreementProcessor, "retrieveAgreementFromEvent")
                .choice()
                    .when(body().isNull())
                        .bean(agreementProcessor, "recordAgreementCancelIssue(${header.tradeEvent})")
                    .otherwise()
                        .bean(agreementProcessor, "executeCancelUpdate")
                .end()
            .bean(eventProcessor, "updateEventStatus(${header.tradeEvent}, PROCESSED)")
            .log("<<< Finished GET_TRADE_CANCELATION subprocess with expected processing statuses: "
                + "TradeEvent[PROCESSED], Agreement[CANCELED]")
        .end();

        from(buildGetNotProcessedTradeEventQuery(CONTRACT_CANCELED))
            .routeId("GetLoanContractCanceled")
            .log(">>> Started GET_LOAN_CONTRACT_CANCELED subprocess")
            .bean(oneSourceMapper, "toModel")
            .setHeader("tradeEvent", body())
            .bean(contractProcessor, "retrieveContractFromEvent")
                .choice()
                    .when(body().isNull())
                        .bean(eventProcessor, "recordContractCancelIssue(${header.tradeEvent})")
                    .otherwise()
                        .bean(contractProcessor, "executeCancelUpdate")
            .end()
            .bean(eventProcessor, "updateEventStatus(${header.tradeEvent}, PROCESSED)")
            .log("<<< Finished GET_LOAN_CONTRACT_CANCELED subprocess with expected processing statuses: "
                + "TradeEvent[PROCESSED], Contract[CANCELED]")
        .end();

        from(buildGetNotProcessedTradeEventQuery(CONTRACT_CANCEL_PENDING))
            .routeId("ProcessLoanContractPendingCancellation")
            .log(">>> Started PROCESS_LOAN_CONTRACT_PENDING_CANCELLATION subprocess")
            .bean(oneSourceMapper, "toModel")
            .setHeader("tradeEvent", body())
            .bean(contractProcessor, "retrieveContractFromEvent")
                .choice()
                    .when(body().isNull())
                        .bean(eventProcessor, "recordCancelPendingIssue(${header.tradeEvent})")
                .otherwise()
                    .bean(contractProcessor, "updateContractStatus(${body}, CANCEL_PENDING)")
                    .bean(contractProcessor, "updateContractProcessingStatus(${body}, CANCEL_PENDING)")
                    .bean(contractProcessor, "recordCancelPendingEvent")
            .end()
            .bean(eventProcessor, "updateEventStatus(${header.tradeEvent}, PROCESSED)")
            .log("<<< Finished PROCESS_LOAN_CONTRACT_PENDING_CANCELLATION subprocess with expected processing statuses: "
                + "TradeEvent[PROCESSED], Contract[CANCEL_PENDING]")
            .end();
    }
    private String buildDiscrepanciesContractQuery() {
        String query = """
            SELECT c FROM ContractEntity c \
            JOIN PositionEntity p ON c.matchingSpirePositionId = p.positionId \
            WHERE c.processingStatus = 'DISCREPANCIES' \
            AND p.processingStatus = 'UPDATED' \
            AND p.positionType.positionType = 'CASH BORROW' \
            AND c.lastUpdateDateTime < p.lastUpdateDateTime""";
        return String.format(CAMEL_JPA_CONFIG,
            "com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity",
            String.format("delay=%d", updateTimer),
            query);
    }

    private String buildGetDeclineInstructionsQuery() {
        String query = """
            SELECT d FROM DeclineInstructionEntity d WHERE d.relatedProposalType = 'CONTRACT' \
            AND (d.processingStatus IS NULL OR d.processingStatus = 'CREATED')""";
        return String.format(CAMEL_JPA_CONFIG,
            "com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity",
            String.format("delay=%d", updateTimer),
            query);

    }

    private String buildGetContractByStatusQuery(ProcessingStatus... statuses) {
        String query = String.format("""
            SELECT c FROM ContractEntity c WHERE c.processingStatus IN ('%s')""",
            Arrays.stream(statuses).map(ProcessingStatus::toString).collect(Collectors.joining("','")));
        return String.format(CAMEL_JPA_CONFIG,
            "com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity",
            String.format("delay=%d", updateTimer),
            query);
    }

    private String buildContractQueryByParticipantAndProcessingStatuses(String participantPositionType,
        ProcessingStatus... statuses) {
        String query = String.format("""
            SELECT c FROM ContractEntity c \
            JOIN PositionEntity p ON c.matchingSpirePositionId = p.positionId \
            WHERE p.positionType.positionType = '%s' \
            AND c.processingStatus IN ('%s')""",
            participantPositionType,
            Arrays.stream(statuses).map(ProcessingStatus::toString).collect(Collectors.joining("','")));
        return String.format(CAMEL_JPA_CONFIG,
            "com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity",
            String.format("delay=%d", updateTimer),
            query);
    }

    private String buildLenderPostLoanContractQuery(ProcessingStatus... statuses) {
        String query = String.format("""
            SELECT p FROM PositionEntity p WHERE p.matching1SourceLoanContractId IS NULL \
            AND p.positionType.positionType = 'CASH LOAN' \
            AND p.processingStatus IN ('%s')""",
            Arrays.stream(statuses).map(ProcessingStatus::toString).collect(Collectors.joining("','")));
        return String.format(CAMEL_JPA_CONFIG,
            "com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity",
            String.format("delay=%d", updateTimer),
            query);
    }

    private String buildGetAgreementByStatusQuery(ProcessingStatus... statuses) {
        return String.format(AGREEMENT_SQL_ENDPOINT,
            String.format("delay=%d", updateTimer),
            Arrays.stream(statuses).map(ProcessingStatus::toString).collect(Collectors.joining("','")));
    }

    private String buildGetPositionByStatusQuery(ProcessingStatus... statuses) {
        return String.format(POSITION_SQL_ENDPOINT,
            String.format("delay=%d", updateTimer),
            Arrays.stream(statuses).map(ProcessingStatus::toString).collect(Collectors.joining("','")));
    }

    private String buildGetPositionByParticipantAndStatusQuery(String participant, ProcessingStatus... statuses) {
        return String.format(POSITION_BY_PARTICIPANT_SQL_ENDPOINT,
            String.format("delay=%d", updateTimer),
            Arrays.stream(statuses).map(ProcessingStatus::toString).collect(Collectors.joining("','")),
            participant);
    }

    private String buildGetValidatedContractForBorrowerQuery(ProcessingStatus... statuses) {
        String query = String.format("""
            SELECT c FROM ContractEntity c \
            JOIN PositionEntity p ON c.matchingSpirePositionId = p.positionId \
            WHERE p.positionType.positionType = 'CASH BORROW' \
            AND c.processingStatus IN ('%s')""",
            Arrays.stream(statuses).map(ProcessingStatus::toString).collect(Collectors.joining("','")));
        return String.format(CAMEL_JPA_CONFIG,
            "com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity",
            String.format("delay=%d", updateTimer),
            query);
    }

    private String buildGetNotProcessedTradeEventQuery(EventType... eventTypes) {
        return String.format(NEW_TRADE_EVENT_SQL_ENDPOINT,
            String.format("delay=%d", updateTimer),
            Arrays.stream(eventTypes).map(EventType::toString).collect(Collectors.joining("','")));
    }
}
