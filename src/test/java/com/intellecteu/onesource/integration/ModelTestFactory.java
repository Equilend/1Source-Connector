package com.intellecteu.onesource.integration;

import static com.intellecteu.onesource.integration.EntityTestFactory.createFieldImpacted;
import static com.intellecteu.onesource.integration.TestConfig.createTestObjectMapper;
import static com.intellecteu.onesource.integration.model.enums.FieldExceptionType.DISCREPANCY;
import static com.intellecteu.onesource.integration.model.enums.FieldExceptionType.UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.FieldSource.ONE_SOURCE_LOAN_CONTRACT;
import static com.intellecteu.onesource.integration.model.enums.FieldSource.ONE_SOURCE_RERATE;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.OPEN;
import static com.intellecteu.onesource.integration.model.onesource.CollateralDescription.DEBT;
import static com.intellecteu.onesource.integration.model.onesource.CollateralType.CASH;
import static com.intellecteu.onesource.integration.model.onesource.CurrencyCd.USD;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.onesource.PriceUnit.LOT;
import static com.intellecteu.onesource.integration.model.onesource.RoundingMode.ALWAYSUP;
import static com.intellecteu.onesource.integration.model.onesource.SettlementType.DVP;
import static com.intellecteu.onesource.integration.model.onesource.VenueType.ONPLATFORM;

import com.intellecteu.onesource.integration.model.backoffice.Currency;
import com.intellecteu.onesource.integration.model.backoffice.Index;
import com.intellecteu.onesource.integration.model.backoffice.LoanBorrow;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.PositionAccount;
import com.intellecteu.onesource.integration.model.backoffice.PositionExposure;
import com.intellecteu.onesource.integration.model.backoffice.PositionSecurityDetail;
import com.intellecteu.onesource.integration.model.backoffice.PositionStatus;
import com.intellecteu.onesource.integration.model.backoffice.PositionType;
import com.intellecteu.onesource.integration.model.backoffice.TradeOut;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RelatedObject;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.SystemEventData;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventMetadata;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudSystemEvent;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.IntegrationCloudEvent;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Collateral;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.FeeRate;
import com.intellecteu.onesource.integration.model.onesource.FixedRate;
import com.intellecteu.onesource.integration.model.onesource.Instrument;
import com.intellecteu.onesource.integration.model.onesource.InternalReference;
import com.intellecteu.onesource.integration.model.onesource.LocalVenueField;
import com.intellecteu.onesource.integration.model.onesource.Party;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.Price;
import com.intellecteu.onesource.integration.model.onesource.Rate;
import com.intellecteu.onesource.integration.model.onesource.RebateRate;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.onesource.SettlementInstruction;
import com.intellecteu.onesource.integration.model.onesource.SettlementType;
import com.intellecteu.onesource.integration.model.onesource.TermType;
import com.intellecteu.onesource.integration.model.onesource.TradeAgreement;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.model.onesource.TransactingParty;
import com.intellecteu.onesource.integration.model.onesource.Venue;
import com.intellecteu.onesource.integration.model.onesource.VenueParty;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.experimental.UtilityClass;

/**
 * Utility class for building model objects for tests.
 */
@UtilityClass
public class ModelTestFactory {

    public static Instrument buildInstrument() {
        return Instrument.builder()
            .id(9999)
            .securityId(123L)
            .ticker("testTicker")
            .cusip("testCusip")
            .isin("testIsin")
            .sedol("testSedol")
            .quickCode("testQuick")
            .figi("testFigi")
            .description("testDescription")
            .price(buildPrice())
            .priceFactor(22)
            .build();
    }

    public static Contract buildContract() {
        return Contract.builder()
            .contractId("32b71278-9ad2-445a-bfb0-b5ada72f7194")
            .lastUpdatePartyId("test")
            .lastUpdateDateTime(LocalDateTime.now())
            .settlement(List.of(buildSettlement()))
            .trade(buildTradeAgreement())
            .build();
    }

    public static TradeAgreement buildTradeAgreement() {
        return TradeAgreement.builder()
            .id(1L)
            .venue(buildVenue())
            .instrument(buildInstrument())
            .rate(buildRate())
            .quantity(BigDecimal.valueOf(2L))
            .billingCurrency(USD)
            .dividendRatePct(2d)
            .tradeDate(LocalDate.now())
            .termType(TermType.OPEN)
            .termDate(LocalDate.now())
            .settlementDate(LocalDate.now())
            .settlementType(DVP)
            .collateral(buildCollateral())
            .transactingParties(createTransactionParties())
            .resourceUri("test/ledger/agreements/32b71278-9ad2-445a-bfb0-b5ada72f7199")
            .build();
    }

    public static List<TransactingParty> createTransactionParties() {
        return List.of(
            createTransactionParty(LENDER, "lender-lei"),
            createTransactionParty(BORROWER, "borrower-lei"));
    }

