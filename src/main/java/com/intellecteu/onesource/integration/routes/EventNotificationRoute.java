package com.intellecteu.onesource.integration.routes;

import com.intellecteu.onesource.integration.dto.record.CloudEvent;
import com.intellecteu.onesource.integration.services.EventNotificationService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventNotificationRoute extends RouteBuilder {

    private final EventNotificationService<CloudEvent> eventNotificationService;

    @Value("${notification.enable}")
    private boolean isEnabled;

    @Override
    public void configure() {
        from("timer://eventTimer?period={{notification.pulling-timer}}")
            .routeId("EventNotificationRoute")
            .autoStartup(isEnabled)
            .log("Sending events...")
            .setHeader("timestamp", constant("{{notification.pulling-timer}}"))
            .bean(eventNotificationService, "sendAllEvents")
            .log("Sending events process was finished!");
    }
}
