package com.intellecteu.onesource.integration;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.CollateralDto;
import com.intellecteu.onesource.integration.dto.FeeRateDto;
import com.intellecteu.onesource.integration.dto.InstrumentDto;
import com.intellecteu.onesource.integration.dto.InternalReferenceDto;
import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.dto.PlatformDto;
import com.intellecteu.onesource.integration.dto.PriceDto;
import com.intellecteu.onesource.integration.dto.RateDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.TransactingPartyDto;
import com.intellecteu.onesource.integration.dto.VenueDto;
import com.intellecteu.onesource.integration.dto.VenuePartyDto;
import com.intellecteu.onesource.integration.dto.spire.AccountDto;
import com.intellecteu.onesource.integration.dto.spire.CurrencyDto;
import com.intellecteu.onesource.integration.dto.spire.LoanBorrowDto;
import com.intellecteu.onesource.integration.dto.spire.PositionCollateralTypeDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.dto.spire.PositionExposureDto;
import com.intellecteu.onesource.integration.dto.spire.PositionTypeDto;
import com.intellecteu.onesource.integration.dto.spire.SecurityDetailDto;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.SettlementType;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.List;

import static com.intellecteu.onesource.integration.model.AgreementStatus.CONFIRMED;
import static com.intellecteu.onesource.integration.model.CollateralDescription.DEBT;
import static com.intellecteu.onesource.integration.model.CollateralType.CASH;
import static com.intellecteu.onesource.integration.model.CurrencyCd.EUR;
import static com.intellecteu.onesource.integration.model.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.PriceUnit.LOT;
import static com.intellecteu.onesource.integration.model.RoundingMode.ALWAYSUP;
import static com.intellecteu.onesource.integration.model.SettlementType.DVP;
import static com.intellecteu.onesource.integration.model.TermType.OPEN;
import static com.intellecteu.onesource.integration.model.VenueType.ONPLATFORM;

/**
 * Utility class for building dto objects for tests.
 */
@UtilityClass
public class DtoTestFactory {

  public static AgreementDto buildAgreementDto() {
    return AgreementDto.builder()
        .agreementId("testId")
        .status(CONFIRMED)
        .lastUpdateDatetime(LocalDateTime.now())
        .trade(buildTradeAgreementDto())
        .build();
  }

  public static TradeAgreementDto buildTradeAgreementDto() {
    return TradeAgreementDto.builder()
        .executionVenue(buildVenueDto())
        .instrument(buildInstrumentDto())
        .rate(buildRateDto())
        .quantity(2)
        .billingCurrency(EUR)
        .dividendRatePct(2)
        .tradeDate(LocalDateTime.now())
        .termType(OPEN)
        .termDate(LocalDateTime.now())
        .settlementDate(LocalDateTime.now())
        .settlementType(DVP)
        .collateral(buildCollateralDto())
        .transactingParties(createTransactionParties())
        .build();
  }

  public static PositionDto buildPositionDtoFromTradeAgreement() {
    var tradeAgreement = buildTradeAgreementDto();
    return buildPositionDtoFromTradeAgreement(tradeAgreement);

  }

  public static PositionDto buildPositionDtoFromTradeAgreement(TradeAgreementDto tradeAgreement) {
    return PositionDto.builder()
        .id(1L)
        .positionId("testSpirePositionId")
        .customValue2(tradeAgreement.getExecutionVenue().getPlatform().getVenueRefId())
        .securityDetailDto(buildSecurityDetailDto(tradeAgreement))
        .rate(tradeAgreement.getRate().getFee().getBaseRate())
        .quantity(tradeAgreement.getQuantity().doubleValue())
        .currency(buildCurrencyDto(tradeAgreement))
        .loanBorrowDto(buildLoanBorrowDto(tradeAgreement))
        .tradeDate(tradeAgreement.getTradeDate())
        .settleDate(tradeAgreement.getSettlementDate())
        .deliverFree(translateDeliverFree(tradeAgreement.getSettlementType()))
        .amount(tradeAgreement.getCollateral().getCollateralValue())
        .price(tradeAgreement.getCollateral().getContractPrice())
        .contractValue(tradeAgreement.getCollateral().getContractValue())
        .collateralTypeDto(buildCollateralTypeDto(tradeAgreement))
        .exposureDto(buildPositionExposureDto(tradeAgreement))
        .positionTypeDto(buildPositionTypeDto(tradeAgreement))
        .accountDto(buildAccountDto(tradeAgreement.getTransactingParties().get(0)))
        .cpDto(buildAccountDto(tradeAgreement.getTransactingParties().get(1)))
        .build();
  }

  private static AccountDto buildAccountDto(TransactingPartyDto party) {
    return AccountDto.builder()
        .lei(retrieveLei(party))
        .build();
  }

  private static PositionTypeDto buildPositionTypeDto(TradeAgreementDto tradeAgreement) {
    return PositionTypeDto.builder()
        .positionType(retrievePositionType(tradeAgreement.getTransactingParties()))
        .build();
  }

