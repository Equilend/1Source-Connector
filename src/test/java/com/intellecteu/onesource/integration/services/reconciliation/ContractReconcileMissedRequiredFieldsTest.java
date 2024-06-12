package com.intellecteu.onesource.integration.services.reconciliation;

import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_ACCOUNT_LEI;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_CP_ACCOUNT_LEI;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_QUANTITY;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_TYPE_IS_CASH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.constant.AgreementConstant;
import com.intellecteu.onesource.integration.constant.AgreementConstant.Field;
import com.intellecteu.onesource.integration.constant.PositionConstant;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.model.ProcessExceptionDetails;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.PositionSecurityDetail;
import com.intellecteu.onesource.integration.model.enums.FieldExceptionType;
import com.intellecteu.onesource.integration.model.enums.FieldSource;
import com.intellecteu.onesource.integration.model.onesource.Benchmark;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.FloatingRate;
import com.intellecteu.onesource.integration.model.onesource.Instrument;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ContractReconcileMissedRequiredFieldsTest {

    private ReconcileService<Contract, Position> service;
    private Contract contract;
    private Position position;

    @BeforeEach
    void setUp() {
        service = new ContractReconcileService();
        contract = ModelTestFactory.buildContract();
        position = ModelTestFactory.buildPositionFromTradeAgreement(contract.getTrade());
    }

    @Test
    @Order(1)
    @DisplayName("Loan contract proposal must have instrument field")
    void checkContractRequiredField_mustHaveInstrument() {
        contract.getTrade().setInstrument(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(AgreementConstant.Field.INSTRUMENT, missedField.getFieldName());
    }

    @Test
    @Order(2)
    @DisplayName("Loan contract proposal must have at least one instrument in the Instrument field")
    void checkContractRequiredField_mustHaveAtLeastOneInInstrument() {
        final Instrument instrument = contract.getTrade().getInstrument();
        instrument.setCusip(null);
        instrument.setIsin(null);
        instrument.setSedol(null);
        instrument.setQuickCode(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertTrue(missedField.getFieldName().contains(Field.CUSIP));
        assertTrue(missedField.getFieldName().contains(Field.ISIN));
        assertTrue(missedField.getFieldName().contains(Field.SEDOL));
        assertTrue(missedField.getFieldName().contains(Field.QUICK));
    }

    @Test
    @Order(3)
    @DisplayName("Loan contract proposal must have rate field")
    void checkContractRequiredField_mustHaveFixedBaseRate() {
        contract.getTrade().setRate(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.RATE, missedField.getFieldName());
    }

    @Test
    @Order(4)
    @DisplayName("Loan contract proposal must have rebate field in rate")
    void checkContractRequiredField_mustHaveRebateInRate() {
        contract.getTrade().getRate().setRebate(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.REBATE, missedField.getFieldName());
    }

    @Test
    @Order(5)
    @DisplayName("Loan contract proposal must have rebate field in rate")
    void checkContractRequiredField_mustHaveFixedOrFloatingInRebateInsideRate() {
        contract.getTrade().getRate().getRebate().setFixed(null);
        contract.getTrade().getRate().getRebate().setFloating(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.REBATE, missedField.getFieldName());
    }

    @Test
    @Order(6)
    @DisplayName("Loan contract proposal must have base rate in fixed in rebate field in rate")
    void checkContractRequiredField_mustHaveBaseRateInFixedInRebateInsideRate() {
        contract.getTrade().getRate().getRebate().getFixed().setBaseRate(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.REBATE_FIXED_BASE_RATE, missedField.getFieldName());
    }

    @Test
    @Order(7)
    @DisplayName("Loan contract proposal must have benchmark in floating in rebate field in rate")
    void checkContractRequiredField_mustHaveBenchmarkInFloatingInRebateInsideRate() {
        var floating = new FloatingRate();
        floating.setEffectiveDate(LocalDate.now());
        contract.getTrade().getRate().getRebate().setFloating(floating);
        contract.getTrade().getRate().getRebate().setFixed(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.REBATE_FLOATING_BENCHMARK, missedField.getFieldName());
    }

    @Test
    @Order(8)
    @DisplayName("Loan contract proposal must have effective rate in floating in rebate field in rate")
    void checkContractRequiredField_mustHaveEffectiveRateInFloatingInRebateInsideRate() {
        var floating = new FloatingRate();
        floating.setBenchmark(Benchmark.BGCR);
        contract.getTrade().getRate().getRebate().setFloating(floating);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.REBATE_FLOATING_EFFECTIVE_RATE, missedField.getFieldName());
    }

    @Test
    @Order(9)
    @DisplayName("Loan contract proposal must have quantity in trade")
    void checkContractRequiredField_mustHaveQuantityInTrade() {
        contract.getTrade().setQuantity(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.QUANTITY, missedField.getFieldName());
    }

    @Test
    @Order(10)
    @DisplayName("Loan contract proposal must have billingCurrency in trade")
    void checkContractRequiredField_mustHaveBillingCurrencyInTrade() {
        contract.getTrade().setBillingCurrency(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.BILLING_CURRENCY, missedField.getFieldName());
    }

    @Test
    @Order(11)
    @DisplayName("Loan contract proposal must have trade date in trade")
    void checkContractRequiredField_mustHaveTradeDateInTrade() {
        contract.getTrade().setTradeDate(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.TRADE_DATE, missedField.getFieldName());
    }

    @Test
    @Order(12)
    @DisplayName("Loan contract proposal must have settlement date in trade")
    void checkContractRequiredField_mustHaveSettlementDateInTrade() {
        contract.getTrade().setSettlementDate(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.SETTLEMENT_DATE, missedField.getFieldName());
    }

    @Test
    @Order(13)
    @DisplayName("Loan contract proposal must have settlement type in trade")
    void checkContractRequiredField_mustHaveSettlementTypeInTrade() {
        contract.getTrade().setSettlementType(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.SETTLEMENT_TYPE, missedField.getFieldName());
    }

    @Test
    @Order(14)
    @DisplayName("Loan contract proposal must have collateral in trade")
    void checkContractRequiredField_mustHaveCollateralInTrade() {
        contract.getTrade().setCollateral(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.COLLATERAL, missedField.getFieldName());
    }

    @Test
    @Order(15)
    @DisplayName("Loan contract proposal must have collateral contractPrice in trade")
    void checkContractRequiredField_mustHaveCollateralContractPriceInTrade() {
        contract.getTrade().getCollateral().setContractPrice(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.CONTRACT_PRICE, missedField.getFieldName());
    }

    @Test
    @Order(16)
    @DisplayName("Loan contract proposal must have collateral collateralValue in trade")
    void checkContractRequiredField_mustHaveCollateralValueInTrade() {
        contract.getTrade().getCollateral().setCollateralValue(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.COLLATERAL_VALUE, missedField.getFieldName());
    }

    @Test
    @Order(17)
    @DisplayName("Loan contract proposal must have collateral currency in trade")
    void checkContractRequiredField_mustHaveCollateralCurrencyInTrade() {
        contract.getTrade().getCollateral().setCurrency(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.CURRENCY, missedField.getFieldName());
    }

    @Test
    @Order(18)
    @DisplayName("Loan contract proposal must have collateral type in trade")
    void checkContractRequiredField_mustHaveCollateralTypeInTrade() {
        contract.getTrade().getCollateral().setType(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.COLLATERAL_TYPE, missedField.getFieldName());
    }

    @Test
    @Order(19)
    @DisplayName("Loan contract proposal must have transacting parties in trade")
    void checkContractRequiredField_mustHaveTransactingPartiesInTrade() {
        contract.getTrade().setTransactingParties(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.TRANSACTING_PARTIES, missedField.getFieldName());
    }

    @Test
    @Order(20)
    @DisplayName("Loan contract proposal must have gleifLei in transacting parties in trade")
    void checkContractRequiredField_mustHavePartiesInTransactingPartiesInTrade() {
        contract.getTrade().getTransactingParties().get(0).getParty().setGleifLei(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.GLEIF_LEI, missedField.getFieldName());
    }

    @Test
    @Order(21)
    @DisplayName("Loan contract proposal must have trade")
    void checkContractRequiredField_mustHaveTrade() {
        contract.setTrade(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.ONE_SOURCE_LOAN_CONTRACT).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(Field.TRADE, missedField.getFieldName());
    }

    @Test
    @Order(22)
    @DisplayName("Position must have securityDetailDTO field")
    void checkPositionRequiredField_mustHaveSecurityDetailDTO() {
        position.setPositionSecurityDetail(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(PositionConstant.Field.POSITION_SECURITY, missedField.getFieldName());
    }

    @Test
    @Order(23)
    @DisplayName("Position must have at least one required field in the securityDetailDTO field")
    void checkContractRequiredField_mustHaveAtLeastOneInSecurityDetailDTO() {
        final PositionSecurityDetail positionSecurityDetail = position.getPositionSecurityDetail();
        positionSecurityDetail.setCusip(null);
        positionSecurityDetail.setIsin(null);
        positionSecurityDetail.setSedol(null);
        positionSecurityDetail.setQuickCode(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertTrue(missedField.getFieldName().contains(PositionConstant.Field.POSITION_CUSIP));
        assertTrue(missedField.getFieldName().contains(PositionConstant.Field.POSITION_ISIN));
        assertTrue(missedField.getFieldName().contains(PositionConstant.Field.POSITION_SEDOL));
        assertTrue(missedField.getFieldName().contains(PositionConstant.Field.POSITION_QUICK));
    }

    @Test
    @Order(24)
    @DisplayName("Position must have rate")
    void checkPositionRequiredField_mustHaveRate() {
        position.setRate(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(PositionConstant.Field.RATE, missedField.getFieldName());
    }

    @Test
    @Order(25)
    @DisplayName("Position must have index")
    void checkPositionRequiredField_mustHaveIndexDTO() {
        position.setIndex(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(PositionConstant.Field.POSITION_INDEX, missedField.getFieldName());
    }

    @Test
    @Order(26)
    @DisplayName("Position must have index name")
    void checkPositionRequiredField_mustHaveIndexName() {
        position.getIndex().setIndexName(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(PositionConstant.Field.POSITION_INDEX_NAME, missedField.getFieldName());
    }

    @Test
    @Order(27)
    @DisplayName("Position must have quantity")
    void checkPositionRequiredField_mustHaveQuantity() {
        position.setQuantity(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(POSITION_QUANTITY, missedField.getFieldName());
    }

    @Test
    @Order(28)
    @DisplayName("Position must have currency")
    void checkPositionRequiredField_mustHaveCurrency() {
        position.setCurrency(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(PositionConstant.Field.POSITION_CURRENCY, missedField.getFieldName());
    }

    @Test
    @Order(29)
    @DisplayName("Position must have currencyKy")
    void checkPositionRequiredField_mustHaveCurrencyKy() {
        position.getCurrency().setCurrencyKy(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(PositionConstant.Field.POSITION_CURRENCY_KY, missedField.getFieldName());
    }

    @Test
    @Order(30)
    @DisplayName("Position must have trade date")
    void checkPositionRequiredField_mustHaveTradeDate() {
        position.setTradeDate(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(PositionConstant.Field.POSITION_TRADE_DATE, missedField.getFieldName());
    }

    @Test
    @Order(31)
    @DisplayName("Position must have settle date")
    void checkPositionRequiredField_mustHaveSettleDate() {
        position.setSettleDate(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(PositionConstant.Field.SETTLE_DATE, missedField.getFieldName());
    }

    @Test
    @Order(32)
    @DisplayName("Position must have deliver free")
    void checkPositionRequiredField_mustHaveDeliverFree() {
        position.setDeliverFree(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(PositionConstant.Field.DELIVER_FREE, missedField.getFieldName());
    }

    @Test
    @Order(33)
    @DisplayName("Position must have price")
    void checkPositionRequiredField_mustHavePrice() {
        position.setPrice(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(PositionConstant.Field.POSITION_PRICE, missedField.getFieldName());
    }

    @Test
    @Order(34)
    @DisplayName("Position must have amount")
    void checkPositionRequiredField_mustHaveAmount() {
        position.setAmount(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(PositionConstant.Field.POSITION_AMOUNT, missedField.getFieldName());
    }

    @Test
    @Order(35)
    @DisplayName("Position must have position type")
    void checkPositionRequiredField_mustHavePositionType() {
        position.setPositionType(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(PositionConstant.Field.POSITION_TYPE, missedField.getFieldName());
    }

    @Test
    @Order(36)
    @DisplayName("Position must have is cash position type")
    void checkPositionRequiredField_mustHaveIsCashPositionType() {
        position.getPositionType().setIsCash(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(PositionConstant.Field.POSITION_TYPE_IS_CASH, missedField.getFieldName());
    }

    @Test
    @Order(37)
    @DisplayName("Position must have counterparty account")
    void checkPositionRequiredField_mustHaveCpAccount() {
        position.setPositionCpAccount(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(PositionConstant.Field.POSITION_CP_ACCOUNT, missedField.getFieldName());
    }

    @Test
    @Order(38)
    @DisplayName("Position must have account")
    void checkPositionRequiredField_mustHaveAccount() {
        position.setPositionAccount(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(PositionConstant.Field.POSITION_ACCOUNT, missedField.getFieldName());
    }

    @Test
    @Order(39)
    @DisplayName("Position must have lei in position account")
    void checkPositionRequiredField_mustHaveLeiInPositionAccount() {
        position.getPositionAccount().setLei(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(String.format("%s or %s", POSITION_ACCOUNT_LEI, POSITION_CP_ACCOUNT_LEI),
            missedField.getFieldName());
    }

    @Test
    @Order(40)
    @DisplayName("Position must have lei in counterparty position account")
    void checkPositionRequiredField_mustHaveLeiInCpPositionAccount() {
        position.getPositionCpAccount().setLei(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        ProcessExceptionDetails missedField = exception.getErrorList().stream().filter(
            e -> e.getSource() == FieldSource.BACKOFFICE_POSITION).findAny().orElse(null);

        assertNotNull(missedField);
        assertEquals(FieldExceptionType.MISSING, missedField.getFieldExceptionType());
        assertEquals(String.format("%s or %s", POSITION_ACCOUNT_LEI, POSITION_CP_ACCOUNT_LEI),
            missedField.getFieldName());
    }

    @Test
    @Order(41)
    @DisplayName("Reconciliation must capture all missed required fields for contract")
    void checkContractRequiredField_mustCaptureAllMissedFields() {
        contract.getTrade().setQuantity(null);
        contract.getTrade().getCollateral().setContractPrice(null);
        contract.getTrade().getTransactingParties().forEach(p -> p.getParty().setGleifLei(null));

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        List<ProcessExceptionDetails> missedField = exception.getErrorList();
        final Set<String> missedFieldNames = missedField.stream().map(ProcessExceptionDetails::getFieldName)
            .collect(Collectors.toSet());

        assertNotNull(missedField);
        assertTrue(missedFieldNames.contains(Field.QUANTITY));
        assertTrue(missedFieldNames.contains(Field.CONTRACT_PRICE));
        assertTrue(missedFieldNames.contains(Field.GLEIF_LEI));
    }

    @Test
    @Order(42)
    @DisplayName("Reconciliation must capture all missed required fields for position")
    void checkPositionRequiredField_mustCaptureAllMissedFields() {
        position.setQuantity(null);
        position.getPositionType().setIsCash(null);
        position.getPositionCpAccount().setLei(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        List<ProcessExceptionDetails> missedField = exception.getErrorList();
        final Set<String> missedFieldNames = missedField.stream().map(ProcessExceptionDetails::getFieldName)
            .collect(Collectors.toSet());

        assertNotNull(missedField);
        assertTrue(missedFieldNames.contains(POSITION_QUANTITY));
        assertTrue(missedFieldNames.contains(POSITION_TYPE_IS_CASH));
        assertTrue(missedFieldNames.contains(String.format("%s or %s", POSITION_ACCOUNT_LEI, POSITION_CP_ACCOUNT_LEI)));
    }

    @Test
    @Order(43)
    @DisplayName("Reconciliation must capture all missed required fields for contract and position")
    void checkPositionRequiredField_mustCaptureAllMissedFieldsForContractAndPosition() {
        position.setQuantity(null);
        position.getPositionType().setIsCash(null);
        contract.getTrade().setQuantity(null);
        contract.getTrade().getCollateral().setContractPrice(null);

        var exception = assertThrows(ReconcileException.class, () -> service.reconcile(contract, position));
        List<ProcessExceptionDetails> missedField = exception.getErrorList();
        final Set<String> missedFieldNames = missedField.stream().map(ProcessExceptionDetails::getFieldName)
            .collect(Collectors.toSet());

        assertNotNull(missedField);
        assertTrue(missedFieldNames.contains(POSITION_QUANTITY));
        assertTrue(missedFieldNames.contains(POSITION_TYPE_IS_CASH));
        assertTrue(missedFieldNames.contains(Field.QUANTITY));
        assertTrue(missedFieldNames.contains(Field.CONTRACT_PRICE));
    }
}