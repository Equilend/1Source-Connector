package com.intellecteu.onesource.integration.routes;

import com.intellecteu.onesource.integration.services.PositionService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PositionUpdateRoute extends RouteBuilder {

    private final PositionService positionService;

    @Value("${camel.route.autostart}")
    private boolean isAutoStarted;

    public PositionUpdateRoute(PositionService positionService) {
        this.positionService = positionService;
    }

    @Override
    public void configure() {

        from("timer://eventTimer?period={{camel.positionTimer}}")
            .routeId("PositionUpdateRoute")
            .autoStartup(isAutoStarted)
            .log("Start processing updated positions")
            .bean(positionService, "processUpdatedPositions")
            .log("Updated positions successfully updated");

    }
}
