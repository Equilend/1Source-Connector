package com.intellecteu.onesource.integration.routes.delegate_flow;

import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_CANCELED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_DECLINED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_OPENED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_PENDING;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_PROPOSED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.TRADE_CANCELED;
import static com.intellecteu.onesource.integration.model.onesource.ProcessingStatus.CREATED;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.ContractProcessor;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.EventProcessor;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.PositionProcessor;
import com.intellecteu.onesource.integration.utils.IntegrationUtils;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Deprecated(since = "0.0.5-SNAPSHOT", forRemoval = true)
@Component
@ConditionalOnProperty(
    value = "route.delegate-flow.contract-initiation.enable",
    havingValue = "disabled",
    matchIfMissing = true)
public class ContractInitiationWithoutTradeRouteObsolete extends RouteBuilder {

    private static final String POSITION_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity?"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT p FROM PositionEntity p WHERE p.processingStatus IN ('%s')";

    private static final String TRADE_EVENT_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.repository.entity.onesource.TradeEventEntity?"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT e FROM TradeEventEntity e WHERE e.processingStatus = '%s' AND e.eventType IN ('%s')";

    private static final String DECLINE_INSTRUCTION_SQL_ENDPOINT =
        "jpa://com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity?"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT d FROM DeclineInstructionEntity d WHERE d.processingStatus IS NULL";

    private final PositionProcessor positionProcessor;
    private final EventProcessor eventProcessor;
    private final ContractProcessor contractProcessor;
    private final BackOfficeMapper backOfficeMapper;
    private final OneSourceMapper oneSourceMapper;

    @Autowired
    public ContractInitiationWithoutTradeRouteObsolete(PositionProcessor positionProcessor,
        EventProcessor eventProcessor,
        ContractProcessor contractProcessor, BackOfficeMapper backOfficeMapper, OneSourceMapper oneSourceMapper) {
        this.positionProcessor = positionProcessor;
        this.eventProcessor = eventProcessor;
        this.contractProcessor = contractProcessor;
        this.backOfficeMapper = backOfficeMapper;
        this.oneSourceMapper = oneSourceMapper;
    }

    @Override
    //@formatter:off
    public void configure() throws Exception {

        //Process positions (step 2)
        from(createPositionSQLEndpoint(CREATED))
                .bean(backOfficeMapper, "toModel")
                .bean(positionProcessor, "matchTradeAgreement")
                .choice()
                    .when(method(IntegrationUtils.class, "isBorrower"))
                        .bean(positionProcessor, "matchContractProposalObsolete")
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
            .bean(oneSourceMapper, "toModel")
            .bean(eventProcessor, "processTradeCanceledEvent")
            .bean(eventProcessor, "updateEventStatus(${body}, PROCESSED)")
            .bean(eventProcessor, "saveEvent")
            .log("<<<<< Finished processing TradeCanceledEvent with id ${body.eventId}");

        //Process positions (steps 7a, 12a, 19a in business flow)
        from(createTradeEventSQLEndpoint(CREATED, CONTRACT_OPENED, CONTRACT_PENDING, CONTRACT_DECLINED,
            CONTRACT_PROPOSED, CONTRACT_CANCELED))
            .log(">>>>> Started processing ContractEvent with eventId ${body.eventId}")
            .bean(oneSourceMapper, "toModel")
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

        //Pull data to decline loan contract instruction
        from(DECLINE_INSTRUCTION_SQL_ENDPOINT)
            .log(">>>>> Started decline loan contract for Decline Instruction=${body.declineInstructionId}")
            .bean(contractProcessor, "processDecline")
            .log("<<<<< Finished processing decline instruction")
            .end();
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
