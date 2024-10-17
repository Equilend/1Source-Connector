package com.intellecteu.onesource.integration.routes.delegate_flow;

import static com.intellecteu.onesource.integration.constant.PositionConstant.Status.PENDING_LEDGER_CONFIRMATION;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.ContractProcessor;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.UpdatePositionProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "route.delegate-flow.update-position.enable")
public class UpdatePositionDelegateFlowRoute extends RouteBuilder {

    private static final String UPDATES_POSITION_SQL_ENDPOINT = """
        jpa://com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity\
        ?%s&entityType=java.util.List&consumeLockEntity=false&consumeDelete=false\
        &sharedEntityManager=true&joinTransaction=false\
        &query=SELECT p FROM PositionEntity p WHERE (p.processingStatus is NULL OR p.processingStatus = 'CREATED') \
        AND p.positionStatus.status IN ('%s')""";

    private final long updateTimer;
    private final UpdatePositionProcessor updatePositionProcessor;
    private final BackOfficeMapper backOfficeMapper;

    public UpdatePositionDelegateFlowRoute(
        @Value("${route.delegate-flow.update-position.timer}") int updateTimer,
        UpdatePositionProcessor updatePositionProcessor,
        ContractProcessor contractProcessor, BackOfficeMapper backOfficeMapper) {
        this.updatePositionProcessor = updatePositionProcessor;
        this.updateTimer = updateTimer;
        this.backOfficeMapper = backOfficeMapper;
    }

    @Override
    public void configure() {
        from(createPositionSQLEndpoint(PENDING_LEDGER_CONFIRMATION))
            .routeId("GetUpdatedPositionsPendingConfirmation")
            .log(">>> Start GET_UPDATED_POSITIONS_PENDING_CONFIRMATION subprocess.")
            .bean(backOfficeMapper, "toPositionList")
            .setHeader("initialPositions", body())
            .bean(updatePositionProcessor, "fetchUpdatesOnPositions")
            .split(body())
            .choice()
            .when().simple("${body.tradeType} == 'Rerate'").to("direct:updatePositionForRerateTrade")
            .when().simple("${body.tradeType} == 'Rerate Borrow'").to("direct:updatePositionForRerateBorrowTrade")
            .when().simple("${body.tradeType} == 'Roll Loan'").to("direct:updatePositionForRollTrade")
            .when().simple("${body.tradeType} == 'Roll Borrow'").to("direct:updatePositionForRollBorrowTrade")
            .when().simple("${body.tradeType} == 'Cancel Loan'").to("direct:updatePositionForCancelLoanTrade")
            .when().simple("${body.tradeType} == 'Cancel New Borrow'").to("direct:updatePositionForCancelBorrowTrade")
            .endChoice()
            .end()
            .log("<<< Finished GET_UPDATED_POSITIONS_PENDING_CONFIRMATION subprocess.");

        from("direct:updatePositionForRerateTrade")
            .routeId("RerateTradeTypeRoute")
            .log(">>> Start RerateTradeTypeRoute subprocess.")
            .bean(updatePositionProcessor, "updatePositionForRerateTrade(${body}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, UPDATED)")
            .bean(updatePositionProcessor, "savePosition")
            .filter(simple("${body.matching1SourceLoanContractId} != null"))
            .setHeader("position", body())
            .bean(updatePositionProcessor, "instructProposalCancel")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "delinkContract(${header.position})")
            .log("<<< Finished RerateTradeTypeRoute subprocess.");

        from("direct:updatePositionForRerateBorrowTrade")
            .routeId("RerateBorrowTradeTypeRoute")
            .log(">>> Start RerateBorrowTradeTypeRoute subprocess.")
            .bean(updatePositionProcessor, "updatePositionForRerateTrade(${body}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, UPDATED)")
            .bean(updatePositionProcessor, "savePosition")
            .log("<<< Finished RerateBorrowTradeTypeRoute subprocess.");

        from("direct:updatePositionForRollTrade")
            .routeId("RollLoanTradeTypeRoute")
            .log(">>> Start RollLoanTradeTypeRoute subprocess.")
            .bean(updatePositionProcessor, "updatePositionForRollTrade(${body}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, UPDATED)")
            .bean(updatePositionProcessor, "savePosition")
            .filter(simple("${body.matching1SourceLoanContractId} != null"))
            .bean(updatePositionProcessor, "instructProposalCancel")
            .log("<<< Finished RollLoanTradeTypeRoute subprocess.");

        from("direct:updatePositionForRollBorrowTrade")
            .routeId("RollBorrowTradeTypeRoute")
            .log(">>> Start RollBorrowTradeTypeRoute subprocess.")
            .bean(updatePositionProcessor, "updatePositionForRollTrade(${body}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, UPDATED)")
            .bean(updatePositionProcessor, "savePosition")
            .log("<<< Finished RollBorrowTradeTypeRoute subprocess.");

        from("direct:updatePositionForCancelLoanTrade")
            .routeId("CancelLoanTradeTypeRoute")
            .log(">>> Start CancelLoanTradeTypeRoute subprocess.")
            .setBody(simple("${body.position}"))
            .bean(updatePositionProcessor, "getPositionToUpdateById(${body.positionId}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionStatus(${body}, CANCELLED)")
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, CANCELED)")
            .bean(updatePositionProcessor, "savePosition")
            .filter(simple("${body.matching1SourceLoanContractId} != null"))
            .bean(updatePositionProcessor, "cancelContractForCancelLoanTrade")
            .log("<<< Finished CancelLoanTradeTypeRoute subprocess.");

        from("direct:updatePositionForCancelBorrowTrade")
            .routeId("CancelBorrowTradeTypeRoute")
            .log(">>> Start CancelBorrowTradeTypeRoute subprocess.")
            .setBody(simple("${body.position}"))
            .bean(updatePositionProcessor,
                "getPositionToUpdateById(${body.positionId}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionStatus(${body}, CANCELLED)")
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, CANCELED)")
            .bean(updatePositionProcessor, "savePosition")
            .bean(updatePositionProcessor, "recordPositionCanceledSystemEvent")
            .bean(updatePositionProcessor, "updateLoanContract")
            .bean(updatePositionProcessor, "recordPositionUnmatchedSystemEvent")
            .log("<<< Finished CancelBorrowTradeTypeRoute subprocess.");
    }

    private String createPositionSQLEndpoint(String... positionStatuses) {
        return String.format(UPDATES_POSITION_SQL_ENDPOINT,
            String.format("delay=%d", updateTimer),
            String.join("','", positionStatuses));
    }

}