    public static TransactingParty createTransactionParty(PartyRole role, String gleifLei) {
        return TransactingParty.builder()
            .partyRole(role)
            .party(createParty(gleifLei))
            .build();
    }

    public static Party createParty(String gleifLei) {
        return Party.builder()
            .partyId("testPartyId")
            .partyName("testPartyName")
            .gleifLei(gleifLei)
            .internalPartyId("testInternalPartyId")
            .build();
    }

    public static Rate buildRate() {
        return Rate.builder()
            .fee(buildFeeRate())
            .rebate(buildRebateRate())
            .build();
    }

    public static RebateRate buildRebateRate() {
        return RebateRate.builder()
            .fixed(new FixedRate(10.2d))
            .build();
    }

    public static FeeRate buildFeeRate() {
        return FeeRate.builder()
            .baseRate(10.2d)
            .effectiveRate(4.0d)
            .effectiveDate(null)
            .cutoffTime(null)
            .build();
    }

    public static Venue buildVenue() {
        return Venue.builder()
            .id(99999L)
            .partyId("testPartyId")
            .type(ONPLATFORM)
            .venueName("testVenueName")
            .venueRefKey("testVenueRefId")
            .transactionDateTime(LocalDateTime.now())
            .venueParties(Set.of(buildVenueParty()))
            .localVenueFields(Set.of(buildVenueFields()))
            .build();

    }

    public static LocalVenueField buildVenueFields() {
        return LocalVenueField.builder()
            .localFieldName("testName")
            .localFieldValue("testValue")
            .build();
    }

    public static Settlement buildSettlement() {
        return Settlement.builder()
            .partyRole(BORROWER)
            .instruction(buildInstruction())
            .build();
    }

    public static SettlementInstruction buildInstruction() {
        return SettlementInstruction.builder()
            .localAgentAcct("testacc")
            .localAgentBic("RHBBMYKL")
            .localAgentName("nestname")
            .settlementBic("RHBBMYKL")
            .dtcParticipantNumber("123")
            .build();
    }

    public static TradeEvent buildTradeEvent() {
        return TradeEvent.builder()
            .eventId(1L)
            .build();
    }

    public static Agreement buildAgreement() {
        var agreementDto = DtoTestFactory.buildAgreementDto();
        var om = createTestObjectMapper();
        return om.convertValue(agreementDto, Agreement.class);
    }

    public static TradeOut buildSpireTrade(String tradeType) {
        return TradeOut.builder()
            .tradeId(34L)
            .tradeDate(LocalDateTime.now().minusDays(3))
            .postDate(LocalDateTime.now().minusHours(1))
            .settleDate(LocalDateTime.now().minusDays(2))
            .accrualDate(LocalDateTime.now())
            .rateOrSpread(123.00)
            .tradeType(tradeType)
            .index(new Index(12, "testIndexName", 1.05))
            .status("testStatus")
            .statusId(66)
            .position(buildPosition())
            .build();
    }

    public static Position buildPositionFromTradeAgreement(TradeAgreement tradeAgreement) {
        return Position.builder()
            .positionId(9L)
            .customValue2(tradeAgreement.getVenue().getVenueRefKey())
            .positionSecurityDetail(buildPositionSecurityDetail(tradeAgreement))
            .rate(tradeAgreement.getRate().getFee().getBaseRate())
            .quantity(tradeAgreement.getQuantity().doubleValue())
            .currency(buildCurrency(tradeAgreement))
            .loanBorrow(buildLoanBorrow(tradeAgreement))
            .tradeDate(LocalDateTime.of(tradeAgreement.getTradeDate(), LocalTime.now()))
            .settleDate(LocalDateTime.of(tradeAgreement.getSettlementDate(), LocalTime.now()))
            .deliverFree(translateDeliverFree(tradeAgreement.getSettlementType()))
            .amount(tradeAgreement.getCollateral().getCollateralValue())
            .price(tradeAgreement.getCollateral().getContractPrice())
            .exposure(buildPositionExposure(tradeAgreement))
            .positionType(buildPositionType(tradeAgreement))
            .positionAccount(buildAccount(tradeAgreement.getTransactingParties().get(0)))
            .positionCpAccount(buildAccount(tradeAgreement.getTransactingParties().get(1)))
            .build();
    }

    public static PositionAccount buildAccount(TransactingParty party) {
        PositionAccount account = new PositionAccount();
        account.setLei(retrieveLei(party));
        return account;
    }

    private static String retrieveLei(TransactingParty party) {
        return party.getParty().getGleifLei();
    }

    public static PositionType buildPositionType(TradeAgreement tradeAgreement) {
        return PositionType.builder()
            .positionType(retrievePositionType(tradeAgreement.getTransactingParties()))
            .isCash(true)
            .build();
    }

