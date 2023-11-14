package com.intellecteu.onesource.integration.routes;

import com.intellecteu.onesource.integration.services.PositionService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CreatingContractWOTARoute extends RouteBuilder {

    private final PositionService positionService;

    @Value("${camel.route.autostart}")
    private boolean isAutoStarted;

    public CreatingContractWOTARoute(PositionService positionService) {
        this.positionService = positionService;
    }

    @Override
    public void configure() {

        from("timer://eventTimer?period={{camel.timer}}")
            .routeId("ContractCreatingWithoutTA")
            .autoStartup(false)
            .log("Start contract creating")
            .bean(positionService, "createLoanContractWithoutTA")
            .log("Contract creating success");

    }
}
