package com.intellecteu.onesource.integration;

import static com.intellecteu.onesource.integration.TestConfig.createTestObjectMapper;
import static com.intellecteu.onesource.integration.constant.PositionConstant.PositionStatus.OPEN;
import static com.intellecteu.onesource.integration.model.onesource.CollateralDescription.DEBT;
import static com.intellecteu.onesource.integration.model.onesource.CollateralType.CASH;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.onesource.PriceUnit.LOT;
import static com.intellecteu.onesource.integration.model.onesource.RoundingMode.ALWAYSUP;

import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.CloudEventEntity;
import com.intellecteu.onesource.integration.model.onesource.Collateral;
import com.intellecteu.onesource.integration.model.onesource.Instrument;
import com.intellecteu.onesource.integration.model.onesource.InternalReference;
import com.intellecteu.onesource.integration.model.onesource.Price;
import com.intellecteu.onesource.integration.model.onesource.VenueParty;
import com.intellecteu.onesource.integration.model.backoffice.spire.Currency;
import com.intellecteu.onesource.integration.model.backoffice.spire.LoanBorrow;
import com.intellecteu.onesource.integration.model.backoffice.spire.Position;
import com.intellecteu.onesource.integration.model.backoffice.spire.PositionAccount;
import com.intellecteu.onesource.integration.model.backoffice.spire.PositionCollateralType;
import com.intellecteu.onesource.integration.model.backoffice.spire.PositionExposure;
import com.intellecteu.onesource.integration.model.backoffice.spire.PositionSecurityDetail;
import com.intellecteu.onesource.integration.model.backoffice.spire.PositionStatus;
import com.intellecteu.onesource.integration.model.backoffice.spire.PositionType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.experimental.UtilityClass;

/**
 * Utility class for building model objects for tests.
 */
@UtilityClass
public class ModelTestFactory {

    public static Instrument buildInstrument() {
        return Instrument.builder()
            .id(9999L)
            .ticker("testTicker")
            .cusip("testCusip")
            .isin("testIsin")
            .sedol("testSedol")
            .quick("testQuick")
            .figi("testFigi")
            .description("testDescription")
            .price(buildPrice())
            .build();
    }

    public static Agreement buildAgreement() {
        var agreementDto = DtoTestFactory.buildAgreementDto();
        var om = createTestObjectMapper();
        return om.convertValue(agreementDto, Agreement.class);
    }

    public static Position buildPosition() {
        return buildPosition(null);
    }

    public static Position buildPosition(PositionStatus positionStatus) {
        if (positionStatus == null) {
            positionStatus = new PositionStatus(OPEN);
        }
        return Position.builder()
            .venueRefId("testVenueRefId")
            .positionId("testSpirePositionId")
            .customValue2("customValue2")
            .termId(1)
            .positionSecurityDetail(buildSecurityDetail())
            .rate(0.1d)
            .quantity(15000.0d)
            .currency(new Currency("USD"))
            .loanBorrow(new LoanBorrow(0.5d))
            .tradeDate(LocalDateTime.now())
            .settleDate(LocalDateTime.now())
            .deliverFree(true)
            .amount(1.0d)
            .price(100.0d)
            .contractValue(123.0d)
            .positionStatus(positionStatus)
            .positionCollateralType(new PositionCollateralType("CASH"))
            .exposure(new PositionExposure(0.05d, 10, 12))
            .positionType(new PositionType("CASH BORROW"))
            .positionAccount(new PositionAccount(1l, "testLei", "testLeiName", "testAccountId"))
            .positionCpAccount(new PositionAccount(2l, "testCpLei", "testCpLeiName", "testAccountId"))
            .endDate(LocalDateTime.now())
            .lastUpdateDateTime(LocalDateTime.now().minusDays(1))
            .build();
    }

    private static PositionSecurityDetail buildSecurityDetail() {
        PositionSecurityDetail securityDetail = new PositionSecurityDetail();
        securityDetail.setTicker("testTicker");
        securityDetail.setPriceFactor(1);
        return securityDetail;
    }

    public static CloudEventEntity buildCloudEventEntity() {
        return CloudEventEntity.builder()
            .id(UUID.randomUUID().toString())
            .specVersion("testSpecVersion")
            .type("testType")
            .source("testSource")
            .subject("testSubject")
            .time(LocalDateTime.now())
            .relatedProcess("testRelatedProcess")
            .relatedSubProcess("testRelatedSubProcess")
            .dataContentType("testDataContentType")
            .data("testData")
            .build();
    }

    public static Price buildPrice() {
        return Price.builder()
            .value(10.0d)
            .currency("USD")
            .unit(LOT)
            .build();
    }

    public static VenueParty buildVenueParty() {
        return VenueParty.builder()
            .partyRole(LENDER)
            .venueId("testVenuePartyRefKey")
            .build();
    }

    private static InternalReference buildInternalReferenceDto() {
        return InternalReference.builder()
            .brokerCd("testBrokerCd")
            .accountId("testAccountId")
            .internalRefId("testInternalRefId")
            .build();
    }

    public static Collateral buildCollateral() {
        return Collateral.builder()
            .contractPrice(100.0)
            .contractValue(4.25)
            .collateralValue(400.33)
            .currency("USD")
            .type(CASH)
            .description(DEBT)
            .margin(2.05)
            .roundingRule(2)
            .roundingMode(ALWAYSUP)
            .build();
    }
}
