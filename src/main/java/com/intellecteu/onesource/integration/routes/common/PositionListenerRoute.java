package com.intellecteu.onesource.integration.routes.common;

import com.intellecteu.onesource.integration.routes.common.processor.PositionListenerProcessor;
import com.intellecteu.onesource.integration.routes.common.processor.PositionPendingConfirmationProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PositionListenerRoute extends RouteBuilder {

    private final PositionPendingConfirmationProcessor updatePositionService;
    private final PositionListenerProcessor positionListenerProcessor;

    @Value("${camel.route.autostart}")
    private boolean isAutoStarted;

    @Override
    public void configure() {

        from("timer://eventTimer?period={{camel.newPositionTimer}}")
            .routeId("NewPositionsRoute")
            .autoStartup(isAutoStarted)
            .log(">>>>> Started fetching new positions!")
            .bean(positionListenerProcessor, "fetchNewPositions")
            .log("<<<<< Finished fetching new positions!");

        from("timer://eventTimer?period={{camel.positionTimer}}")
            .routeId("PositionUpdateRoute")
            .autoStartup(isAutoStarted)
            .log(">>>>> Started processing updated positions.")
            .bean(updatePositionService, "processUpdatedPositions")
            .log("<<<<< Finished updated positions.");
    }
}
