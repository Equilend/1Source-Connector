package com.intellecteu.onesource.integration.routes.returns;

import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TO_VALIDATE;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RETURN_PENDING;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
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

    public static final String RETURN_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.repository.entity.onesource.ReturnEntity?"
            + "%s&"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT r FROM ReturnEntity r WHERE r.processingStatus = '%s'";

    private final ReturnProcessor returnProcessor;
    private final ReturnEventProcessor returnEventProcessor;
    private final BackOfficeMapper backOfficeMapper;
    private final OneSourceMapper oneSourceMapper;
    private final Integer redeliveryPolicyMaxRedeliveries;
    private final String redeliveryPolicyDelayPattern;
    private final long updateTimer;

    @Autowired
    public ReturnRoute(ReturnProcessor returnProcessor, ReturnEventProcessor returnEventProcessor,
        BackOfficeMapper backOfficeMapper,
        OneSourceMapper oneSourceMapper,
        @Value("${route.returns.redelivery-policy.max-redeliveries}") Integer redeliveryPolicyMaxRedeliveries,
        @Value("${route.returns.redelivery-policy.delay-pattern}") String redeliveryPolicyDelayPattern,
        @Value("${route.returns.timer}") long updateTimer) {
        this.returnProcessor = returnProcessor;
        this.returnEventProcessor = returnEventProcessor;
        this.backOfficeMapper = backOfficeMapper;
        this.oneSourceMapper = oneSourceMapper;
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

}
