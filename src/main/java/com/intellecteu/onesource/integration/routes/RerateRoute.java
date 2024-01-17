package com.intellecteu.onesource.integration.routes;

import com.intellecteu.onesource.integration.routes.processor.RerateProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    value="integration-toolkit.route.rerate.enable",
    havingValue = "true",
    matchIfMissing = true)
public class RerateRoute extends RouteBuilder {

    private final RerateProcessor rerateProcessor;

    @Autowired
    public RerateRoute(RerateProcessor rerateProcessor) {
        this.rerateProcessor = rerateProcessor;
    }

    @Override
    public void configure() throws Exception {

        from("timer://eventTimer?period={{camel.newBackOfficeTradeEventTimer}}")
            .routeId("NewBackOfficeTradeOutRoute")
            .log(">>>>> Started fetching BackOffice Trades")
            .bean(rerateProcessor, "fetchNewTradeOut")
            .log("<<<<< Finished fetching BackOffice Trades!");

    }
}
