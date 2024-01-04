package com.intellecteu.onesource.integration.routes;

import com.intellecteu.onesource.integration.routes.processor.ContractProcessor;
import com.intellecteu.onesource.integration.routes.processor.EventProcessor;
import com.intellecteu.onesource.integration.routes.processor.PositionProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContractInitiationWithoutTradeRoute extends RouteBuilder {

    private final PositionProcessor positionProcessor;
    private final EventProcessor eventProcessor;
    private final ContractProcessor contractProcessor;

    @Autowired
    public ContractInitiationWithoutTradeRoute(PositionProcessor positionProcessor, EventProcessor eventProcessor,
        ContractProcessor contractProcessor) {
        this.positionProcessor = positionProcessor;
        this.eventProcessor = eventProcessor;
        this.contractProcessor = contractProcessor;
    }

    @Override
    public void configure() throws Exception {

        //Process positions (steps 1-5 in business flow)
        //TODO read new position move to PositionUpdateRoute. Here leave processing new positions from database
        from("timer://eventTimer?period={{camel.positionTimer}}")
            .routeId("ContractInitiationRoute")
            .log(">>>>> Start retrieved positions for matching")
            .bean(positionProcessor, "startContractInitiation")
            .log("<<<<< ContractInitiationRoute is processed");

        //Process positions (steps 7a, 12a, 19a in business flow)
        from("timer://eventTimer?period={{camel.timer}}")
            .routeId("retrievingEventData")
            .log("Call for event data")
            .log("{{camel.timer}}")
            .log(">>>>> Processing Event Data")
            .bean(eventProcessor, "processEvents")
            .log("<<<<< Process Event Data success");

        //Process contract (steps 8-... in business flow)
        from("timer://eventTimer?period={{camel.timer}}")
            .routeId("ContractProcessingRoute")
            .log(">>>>> Start processing contract data")
            .setHeader("timestamp", constant("{{camel.timestamp}}"))
            .bean(contractProcessor, "processTradeData")
            .log("<<<<< Contract processing success");

        from("timer://eventTimer?period={{camel.timer}}")
            .routeId("ContractCancellation")
            .log("Call for cancel routes")
            .log("{{camel.timer}}")
            .log(">>>>> Start cancelContract processing")
            .bean(eventProcessor, "cancelContract")
            .log("<<<<< Cancel Contract success");

    }
}