  private static PositionExposureDto buildPositionExposureDto(TradeAgreementDto tradeAgreement) {
    return PositionExposureDto.builder()
        .cpHaircut(tradeAgreement.getCollateral().getMargin())
        .cpMarkRoundTo(tradeAgreement.getCollateral().getRoundingRule())
        .build();
  }

  private static PositionCollateralTypeDto buildCollateralTypeDto(TradeAgreementDto tradeAgreement) {
    return PositionCollateralTypeDto.builder()
        .collateralType(tradeAgreement.getCollateral().getType().name())
        .build();
  }

  private static LoanBorrowDto buildLoanBorrowDto(TradeAgreementDto tradeAgreement) {
    return LoanBorrowDto.builder()
        .taxWithholdingRate(tradeAgreement.getDividendRatePct().doubleValue())
        .build();
  }

  private static CurrencyDto buildCurrencyDto(TradeAgreementDto tradeAgreement) {
    return CurrencyDto.builder()
        .currencyName(tradeAgreement.getBillingCurrency().name())
        .build();
  }

  private static SecurityDetailDto buildSecurityDetailDto(TradeAgreementDto tradeAgreement) {
    return SecurityDetailDto.builder()
        .ticker(tradeAgreement.getInstrument().getTicker())
        .cusip(tradeAgreement.getInstrument().getCusip())
        .isin(tradeAgreement.getInstrument().getIsin())
        .sedol(tradeAgreement.getInstrument().getSedol())
        .quickCode(tradeAgreement.getInstrument().getQuick())
        .bloombergId(tradeAgreement.getInstrument().getFigi())
        .build();
  }

  private static String retrieveLei(TransactingPartyDto party) {
    return party.getParty().getGleifLei();
  }

  private static String retrievePositionType(List<TransactingPartyDto> parties) {
    boolean isCashLoan = parties.stream().anyMatch(p -> p.getPartyRole() == LENDER);
    if (isCashLoan) {
      return "CASH LOAN";
    }
    boolean isCashBorrow = parties.stream().anyMatch(p -> p.getPartyRole() == BORROWER);
    if (isCashBorrow) {
      return "CASH BORROWER";
    }
    return null;
  }

  private static Boolean translateDeliverFree(SettlementType settlementType) {
    return settlementType != DVP;
  }

  private static TransactingPartyDto createTransactionParty(PartyRole role, String gleifLei) {
    return TransactingPartyDto.builder()
        .partyRole(role)
        .party(createPartyDto(gleifLei))
        .build();
  }

  private static List<TransactingPartyDto> createTransactionParties() {
    return List.of(
        createTransactionParty(LENDER, "lender-lei"),
        createTransactionParty(BORROWER, "borrower-lei"));
  }

  private static PartyDto createPartyDto(String gleifLei) {
    return PartyDto.builder()
        .partyId("testPartyId")
        .partyName("testPartyName")
        .gleifLei(gleifLei)
        .internalPartyId("testInternalPartyId")
        .build();
  }

  public static CollateralDto buildCollateralDto() {
    return CollateralDto.builder()
        .contractPrice(100.00)
        .contractValue(4.52)
        .collateralValue(400.32)
        .currency("EUR")
        .type(CASH)
        .descriptionCd(DEBT)
        .margin(2.02)
        .roundingRule(2)
        .roundingMode(ALWAYSUP)
        .build();
  }

  private static RateDto buildRateDto() {
    return RateDto.builder()
        .fee(buildFeeRateDto())
        .rebate(null)
        .build();
  }

  private static FeeRateDto buildFeeRateDto() {
    return FeeRateDto.builder()
        .baseRate(10.2d)
        .effectiveRate(4.0d)
        .effectiveDate(null)
        .cutoffTime(null)
        .build();
  }

  public static InstrumentDto buildInstrumentDto() {
    return InstrumentDto.builder()
        .ticker("testTicker")
        .cusip("testCusip")
        .isin("testIsin")
        .sedol("testSedol")
        .quick("testQuick")
        .figi("testFigi")
        .description("testDescription")
        .price(buildPriceDto())
        .build();
  }

  private static PriceDto buildPriceDto() {
    return PriceDto.builder()
        .value(10.0d)
        .currency("EUR")
        .unit(LOT)
        .build();
  }

  private static VenueDto buildVenueDto() {
    return VenueDto.builder()
        .type(ONPLATFORM)
        .platform(buildPlatformDto())
        .venueParties(List.of(buildVenuePartyDto()))
        .build();

  }

  public static VenuePartyDto buildVenuePartyDto() {
    return VenuePartyDto.builder()
        .partyRole(LENDER)
        .venuePartyId("testVenuePartyId")
        .internalRef(buildInternalReferenceDto())
        .build();
  }

  private static InternalReferenceDto buildInternalReferenceDto() {
    return InternalReferenceDto.builder()
        .brokerCd("testBrokerCd")
        .accountId("testAccountId")
        .internalRefId("testInternalRefId")
        .build();
  }

  private static PlatformDto buildPlatformDto() {
    return PlatformDto.builder()
        .gleifLei("testGleiFlei")
        .legalName("testLegalName")
        .mic("testMic")
        .venueName("testVenueName")
        .venueRefId("testVenueRefId")
        .transactionDatetime(LocalDateTime.now())
        .build();
  }
}
