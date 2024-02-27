package com.intellecteu.onesource.integration.routes.common;

import com.intellecteu.onesource.integration.routes.common.processor.PositionListenerProcessor;
import com.intellecteu.onesource.integration.routes.common.processor.PositionPendingConfirmationProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PositionListenerRoute extends RouteBuilder {

    private final PositionPendingConfirmationProcessor updatePositionService;
    private final PositionListenerProcessor positionListenerProcessor;
    private final boolean enabled;

    public PositionListenerRoute(
        PositionPendingConfirmationProcessor updatePositionService,
        PositionListenerProcessor positionListenerProcessor,
        @Value("${route.position-listener.enable}") boolean enabled) {
        this.updatePositionService = updatePositionService;
        this.positionListenerProcessor = positionListenerProcessor;
        this.enabled = enabled;
    }


    @Override
    public void configure() {

        from("timer://eventTimer?period={{route.position-listener.timer}}")
            .routeId("FetchNewPositions")
            .autoStartup(enabled)
            .log(">>>>> Started fetching new positions!")
            .bean(positionListenerProcessor, "fetchNewPositions")
            .log("<<<<< Finished fetching new positions!");

        from("timer://eventTimer?period={{route.position-listener.update-timer}}")
            .routeId("PositionUpdateRoute")
            .autoStartup(enabled)
            .log(">>>>> Started processing updated positions.")
            .bean(updatePositionService, "processUpdatedPositions")
            .log("<<<<< Finished updated positions.");
    }
}
