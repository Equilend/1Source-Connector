package com.intellecteu.onesource.integration.routes.unilateral_flow;

import static com.intellecteu.onesource.integration.model.onesource.EventType.RECALL_CANCELED;
import static com.intellecteu.onesource.integration.model.onesource.EventType.RECALL_OPENED;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.enums.RecallInstructionType;
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
        from(buildSpireRecallByProcessingStatusSqlRequest(ProcessingStatus.CREATED))
            .routeId("ProcessSpireRecallInstruction")
            .log(">>> Started PROCESS_SPIRE_RECALL_INSTRUCTION for RecallSpire: ${body.recallId}")
            .bean(backOfficeMapper, "toModel")
            .bean(recallProcessor, "processRecallInstruction")
            .log("<<< Finished PROCESS_SPIRE_RECALL_INSTRUCTION for RecallSpire: "
                + "${body.recallId} with expected statuses: RecallSpire[SUBMITTED]")
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

        from(build1SourceRecallByProcessingStatusSqlRequest(ProcessingStatus.CREATED))
            .routeId("MatchRecall")
            .log(">>> Started MATCH_RECALL for Recall1Source: ${body.recallId}")
            .bean(oneSourceMapper, "toModel")
            .bean(recallProcessor, "matchRecalls")
            .log("<<< Finished MATCH_RECALL for Recall1Source: "
                + "${body.recallId} with expected statuses: Recall1Source[CONFIRMED_LENDER, CONFIRMED_BORROWER]")
            .end();

        from(getNewRecallInstructionForConfirmedRecallSpire(RecallInstructionType.RECALL_CANCELLATION))
            .routeId("SpireRecallCancellationInstruction")
            .log(
                ">>> Started PROCESS_SPIRE_RECALL_CANCELLATION_INSTRUCTION for RecallSpireInstruction: ${body.instructionId}")
            .bean(backOfficeMapper, "toModel")
            .setHeader("recallInstruction", body())
            .bean(recallProcessor, "getRecallToCancel")
            .bean(recallProcessor, "instructRecallCancellation(${header.recallInstruction}, ${body})")
            .bean(recallProcessor, "updateInstructionStatus(${header.recallInstruction}, PROCESSED)")
            .log("<<< Finished PROCESS_SPIRE_RECALL_CANCELLATION_INSTRUCTION for RecallSpireInstruction: "
                + "${header.recallInstruction} with expected statuses: RecallSpire[CANCEL_SUBMITTED], RecallSpireInstruction[PROCESSED]")
            .end();

        from(getNotProcessedTradeEvent(RECALL_CANCELED))
            .routeId("Process1SourceRecallCancellation")
            .log(">>> Started PROCESS_1SOURCE_RECALL_CANCELLATION for Trade Event: ${body.eventId}")
            .bean(oneSourceMapper, "toModel")
            .setHeader("tradeEvent", body())
            .bean(recallProcessor, "retrieve1SourceRecall")
            .choice()
            .when(body().isNotNull())
            .bean(recallProcessor, "markRecallsCancelled")
            .end()
            .bean(eventProcessor, "updateEventStatus(${header.tradeEvent}, PROCESSED)")
            .log("<<< Finished PROCESS_1SOURCE_RECALL_CANCELLATION for Trade Event: "
                + "${body.eventId} with expected statuses: Recall1Source[CANCELED], RecallSpire[CANCELED] TradeEvent[PROCESSED]")
            .end();
    }

    private String buildSpireRecallByProcessingStatusSqlRequest(ProcessingStatus processingStatus) {
        String request = "jpa://com.intellecteu.onesource.integration.repository.entity.backoffice.RecallSpireEntity?"
            + "%s&"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT r FROM RecallSpireEntity r WHERE r.processingStatus = '%s'";
        return String.format(request, String.format("delay=%d", updateTimer), processingStatus);
    }

    private String build1SourceRecallByProcessingStatusSqlRequest(ProcessingStatus processingStatus) {
        String request = "jpa://com.intellecteu.onesource.integration.repository.entity.onesource.Recall1SourceEntity?"
            + "%s&"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT r FROM Recall1SourceEntity r WHERE r.processingStatus = '%s'";
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

    private String getNewRecallInstructionForConfirmedRecallSpire(RecallInstructionType type) {
        String sql = String.format("""
            SELECT i FROM RecallSpireInstructionEntity i \
            join RecallSpireEntity r on i.spireRecallId = r.recallId \
            WHERE r.processingStatus = 'CONFIRMED_LENDER' and i.instructionType = '%s' \
            and (i.processingStatus IS NULL OR i.processingStatus = 'CREATED')""", type);
        String request = """
            jpa://com.intellecteu.onesource.integration.repository.entity.backoffice.RecallSpireInstructionEntity\
            ?consumeLockEntity=false&consumeDelete=false\
            &%s\
            &sharedEntityManager=true&joinTransaction=false\
            &query=%s""";
        return String.format(request, String.format("delay=%d", updateTimer), sql);
    }
}
