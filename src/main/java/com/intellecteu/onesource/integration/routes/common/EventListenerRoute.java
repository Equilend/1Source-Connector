package com.intellecteu.onesource.integration.routes.common;

import com.intellecteu.onesource.integration.routes.common.processor.EventListenerProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventListenerRoute extends RouteBuilder {

    private final EventListenerProcessor eventListenerProcessor;
    private final boolean isAutoStarted;

    @Autowired
    public EventListenerRoute(EventListenerProcessor eventListenerProcessor, @Value("${camel.route.autostart}") boolean isAutoStarted) {
        this.eventListenerProcessor = eventListenerProcessor;
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
            .bean(eventListenerProcessor, "consumeEvents")
            .log("<<<<< Finished consuming events!");
    }
}
