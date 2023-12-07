package com.intellecteu.onesource.integration.routes;

import com.intellecteu.onesource.integration.services.EventService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CancelContractRoute extends RouteBuilder {

    private final EventService eventService;

    @Value("${camel.route.autostart}")
    private boolean isAutoStarted;

    public CancelContractRoute(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void configure() {

        from("timer://eventTimer?period={{camel.timer}}")
            .routeId("ContractCancellation")
            .autoStartup(isAutoStarted)
            .log("Call for cancel routes")
            .log("{{camel.timer}}")
            .to("direct:cancelContract");
        from("direct:cancelContract")
            .autoStartup(isAutoStarted)
            .log("cancelContract")
            .bean(eventService, "cancelContract")
            .log("Cancel Contract success");

    }
}
