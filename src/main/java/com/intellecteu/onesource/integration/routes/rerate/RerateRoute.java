package com.intellecteu.onesource.integration.routes.rerate;

import static com.intellecteu.onesource.integration.model.enums.CorrectionInstructionType.RERATE_AMEND;
import static com.intellecteu.onesource.integration.model.enums.CorrectionInstructionType.RERATE_CANCELLED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.PROPOSAL_APPROVED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.PROPOSED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TO_VALIDATE;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.VALIDATED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RERATE_APPLIED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RERATE_CANCELED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RERATE_CANCEL_PENDING;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RERATE_DECLINED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RERATE_PENDING;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RERATE_PROPOSED;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.mapper.CorrectionInstructionMapper;
import com.intellecteu.onesource.integration.mapper.DeclineInstructionMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.enums.CorrectionInstructionType;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.routes.rerate.processor.RerateEventProcessor;
import com.intellecteu.onesource.integration.routes.rerate.processor.RerateProcessor;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    value = "route.rerate.enable",
    havingValue = "true",
    matchIfMissing = true)
public class RerateRoute extends RouteBuilder {

    private static final String TRADE_EVENT_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.repository.entity.onesource.TradeEventEntity?"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT e FROM TradeEventEntity e WHERE e.processingStatus = '%s' AND e.eventType IN ('%s')";

    private static final String RERATE_TRADE_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.repository.entity.backoffice.RerateTradeEntity?"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT r FROM RerateTradeEntity r WHERE r.processingStatus = '%s'";

    private static final String RERATE_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.repository.entity.onesource.RerateEntity?"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT r FROM RerateEntity r WHERE r.processingStatus = '%s'";

    private static final String DECLINE_INSTRUCTION_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity?"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT d FROM DeclineInstructionEntity d WHERE d.relatedProposalType = 'RERATE' AND d.processingStatus IS NULL";

    private static final String CORRECTION_INSTRUCTION_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.repository.entity.toolkit.CorrectionInstructionEntity?"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT c FROM CorrectionInstructionEntity c WHERE c.instructionType = '%s' AND c.relatedProposalType = 'RERATE' AND c.processingStatus IS NULL";

    private final RerateProcessor rerateProcessor;
    private final RerateEventProcessor rerateEventProcessor;
    private final OneSourceMapper oneSourceMapper;
    private final BackOfficeMapper backOfficeMapper;
    private final DeclineInstructionMapper declineInstructionMapper;
    private final CorrectionInstructionMapper correctionInstructionMapper;

    @Autowired
    public RerateRoute(RerateProcessor rerateProcessor, RerateEventProcessor rerateEventProcessor, OneSourceMapper oneSourceMapper,
        BackOfficeMapper backOfficeMapper, DeclineInstructionMapper declineInstructionMapper,
        CorrectionInstructionMapper correctionInstructionMapper) {
        this.rerateProcessor = rerateProcessor;
        this.rerateEventProcessor = rerateEventProcessor;
        this.oneSourceMapper = oneSourceMapper;
        this.backOfficeMapper = backOfficeMapper;
        this.declineInstructionMapper = declineInstructionMapper;
        this.correctionInstructionMapper = correctionInstructionMapper;
    }

