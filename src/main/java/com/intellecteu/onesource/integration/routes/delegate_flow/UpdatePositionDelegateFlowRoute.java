package com.intellecteu.onesource.integration.routes.delegate_flow;

import static com.intellecteu.onesource.integration.constant.PositionConstant.Status.PENDING_ONESOURCE_CONFIRMATION;

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
    private final ContractProcessor contractProcessor;
    private final BackOfficeMapper backOfficeMapper;

    public UpdatePositionDelegateFlowRoute(
        @Value("${route.delegate-flow.update-position.timer}") int updateTimer,
        UpdatePositionProcessor updatePositionProcessor,
        ContractProcessor contractProcessor, BackOfficeMapper backOfficeMapper) {
        this.updatePositionProcessor = updatePositionProcessor;
        this.updateTimer = updateTimer;
        this.contractProcessor = contractProcessor;
        this.backOfficeMapper = backOfficeMapper;
    }

    @Override
    public void configure() {
        from(createPositionSQLEndpoint(PENDING_ONESOURCE_CONFIRMATION))
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
            .when().simple("${body.tradeType} == 'Cancel Borrow'").to("direct:updatePositionForCancelBorrowTrade")
            .endChoice()
            .end()
            .log("<<< Finished GET_UPDATED_POSITIONS_PENDING_CONFIRMATION subprocess.");

        from("direct:updatePositionForRerateTrade")
            .routeId("RerateTradeTypeRoute")
            .bean(updatePositionProcessor, "updatePositionForRerateTrade(${body}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, UPDATED)")
            .bean(updatePositionProcessor, "savePosition")
            .filter(simple("${body.matching1SourceLoanContractId} != null"))
            .bean(updatePositionProcessor, "executeCancelRequest")
            .filter(body().isNotNull())
            .bean(contractProcessor, "saveContract")
            .log("Rerate trade type was updated");

        from("direct:updatePositionForRerateBorrowTrade")
            .routeId("RerateBorrowTradeTypeRoute")
            .bean(updatePositionProcessor, "updatePositionForRerateTrade(${body}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, UPDATED)")
            .bean(updatePositionProcessor, "savePosition")
            .log("Rerate Borrow trade type was updated");

        from("direct:updatePositionForRollTrade")
            .routeId("RollLoanTradeTypeRoute")
            .bean(updatePositionProcessor, "updatePositionForRollTrade(${body}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, UPDATED)")
            .bean(updatePositionProcessor, "savePosition")
            .filter(simple("${body.matching1SourceLoanContractId} != null"))
            .bean(updatePositionProcessor, "executeCancelRequest")
            .filter(body().isNotNull())
            .bean(contractProcessor, "saveContract")
            .log("Roll Loan trade type was updated");

        from("direct:updatePositionForRollBorrowTrade")
            .routeId("RollBorrowTradeTypeRoute")
            .bean(updatePositionProcessor, "updatePositionForRollTrade(${body}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, UPDATED)")
            .bean(updatePositionProcessor, "savePosition")
            .log("Roll Loan trade type was updated");

        from("direct:updatePositionForCancelLoanTrade")
            .routeId("CancelLoanTradeTypeRoute")
            .setBody(simple("${body.position}"))
            .bean(updatePositionProcessor, "getPositionToUpdateById(${body.positionId}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, CANCELED)")
            .bean(updatePositionProcessor, "savePosition")
            .filter(simple("${body.matching1SourceLoanContractId} != null"))
            .bean(updatePositionProcessor, "cancelContractForCancelLoanTrade")
            .log("Cancel Loan trade type was updated");

        from("direct:updatePositionForCancelBorrowTrade")
            .routeId("CancelBorrowTradeTypeRoute")
            .setBody(simple("${body.position}"))
            .bean(updatePositionProcessor,
                "getPositionToUpdateById(${body.positionId}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, CANCELED)")
            .bean(updatePositionProcessor, "savePosition")
            .bean(updatePositionProcessor, "recordPositionCanceledSystemEvent")
            .bean(updatePositionProcessor, "updateLoanContract")
            .bean(updatePositionProcessor, "recordPositionUnmatchedSystemEvent")
            .log("Cancel Borrow trade type was updated");
    }

    private String createPositionSQLEndpoint(String... positionStatuses) {
        return String.format(UPDATES_POSITION_SQL_ENDPOINT,
            String.format("delay=%d", updateTimer),
            String.join("','", positionStatuses));
    }

}
