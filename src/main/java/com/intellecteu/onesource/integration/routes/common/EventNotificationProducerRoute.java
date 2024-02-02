package com.intellecteu.onesource.integration.routes.common;

import com.intellecteu.onesource.integration.routes.common.processor.CloudEventNotificationProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventNotificationProducerRoute extends RouteBuilder {

    private final CloudEventNotificationProcessor eventNotificationProcessor;

    @Value("${notification.enable}")
    private boolean isEnabled;

    @Override
    public void configure() {
        from("timer://eventTimer?period={{notification.timer}}")
            .routeId("EventNotificationRoute")
            .autoStartup(isEnabled)
            .log(">>>> Sending notifications...")
            .setHeader("timestamp", constant("{{notification.timer}}"))
            .bean(eventNotificationProcessor, "sendAllEvents")
            .log("<<<<< Sending events process was finished!");
    }
}
