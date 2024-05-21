package com.intellecteu.onesource.integration.routes.returns;

import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
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

    private static final String BORROW_RETURN_TRADE_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.repository.entity.backoffice.ReturnTradeEntity?"
            + "%s&"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT r FROM ReturnTradeEntity r WHERE r.processingStatus = '%s' "
            + "AND (r.tradeOut.tradeType LIKE '%%Return Borrow%%' OR r.tradeOut.tradeType LIKE '%%Return Borrow (Full)%%') ";

    private final ReturnProcessor returnsProcessor;
    private final BackOfficeMapper backOfficeMapper;
    private final Integer redeliveryPolicyMaxRedeliveries;
    private final String redeliveryPolicyDelayPattern;
    private final long updateTimer;

    @Autowired
    public ReturnRoute(ReturnProcessor returnsProcessor, BackOfficeMapper backOfficeMapper,
        @Value("${route.returns.redelivery-policy.max-redeliveries}") Integer redeliveryPolicyMaxRedeliveries,
        @Value("${route.returns.redelivery-policy.delay-pattern}") String redeliveryPolicyDelayPattern,
        @Value("${route.returns.timer}") long updateTimer) {
        this.returnsProcessor = returnsProcessor;
        this.backOfficeMapper = backOfficeMapper;
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

        from(createBorrowerReturnTradeSQLEndpoint(CREATED))
            .log(">>> Started POST_RETURN for ReturnTrade: ${body.tradeId}")
            .bean(backOfficeMapper, "toModel")
            .bean(returnsProcessor, "postReturnTrade")
            .bean(returnsProcessor, "saveReturnTradeWithProcessingStatus(${body}, SUBMITTED)")
            .log("<<< Finished POST_RETURN for ReturnTrade: ${body.tradeId} with expected statuses: ReturnTrade[SUBMITTED]");
    }
    //@formatter:on

    private String createBorrowerReturnTradeSQLEndpoint(ProcessingStatus processingStatus) {
        return String.format(BORROW_RETURN_TRADE_SQL_ENDPOINT, String.format("delay=%d", updateTimer), processingStatus);
    }

}
