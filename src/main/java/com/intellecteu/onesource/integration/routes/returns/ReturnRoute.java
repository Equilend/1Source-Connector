package com.intellecteu.onesource.integration.routes.returns;

import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CONFIRMATION_POSTPONED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TO_CONFIRM;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TO_VALIDATE;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.VALIDATED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RETURN_ACKNOWLEDGED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RETURN_PENDING;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RETURN_SETTLED;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.mapper.NackInstructionMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.routes.returns.processor.ReturnEventProcessor;
import com.intellecteu.onesource.integration.routes.returns.processor.ReturnProcessor;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    value = "route.returns.enable",
    havingValue = "true",
    matchIfMissing = true)
public class ReturnRoute extends RouteBuilder {

    private static final String TRADE_EVENT_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.repository.entity.onesource.TradeEventEntity?"
            + "%s&"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT e FROM TradeEventEntity e WHERE e.processingStatus = '%s' AND e.eventType IN ('%s')";

    private static final String BORROW_RETURN_TRADE_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.repository.entity.backoffice.ReturnTradeEntity?"
            + "%s&"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT r FROM ReturnTradeEntity r WHERE r.processingStatus = '%s' "
            + "AND (r.tradeOut.tradeType LIKE '%%Return Borrow%%' OR r.tradeOut.tradeType LIKE '%%Return Borrow (Full)%%') ";

    private static final String RETURN_TRADE_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.repository.entity.backoffice.ReturnTradeEntity?"
            + "%s&"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT r FROM ReturnTradeEntity r WHERE r.processingStatus = '%s' ";

    public static final String RETURN_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.repository.entity.onesource.ReturnEntity?"
            + "%s&"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT r FROM ReturnEntity r WHERE r.processingStatus = '%s'";

    private final ReturnProcessor returnProcessor;
    private final ReturnEventProcessor returnEventProcessor;
    private final BackOfficeMapper backOfficeMapper;
    private final OneSourceMapper oneSourceMapper;
    private final NackInstructionMapper nackInstructionMapper;
    private final Integer redeliveryPolicyMaxRedeliveries;
    private final String redeliveryPolicyDelayPattern;
    private final long updateTimer;

