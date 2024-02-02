package com.intellecteu.onesource.integration.routes.rerate;

import static com.intellecteu.onesource.integration.model.onesource.EventType.RERATE_PROPOSED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.CREATED;

import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import com.intellecteu.onesource.integration.routes.contract_initiation_without_trade.processor.EventProcessor;
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
        "jpa://com.intellecteu.onesource.integration.model.onesource.TradeEvent?"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT e FROM TradeEvent e WHERE e.processingStatus = '%s' AND e.eventType IN ('%s')";

    private static final String RERATE_TRADE_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.model.spire.RerateTrade?"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT r FROM RerateTrade r WHERE r.processingStatus = '%s'";

    private static final String RERATE_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.model.onesource.Rerate?"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT r FROM Rerate r WHERE r.processingStatus = '%s'";

    private final RerateProcessor rerateProcessor;
    private final EventProcessor eventProcessor;

    @Autowired
    public RerateRoute(RerateProcessor rerateProcessor, EventProcessor eventProcessor) {
        this.rerateProcessor = rerateProcessor;
        this.eventProcessor = eventProcessor;
    }

    @Override
    //@formatter:off
    public void configure() throws Exception {

        from("timer://eventTimer?period={{camel.newBackOfficeTradeEventTimer}}")
            .routeId("NewBackOfficeTradeOutRoute")
            .log(">>>>> Started fetching BackOffice Rerate Trades")
            .bean(rerateProcessor, "fetchNewRerateTrades")
            .split(body())
                .to("direct:recordRerateTrade")
            .end()
            .log("<<<<< Finished fetching BackOffice Rerate Trades");

        from("direct:recordRerateTrade")
            .log(">>>>> Started recording Rerate Trades with tradeId ${body.tradeId}")
            .choice()
                .when(simple("${body.tradeOut.tradeType} == 'Rerate'"))
                    .bean(rerateProcessor, "updateRerateTradeProcessingStatus(${body}, CREATED)")
                    .bean(rerateProcessor, "saveRerateTrade")
                    .to("direct:processRerateTrade")
                .endChoice()
            .end()
            .log(">>>>> Finished recording Rerate Trades with tradeId ${body.tradeId}");

        from("direct:processRerateTrade")
            .log(">>>>> Started processing Rerate Trade with tradeId ${body.tradeId}")
            .bean(rerateProcessor, "matchContract")
            .bean(rerateProcessor, "matchRerate")
                .bean(rerateProcessor, "saveRerateTrade")
            .log("<<<<< Finished processing Rerate Trade with tradeId ${body.tradeId}");


        from(createTradeEventSQLEndpoint(CREATED, RERATE_PROPOSED))
            .log(">>>>> Started processing RerateEvent with eventId ${body.eventId}")
            .bean(eventProcessor, "processRerateEvent")
            .bean(eventProcessor, "updateEventStatus(${body}, PROCESSED)")
            .bean(eventProcessor, "saveEvent")
            .log("<<<<< Finished processing RerateEvent with eventId ${body.eventId}");

        from(createRerateSQLEndpoint(CREATED))
            .log(">>>>> Started processing Rerate with rerateId ${body.rerateId}")
            .bean(rerateProcessor, "matchContract")
            .bean(rerateProcessor, "matchRerateTrade")
            .choice()
                .when(simple("${body.matchingSpireTradeId} != null"))
                    .bean(rerateProcessor, "updateRerateProcessingStatus(${body}, MATCHED_RERATE_TRADE)")
                .endChoice()
                .otherwise()
                    .bean(rerateProcessor, "updateRerateProcessingStatus(${body}, PROPOSED)")
                .endChoice()
            .end()
            .bean(rerateProcessor, "saveRerate")
            .log(">>>>> Finished processing Rerate with rerateId ${body.rerateId}");

    }
    //@formatter:on

    private String createTradeEventSQLEndpoint(ProcessingStatus status, EventType... eventTypes) {
        return String.format(TRADE_EVENT_SQL_ENDPOINT, status,
            Arrays.stream(eventTypes).map(EventType::toString).collect(Collectors.joining("','")));
    }

    private String createRerateTradeSQLEndpoint(ProcessingStatus status) {
        return String.format(RERATE_TRADE_SQL_ENDPOINT, status);
    }

    private String createRerateSQLEndpoint(ProcessingStatus status) {
        return String.format(RERATE_SQL_ENDPOINT, status);
    }
}
