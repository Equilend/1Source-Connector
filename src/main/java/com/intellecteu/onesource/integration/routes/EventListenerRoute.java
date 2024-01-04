package com.intellecteu.onesource.integration.routes;

import com.intellecteu.onesource.integration.routes.processor.EventProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventListenerRoute extends RouteBuilder {

    private final EventProcessor eventProcessor;
    private final boolean isAutoStarted;

    @Autowired
    public EventListenerRoute(EventProcessor eventProcessor, @Value("${camel.route.autostart}") boolean isAutoStarted) {
        this.eventProcessor = eventProcessor;
        this.isAutoStarted = isAutoStarted;
    }

    @Override
    public void configure() throws Exception {
        //Retrieve one source events
        from("timer://eventTimer?period={{camel.timer}}")
            .routeId("RetrievingNewEventsRoute")
            .autoStartup(isAutoStarted)
            .log(">>>>> Consuming new events.")
            .log("{{camel.timer}}")
            .setHeader("timestamp", constant("{{camel.timestamp}}"))
            .bean(eventProcessor, "consumeEvents")
            .log("<<<<< Finished consuming events!");
    }
}
