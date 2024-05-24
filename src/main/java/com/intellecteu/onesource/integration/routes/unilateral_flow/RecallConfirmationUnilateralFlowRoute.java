package com.intellecteu.onesource.integration.routes.unilateral_flow;

import static com.intellecteu.onesource.integration.model.onesource.EventType.RECALL_OPENED;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.EventProcessor;
import com.intellecteu.onesource.integration.routes.unilateral_flow.pocessor.RecallProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    value = "route.unilateral-flow.recall-confirmation.enable"
)
public class RecallConfirmationUnilateralFlowRoute extends RouteBuilder {

    private final BackOfficeMapper backOfficeMapper;
    private final OneSourceMapper oneSourceMapper;
    private final EventProcessor eventProcessor;
    private final RecallProcessor recallProcessor;

    private final long updateTimer;

    public RecallConfirmationUnilateralFlowRoute(
        BackOfficeMapper backOfficeMapper, OneSourceMapper oneSourceMapper, EventProcessor eventProcessor,
        RecallProcessor recallProcessor,
        @Value("${route.unilateral-flow.recall-confirmation.timer}") long updateTimer) {
        this.backOfficeMapper = backOfficeMapper;
        this.oneSourceMapper = oneSourceMapper;
        this.eventProcessor = eventProcessor;
        this.recallProcessor = recallProcessor;
        this.updateTimer = updateTimer;
    }

    @Override
    public void configure() throws Exception {
        from(getRecallByProcessingStatusSqlRequest(ProcessingStatus.CREATED))
            .routeId("ProcessSpireRecallInstruction")
            .log(">>> Started PROCESS_SPIRE_RECALL_INSTRUCTION for Recall: ${body.recallId}")
            .bean(backOfficeMapper, "toModel")
            .bean(recallProcessor, "processRecallInstruction")
            .log("<<< Finished PROCESS_SPIRE_RECALL_INSTRUCTION for Recall: "
                + "${body.recallId} with expected statuses: Recall[SUBMITTED]")
            .end();

        from(getNotProcessedTradeEvent(RECALL_OPENED))
            .routeId("GetRecallDetails")
            .log(">>> Started GET_RECALL_DETAILS for Trade Event: ${body.eventId}")
            .bean(oneSourceMapper, "toModel")
            .setHeader("tradeEvent", body())
            .bean(recallProcessor, "getRecall1SourceDetails")
            .choice()
            .when(body().isNotNull())
            .bean(recallProcessor, "createRecall1Source")
            .end()
            .bean(eventProcessor, "updateEventStatus(${header.tradeEvent}, PROCESSED)")
            .log("<<< Finished GET_RECALL_DETAILS for Trade Event: "
                + "${body.eventId} with expected statuses: Recall1Source[CREATED], TradeEvent[PROCESSED]")
            .end();
    }

    private String getRecallByProcessingStatusSqlRequest(ProcessingStatus processingStatus) {
        String request = "jpa://com.intellecteu.onesource.integration.repository.entity.backoffice.RecallEntity?"
            + "%s&"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT r FROM RecallEntity r WHERE r.processingStatus = '%s'";
        return String.format(request, String.format("delay=%d", updateTimer), processingStatus);
    }

    private String getNotProcessedTradeEvent(EventType eventType) {
        String request = """
            jpa://com.intellecteu.onesource.integration.repository.entity.onesource.TradeEventEntity\
            ?consumeLockEntity=false&consumeDelete=false\
            &%s\
            &sharedEntityManager=true&joinTransaction=false\
            &query=SELECT e FROM TradeEventEntity e WHERE e.eventType = '%s' \
            and (e.processingStatus IS NULL OR e.processingStatus = 'CREATED')""";
        return String.format(request, String.format("delay=%d", updateTimer), eventType);
    }
}
