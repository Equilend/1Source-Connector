package com.intellecteu.onesource.integration.routes.delegate_flow;

import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.MATCHED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.PROPOSED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UPDATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.VALIDATED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_DECLINED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_OPENED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_PENDING;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_PROPOSED;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.mapper.DeclineInstructionMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.ContractProcessor;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.EventProcessor;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.PositionProcessor;
import com.intellecteu.onesource.integration.utils.IntegrationUtils;
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

    private static final String POSITION_SQL_ENDPOINT = """
        jpa://com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity\
        ?consumeLockEntity=false&consumeDelete=false&%s\
        &sharedEntityManager=true&joinTransaction=false\
        &query=SELECT p FROM PositionEntity p WHERE p.processingStatus IN ('%s')""";

    private static final String NEW_TRADE_EVENT_SQL_ENDPOINT = """
        jpa://com.intellecteu.onesource.integration.repository.entity.onesource.TradeEventEntity\
        ?consumeLockEntity=false&consumeDelete=false&%s\
        &sharedEntityManager=true&joinTransaction=false\
        &query=SELECT e FROM TradeEventEntity e WHERE e.eventType IN ('%s') \
        and e.processingStatus IS NULL OR e.processingStatus = 'CREATED'""";

    private final long updateTimer;

    private final BackOfficeMapper backOfficeMapper;
    private final OneSourceMapper oneSourceMapper;
    private final DeclineInstructionMapper declineInstructionMapper;
    private final ContractProcessor contractProcessor;
    private final PositionProcessor positionProcessor;
    private final EventProcessor eventProcessor;

    public ContractInitiationDelegateFlowRoute(
        @Value("${route.delegate-flow.update-position.timer}") long updateTimer, BackOfficeMapper backOfficeMapper,
        OneSourceMapper oneSourceMapper, DeclineInstructionMapper declineInstructionMapper,
        ContractProcessor contractProcessor, PositionProcessor positionProcessor,
        EventProcessor eventProcessor) {
        this.updateTimer = updateTimer;
        this.backOfficeMapper = backOfficeMapper;
        this.oneSourceMapper = oneSourceMapper;
        this.declineInstructionMapper = declineInstructionMapper;
        this.contractProcessor = contractProcessor;
        this.positionProcessor = positionProcessor;
        this.eventProcessor = eventProcessor;
    }

    @Override
    //@formatter:off
    public void configure() throws Exception {

        from(buildGetNotProcessedTradeEventQuery(CONTRACT_PROPOSED))
            .routeId("GetLoanContractDetails")
            .log(">>> Started GET_LOAN_CONTRACT_PROPOSAL subprocess")
            .bean(oneSourceMapper, "toModel")
            .setHeader("tradeEvent", body())
            .bean(contractProcessor, "getLoanContractDetails")
            .filter(body().isNotNull())
            .bean(contractProcessor, "updateContractProcessingStatusAndCreatedTime")
            .bean(contractProcessor, "saveContract")
            .bean(eventProcessor, "updateEventStatus(${header.tradeEvent}, PROCESSED)")
            .log("<<< Finished GET_LOAN_CONTRACT_PROPOSAL subprocess "
                + "with expected processing statuses: TradeEvent[PROCESSED], Contract[PROPOSED]")
        .end();

        from(buildLenderPostLoanContractQuery(CREATED, UPDATED))
            .routeId("PostLoanContractProposal")
            .log(">>> Started POST_LOAN_CONTRACT_PROPOSAL subprocess")
            .bean(backOfficeMapper, "toModel")
            .setHeader("notMatchedPosition", body())
            .bean(contractProcessor, "createProposalFromPosition")
            .filter(method(positionProcessor, "instructLoanContractProposal(${body}, ${header.notMatchedPosition})"))
            .bean(positionProcessor, "updatePositionForInstructedProposal(${header.notMatchedPosition})")
            .bean(positionProcessor, "savePosition")
            .bean(positionProcessor, "recordEventProposalInstructed")
            .log("<<< Finished POST_LOAN_CONTRACT_PROPOSAL subprocess "
                + "with expected processing statuses: Position[SUBMITTED, UPDATE_SUBMITTED]")
        .end();

        from(buildGetPositionByStatusQuery(CREATED))
            .routeId("MatchPositionWithContractProposal")
            .log(">>> Started GET_NEW_POSITIONS_PENDING_CONFIRMATION process.")
            .bean(backOfficeMapper, "toModel")
                .choice()
                    .when(method(IntegrationUtils.class, "isBorrower"))
                        .bean(positionProcessor, "matchContractProposalAsBorrower")
                .endChoice()
            .end()
            .log("Finished GET_NEW_POSITIONS_PENDING_CONFIRMATION process "
                + "with expected processing statuses: Contract[MATCHED]")
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
            .log("Finished APPROVE_LOAN_CONTRACT_PROPOSAL process"
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
            .filter(body().isNotNull())
            .bean(contractProcessor, "updateContractProcessingStatus(${body}, APPROVED)")
            .bean(contractProcessor, "saveContract")
            .setHeader("contract", body())
            .bean(positionProcessor, "findByPositionId(${body.matchingSpirePositionId})")
            .filter(body().isNotNull())
            .bean(positionProcessor, "updateProcessingStatus(${body}, PROPOSAL_APPROVED)")
            .bean(contractProcessor, "recordApprovedSystemEvent(${header.contract})")
            .bean(eventProcessor, "updateEventStatus(${header.tradeEvent}, PROCESSED)")
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
                .bean(positionProcessor, "updateProcessingStatus(${header.position}, CONFIRMED)")
            .log("<<< Finished POST_POSITION_UPDATE subprocess with expected processing statuses: Position[CONFIRMED]")
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
            .log("Finished UPDATE_LOAN_CONTRACT_SETTL_STATUS process"
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

    private String buildGetPositionByStatusQuery(ProcessingStatus... statuses) {
        return String.format(POSITION_SQL_ENDPOINT,
            String.format("delay=%d", updateTimer),
            Arrays.stream(statuses).map(ProcessingStatus::toString).collect(Collectors.joining("','")));
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