    @Autowired
    public ReturnRoute(ReturnProcessor returnProcessor, ReturnEventProcessor returnEventProcessor,
        BackOfficeMapper backOfficeMapper,
        OneSourceMapper oneSourceMapper, NackInstructionMapper nackInstructionMapper,
        @Value("${route.returns.redelivery-policy.max-redeliveries}") Integer redeliveryPolicyMaxRedeliveries,
        @Value("${route.returns.redelivery-policy.delay-pattern}") String redeliveryPolicyDelayPattern,
        @Value("${route.returns.timer}") long updateTimer) {
        this.returnProcessor = returnProcessor;
        this.returnEventProcessor = returnEventProcessor;
        this.backOfficeMapper = backOfficeMapper;
        this.oneSourceMapper = oneSourceMapper;
        this.nackInstructionMapper = nackInstructionMapper;
        this.redeliveryPolicyMaxRedeliveries = redeliveryPolicyMaxRedeliveries;
        this.redeliveryPolicyDelayPattern = redeliveryPolicyDelayPattern;
        this.updateTimer = updateTimer;
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

        from(String.format("timer://eventTimer?period=%d", updateTimer))
            .log(">>> Started GET_NEW_RETURN_PENDING_CONFIRMATION for ReturnTrades")
            .bean(returnProcessor, "fetchNewReturnTrades")
            .split(body())
            .to("direct:recordReturnTrade")
            .end()
            .log("<<< Finished GET_NEW_RETURN_PENDING_CONFIRMATION for ReturnTrades");

        from("direct:recordReturnTrade")
            .log(">>> Started PROCESS_RETURN_PENDING_CONFIRMATION for ReturnTrade: ${body.tradeId}")
            .bean(returnProcessor, "updateReturnTradeCreationDatetime")
            .bean(returnProcessor, "saveReturnTradeWithProcessingStatus(${body}, CREATED)")
            .log("<<< Finished PROCESS_RETURN_PENDING_CONFIRMATION for ReturnTrade: ${body.tradeId} with expected statuses: ReturnTrade[CREATED]");

        from(createBorrowerReturnTradeSQLEndpoint(CREATED))
            .log(">>> Started POST_RETURN for ReturnTrade: ${body.tradeId}")
            .bean(backOfficeMapper, "toModel")
            .bean(returnProcessor, "postReturnTrade")
            .bean(returnProcessor, "saveReturnTradeWithProcessingStatus(${body}, SUBMITTED)")
            .log("<<< Finished POST_RETURN for ReturnTrade: ${body.tradeId} with expected statuses: ReturnTrade[SUBMITTED]");

        from(createTradeEventSQLEndpoint(CREATED, RETURN_PENDING))
            .log(">>> Started GET_RETURN for TradeEvent: ${body.eventId}")
            .bean(oneSourceMapper, "toModel")
            .bean(returnEventProcessor, "processReturnPendingEvent")
            .bean(returnEventProcessor, "saveEventWithProcessingStatus(${body}, PROCESSED)")
            .log("<<< Finished GET_RETURN for TradeEvent: ${body.eventId} with expected statuses: TradeEvent[PROCESSED], Return[CREATED]");

        from(createReturnSQLEndpoint(CREATED))
            .log(">>> Started MATCH_RETURN for Return: ${body.returnId}")
            .bean(oneSourceMapper, "toModel")
            .bean(returnProcessor, "matchingReturn")
            .bean(returnProcessor, "saveReturn")
            .log("<<< Finished MATCH_RETURN for Return: ${body.returnId} with expected statuses: Return[TO_VALIDATE, UNMATCHED, CONFIRMED], ReturnTrade[TO_CONFIRM]");

        from(createReturnSQLEndpoint(TO_VALIDATE))
            .log(">>> Started VALIDATE_RETURN for Return: ${body.returnId}")
            .bean(oneSourceMapper, "toModel")
            .bean(returnProcessor, "validateReturn")
            .bean(returnProcessor, "saveReturn")
            .log("<<< Finished VALIDATE_RETURN for Return: ${body.returnId} with expected statuses: Return[VALIDATED, DISCREPANCIES]");

        from(createReturnSQLEndpoint(VALIDATED))
            .log(">>> Started ACKNOWLEDGE_RETURN_POSITIVELY for Return: ${body.returnId}")
            .bean(oneSourceMapper, "toModel")
            .bean(returnProcessor, "sendPositiveAck")
            .bean(returnProcessor, "saveReturnWithProcessingStatus(${body}, ACK_SUBMITTED)")
            .log("<<< Finished ACKNOWLEDGE_RETURN_POSITIVELY for Return: ${body.returnId} with expected statuses: Return[ACK_SUBMITTED]");

        from(createUnprocessedNackInstructionSQLEndpoint())
            .log(">>> Started ACKNOWLEDGE_RETURN_NEGATIVELY for NackInstruction: ${body.nackInstructionId}")
            .bean(nackInstructionMapper, "toModel")
            .bean(returnProcessor, "sendNegativeAck")
            .bean(returnProcessor, "saveNackInstructionWithProcessingStatus(${body}, PROCESSED)")
            .log("<<< Finished ACKNOWLEDGE_RETURN_NEGATIVELY for NackInstruction: ${body.nackInstructionId} with expected statuses: NackInstruction[PROCESSED], Return[NACK_SUBMITTED]");

        from(createTradeEventSQLEndpoint(CREATED, RETURN_ACKNOWLEDGED))
            .log(">>> Started CAPTURE_RETURN_ACKNOWLEDGEMENT for TradeEvent: ${body.eventId}")
            .bean(oneSourceMapper, "toModel")
            .bean(returnEventProcessor, "processReturnAcknowledgedEvent")
            .bean(returnEventProcessor, "saveEventWithProcessingStatus(${body}, PROCESSED)")
            .log("<<< Finished CAPTURE_RETURN_ACKNOWLEDGEMENT for TradeEvent: ${body.eventId} with expected statuses: TradeEvent[PROCESSED], Return[POSITIVELY_ACKNOWLEDGED, NEGATIVELY_ACKNOWLEDGED]");

        from(createReturnTradeSQLEndpoint(TO_CONFIRM))
            .to("direct:confirmReturnTrade");

        from(createReturnTradeSQLEndpoint(CONFIRMATION_POSTPONED, 5000L))
            .to("direct:confirmReturnTrade");

        from("direct:confirmReturnTrade")
            .log(">>> Started CONFIRM_RETURN_TRADE for ReturnTrade: ${body.tradeId}")
            .bean(backOfficeMapper, "toModel")
            .choice()
                .when(method(returnProcessor, "isReturnTradePostponed"))
                    .bean(returnProcessor, "saveReturnTradeWithProcessingStatus(${body}, CONFIRMATION_POSTPONED)")
                .otherwise()
                    .bean(returnProcessor, "confirmReturnTrade")
                    .bean(returnProcessor, "saveReturnTradeWithProcessingStatus(${body}, CONFIRMED)")
                .endChoice()
            .end()
            .log("<<< Finished CONFIRM_RETURN_TRADE for ReturnTrade: ${body.tradeId} with expected statuses: ReturnTrade[CONFIRMED, CONFIRMATION_POSTPONED]");

        from(String.format("timer://eventTimer?period=%d", 60000))
            .log(">>> Started PROCESS_RETURN_TRADE_SETTLED for ReturnTrades")
            .bean(returnProcessor, "fetchOpenReturnTrades")
            .split(body())
                .bean(returnProcessor, "postSettlementStatus")
                .bean(returnProcessor, "saveReturnTradeWithProcessingStatus(${body}, SETTLED)")
            .end()
            .log("<<< Finished PROCESS_RETURN_TRADE_SETTLED for ReturnTrades with expected statuses: ReturnTrade[SETTLED]");

        from(createTradeEventSQLEndpoint(CREATED, RETURN_SETTLED))
            .log(">>> Started PROCESS_RETURN_SETTLED for TradeEvent: ${body.eventId}")
            .bean(oneSourceMapper, "toModel")
            .bean(returnEventProcessor, "processReturnSettledEvent")
            .bean(returnEventProcessor, "saveEventWithProcessingStatus(${body}, PROCESSED)")
            .log("<<< Finished PROCESS_RETURN_SETTLED for TradeEvent: ${body.eventId} with expected statuses: TradeEvent[PROCESSED], Return[SETTLED]");

        from(String.format("timer://eventTimer?period=%d", 60000))
            .log(">>> Started PROCESS_RETURN_TRADE_CANCELED for ReturnTrades")
            .bean(returnProcessor, "fetchAndProcessCanceledReturnTrades")
            .split(body())
                .bean(returnProcessor, "saveReturnTradeWithProcessingStatus(${body}, CANCELED)")
            .end()
            .log("<<< Finished PROCESS_RETURN_TRADE_CANCELED for ReturnTrades with expected statuses: ReturnTrade[CANCELED], Return[UNMATCHED, CANCEL_SUBMITTED]");
    }
    //@formatter:on

