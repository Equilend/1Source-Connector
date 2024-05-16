package com.intellecteu.onesource.integration.services.systemevent;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.FIGI;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.GLEIF_LEI;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.QUANTITY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.model.ProcessExceptionDetails;
import com.intellecteu.onesource.integration.model.enums.FieldExceptionType;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ContractInitiationCloudSystemEventBuilderTest {

    private ContractInitiationCloudEventBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new ContractInitiationCloudEventBuilder("specVersion", "http://localhost:8000");
    }

    @Test
    @Disabled(value = "should be reworked according to new changes")
    void createLoanContractProposalReconcileFailBuildRequest() {
        var contractDto = ModelTestFactory.buildContract();
        var position = ModelTestFactory.buildPosition();
        var firstException = new ProcessExceptionDetails(null, QUANTITY, "First test message",
            FieldExceptionType.DISCREPANCY);
        var secondException = new ProcessExceptionDetails(null, GLEIF_LEI, "Second test message",
            FieldExceptionType.DISCREPANCY);
        var thirdException = new ProcessExceptionDetails(null, FIGI, "Third test message",
            FieldExceptionType.DISCREPANCY);
        var discrepancies = List.of(firstException, secondException, thirdException);

        String expectedDataMsg = """
            Discrepancies have been found between the Lender's loan contract proposal %s \
            and the matched SPIRE position %s. \n
            List of discrepancies:
            - First test message
            - Second test message
            - Third test message""".formatted(contractDto.getContractId(), position.getPositionId());

        CloudEventBuildRequest actualBuildRequest = builder.buildRequest(contractDto.getContractId(),
            RecordType.LOAN_CONTRACT_PROPOSAL_DISCREPANCIES, String.valueOf(position.getPositionId()), discrepancies);

        final String actualDataMsg = actualBuildRequest.getData().getMessage();
        assertEquals(expectedDataMsg, actualDataMsg);
    }
}