    private static String retrievePositionType(List<TransactingParty> parties) {
        boolean isCashLoan = parties.stream().anyMatch(p -> p.getPartyRole() == LENDER);
        if (isCashLoan) {
            return "CASH LOAN";
        }
        boolean isCashBorrow = parties.stream().anyMatch(p -> p.getPartyRole() == BORROWER);
        if (isCashBorrow) {
            return "CASH BORROW";
        }
        return null;
    }

    public static PositionExposure buildPositionExposure(TradeAgreement tradeAgreement) {
        return PositionExposure.builder()
            .cpHaircut(tradeAgreement.getCollateral().getMargin() / 100.0)
            .cpMarkRoundTo(tradeAgreement.getCollateral().getRoundingRule())
            .build();
    }

    private static Boolean translateDeliverFree(SettlementType settlementType) {
        return settlementType != DVP;
    }

    public static LoanBorrow buildLoanBorrow(TradeAgreement tradeAgreement) {
        return LoanBorrow.builder()
            .taxWithholdingRate(tradeAgreement.getDividendRatePct())
            .build();
    }

    public static Currency buildCurrency(TradeAgreement tradeAgreement) {
        return Currency.builder()
            .currencyKy(tradeAgreement.getBillingCurrency().name())
            .build();
    }

    public static PositionSecurityDetail buildPositionSecurityDetail(TradeAgreement tradeAgreement) {
        return PositionSecurityDetail.builder()
            .ticker(tradeAgreement.getInstrument().getTicker())
            .cusip(tradeAgreement.getInstrument().getCusip())
            .isin(tradeAgreement.getInstrument().getIsin())
            .sedol(tradeAgreement.getInstrument().getSedol())
            .quickCode(tradeAgreement.getInstrument().getQuickCode())
            .bloombergId(tradeAgreement.getInstrument().getFigi())
            .build();
    }

    public static Position buildPosition() {
        return buildPosition(null);
    }

    public static Position buildPosition(PositionStatus positionStatus) {
        if (positionStatus == null) {
            positionStatus = new PositionStatus(33, OPEN.getValue());
        }
        return Position.builder()
            .venueRefId("testVenueRefId")
            .positionId(100L)
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
            .positionStatus(positionStatus)
            .exposure(new PositionExposure(46, 0.05d, 10, 12))
            .positionType(new PositionType(11, "CASH BORROW", true))
            .positionAccount(new PositionAccount(1L, 11L, "testLei", "testLeiName", 123L, 123L))
            .positionCpAccount(new PositionAccount(2L, 22L, "testCpLei", "testCpLeiName", 345L, 345L))
            .endDate(LocalDateTime.now())
            .lastUpdateDateTime(LocalDateTime.now().minusDays(1))
            .index(new Index(123, "testIndexName", 4.04))
            .build();
    }

    private static PositionSecurityDetail buildSecurityDetail() {
        PositionSecurityDetail securityDetail = new PositionSecurityDetail();
        securityDetail.setTicker("testTicker");
        securityDetail.setPriceFactor(1);
        return securityDetail;
    }

    public static CloudSystemEvent buildCloudEventModel() {
        return CloudSystemEvent.builder()
            .id(UUID.randomUUID().toString())
            .specVersion("testSpecVersion")
            .type("testType")
            .source("testSource")
            .subject("testSubject")
            .time(LocalDateTime.now())
            .relatedProcess("testRelatedProcess")
            .relatedSubProcess("testRelatedSubProcess")
            .dataContentType("testDataContentType")
            .eventData(buildSystemEventDataModel())
            .build();
    }

    public static SystemEventData buildSystemEventDataModel() {
        FieldImpacted firstFieldImpacted = createFieldImpacted(ONE_SOURCE_RERATE, DISCREPANCY);
        FieldImpacted secondFieldImpacted = createFieldImpacted(ONE_SOURCE_LOAN_CONTRACT, UNMATCHED);

        SystemEventData dataModel = new SystemEventData();
        dataModel.setMessage("Test message");
        dataModel.setFieldsImpacted(List.of(firstFieldImpacted, secondFieldImpacted));
        dataModel.setRelatedObjects(List.of(RelatedObject.notApplicable(), new RelatedObject("testId", "testType")));
        return dataModel;
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

    public static CloudEventMetadata buildCloudEventMetadata() {
        return CloudEventMetadata.builder()
            .specVersion("testSpecVersion")
            .type("testType")
            .source(URI.create("https://test.com"))
            .subject("testSubject")
            .time(LocalDateTime.now())
            .relatedProcess("testRelatedProcess")
            .relatedSubProcess("testRelatedSubProcess")
            .dataContentType("testDataContentType")
            .build();
    }

    public static IntegrationCloudEvent createIntegrationRecord() {
        SystemEventData systemEventData = buildSystemEventDataModel();
        CloudEventMetadata metadata = buildCloudEventMetadata();
        return new IntegrationCloudEvent(metadata, systemEventData);
    }
}