    @Override
    //@formatter:off
    public void configure() throws Exception {

        from("timer://eventTimer?period={{camel.newBackOfficeTradeEventTimer}}")
            .routeId("NewBackOfficeRerateTradeRoute")
            .log(">>> Started GET_NEW_TRADE_EVENTS_PENDING_CONFIRMATION for RerateTrades")
            .bean(rerateProcessor, "fetchNewRerateTrades")
            .split(body())
                .to("direct:recordRerateTrade")
            .end()
            .log("<<< Finished GET_NEW_TRADE_EVENTS_PENDING_CONFIRMATION for RerateTrades");

        from("direct:recordRerateTrade")
            .log(">>> Started PROCESS_RERATE_PENDING_CONFIRMATION for RerateTrade: ${body.tradeId}")
            .bean(rerateProcessor, "updateRerateTradeCreationDatetime")
            .bean(rerateProcessor, "updateRerateTradeProcessingStatus(${body}, CREATED)")
            .bean(rerateProcessor, "saveRerateTrade")
            .to("direct:matchRerate")
            .log("<<< Finished PROCESS_RERATE_PENDING_CONFIRMATION for RerateTrade: ${body.tradeId} with expected statuses: RerateTrade[CREATED]");

        from("direct:matchRerate")
            .log(">>> Started PROCESS_RERATE_PENDING_CONFIRMATION for RerateTrade: ${body.tradeId}")
            .bean(rerateProcessor, "matchBackOfficeRerateTradeWith1SourceRerate")
            .bean(rerateProcessor, "saveRerateTrade")
            .log("<<< Finished PROCESS_RERATE_PENDING_CONFIRMATION for RerateTrade: ${body.tradeId} with expected statuses: Rerate[MATCHED]");

        from(createUnmatchedRerateTradeSQLEndpoint(CREATED))
            .log(">>> Started POST_RERATE_PROPOSAL for RerateTrade: ${body.tradeId}")
            .bean(backOfficeMapper, "toModel")
            .bean(rerateProcessor, "instructRerateTrade")
            .bean(rerateProcessor, "saveRerateTrade")
            .log("<<< Finished POST_RERATE_PROPOSAL for RerateTrade: ${body.tradeId} with expected statuses: RerateTrade[SUBMITTED, WAITING_PROPOSAL]");

        from(createTradeEventSQLEndpoint(CREATED, RERATE_PROPOSED))
            .log(">>> Started GET_RERATE_PROPOSAL for TradeEvent: ${body.eventId}")
            .bean(oneSourceMapper, "toModel")
            .bean(rerateEventProcessor, "processRerateProposedEvent")
            .bean(rerateEventProcessor, "updateEventProcessingStatus(${body}, PROCESSED)")
            .bean(rerateEventProcessor, "saveEvent")
            .log("<<< Finished GET_RERATE_PROPOSAL for TradeEvent: ${body.eventId} with expected statuses: TradeEvent[PROCESSED], Rerate[PROPOSED]");

        from(createRerateSQLEndpoint(PROPOSED))
            .log(">>> Started MATCH_RERATE_PROPOSAL for Rerate: ${body.rerateId}")
            .bean(oneSourceMapper, "toModel")
            .bean(rerateProcessor, "match1SourceRerateWithBackOfficeRerateTrade")
            .bean(rerateProcessor, "saveRerate")
            .log(">>> Finished MATCH_RERATE_PROPOSAL for Rerate: ${body.rerateId} with expected statuses: Rerate[MATCHED, TO_VALIDATE, UNMATCHED]");

        from(createRerateSQLEndpoint(TO_VALIDATE))
            .log(">>> Started VALIDATE_RERATE_PROPOSAL for Rerate: ${body.rerateId}")
            .bean(oneSourceMapper, "toModel")
            .bean(rerateProcessor, "validateRerate")
            .bean(rerateProcessor, "saveRerate")
            .log(">>> Finished VALIDATE_RERATE_PROPOSAL for Rerate: ${body.rerateId} with expected statuses: Rerate[VALIDATED, DISCREPANCIES]");

        from(createRerateSQLEndpoint(VALIDATED))
            .log(">>> Started APPROVE_RERATE_PROPOSAL for Rerate: ${body.rerateId}")
            .bean(oneSourceMapper, "toModel")
            .bean(rerateProcessor, "approveRerate")
            .bean(rerateProcessor, "saveRerate")
            .log(">>> Finished APPROVE_RERATE_PROPOSAL for Rerate: ${body.rerateId} with expected statuses: Rerate[APPROVAL_SUBMITTED]");

        from(createTradeEventSQLEndpoint(CREATED, RERATE_PENDING))
            .log(">>> Started GET_RERATE_APPROVED for TradeEvent: ${body.eventId}")
            .bean(oneSourceMapper, "toModel")
            .bean(rerateEventProcessor, "processReratePendingEvent")
            .bean(rerateEventProcessor, "updateEventProcessingStatus(${body}, PROCESSED)")
            .bean(rerateEventProcessor, "saveEvent")
            .log("<<< Finished GET_RERATE_APPROVED for TradeEvent: ${body.eventId} with expected statuses: TradeEvent[PROCESSED], Rerate[APPROVED], RerateTrade[PROPOSAL_APPROVED]");

        from(createRerateTradeSQLEndpoint(PROPOSAL_APPROVED))
            .log(">>> Started POST_RERATE_TRADE_CONFIRMATION for RerateTrade: ${body.tradeId}")
            .bean(backOfficeMapper, "toModel")
            .bean(rerateProcessor, "confirmRerateTrade")
            .bean(rerateProcessor, "saveRerateTrade")
            .log("<<< Finished POST_RERATE_TRADE_CONFIRMATION for RerateTrade: ${body.tradeId} with expected statuses: RerateTrade[CONFIRMED]");

        from(createTradeEventSQLEndpoint(CREATED, RERATE_APPLIED))
            .log(">>> Started PROCESS_RERATE_APPLIED for TradeEvent: ${body.eventId}")
            .bean(oneSourceMapper, "toModel")
            .bean(rerateEventProcessor, "processRerateAppliedEvent")
            .bean(rerateEventProcessor, "updateEventProcessingStatus(${body}, PROCESSED)")
            .bean(rerateEventProcessor, "saveEvent")
            .log("<<< Finished PROCESS_RERATE_APPLIED for TradeEvent: ${body.eventId} with expected statuses: TradeEvent[PROCESSED], Rerate[APPLIED]");

        from(createUnprocessedDeclineInstructionSQLEndpoint())
            .log(">>> Started DECLINE_RERATE_PROPOSAL for DeclineInstruction: ${body.declineInstructionId}")
            .bean(declineInstructionMapper, "toModel")
            .bean(rerateProcessor, "declineRerate")
            .bean(rerateProcessor, "updateDeclineInstructionProcessingStatus(${body}, PROCESSED)")
            .bean(rerateProcessor, "saveDeclineInstruction")
            .log("<<< Finished DECLINE_RERATE_PROPOSAL for DeclineInstruction: ${body.declineInstructionId} with expected statuses: DeclineInstruction[PROCESSED], Rerate[DECLINE_SUBMITTED]");

        from(createTradeEventSQLEndpoint(CREATED, RERATE_DECLINED))
            .log(">>> Started PROCESS_RERATE_DECLINED for TradeEvent: ${body.eventId}")
            .bean(oneSourceMapper, "toModel")
            .bean(rerateEventProcessor, "processRerateDeclinedEvent")
            .bean(rerateEventProcessor, "updateEventProcessingStatus(${body}, PROCESSED)")
            .bean(rerateEventProcessor, "saveEvent")
            .log("<<< Finished PROCESS_RERATE_DECLINED for TradeEvent: ${body.eventId} with expected statuses: TradeEvent[PROCESSED], Rerate[DECLINED]");

        from(createUnprocessedCorrectionInstructionSQLEndpoint(RERATE_AMEND))
            .log(">>> Started PROCESS_TRADE_UPDATE for CorrectionInstruction: ${body.instructionId}")
            .bean(correctionInstructionMapper, "toModel")
            .bean(rerateProcessor, "amendRerateTrade")
            .bean(rerateProcessor, "updateCorrectionInstructionProcessingStatus(${body}, PROCESSED)")
            .bean(rerateProcessor, "saveCorrectionInstruction")
            .log("<<< Finished PROCESS_TRADE_UPDATE for CorrectionInstruction: ${body.instructionId} with expected statuses: CorrectionInstruction[PROCESSED], Rerate[PROPOSED, TO_VALIDATE], RerateTrade[REPLACED]");

        from(createUnprocessedCorrectionInstructionSQLEndpoint(RERATE_CANCELLED))
            .log(">>> Started PROCESS_TRADE_CANCEL for CorrectionInstruction: ${body.instructionId}")
            .bean(correctionInstructionMapper, "toModel")
            .bean(rerateProcessor, "cancelRerateTrade")
            .bean(rerateProcessor, "updateCorrectionInstructionProcessingStatus(${body}, PROCESSED)")
            .bean(rerateProcessor, "saveCorrectionInstruction")
            .log("<<< Finished PROCESS_TRADE_CANCEL for CorrectionInstruction: ${body.instructionId} with expected statuses: CorrectionInstruction[PROCESSED], RerateTrade[CANCELED], Rerate[PROPOSED, CANCEL_SUBMITTED]");

        from(createTradeEventSQLEndpoint(CREATED, RERATE_CANCELED))
            .log(">>> Started PROCESS_RERATE_PROPOSAL_CANCELED for TradeEvent: ${body.eventId}")
            .bean(oneSourceMapper, "toModel")
            .bean(rerateEventProcessor, "processRerateCanceledEvent")
            .bean(rerateEventProcessor, "updateEventProcessingStatus(${body}, PROCESSED)")
            .bean(rerateEventProcessor, "saveEvent")
            .log("<<< Finished PROCESS_RERATE_PROPOSAL_CANCELED for TradeEvent: ${body.eventId} with expected statuses: TradeEvent[PROCESSED], Rerate[CANCELED]");

        from(createTradeEventSQLEndpoint(CREATED, RERATE_CANCEL_PENDING))
            .log(">>> Started PROCESS_RERATE_CANCEL_PENDING for TradeEvent: ${body.eventId}")
            .bean(oneSourceMapper, "toModel")
            .bean(rerateEventProcessor, "processReratePendingCancelEvent")
            .bean(rerateEventProcessor, "updateEventProcessingStatus(${body}, PROCESSED)")
            .bean(rerateEventProcessor, "saveEvent")
            .log("<<< Finished PROCESS_RERATE_CANCEL_PENDING for TradeEvent: ${body.eventId} with expected statuses: TradeEvent[PROCESSED], Rerate[CANCEL_PENDING]");

    }
    //@formatter:on

