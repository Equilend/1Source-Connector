package com.intellecteu.onesource.integration.routes;

import com.intellecteu.onesource.integration.routes.processor.AgreementProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    value = "integration-toolkit.flow-Version",
    havingValue = "F1")
public class ContractInitiationTradeStartedRoute extends RouteBuilder {

    private final AgreementProcessor agreementProcessor;


    public ContractInitiationTradeStartedRoute(CamelContext context, AgreementProcessor agreementProcessor) {
        super(context);
        this.agreementProcessor = agreementProcessor;
    }

    @Override
    public void configure() throws Exception {
        //Process one source events (steps 3-5 in business flow F1)
        from("timer://eventTimer?period={{camel.timer}}")
            .routeId("AgreementProcessingRoute")
            .log(">>>>> Start processing Agreement data")
            .setHeader("timestamp", constant("{{camel.timestamp}}"))
            .bean(agreementProcessor, "processTradeData")
            .log("<<<<< Agreement processing success");
    }
}
