package com.intellecteu.onesource.integration.routes;

import com.intellecteu.onesource.integration.services.EventService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventDataProcessingRoute extends RouteBuilder {

    private final EventService eventService;

    @Value("${camel.route.autostart}")
    private boolean isAutoStarted;

    public EventDataProcessingRoute(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void configure() {

        from("timer://eventTimer?period={{camel.timer}}")
            .routeId("retrievingEventData")
            .autoStartup(isAutoStarted)
            .log("Call for event data")
            .log("{{camel.timer}}")
            .log("processing Event Data")
            .bean(eventService, "processEventData")
            .log("Process Event Data success");

    }
}
