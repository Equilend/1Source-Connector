package com.intellecteu.onesource.integration.routes.rerate;

import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.PROPOSED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.TO_VALIDATE;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.VALIDATED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RERATE_PENDING;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RERATE_PROPOSED;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
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
    value="integration-toolkit.route.rerate.enable",
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

    private final RerateProcessor rerateProcessor;
    private final RerateEventProcessor rerateEventProcessor;
    private final OneSourceMapper oneSourceMapper;
    private final BackOfficeMapper backOfficeMapper;

    @Autowired
    public RerateRoute(RerateProcessor rerateProcessor, RerateEventProcessor rerateEventProcessor, OneSourceMapper oneSourceMapper,
        BackOfficeMapper backOfficeMapper) {
        this.rerateProcessor = rerateProcessor;
        this.rerateEventProcessor = rerateEventProcessor;
        this.oneSourceMapper = oneSourceMapper;
        this.backOfficeMapper = backOfficeMapper;
    }

    @Override
    //@formatter:off
    public void configure() throws Exception {

        from("timer://eventTimer?period={{camel.newBackOfficeTradeEventTimer}}")
            .routeId("NewBackOfficeRerateTradeRoute")
            .log(">>>>> Started fetching BackOffice Rerate Trades")
            .bean(rerateProcessor, "fetchNewRerateTrades")
            .split(body())
                .to("direct:recordRerateTrade")
            .end()
            .log("<<<<< Finished fetching BackOffice Rerate Trades");

        from("direct:recordRerateTrade")
            .log(">>>>> Started recording BackOffice Rerate Trades with tradeId ${body.tradeId}")
            .bean(rerateProcessor, "updateRerateTradeCreationDatetime")
            .bean(rerateProcessor, "updateRerateTradeProcessingStatus(${body}, CREATED)")
            .bean(rerateProcessor, "saveRerateTrade")
            .to("direct:matchRerate")
            .log(">>>>> Finished recording BackOffice Rerate Trades with tradeId ${body.tradeId}");

        from("direct:matchRerate")
            .log(">>>>> Started matching BackOffice Rerate Trade with tradeId ${body.tradeId} with 1Source Rerate")
            .bean(rerateProcessor, "matchBackOfficeRerateTradeWith1SourceRerate")
            .bean(rerateProcessor, "saveRerateTrade")
            .log("<<<<< Finished matching BackOffice Rerate Trade with tradeId ${body.tradeId} with 1Source Rerate");

        from(createUnmatchedRerateTradeSQLEndpoint(CREATED))
            .log(">>>>> Started instruction BackOffice Rerate Trade with tradeId ${body.tradeId}")
            .bean(backOfficeMapper, "toModel")
            .bean(rerateProcessor, "instructRerateTrade")
            //Success instruction changes status to SUBMITTED
            .bean(rerateProcessor, "saveRerateTrade")
            .log(">>>>> Finished instruction BackOffice Rerate Trade with tradeId ${body.tradeId}");

        from(createTradeEventSQLEndpoint(CREATED, RERATE_PROPOSED))
            .log(">>>>> Started processing RerateEvent with eventId ${body.eventId}")
            .bean(oneSourceMapper, "toModel")
            .bean(rerateEventProcessor, "processRerateProposedEvent")
            //1Source Rerate
            .bean(rerateEventProcessor, "updateEventStatus(${body}, PROCESSED)")
            .bean(rerateEventProcessor, "saveEvent")
            .log("<<<<< Finished processing RerateEvent with eventId ${body.eventId}");

        from(createRerateSQLEndpoint(PROPOSED))
            .log(">>>>> Started processing 1Source Rerate with rerateId ${body.rerateId}")
            .bean(oneSourceMapper, "toModel")
            .bean(rerateProcessor, "match1SourceRerateWithBackOfficeRerateTrade")
             //Result status can be MATCHED, TO_VALIDATE or UNMATCHED
            .bean(rerateProcessor, "saveRerate")
            .log(">>>>> Finished processing 1Source Rerate with rerateId ${body.rerateId}");

        from(createRerateSQLEndpoint(TO_VALIDATE))
            .log(">>>>> Started validation 1Source Rerate with rerateId ${body.rerateId}")
            .bean(oneSourceMapper, "toModel")
            .bean(rerateProcessor, "validate")
            //Result status can be VALIDATED, DISCREPANCIES
            .bean(rerateProcessor, "saveRerate")
            .log(">>>>> Finished validation 1Source Rerate with rerateId ${body.rerateId}");

        from(createRerateSQLEndpoint(VALIDATED))
            .log(">>>>> Started approval 1Source Rerate with rerateId ${body.rerateId}")
            .bean(oneSourceMapper, "toModel")
            .bean(rerateProcessor, "approve")
            .bean(rerateProcessor, "saveRerate")
            .log(">>>>> Finished approval 1Source Rerate with rerateId ${body.rerateId}");

        from(createTradeEventSQLEndpoint(CREATED, RERATE_PENDING))
            .log(">>>>> Started processing RerateEvent with eventId ${body.eventId}")
            .bean(oneSourceMapper, "toModel")
            .bean(rerateEventProcessor, "processReratePendingEvent")
            .bean(rerateEventProcessor, "updateEventStatus(${body}, PROCESSED)")
            .bean(rerateEventProcessor, "saveEvent")
            .log("<<<<< Finished processing RerateEvent with eventId ${body.eventId}");
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
}
