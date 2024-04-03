package com.intellecteu.onesource.integration.routes.common;

import com.intellecteu.onesource.integration.routes.common.processor.CloudEventNotificationProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventNotificationProducerRoute extends RouteBuilder {

    private final CloudEventNotificationProcessor eventNotificationProcessor;
    private final boolean enabled;
    private final Integer timer;

    public EventNotificationProducerRoute(CloudEventNotificationProcessor eventNotificationProcessor,
        @Value("${notification.enable}") boolean enabled,
        @Value("${notification.timer}") Integer timer) {
        this.eventNotificationProcessor = eventNotificationProcessor;
        this.enabled = enabled;
        this.timer = timer;
    }

    @Override
    public void configure() {
        from(String.format("timer://eventTimer?period=%d", timer))
            .routeId("EventNotificationRoute")
            .autoStartup(enabled)
            .log(">>> Starting SEND_SYSTEM_EVENT_NOTIFICATIONS process.")
            .bean(eventNotificationProcessor, "sendAllEvents")
            .log("<<< SEND_SYSTEM_EVENT_NOTIFICATIONS process was finished!");
    }
}
