package com.intellecteu.onesource.integration.routes.delegate_flow;

import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.POSITION_ONESOURCE_CONFIRMATION;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.UpdatePositionProcessor;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "route.delegate-flow.update-position.enable")
public class UpdatePositionDelegateFlowRoute extends RouteBuilder {

    private static final String POSITION_SQL_ENDPOINT = """
        jpa://com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity\
        ?%s&entityType=java.util.List&consumeLockEntity=false&consumeDelete=false\
        &sharedEntityManager=true&joinTransaction=false&\
        query=SELECT p FROM PositionEntity p WHERE p.processingStatus IN ('%s')""";

    private final long updateTimer;
    private final UpdatePositionProcessor updatePositionProcessor;
    private final BackOfficeMapper backOfficeMapper;

    public UpdatePositionDelegateFlowRoute(
        @Value("${route.delegate-flow.update-position.timer}") int updateTimer,
        UpdatePositionProcessor updatePositionProcessor, BackOfficeMapper backOfficeMapper) {
        this.updatePositionProcessor = updatePositionProcessor;
        this.updateTimer = updateTimer;
        this.backOfficeMapper = backOfficeMapper;
    }

    @Override
    public void configure() {
        from(createPositionSQLEndpoint(updateTimer, POSITION_ONESOURCE_CONFIRMATION))
            .routeId("GetUpdatedPositionsPendingConfirmation")
            .log(">>>>> Start processing updated positions.")
            .bean(backOfficeMapper, "toPositionList")
            .setHeader("initialPositions", body())
            .bean(updatePositionProcessor, "fetchUpdatesOnPositions")
            .split(body())
            .choice()
            .when().simple("${body.tradeType} == 'RERATE'").to("direct:updatePositionForRerateTrade")
            .when().simple("${body.tradeType} == 'RERATE BORROW'").to("direct:updatePositionForRerateBorrowTrade")
            .when().simple("${body.tradeType} == 'ROLL LOAN'").to("direct:updatePositionForRollTrade")
            .when().simple("${body.tradeType} == 'ROLL BORROW'").to("direct:updatePositionForRollBorrowTrade")
            .when().simple("${body.tradeType} == 'CANCEL LOAN'").to("direct:updatePositionForCancelLoanTrade")
            .when().simple("${body.tradeType} == 'CANCEL BORROW'").to("direct:updatePositionForCancelBorrowTrade")
            .endChoice()
            .end()
            .log("<<<<< Finished updated positions.");

        from("direct:updatePositionForRerateTrade")
            .bean(updatePositionProcessor, "updatePositionForRerateTrade(${body}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, UPDATED)")
            .bean(updatePositionProcessor, "savePosition")
            .bean(updatePositionProcessor, "executeCancelRequest");

        from("direct:updatePositionForRerateBorrowTrade")
            .bean(updatePositionProcessor, "updatePositionForRerateTrade(${body}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, UPDATED)")
            .bean(updatePositionProcessor, "savePosition");

        from("direct:updatePositionForRollTrade")
            .bean(updatePositionProcessor, "updatePositionForRollTrade(${body}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, UPDATED)")
            .bean(updatePositionProcessor, "savePosition")
            .bean(updatePositionProcessor, "executeCancelRequest");

        from("direct:updatePositionForRollBorrowTrade")
            .bean(updatePositionProcessor, "updatePositionForRollTrade(${body}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, UPDATED)")
            .bean(updatePositionProcessor, "savePosition");

        from("direct:updatePositionForCancelLoanTrade")
            .setBody(simple("${body.position}"))
            .filter().method(updatePositionProcessor,
                "getPositionToUpdateById(${body.positionId}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, CANCELED)")
            .bean(updatePositionProcessor, "savePosition")
            .bean(updatePositionProcessor, "cancelContractForCancelLoanTrade");

        from("direct:updatePositionForCancelBorrowTrade")
            .setBody(simple("${body.position}"))
            .filter().method(updatePositionProcessor,
                "getPositionToUpdateById(${body.positionId}, ${headers.initialPositions})")
            .filter(body().isNotNull())
            .bean(updatePositionProcessor, "updatePositionProcessingStatus(${body}, CANCELED)")
            .bean(updatePositionProcessor, "savePosition")
            .bean(updatePositionProcessor, "recordPositionCanceledSystemEvent")
            .bean(updatePositionProcessor, "updateLoanContract")
            .bean(updatePositionProcessor, "recordPositionUnmatchedSystemEvent");
    }

    private String createPositionSQLEndpoint(Long timer, ProcessingStatus... status) {
        return String.format(POSITION_SQL_ENDPOINT,
            String.format("delay=%d", timer),
            Arrays.stream(status).map(ProcessingStatus::toString).collect(Collectors.joining("','")));
    }

}
