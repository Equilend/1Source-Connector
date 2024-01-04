package com.intellecteu.onesource.integration.routes;

import com.intellecteu.onesource.integration.routes.processor.PositionProcessor;
import com.intellecteu.onesource.integration.services.PositionPendingConfirmationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PositionListenerRoute extends RouteBuilder {

    private final PositionPendingConfirmationServiceImpl updatePositionService;
    private final PositionProcessor positionProcessor;

    @Value("${camel.route.autostart}")
    private boolean isAutoStarted;

    @Override
    public void configure() {

        from("timer://eventTimer?period={{camel.newPositionTimer}}")
            .routeId("NewPositionsRoute")
            .autoStartup(isAutoStarted)
            .log(">>>>> Started fetching new positions!")
            .bean(positionProcessor, "fetchNewPositions")
            .log("<<<<< Finished fetching new positions!");

        from("timer://eventTimer?period={{camel.positionTimer}}")
            .routeId("PositionUpdateRoute")
            .autoStartup(isAutoStarted)
            .log(">>>>> Started processing updated positions.")
            .bean(updatePositionService, "processUpdatedPositions")
            .log("<<<<< Finished updated positions.");
    }
}
