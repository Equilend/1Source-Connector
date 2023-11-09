package com.intellecteu.onesource.integration.services.record;

import com.intellecteu.onesource.integration.DtoTestFactory;
import com.intellecteu.onesource.integration.dto.ExceptionMessageDto;
import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.enums.RecordType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.FIGI;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.GLEIF_LEI;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.QUANTITY;
import static org.junit.jupiter.api.Assertions.*;

class ContractInitiationCloudEventBuilderTest {

  private ContractInitiationCloudEventBuilder builder;

  @BeforeEach
  void setUp() {
    builder = new ContractInitiationCloudEventBuilder();
  }

  @Test
  void createTradeAgreementReconcileFailBuildRequest() {
    var agreement = DtoTestFactory.buildAgreementDto();
    var position = DtoTestFactory.buildPositionDtoFromTradeAgreement(agreement.getTrade());
    var firstException = new ExceptionMessageDto(QUANTITY, "First test message");
    var secondException = new ExceptionMessageDto(GLEIF_LEI, "Second test message");
    var thirdException = new ExceptionMessageDto(FIGI, "Third test message");
    var discrepancies = List.of(firstException, secondException, thirdException);

    String expectedDataMsg = """
        The trade agreement testId is in discrepancies with the position testSpirePositionId in SPIRE.
        List of discrepancies:
        - First test message
        - Second test message
        - Third test message""";

    CloudEventBuildRequest actualBuildRequest = builder.buildRequest(agreement.getAgreementId(),
        RecordType.TRADE_AGREEMENT_RECONCILED, position.getPositionId(), discrepancies);

    final String actualDataMsg = actualBuildRequest.getData().getMessage();
    assertEquals(expectedDataMsg, actualDataMsg);
  }
}