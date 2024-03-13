package com.intellecteu.onesource.integration.routes.delegate_flow;

import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CREATED;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.routes.delegate_flow.processor.PositionProcessor;
import com.intellecteu.onesource.integration.utils.IntegrationUtils;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    value = "route.delegate-flow.contract-initiation.enable",
    havingValue = "true",
    matchIfMissing = true)
@RequiredArgsConstructor
public class ContractInitiationDelegateFlowRoute extends RouteBuilder {

    private static final String POSITION_SQL_ENDPOINT = """
        jpa://com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity?\
        delay=60000&consumeLockEntity=false&consumeDelete=false&sharedEntityManager=true&joinTransaction=false&\
        query=SELECT p FROM PositionEntity p WHERE p.processingStatus IN ('%s')""";

    private final BackOfficeMapper backOfficeMapper;
    private final PositionProcessor positionProcessor;

    @Override
    //@formatter:off
    public void configure() throws Exception {
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

    private String createPositionSQLEndpoint(ProcessingStatus... status) {
        return String.format(POSITION_SQL_ENDPOINT,
            Arrays.stream(status).map(ProcessingStatus::toString).collect(Collectors.joining("','")));
    }
}
