package com.intellecteu.onesource.integration.routes.unilateral_flow;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
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
    private final RecallProcessor recallProcessor;

    private final long updateTimer;

    public RecallConfirmationUnilateralFlowRoute(
        BackOfficeMapper backOfficeMapper, RecallProcessor recallProcessor,
        @Value("${route.unilateral-flow.recall-confirmation.timer}") long updateTimer) {
        this.backOfficeMapper = backOfficeMapper;
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
                + "${body.recallId} with expected statuses: Recall[SUBMITTED]");
    }

    private String getRecallByProcessingStatusSqlRequest(ProcessingStatus processingStatus) {
        String request = "jpa://com.intellecteu.onesource.integration.repository.entity.backoffice.RecallEntity?"
            + "%s&"
            + "consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&"
            + "query=SELECT r FROM RecallEntity r WHERE r.processingStatus = '%s'";
        return String.format(request, String.format("delay=%d", updateTimer), processingStatus);
    }
}