    private String createBorrowerReturnTradeSQLEndpoint(ProcessingStatus processingStatus) {
        return String.format(BORROW_RETURN_TRADE_SQL_ENDPOINT, String.format("delay=%d", updateTimer),
            processingStatus);
    }

    private String createTradeEventSQLEndpoint(ProcessingStatus status, EventType... eventTypes) {
        return String.format(TRADE_EVENT_SQL_ENDPOINT, String.format("delay=%d", updateTimer), status,
            Arrays.stream(eventTypes).map(EventType::toString).collect(Collectors.joining("','")));
    }

    private String createReturnSQLEndpoint(ProcessingStatus processingStatus) {
        return String.format(RETURN_SQL_ENDPOINT, String.format("delay=%d", updateTimer),
            processingStatus);
    }

    private String createUnprocessedNackInstructionSQLEndpoint() {
        String nackInstructionSQL =
            "jpa://com.intellecteu.onesource.integration.repository.entity.toolkit.NackInstructionEntity?"
                + "%s&"
                + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
                + "query=SELECT d FROM NackInstructionEntity d WHERE d.processingStatus IS NULL";
        return String.format(nackInstructionSQL, String.format("delay=%d", updateTimer));
    }

    private String createReturnTradeSQLEndpoint(ProcessingStatus processingStatus, long delay) {
        return String.format(RETURN_TRADE_SQL_ENDPOINT, String.format("delay=%d", delay),
            processingStatus);
    }

    private String createReturnTradeSQLEndpoint(ProcessingStatus processingStatus) {
        return createReturnTradeSQLEndpoint(processingStatus, updateTimer);
    }

}
