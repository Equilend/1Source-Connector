package com.intellecteu.onesource.integration.routes;

import com.intellecteu.onesource.integration.services.EventConsumer;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OneSourceEventRoute extends RouteBuilder {

    private final EventConsumer eventService;

    @Value("${camel.route.autostart}")
    private boolean isAutoStarted;

    public OneSourceEventRoute(EventConsumer eventService) {
        this.eventService = eventService;
    }

    @Override
    public void configure() {

        from("timer://eventTimer?period={{camel.timer}}")
            .routeId("RetrievingNewEventsRoute")
            .autoStartup(isAutoStarted)
            .log("Call for new events")
            .log("{{camel.timer}}")
            .log("retrieving Events")
            .log("{{camel.timestamp}}")
            .setHeader("timestamp", constant("{{camel.timestamp}}"))
            .bean(eventService, "consumeEvents")
            .log("Retrieve Events success");

    }
}
