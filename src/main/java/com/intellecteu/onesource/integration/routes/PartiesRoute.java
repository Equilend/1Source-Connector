package com.intellecteu.onesource.integration.routes;

import com.intellecteu.onesource.integration.routes.processor.PartyProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PartiesRoute extends RouteBuilder {

    private final PartyProcessor partyProcessor;

    @Value("${camel.route.autostart}")
    private boolean isAutoStarted;

    @Value("${camel.partyTimer}")
    private String partyTimer;

    public PartiesRoute(PartyProcessor partyProcessor) {
        this.partyProcessor = partyProcessor;
    }

    @Override
    public void configure() {

        from("quartz://myTimer?cron=" + partyTimer)
            .log("Start route at 6:00 am every day")
            .routeId("PartiesRoute")
            .autoStartup(isAutoStarted)
            .log("Start retrieving parties")
            .bean(partyProcessor, "processParties")
            .log("Retrieving parties success");

    }
}
