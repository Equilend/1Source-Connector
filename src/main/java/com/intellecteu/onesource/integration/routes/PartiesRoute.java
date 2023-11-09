package com.intellecteu.onesource.integration.routes;

import com.intellecteu.onesource.integration.services.EventService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PartiesRoute extends RouteBuilder {

    private final EventService eventService;

    @Value("${camel.route.autostart}")
    private boolean isAutoStarted;

    public PartiesRoute(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void configure() {

        from("quartz2://myTimer?cron=0+0+6+*+*+?")
            .log("Start route at 6:00 am every day")
            .routeId("PartiesRoute")
            .autoStartup(isAutoStarted)
            .log("Start retrieving parties")
            .bean(eventService, "processParties")
            .log("Retrieving parties success");

    }
}
