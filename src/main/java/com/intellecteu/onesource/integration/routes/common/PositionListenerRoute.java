package com.intellecteu.onesource.integration.routes.common;

import com.intellecteu.onesource.integration.routes.common.processor.PositionListenerProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PositionListenerRoute extends RouteBuilder {

    private final PositionListenerProcessor positionProcessor;
    private final boolean enabled;

    public PositionListenerRoute(
        PositionListenerProcessor positionProcessor,
        @Value("${route.position-listener.enable}") boolean enabled) {
        this.positionProcessor = positionProcessor;
        this.enabled = enabled;
    }


    @Override
    public void configure() {

        from("timer://eventTimer?period={{route.position-listener.timer}}")
            .routeId("FetchNewPositions")
            .autoStartup(enabled)
            .log(">>>>> Started fetching new positions!")
            .bean(positionProcessor, "fetchNewPositions")
            .log("<<<<< Finished fetching new positions!");
    }
}
