package com.intellecteu.onesource.integration.routes.delegate_flow;

import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UPDATED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.CONTRACT_PROPOSED;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.ContractProcessor;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.EventProcessor;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.PositionProcessor;
import com.intellecteu.onesource.integration.utils.IntegrationUtils;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    value = "route.delegate-flow.contract-initiation.enable",
    havingValue = "true",
    matchIfMissing = true)
public class ContractInitiationDelegateFlowRoute extends RouteBuilder {

    private static final String CAMEL_JPA_CONFIG = """
        jpa://%s\
        ?consumeLockEntity=false&consumeDelete=false\
        &sharedEntityManager=true&joinTransaction=false\
        &%s\
        &query=%s""";

    private static final String POSITION_SQL_ENDPOINT = """
        jpa://com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity\
        ?entityType=java.util.List&consumeLockEntity=false&consumeDelete=false&%s\
        &sharedEntityManager=true&joinTransaction=false\
        &query=SELECT p FROM PositionEntity p WHERE p.processingStatus IN ('%s')""";

    private static final String NEW_TRADE_EVENT_SQL_ENDPOINT = """
        jpa://com.intellecteu.onesource.integration.repository.entity.onesource.TradeEventEntity\
        ?entityType=java.util.List&consumeLockEntity=false&consumeDelete=false&%s\
        &sharedEntityManager=true&joinTransaction=false\
        &query=SELECT e FROM TradeEventEntity e WHERE e.eventType IN ('%s') \
        and e.processingStatus IS NULL OR e.processingStatus = 'CREATED'""";

    private final long updateTimer;

    private final BackOfficeMapper backOfficeMapper;
    private final OneSourceMapper oneSourceMapper;
    private final ContractProcessor contractProcessor;
    private final PositionProcessor positionProcessor;
    private final EventProcessor eventProcessor;

    public ContractInitiationDelegateFlowRoute(
        @Value("${route.delegate-flow.update-position.timer}") long updateTimer, BackOfficeMapper backOfficeMapper,
        OneSourceMapper oneSourceMapper, ContractProcessor contractProcessor, PositionProcessor positionProcessor,
        EventProcessor eventProcessor) {
        this.updateTimer = updateTimer;
        this.backOfficeMapper = backOfficeMapper;
        this.oneSourceMapper = oneSourceMapper;
        this.contractProcessor = contractProcessor;
        this.positionProcessor = positionProcessor;
        this.eventProcessor = eventProcessor;
    }

    @Override
    //@formatter:off
    public void configure() throws Exception {

        from(createNotProcessedTradeEventSQLEndpoint(CONTRACT_PROPOSED))
            .routeId("GetLoanContractDetails")
            .log(">>>>> Started GET_LOAN_CONTRACT_PROPOSAL subprocess with eventId ${body.eventId}")
            .split(body())
            .bean(oneSourceMapper, "toModel")
            .setHeader("tradeEvent", body())
            .bean(contractProcessor, "getLoanContractDetails")
            .filter(body().isNotNull())
            .bean(contractProcessor, "updateLoanContractStatus(${body}, PROPOSED)")
            .bean(contractProcessor, "saveContract")
            .bean(eventProcessor, "updateEventStatus(${header.tradeEvent}, PROCESSED)")
            .bean(eventProcessor, "saveEvent")
            .log("<<<<< Finished GET_LOAN_CONTRACT_PROPOSAL subprocess with eventId ${body.eventId}")
            .end();

        from(createLenderPostLoanContractQuery(CREATED, UPDATED))
            .routeId("PostLoanContractProposal")
            .log(">>> Started POST_LOAN_CONTRACT_PROPOSAL subprocess")
            .split(body())
                    .bean(backOfficeMapper, "toModel")
                    .setHeader("notMatchedPosition", body())
                    .bean(contractProcessor, "createProposalFromPosition")
                .filter(method(positionProcessor, "instructLoanContractProposal(${body}, ${header.notMatchedPosition})"))
                    .bean(positionProcessor, "updatePositionForInstructedProposal(${header.notMatchedPosition})")
                    .bean(positionProcessor, "savePosition")
                    .bean(positionProcessor, "recordEventProposalInstructed")
            .log("<<< Finished POST_LOAN_CONTRACT_PROPOSAL subprocess")
        .end();

        from(createPositionSQLEndpoint(CREATED))
            .routeId("MatchPositionWithContractProposal")
            .bean(backOfficeMapper, "toModel")
                .choice()
                    .when(method(IntegrationUtils.class, "isBorrower"))
                        .bean(positionProcessor, "matchContractProposal")
                .endChoice()
            .end()
            .log("Finished matching contract proposal for position with id ${body.positionId}")
        .end();
    }

    private String createLenderPostLoanContractQuery(ProcessingStatus... statuses) {
        String query = String.format("""
            SELECT p FROM PositionEntity p WHERE p.matching1SourceLoanContractId IS NULL \
            AND p.positionType.positionType = 'CASH LOAN' \
            AND p.processingStatus IN ('%s')""",
            Arrays.stream(statuses).map(ProcessingStatus::toString).collect(Collectors.joining("','")));
        return String.format(CAMEL_JPA_CONFIG,
            "com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity",
            String.format("delay=%d&entityType=java.util.List", updateTimer),
            query);
    }

    private String createPositionSQLEndpoint(ProcessingStatus... status) {
        return String.format(POSITION_SQL_ENDPOINT,
            String.format("delay=%d", updateTimer),
            Arrays.stream(status).map(ProcessingStatus::toString).collect(Collectors.joining("','")));
    }

    private String createNotProcessedTradeEventSQLEndpoint(EventType... eventTypes) {
        return String.format(NEW_TRADE_EVENT_SQL_ENDPOINT,
            String.format("delay=%d", updateTimer),
            Arrays.stream(eventTypes).map(EventType::toString).collect(Collectors.joining("','")));
    }
}
