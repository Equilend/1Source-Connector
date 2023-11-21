package com.intellecteu.onesource.integration.routes;

import com.intellecteu.onesource.integration.services.PositionService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PositionRoute extends RouteBuilder {

    private final PositionService positionService;

    @Value("${camel.route.autostart}")
    private boolean isAutoStarted;

    public PositionRoute(PositionService positionService) {
        this.positionService = positionService;
    }

    @Override
    public void configure() {

        from("timer://eventTimer?period={{camel.positionTimer}}")
            .routeId("PositionRoute")
            .autoStartup(isAutoStarted)
            .log("Start retrieved positions for matching")
            .bean(positionService, "createLoanContractWithoutTA")
            .log("Loan Contract proposal without TA success");

    }
}
