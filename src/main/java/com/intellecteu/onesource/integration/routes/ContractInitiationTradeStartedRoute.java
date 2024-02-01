package com.intellecteu.onesource.integration.routes;

import static com.intellecteu.onesource.integration.model.EventType.TRADE_AGREED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CREATED;

import com.intellecteu.onesource.integration.model.EventType;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.routes.processor.AgreementProcessor;
import com.intellecteu.onesource.integration.routes.processor.EventProcessor;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    value = "integration-toolkit.route.contract-initiation-trade-started.enable",
    havingValue = "true",
    matchIfMissing = true)
public class ContractInitiationTradeStartedRoute extends RouteBuilder {

    private static final String TRADE_EVENT_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.model.TradeEvent?"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT o FROM TradeEvent o WHERE o.processingStatus = '%s' AND o.eventType IN ('%s')";

    private final AgreementProcessor agreementProcessor;
    private final EventProcessor eventProcessor;

    @Autowired
    public ContractInitiationTradeStartedRoute(AgreementProcessor agreementProcessor, EventProcessor eventProcessor) {
        this.agreementProcessor = agreementProcessor;
        this.eventProcessor = eventProcessor;
    }

    @Override
    public void configure() throws Exception {

        from(createTradeEventSQLEndpoint(CREATED, TRADE_AGREED))
            .log(">>>>> Started processing TradeAgreementEvent with id ${body.eventId}")
            .bean(eventProcessor, "processTradeEvent")
            .bean(eventProcessor, "updateEventStatus(${body}, PROCESSED)")
            .bean(eventProcessor, "saveEvent")
            .log("<<<<< Finished processing TradeAgreementEvent with id ${body.eventId}");

        //Process one source events (steps 3-5 in business flow F1)
        from("timer://eventTimer?period={{camel.timer}}")
            .routeId("AgreementProcessingRoute")
            .log(">>>>> Start processing Agreement data")
            .setHeader("timestamp", constant("{{camel.timestamp}}"))
            .bean(agreementProcessor, "processTradeData")
            .log("<<<<< Agreement processing success");
    }

    private String createTradeEventSQLEndpoint(ProcessingStatus status, EventType... eventTypes) {
        return String.format(TRADE_EVENT_SQL_ENDPOINT, status,
            Arrays.stream(eventTypes).map(EventType::toString).collect(Collectors.joining("','")));
    }
}
