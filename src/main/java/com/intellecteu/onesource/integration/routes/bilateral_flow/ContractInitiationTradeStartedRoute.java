package com.intellecteu.onesource.integration.routes.bilateral_flow;

import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.TRADE_AGREED;

import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.AgreementProcessor;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.EventProcessor;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Deprecated(since = "0.0.5-SNAPSHOT")
@Component
@ConditionalOnProperty(
    value = "route.bilateral-flow.contract-initiation.enable",
    havingValue = "disabled",
    matchIfMissing = true)
public class ContractInitiationTradeStartedRoute extends RouteBuilder {

    private static final String TRADE_EVENT_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.repository.entity.onesource.TradeEventEntity?"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT o FROM TradeEventEntity o WHERE o.processingStatus = '%s' AND o.eventType IN ('%s')";

    private final AgreementProcessor agreementProcessor;
    private final EventProcessor eventProcessor;
    private final OneSourceMapper oneSourceMapper;

    @Autowired
    public ContractInitiationTradeStartedRoute(AgreementProcessor agreementProcessor, EventProcessor eventProcessor,
        OneSourceMapper oneSourceMapper) {
        this.agreementProcessor = agreementProcessor;
        this.eventProcessor = eventProcessor;
        this.oneSourceMapper = oneSourceMapper;
    }

    @Override
    public void configure() throws Exception {

        from(createTradeEventSQLEndpoint(CREATED, TRADE_AGREED))
            .log(">>>>> Started processing TradeAgreementEvent with id ${body.eventId}")
            .bean(oneSourceMapper, "toModel")
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
