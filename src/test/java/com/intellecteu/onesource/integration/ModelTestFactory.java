package com.intellecteu.onesource.integration;

import com.intellecteu.onesource.integration.model.CloudEventEntity;
import com.intellecteu.onesource.integration.model.Collateral;
import com.intellecteu.onesource.integration.model.Instrument;
import com.intellecteu.onesource.integration.model.InternalReference;
import com.intellecteu.onesource.integration.model.Price;
import com.intellecteu.onesource.integration.model.VenueParty;
import com.intellecteu.onesource.integration.model.spire.Currency;
import com.intellecteu.onesource.integration.model.spire.LoanBorrow;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.model.spire.PositionAccount;
import com.intellecteu.onesource.integration.model.spire.PositionCollateralType;
import com.intellecteu.onesource.integration.model.spire.PositionExposure;
import com.intellecteu.onesource.integration.model.spire.PositionSecurityDetail;
import com.intellecteu.onesource.integration.model.spire.PositionType;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.intellecteu.onesource.integration.model.CollateralDescription.DEBT;
import static com.intellecteu.onesource.integration.model.CollateralType.CASH;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.PriceUnit.LOT;
import static com.intellecteu.onesource.integration.model.RoundingMode.ALWAYSUP;

/**
 * Utility class for building model objects for tests.
 */
@UtilityClass
public class ModelTestFactory {

  public static Instrument buildInstrument() {
    return Instrument.builder()
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

  public static Position buildPosition() {
    return Position.builder()
        .id(1L)
        .venueRefId("testVenueRefId")
        .positionId("testSpirePositionId")
        .customValue2("customValue2")
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
        .positionCollateralType(new PositionCollateralType("testCollateral"))
        .exposure(new PositionExposure(0.05d, 10, 12))
        .positionType(new PositionType("BORROW CASH"))
        .positionAccount(new PositionAccount("testLei", "testLeiName"))
        .positionCpAccount(new PositionAccount("testCpLei", "testCpLeiName"))
        .build();
  }

  private static PositionSecurityDetail buildSecurityDetail() {
    PositionSecurityDetail securityDetail = new PositionSecurityDetail();
    securityDetail.setTicker("testTicker");
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
        .venueId("testVenuePartyId")
        .internalRef(buildInternalReferenceDto())
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
