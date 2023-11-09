package com.intellecteu.onesource.integration.routes;

import com.intellecteu.onesource.integration.services.processor.ContractProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ContractRoute extends RouteBuilder {

    private final ContractProcessor contractProcessor;

    @Value("${camel.route.autostart}")
    private boolean isAutoStarted;

    public ContractRoute(ContractProcessor contractProcessor) {
        this.contractProcessor = contractProcessor;
    }

    @Override
    public void configure() {

        from("timer://eventTimer?period={{camel.timer}}")
            .routeId("ContractProcessingRoute")
            .autoStartup(isAutoStarted)
            .log("Start processing contract data")
            .setHeader("timestamp", constant("{{camel.timestamp}}"))
            .bean(contractProcessor, "processTradeData")
            .log("Contract processing success");

    }
}
