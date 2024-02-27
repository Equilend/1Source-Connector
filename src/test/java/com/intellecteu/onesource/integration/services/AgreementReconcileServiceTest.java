package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.DtoTestFactory.buildPositionDtoFromTradeAgreement;
import static com.intellecteu.onesource.integration.exception.ReconcileException.RECONCILE_EXCEPTION;
import static com.intellecteu.onesource.integration.model.onesource.SettlementType.DVP;
import static com.intellecteu.onesource.integration.model.onesource.SettlementType.FOP;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.dto.spire.CurrencyDto;
import com.intellecteu.onesource.integration.dto.spire.IndexDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.CollateralType;
import com.intellecteu.onesource.integration.model.onesource.FeeRate;
import com.intellecteu.onesource.integration.model.onesource.FixedRate;
import com.intellecteu.onesource.integration.model.onesource.FloatingRate;
import com.intellecteu.onesource.integration.model.onesource.Price;
import com.intellecteu.onesource.integration.model.onesource.PriceUnit;
import com.intellecteu.onesource.integration.model.onesource.Rate;
import com.intellecteu.onesource.integration.model.onesource.RebateRate;
import com.intellecteu.onesource.integration.model.onesource.TermType;
import com.intellecteu.onesource.integration.model.onesource.TransactingParty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AgreementReconcileServiceTest {

    private ReconcileService<Agreement, PositionDto> service;
    private Agreement agreement;
    private PositionDto position;

    @BeforeEach
    void setUp() {
        service = new AgreementReconcileService();
        agreement = ModelTestFactory.buildAgreement();
        position = buildPositionDtoFromTradeAgreement(agreement.getTrade());
    }

    @Test
    @Order(1)
    @DisplayName("Reconcile spire position created from onesource trade agreement")
    void reconcile_shouldSuccess_whenPositionCreatedFromTradeAgreement() throws Exception {
        service.reconcile(agreement, position);
    }

    @Test
    @Order(2)
    @DisplayName("Throw exception on reconciliation fail for venueRefId")
    void reconcile_shouldThrowException_whenReconciliationFailOnVenueRefId() {
        agreement.getTrade().getVenue().setVenueRefKey("customValue");

        verifyReconciliationFailure();
    }

    @Test
    @Order(3)
    @DisplayName("Throw exception if security identifiers are missed")
    void reconcile_shouldThrowException_whenMissedSecurityIdentifiers() {
        position.getSecurityDetailDto().setCusip(null);
        position.getSecurityDetailDto().setIsin(null);
        position.getSecurityDetailDto().setSedol(null);
        position.getSecurityDetailDto().setQuickCode(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(4)
    @Disabled
    @Deprecated(since = "1.0.4", forRemoval = true)
    @DisplayName("Throw exception if reconciliation fails for a ticker security identifier")
    void reconcile_shouldThrowException_whenReconciliationFailsForTicker() {
        position.getSecurityDetailDto().setTicker("customValue");
        position.getSecurityDetailDto().setCusip(null);
        position.getSecurityDetailDto().setIsin(null);
        position.getSecurityDetailDto().setSedol(null);
        position.getSecurityDetailDto().setQuickCode(null);
        position.getSecurityDetailDto().setBloombergId(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(5)
    @DisplayName("Throw exception if reconciliation fails for a cusip security identifier")
    void reconcile_shouldThrowException_whenReconciliationFailsForCusip() {
        position.getSecurityDetailDto().setCusip("customValue");
        position.getSecurityDetailDto().setTicker(null);
        position.getSecurityDetailDto().setIsin(null);
        position.getSecurityDetailDto().setSedol(null);
        position.getSecurityDetailDto().setQuickCode(null);
        position.getSecurityDetailDto().setBloombergId(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(6)
    @DisplayName("Throw exception if reconciliation fails for a isin security identifier")
    void reconcile_shouldThrowException_whenReconciliationFailsForIsin() {
        position.getSecurityDetailDto().setIsin("customValue");
        position.getSecurityDetailDto().setCusip(null);
        position.getSecurityDetailDto().setTicker(null);
        position.getSecurityDetailDto().setSedol(null);
        position.getSecurityDetailDto().setQuickCode(null);
        position.getSecurityDetailDto().setBloombergId(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(7)
    @DisplayName("Throw exception if reconciliation fails for a sedol security identifier")
    void reconcile_shouldThrowException_whenReconciliationFailsForSedol() {
        position.getSecurityDetailDto().setSedol("customValue");
        position.getSecurityDetailDto().setIsin(null);
        position.getSecurityDetailDto().setCusip(null);
        position.getSecurityDetailDto().setTicker(null);
        position.getSecurityDetailDto().setQuickCode(null);
        position.getSecurityDetailDto().setBloombergId(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(8)
    @DisplayName("Throw exception if reconciliation fails for a quickCode security identifier")
    void reconcile_shouldThrowException_whenReconciliationFailsForQuickCode() {
        position.getSecurityDetailDto().setQuickCode("customValue");
        position.getSecurityDetailDto().setSedol(null);
        position.getSecurityDetailDto().setIsin(null);
        position.getSecurityDetailDto().setCusip(null);
        position.getSecurityDetailDto().setTicker(null);
        position.getSecurityDetailDto().setBloombergId(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(9)
    @Disabled
    @Deprecated(since = "1.0.4", forRemoval = true)
    @DisplayName("Should reconcile if bloombergId security is not matched.")
    void reconcile_shouldReconcile_whenBloombergIdNotMatched() throws Exception {
        position.getSecurityDetailDto().setBloombergId("customValue");
        position.getSecurityDetailDto().setQuickCode(null);
        position.getSecurityDetailDto().setSedol(null);
        position.getSecurityDetailDto().setIsin(null);
        position.getSecurityDetailDto().setCusip(null);
        position.getSecurityDetailDto().setTicker(null);

        service.reconcile(agreement, position);
    }

    @Test
    @Order(10)
    @DisplayName("Throw exception if rate is missed")
    void reconcile_shouldThrowException_whenRateIsMissed() {
        position.setRate(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(11)
    @DisplayName("Throw exception if reconciliation fails for a rate")
    void reconcile_shouldThrowException_whenReconciliationFailsForRatePositionChanged() {
        position.setRate(9999.9d);

        verifyReconciliationFailure();
    }

    @Test
    @Order(11)
    @DisplayName("Throw exception if reconciliation fails for a rate")
    void reconcile_shouldThrowException_whenReconciliationFailsForRateAgreementFloatingChanged() {
        final RebateRate rebateRate = new RebateRate();
        final FloatingRate floatingRate = new FloatingRate();
        floatingRate.setEffectiveRate(111.0d);
        rebateRate.setFloating(floatingRate);
        var rate = new Rate();
        rate.setRebate(rebateRate);
        agreement.getTrade().setRate(rate);

        verifyReconciliationFailure();
    }

    @Test
    @Order(11)
    @DisplayName("Should success when trade.rate.rebate.fixed.baseRate matches with position.rate")
    void reconcile_shouldSuccess_whenAgreementFixedRateMatchesWithPosition() throws Exception {
        final RebateRate rebateRate = new RebateRate();
        var fixedRate = new FixedRate();
        fixedRate.setBaseRate(10.2d);
        rebateRate.setFixed(fixedRate);
        var rate = new Rate();
        rate.setRebate(rebateRate);
        agreement.getTrade().setRate(rate);

        service.reconcile(agreement, position);
    }

    @Test
    @Order(11)
    @DisplayName("Should success when trade.rate.rebate.floating.effectiveRate matches with position.rate")
    void reconcile_shouldSuccess_whenAgreementFloatingRateMatchesWithPosition() throws Exception {
        final RebateRate rebateRate = new RebateRate();
        final FloatingRate floatingRate = new FloatingRate();
        floatingRate.setEffectiveRate(10.2d);
        rebateRate.setFloating(floatingRate);
        var rate = new Rate();
        rate.setRebate(rebateRate);
        agreement.getTrade().setRate(rate);

        service.reconcile(agreement, position);
    }

    @Test
    @Order(11)
    @DisplayName("Throw exception if reconciliation fails for a rate")
    void reconcile_shouldThrowException_whenReconciliationFailsForRateAgreementFixedChanged() {
        final RebateRate rebateRate = new RebateRate();
        var fixedRate = new FixedRate();
        fixedRate.setBaseRate(111.0d);
        rebateRate.setFixed(fixedRate);
        var rate = new Rate();
        rate.setRebate(rebateRate);
        agreement.getTrade().setRate(rate);

        verifyReconciliationFailure();
    }

    @Test
    @Order(12)
    @DisplayName("Throw exception if quantity is missed")
    void reconcile_shouldThrowException_whenQuantityIsMissed() {
        position.setQuantity(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(13)
    @DisplayName("Throw exception if reconciliation fails for a quantity")
    void reconcile_shouldThrowException_whenReconciliationFailsForQuantity() {
        position.setQuantity(999.9d);

        verifyReconciliationFailure();
    }

    @Test
    @Order(14)
    @DisplayName("Throw exception if position.currencyDTO is missed")
    void reconcile_shouldThrowException_whenPositionCurrencyIsMissed() {
        position.setCurrency(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(14)
    @DisplayName("Throw exception if position.currencyDTO.currencyKy is missed")
    void reconcile_shouldThrowException_whenPositionCurrencyKyIsMissed() {
        var currency = new CurrencyDto();
        position.setCurrency(currency);

        verifyReconciliationFailure();
    }

    @Test
    @Order(15)
    @DisplayName("Throw exception if reconciliation fails for a currency")
    void reconcile_shouldThrowException_whenReconciliationFailsForCurrency() {
        position.setCurrency(new CurrencyDto("EUR"));

        verifyReconciliationFailure();
    }

    @Test
    @Order(16)
    @DisplayName("Should skip reconciliation when position.loanBorrowDTO.taxWithholdingRate rate is missed.")
    void reconcile_shouldSuccess_whenTaxWithHoldingRateIsMissed() throws Exception {
        position.getLoanBorrowDto().setTaxWithholdingRate(null);
        service.reconcile(agreement, position);
    }

    @Test
    @Order(17)
    @DisplayName("Throw exception if reconciliation fails for a TaxWithHoldingRate")
    void reconcile_shouldThrowException_whenReconciliationFailsForTaxWithHoldingRate() {
        position.getLoanBorrowDto().setTaxWithholdingRate(999.9d);

        verifyReconciliationFailure();
    }

    @Test
    @Order(18)
    @DisplayName("Throw exception if tradeDate is missed")
    void reconcile_shouldThrowException_whenTradeDateIsMissed() {
        position.setTradeDate(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(19)
    @DisplayName("Throw exception if reconciliation fails for a tradeDate")
    void reconcile_shouldThrowException_whenReconciliationFailsForTradeDate() {
        position.setTradeDate(LocalDateTime.now().plusYears(100));

        verifyReconciliationFailure();
    }

    @Test
    @Order(20)
    @DisplayName("Throw exception if settleDate is missed")
    void reconcile_shouldThrowException_whenSettleDateIsMissed() {
        position.setSettleDate(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(21)
    @DisplayName("Throw exception if reconciliation fails for a settleDate")
    void reconcile_shouldThrowException_whenReconciliationFailsForSettleDate() {
        position.setSettleDate(LocalDateTime.now().plusYears(100));

        verifyReconciliationFailure();
    }

    @Test
    @Order(22)
    @DisplayName("Should throw exception if deliverFree is missed")
    void reconcile_shouldReconcile_whenDeliverFreeIsMissed() {
        position.setDeliverFree(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(23)
    @DisplayName("Throw exception if trade.settlementType is 'DVP' and deliverFree is True")
    void reconcile_shouldThrowException_whenReconciliationFailsForDeliverFree() {
        agreement.getTrade().setSettlementType(DVP);
        position.setDeliverFree(true);

        verifyReconciliationFailure();
    }

    @Test
    @Order(24)
    @DisplayName("Reconcile if settlement type is not DVP and deliverFree is True")
    void reconcile_shouldReconcile_whenDeliverFreeIsTrueAndSettlementTypeNotDvp() throws Exception {
        agreement.getTrade().setSettlementType(FOP);
        position.setDeliverFree(true);

        service.reconcile(agreement, position);
    }

    @Test
    @Order(25)
    @DisplayName("Throw exception if trade.settlementType is not 'DVP' and deliverFree is False")
    void reconcile_shouldThrowException_whenDeliverFreeFalseAndSettlementTypeIsNotDvp() {
        agreement.getTrade().setSettlementType(FOP);
        position.setDeliverFree(false);

        verifyReconciliationFailure();
    }


    @Test
    @Order(26)
    @DisplayName("Should throw exception if position.price is missed")
    void reconcile_shouldSuccess_whenPriceIsMissed() {
        position.setPrice(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(28)
    @DisplayName("Ignore reconciliation if position.contractValue is missed")
    void reconcile_shouldSuccess_whenPositionContractValueIsMissed() throws Exception {
        position.setContractValue(null);

        service.reconcile(agreement, position);
    }

    @Test
    @Order(29)
    @DisplayName("Throw exception if reconciliation fails for a contractValue")
    void reconcile_shouldThrowException_whenReconciliationFailsForContractValue() {
        position.setContractValue(9999.0);

        verifyReconciliationFailure();
    }

    @Test
    @Order(30)
    @DisplayName("Throw exception if position.amount is missed")
    void reconcile_shouldThrowException_whenPositionAmountIsMissed() {
        position.setAmount(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(31)
    @DisplayName("Throw exception if reconciliation fails for an amount")
    void reconcile_shouldThrowException_whenReconciliationFailsForAmount() {
        position.setAmount(99999.9d);

        verifyReconciliationFailure();
    }

    @Test
    @Order(32)
    @DisplayName("Trade collateral type shall be CASH when position collateral type is empty")
    void reconcile_shouldSuccess_whenPositionCollateralTypeIsMissedAndTradeCollateralTypeIsCash() throws Exception {
        agreement.getTrade().getCollateral().setType(CollateralType.CASH);
        position.getCollateralTypeDto().setCollateralType(null);

        service.reconcile(agreement, position);
    }

    @Test
    @Order(32)
    @DisplayName("Throw exception if position collateralType is missed and trade collateral type is missed")
    void reconcile_shouldFail_whenPositionCollateralTypeIsMissedAndTradeCollateralTypeIsMissing() {
        agreement.getTrade().getCollateral().setType(null);
        position.getCollateralTypeDto().setCollateralType(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(33)
    @DisplayName("Throw exception if reconciliation fails for a collateralType")
    void reconcile_shouldThrowException_whenCollateralTypeIsNotMatched() {
        agreement.getTrade().getCollateral().setType(CollateralType.CASHPOOL);
        position.getCollateralTypeDto().setCollateralType("customType");

        verifyReconciliationFailure();
    }

    @Test
    @Order(34)
    @DisplayName("Throw exception if cpHaircut is missed")
    void reconcile_shouldThrowException_whenCpHaircutIsMissed() {
        position.getExposureDto().setCpHaircut(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(35)
    @DisplayName("Throw exception if reconciliation fails for a cpHaircut")
    void reconcile_shouldThrowException_whenReconciliationFailsForCpHaircut() {
        position.getExposureDto().setCpHaircut(99999.9d);

        verifyReconciliationFailure();
    }

    @Test
    @Order(36)
    @DisplayName("Ignore reconciliation if cpMarkRoundTo is missed")
    void reconcile_shouldThrowException_whenCpMarkRoundToIsMissed() throws Exception {
        position.getExposureDto().setCpMarkRoundTo(null);

        service.reconcile(agreement, position);
    }

    @Test
    @Order(37)
    @DisplayName("Ignore reconciliation for cpMarkRoundTo")
    void reconcile_shouldSuccess_whenCpMarkRoundToHasMismatch() throws Exception {
        position.getExposureDto().setCpMarkRoundTo(99999);

        service.reconcile(agreement, position);
    }

    @Test
    @Order(38)
    @DisplayName("Reconcile if rounding mode is missed")
    void reconcile_shouldThrowException_whenReconciliationFailsForRoundingMode() throws Exception {
        agreement.getTrade().getCollateral().setRoundingMode(null);

        service.reconcile(agreement, position);
    }

    @Test
    @Order(39)
    @DisplayName("Throw exception if account lei is missed")
    void reconcile_shouldThrowException_whenAccountLeiIsMissed() {
        position.getAccountDto().setLei(null);
        verifyReconciliationFailure();
    }

    @Test
    @Order(40)
    @DisplayName("Throw exception if counterparty lei is missed")
    void reconcile_shouldThrowException_whenCounterPartyLeiIsMissed() {
        position.getCpDto().setLei(null);
        verifyReconciliationFailure();
    }

    @Test
    @Order(41)
    @DisplayName("Throw exception if reconciliation fails for Lei")
    void reconcile_shouldThrowException_whenReconciliationFailsForLei() {
        position.getAccountDto().setLei("randomAccountLei");
        position.getCpDto().setLei("randomCpLei");

        verifyReconciliationFailure();
    }

    @Test
    @Order(42)
    @DisplayName("Reconcile if account lei or counterparty lei matches with party lei")
    void reconcile_shouldReconcile_whenOneOfTwoLeiMatches() throws Exception {
        var firstParty = agreement.getTrade().getTransactingParties().get(0);
        firstParty.getParty().setGleifLei("theSameLeiAsForAccount");
        position.getAccountDto().setLei("theSameLeiAsForAccount");
        position.getCpDto().setLei("randomCpLei");

        service.reconcile(agreement, position);
    }

    @Test
    @Order(43)
    @DisplayName("Should skip reconciliation when trade.dividendRatePct is missed")
    void reconcile_shouldReconcile_whenDividendRatePctIsMissed() throws Exception {
        agreement.getTrade().setDividendRatePct(null);
        service.reconcile(agreement, position);
    }

    @Test
    @Order(44)
    @DisplayName("Throw exception if agreement trade instrument required fields are missed")
    void reconcile_shouldThrowException_whenMissedInstrumentRequiredFields() {
        agreement.getTrade().getInstrument().setCusip(null);
        agreement.getTrade().getInstrument().setIsin(null);
        agreement.getTrade().getInstrument().setSedol(null);
        agreement.getTrade().getInstrument().setQuickCode(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(45)
    @DisplayName("Throw exception if trade agreement rebate rate is missed")
    void reconcile_shouldThrowException_whenTradeAgreementRateRebateBpsIsMissed() {
        var emptyRate = new Rate(1L, new RebateRate(), new FeeRate());
        agreement.getTrade().setRate(emptyRate);

        verifyReconciliationFailure();
    }

    @Test
    @Order(46)
    @DisplayName("Throw exception if trade agreement quantity is missed")
    void reconcile_shouldThrowException_whenTradeAgreementQuantityIsMissed() {
        agreement.getTrade().setQuantity(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(47)
    @DisplayName("Throw exception if trade agreement tradeDate is missed")
    void reconcile_shouldThrowException_whenTradeAgreementTradeDateIsMissed() {
        agreement.getTrade().setTradeDate(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(48)
    @DisplayName("Throw exception if trade agreement settlementDate is missed")
    void reconcile_shouldThrowException_whenTradeAgreementSettlementDateIsMissed() {
        agreement.getTrade().setSettlementDate(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(49)
    @Disabled
    @DisplayName("Throw exception if trade agreement settlement type is missed")
    void reconcile_shouldThrowException_whenTradeAgreementSettlementTypeIsMissed() {
        agreement.getTrade().setSettlementType(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(50)
    @DisplayName("Throw exception if trade agreement collateral contract price is missed")
    void reconcile_shouldThrowException_whenCollateralContractPriceIsMissed() {
        agreement.getTrade().getCollateral().setContractPrice(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(51)
    @DisplayName("Ignore reconciliation when trade contract value is missed")
    void reconcile_shouldSuccess_whenTradeContractValueIsMissed() throws Exception {
        agreement.getTrade().getCollateral().setContractValue(null);

        service.reconcile(agreement, position);
    }

    @Test
    @Order(52)
    @DisplayName("Throw exception if trade agreement collateral value is missed")
    void reconcile_shouldThrowException_whenCollateralValueIsMissed() {
        agreement.getTrade().getCollateral().setCollateralValue(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(53)
    @DisplayName("Throw exception if trade agreement collateral currency is missed")
    void reconcile_shouldThrowException_whenCollateralCurrencyIsMissed() {
        agreement.getTrade().getCollateral().setCurrency(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(54)
    @DisplayName("Throw exception if trade agreement collateral type is missed")
    void reconcile_shouldThrowException_whenTradeAgreementCollateralTypeIsMissed() {
        agreement.getTrade().getCollateral().setType(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(55)
    @DisplayName("Should throw exception if trade agreement collateral margin is missed")
    void reconcile_shouldReconcile_whenTradeAgreementCollateralMarginIsMissed() {
        agreement.getTrade().getCollateral().setMargin(null);

        verifyReconciliationFailure();
    }

    @Test
    @Order(56)
    @DisplayName("Throw exception if trade agreement party gleifLei is missed")
    void reconcile_shouldThrowException_whenTradeAgreementGleifLeiIsMissed() {
        TransactingParty party = agreement.getTrade().getTransactingParties().get(0);
        party.getParty().setGleifLei(null);
        agreement.getTrade().setTransactingParties(List.of(party));

        verifyReconciliationFailure();
    }

    @Test
    @Order(57)
    @DisplayName("Should reconcile if trade agreement internal party id is missed")
    void reconcile_shouldSuccess_whenTradeAgreementInternalPartyIdIsMissed() throws Exception {
        TransactingParty party = agreement.getTrade().getTransactingParties().get(0);
        party.getParty().setInternalPartyId(null);

        service.reconcile(agreement, position);
    }

    @Test
    @Order(58)
    @DisplayName("Should reconcile agreement trade date and position tradeDateTime")
    void reconcile_shouldSuccess_whenTradeDateTimeIsDifferent() throws Exception {
        agreement.getTrade().setTradeDate(LocalDate.of(2023, 12, 1));
        position.setTradeDate(LocalDateTime.of(
            LocalDate.of(2023, 12, 1), LocalTime.of(10, 55)));

        service.reconcile(agreement, position);
    }

    @Test
    @Order(59)
    @DisplayName("Should reconcile agreement settle date and position settleDateTime")
    void reconcile_shouldSuccess_whenSettleDateTimeIsDifferent() throws Exception {
        agreement.getTrade().setSettlementDate(LocalDate.of(2023, 12, 5));
        position.setSettleDate(LocalDateTime.of(
            LocalDate.of(2023, 12, 5), LocalTime.of(10, 55)));

        service.reconcile(agreement, position);
    }

    @Test
    @Order(60)
    @DisplayName("Reconcile success on margin and hairCut diff types of values")
    void reconcile_shouldSuccess_whenOnesourceMarginHasDifferentTypeThanSpireHaircut() throws Exception {
        agreement.getTrade().getCollateral().setMargin(103.0);
        position.getExposureDto().setCpHaircut(1.03);
        service.reconcile(agreement, position);
    }

    @Test
    @Order(61)
    @DisplayName("Reconcile success when position.securityDTO.bloombergId is missed")
    void reconcile_shouldSuccess_whenPositionBloombergIdIsMissed() throws Exception {
        position.getSecurityDetailDto().setBloombergId(null);
        service.reconcile(agreement, position);
    }

    @Test
    @Order(62)
    @DisplayName("Reconcile success when trade.instrument.figi is missed")
    void reconcile_shouldSuccess_whenOnesourceFigiIsMissed() throws Exception {
        agreement.getTrade().getInstrument().setFigi(null);
        service.reconcile(agreement, position);
    }

    @Test
    @Order(63)
    @DisplayName("Should throw exception if position.securityDetailDTO.priceFactor is 1 and "
        + "trade.instrument.price.unit is not SHARE")
    void reconcile_shouldThrowException_whenPriceFactorIsNotShare() {
        agreement.getTrade().getInstrument().setPrice(new Price(PriceUnit.LOT));
        position.getSecurityDetailDto().setPriceFactor(1);

        verifyReconciliationFailure();
    }

    @Test
    @Order(64)
    @DisplayName("Should throw exception if position.securityDetailDTO.priceFactor is 2 and "
        + "trade.instrument.price.unit is not LOT")
    void reconcile_shouldThrowException_whenPriceFactorIsNotLot() {
        agreement.getTrade().getInstrument().setPrice(new Price(PriceUnit.SHARE));
        position.getSecurityDetailDto().setPriceFactor(2);

        verifyReconciliationFailure();
    }

    @Test
    @Order(65)
    @DisplayName("Should throw exception if position.termId is 1 and "
        + "trade.termType is not OPEN")
    void reconcile_shouldThrowException_whenTermTypeIsNotOpen() {
        agreement.getTrade().setTermType(TermType.TERM);
        position.setTermId(1);

        verifyReconciliationFailure();
    }

    @Test
    @Order(66)
    @DisplayName("Should throw exception if position.termId is 1 and "
        + "trade.termType is not TERM")
    void reconcile_shouldThrowException_whenTermTypeIsNotTerm() {
        agreement.getTrade().setTermType(TermType.OPEN);
        position.setTermId(2);

        verifyReconciliationFailure();
    }

    @Test
    @Order(67)
    @DisplayName("Should throw exception if position.price is not matched with trade.contractPrice")
    void reconcile_shouldThrowException_whenPositionPriceIsNotMatchedWithContractPrice() {
        position.setPrice(1.0d);
        agreement.getTrade().getCollateral().setContractPrice(999.0d);

        verifyReconciliationFailure();
    }

    @Test
    @Order(68)
    @DisplayName("Throw exception if trade.rate.rebate.fixed.effectiveDate is not matched with position.settleDate")
    void reconcile_shouldThrowException_whenRateRebateFixedEffectiveDateNotMatched() {
        position.setSettleDate(LocalDateTime.now());
        agreement.getTrade().getRate().getRebate().getFixed().setEffectiveDate(LocalDate.now().minusDays(2));

        verifyReconciliationFailure();
    }

    @Test
    @Order(69)
    @DisplayName("Should reconcile trade.rate.rebate.fixed.effectiveDate with position.settleDate")
    void reconcile_shouldReconcile_whenRateRebateFixedEffectiveDateIsMatched() throws Exception {
        agreement.getTrade().getRate().getRebate().getFixed().setEffectiveDate(
            LocalDate.of(2023, 12, 1));
        position.setSettleDate(LocalDateTime.of(
            LocalDate.of(2023, 12, 1), LocalTime.of(10, 55)));

        // set tradeSettlementDate to match with position settlementDate also
        agreement.getTrade().setSettlementDate(LocalDate.of(2023, 12, 1));

        service.reconcile(agreement, position);
    }

    @Test
    @Order(70)
    @DisplayName("Throw exception if trade.rate.rebate.floating.spread is not matched with position.indexDTO.spread")
    void reconcile_shouldThrowException_whenRateRebateFloatingSpreadIsNotMatched() {
        var floating = FloatingRate.builder().spread(1.0d).build();
        agreement.getTrade().getRate().getRebate().setFixed(null);
        agreement.getTrade().getRate().getRebate().setFloating(floating);
        position.setIndexDto(new IndexDto("testName", 2.0d));

        verifyReconciliationFailure();
    }

    @Test
    @Order(71)
    @DisplayName("Should reconcile trade.rate.rebate.fixed.effectiveDate with position.settleDate")
    void reconcile_shouldReconcile_whenRateRebateFloatingSpreadIsMatched() throws Exception {
        var floating = FloatingRate.builder()
            .spread(1.0d)
            .effectiveRate(10.2d) // must match with positionDto test data position.rate
            .build();
        agreement.getTrade().getRate().getRebate().setFixed(null);
        agreement.getTrade().getRate().getRebate().setFloating(floating);
        position.setIndexDto(new IndexDto("testName", 1.0d));

        service.reconcile(agreement, position);
    }

    @Test
    @Order(72)
    @DisplayName("Throw exception if trade.rate.rebate doesn't have fixed or floating objects")
    void reconcile_shouldThrowException_whenRateRebateFixedAndFloatingMissed() {
        agreement.getTrade().getRate().setRebate(new RebateRate());

        verifyReconciliationFailure();
    }

    @Test
    @Order(73)
    @DisplayName("Throw exception if trade.rate.rebate.floating.effectiveRate is missed for Floating object")
    void reconcile_shouldThrowException_whenRateRebateFloatingEffectiveRateIsMissed() {
        var floating = FloatingRate.builder().effectiveRate(null).build();
        agreement.getTrade().getRate().getRebate().setFixed(null);
        agreement.getTrade().getRate().getRebate().setFloating(floating);

        verifyReconciliationFailure();
    }

    @Test
    @Order(74)
    @DisplayName("Throw exception if trade.rate.rebate.fixed.baseRate is missed for Fixed object")
    void reconcile_shouldThrowException_whenRateRebateFixedBaseRateIsMissed() {
        agreement.getTrade().getRate().getRebate().setFloating(null);
        agreement.getTrade().getRate().getRebate().setFixed(new FixedRate());

        verifyReconciliationFailure();
    }

    /*
     * Verifies that reconciliation fails because one of the
     * business requirements is not met.
     */
    private void verifyReconciliationFailure() {
        var agreementId = agreement.getAgreementId();
        var positionId = position.getPositionId();

        var exception = assertThrows(
            ReconcileException.class,
            () -> service.reconcile(agreement, position)
        );

        var expectedExceptionMessage = format(RECONCILE_EXCEPTION, agreementId, positionId);

        assertEquals(exception.getMessage(), expectedExceptionMessage);
    }

    @AfterEach
    void tearDown() {
        service = null;
        agreement = null;
        position = null;
    }

}