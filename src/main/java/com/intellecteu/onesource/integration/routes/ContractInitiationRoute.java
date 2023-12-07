package com.intellecteu.onesource.integration.routes;

import com.intellecteu.onesource.integration.services.ContractInitiationService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContractInitiationRoute extends RouteBuilder {

    private final ContractInitiationService contractInitiationService;

    @Value("${camel.route.autostart}")
    private boolean isAutoStarted;

    @Override
    public void configure() {

        from("timer://eventTimer?period={{camel.positionTimer}}")
            .routeId("ContractInitiationRoute")
            .autoStartup(isAutoStarted)
            .log("Start retrieved positions for matching")
            .bean(contractInitiationService, "startContractInitiation")
            .log("ContractInitiationRoute is processed");

    }
}