    private String createTradeEventSQLEndpoint(ProcessingStatus status, EventType... eventTypes) {
        return String.format(TRADE_EVENT_SQL_ENDPOINT, status,
            Arrays.stream(eventTypes).map(EventType::toString).collect(Collectors.joining("','")));
    }

    private String createUnmatchedRerateTradeSQLEndpoint(ProcessingStatus status){
        var unmatchedRerateTradeSQLEndpoint = "jpa://com.intellecteu.onesource.integration.repository.entity.backoffice.RerateTradeEntity?"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT r FROM RerateTradeEntity r WHERE r.processingStatus = '%s' and r.matchingRerateId = null";
        return String.format(unmatchedRerateTradeSQLEndpoint, status);
    }

    private String createRerateTradeSQLEndpoint(ProcessingStatus status) {
        return String.format(RERATE_TRADE_SQL_ENDPOINT, status);
    }

    private String createRerateSQLEndpoint(ProcessingStatus status) {
        return String.format(RERATE_SQL_ENDPOINT, status);
    }

    private String createUnprocessedDeclineInstructionSQLEndpoint(){
        return DECLINE_INSTRUCTION_SQL_ENDPOINT;
    }

    private String createUnprocessedCorrectionInstructionSQLEndpoint(CorrectionInstructionType type){
        return String.format(CORRECTION_INSTRUCTION_SQL_ENDPOINT, type);
    }
}
