package com.intellecteu.onesource.integration.routes;

import com.intellecteu.onesource.integration.services.processor.AgreementProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AgreementRoute extends RouteBuilder {

    private final AgreementProcessor agreementProcessor;

    @Value("${camel.route.autostart}")
    private boolean isAutoStarted;

    public AgreementRoute(AgreementProcessor agreementProcessor) {
        this.agreementProcessor = agreementProcessor;
    }

    @Override
    public void configure() {

        from("timer://eventTimer?period={{camel.timer}}")
            .routeId("AgreementProcessingRoute")
            .autoStartup(isAutoStarted)
            .log("Start processing Agreement data")
            .setHeader("timestamp", constant("{{camel.timestamp}}"))
            .bean(agreementProcessor, "processTradeData")
            .log("Agreement processing success");

    }
}
