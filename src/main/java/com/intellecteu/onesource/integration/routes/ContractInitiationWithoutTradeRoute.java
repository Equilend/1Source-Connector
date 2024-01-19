package com.intellecteu.onesource.integration.routes;

import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_CANCELED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_DECLINED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_OPENED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_PENDING;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_PROPOSED;
import static com.intellecteu.onesource.integration.model.EventType.TRADE_CANCELED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.CREATED;

import com.intellecteu.onesource.integration.model.EventType;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.routes.processor.ContractProcessor;
import com.intellecteu.onesource.integration.routes.processor.EventProcessor;
import com.intellecteu.onesource.integration.routes.processor.PositionProcessor;
import com.intellecteu.onesource.integration.utils.IntegrationUtils;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    value="integration-toolkit.route.contract-initiation-without-trade-route.enable",
    havingValue = "true",
    matchIfMissing = true)
public class ContractInitiationWithoutTradeRoute extends RouteBuilder {

    private static final String POSITION_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.model.spire.Position?"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&"
            + "query=SELECT p FROM Position p WHERE p.processingStatus IN ('%s')";

    private static final String TRADE_EVENT_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.model.TradeEvent?"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&"
            + "query=SELECT e FROM TradeEvent e WHERE e.processingStatus = '%s' AND e.eventType IN ('%s')";

    private final PositionProcessor positionProcessor;
    private final EventProcessor eventProcessor;
    private final ContractProcessor contractProcessor;

    @Autowired
    public ContractInitiationWithoutTradeRoute(PositionProcessor positionProcessor, EventProcessor eventProcessor,
        ContractProcessor contractProcessor) {
        this.positionProcessor = positionProcessor;
        this.eventProcessor = eventProcessor;
        this.contractProcessor = contractProcessor;
    }

    @Override
    //@formatter:off
    public void configure() throws Exception {

        //Process positions (step 2)
        from(createPositionSQLEndpoint(ProcessingStatus.CREATED))
                .bean(positionProcessor, "matchTradeAgreement")
                .choice()
                    .when(method(IntegrationUtils.class, "isBorrower"))
                        .bean(positionProcessor, "matchContractProposal")
                    .endChoice()
                .end()
                .log("Finished step 2 for position with id ${body.positionId}")
                .to("direct:fetchSettlementInstruction");

        //Process positions (step 3)
        from("direct:fetchSettlementInstruction")
                .bean(positionProcessor, "fetchSettlementInstruction")
                .bean(positionProcessor, "updateProcessingStatus(${body}, SI_FETCHED)")
                .bean(positionProcessor, "savePosition")
                .log("Finished step 3 for position with id ${body.positionId}")
                .to("direct:reconcileWithAgreement");

        //Process positions (step 4)
        from("direct:reconcileWithAgreement")
                .setHeader("position", simple("${body}"))
                .bean(positionProcessor, "reconcileWithAgreement")
                //possible statuses in body: SI_FETCHED, TRADE_RECONCILED, TRADE_DISCREPANCIES
                .bean(positionProcessor, "updateProcessingStatus(${header.position}, ${body})")
                .bean(positionProcessor, "savePosition")
                .log("Finished step 4 for position with id ${body.positionId}")
                .to("direct:instructLoanContractProposal");

        //Process positions (step 5)
        from("direct:instructLoanContractProposal")
                .choice()
                    .when(method(IntegrationUtils.class, "isLender"))
                        .bean(positionProcessor, "instructLoanContractProposal")
                    .endChoice()
                .end()
                .bean(positionProcessor, "savePosition")
                .log("Finished step 5 for position with id ${body.positionId}");

        from(createTradeEventSQLEndpoint(CREATED, TRADE_CANCELED))
            .log(">>>>> Started processing TradeCanceledEvent with id ${body.eventId}")
            .bean(eventProcessor, "processTradeCanceledEvent")
            .bean(eventProcessor, "updateEventStatus(${body}, PROCESSED)")
            .bean(eventProcessor, "saveEvent")
            .log("<<<<< Finished processing TradeCanceledEvent with id ${body.eventId}");

        //Process positions (steps 7a, 12a, 19a in business flow)
        from(createTradeEventSQLEndpoint(CREATED, CONTRACT_OPENED, CONTRACT_PENDING, CONTRACT_DECLINED,
            CONTRACT_PROPOSED, CONTRACT_CANCELED))
            .log(">>>>> Started processing ContractEvent with eventId ${body.eventId}")
            .bean(eventProcessor, "processContractEvent")
            .bean(eventProcessor, "updateEventStatus(${body}, PROCESSED)")
            .bean(eventProcessor, "saveEvent")
            .log("<<<<< Finished processing ContractEvent with eventId ${body.eventId}")
            .end();

        //Process contract (steps 8-... in business flow)
        from("timer://eventTimer?period={{camel.timer}}")
            .routeId("ContractProcessingRoute")
            .log(">>>>> Start processing contract data")
            .setHeader("timestamp", constant("{{camel.timestamp}}"))
            .bean(contractProcessor, "processTradeData")
            .log("<<<<< Contract processing success");

        from("timer://eventTimer?period={{camel.timer}}")
            .routeId("ContractCancellation")
            .log(">>>>> Call for cancel routes")
            .log("{{camel.timer}}")
            .log("cancelContract")
            .bean(eventProcessor, "cancelContract")
            .log("<<<<< Cancel Contract success");
    }
    //@formatter:on

    private String createPositionSQLEndpoint(ProcessingStatus... status) {
        return String.format(POSITION_SQL_ENDPOINT,
            Arrays.stream(status).map(ProcessingStatus::toString).collect(Collectors.joining("','")));
    }

    private String createTradeEventSQLEndpoint(ProcessingStatus status, EventType... eventTypes) {
        return String.format(TRADE_EVENT_SQL_ENDPOINT, status,
            Arrays.stream(eventTypes).map(EventType::toString).collect(Collectors.joining("','")));
    }

}
