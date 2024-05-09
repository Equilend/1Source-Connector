package com.intellecteu.onesource.integration.routes.returns;

import com.intellecteu.onesource.integration.routes.returns.processor.ReturnProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    value = "route.returns.enable",
    havingValue = "true",
    matchIfMissing = true)
public class ReturnRoute extends RouteBuilder {

    private final ReturnProcessor returnsProcessor;
    private final Integer redeliveryPolicyMaxRedeliveries;
    private final String redeliveryPolicyDelayPattern;
    private final long updateTimer;

    @Autowired
    public ReturnRoute(ReturnProcessor returnsProcessor, @Value("${route.returns.redelivery-policy.max-redeliveries}") Integer redeliveryPolicyMaxRedeliveries,
        @Value("${route.returns.redelivery-policy.delay-pattern}") String redeliveryPolicyDelayPattern,
        @Value("${route.returns.timer}") long updateTimer) {
        this.returnsProcessor = returnsProcessor;
        this.redeliveryPolicyMaxRedeliveries = redeliveryPolicyMaxRedeliveries;
        this.redeliveryPolicyDelayPattern = redeliveryPolicyDelayPattern;
        this.updateTimer = updateTimer;
    }

    @Override
    //@formatter:off
    public void configure() throws Exception {
        onException(Exception.class)
                .maximumRedeliveries(redeliveryPolicyMaxRedeliveries)
                .delayPattern(redeliveryPolicyDelayPattern)
                .handled(true)
                .log("Caught an error: ${exception.message}")
                .end();

        from(String.format("timer://eventTimer?period=%d", updateTimer))
                .log(">>> Started GET_NEW_RETURN_PENDING_CONFIRMATION for ReturnTrades")
                .bean(returnsProcessor, "fetchNewReturnTrades")
                .split(body())
                .to("direct:recordReturnTrade")
                .end()
                .log("<<< Finished GET_NEW_RETURN_PENDING_CONFIRMATION for ReturnTrades");

        from("direct:recordReturnTrade")
            .log(">>> Started PROCESS_RETURN_PENDING_CONFIRMATION for ReturnTrade: ${body.tradeId}")
            .bean(returnsProcessor, "updateReturnTradeCreationDatetime")
            .bean(returnsProcessor, "saveReturnTradeWithProcessingStatus(${body}, CREATED)")
            .log("<<< Finished PROCESS_RETURN_PENDING_CONFIRMATION for ReturnTrade: ${body.tradeId} with expected statuses: ReturnTrade[CREATED]");

    }
    //@formatter:on
}
