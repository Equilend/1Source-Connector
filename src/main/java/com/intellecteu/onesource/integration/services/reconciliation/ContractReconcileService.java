package com.intellecteu.onesource.integration.services.reconciliation;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.BILLING_CURRENCY;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_MARGIN;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_ROUNDING_MODE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_ROUNDING_RULE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_TYPE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_VALUE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CONTRACT_PRICE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CURRENCY;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CUSIP;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.DIVIDENT_RATE_PCT;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.GLEIF_LEI;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.ISIN;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.PRICE_UNIT;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.QUANTITY;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.QUICK;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.REBATE_FIXED;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.REBATE_FIXED_EFFECTIVE_DATE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.REBATE_FLOATING_BENCHMARK;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.REBATE_FLOATING_EFFECTIVE_DATE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.REBATE_FLOATING_EFFECTIVE_RATE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.REBATE_FLOATING_SPREAD;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SEDOL;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SETTLEMENT_DATE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SETTLEMENT_TYPE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.TERM_DATE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.TERM_TYPE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.TRADE_DATE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.VENUE_REF_KEY;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.ACCRUAL_DATE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.CP_HAIRCUT;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.CP_MARKROUND_TO;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.CUSTOM_VALUE_2;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.DELIVER_FREE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_AMOUNT;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_CURRENCY_KY;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_CUSIP;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_END_DATE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_INDEX_NAME;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_ISIN;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_PRICE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_QUANTITY;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_QUICK;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_SEDOL;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_SPREAD;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_TERM_ID;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_TRADE_DATE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.RATE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.SETTLE_DATE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.TAX_WITH_HOLDING_RATE;
import static com.intellecteu.onesource.integration.model.enums.FieldSource.BACKOFFICE_POSITION;
import static com.intellecteu.onesource.integration.model.enums.FieldSource.ONE_SOURCE_LOAN_CONTRACT;
import static com.intellecteu.onesource.integration.model.onesource.PriceUnit.LOT;
import static com.intellecteu.onesource.integration.model.onesource.PriceUnit.SHARE;
import static com.intellecteu.onesource.integration.model.onesource.RoundingMode.ALWAYSUP;
import static com.intellecteu.onesource.integration.model.onesource.SettlementType.DVP;
import static com.intellecteu.onesource.integration.model.onesource.SettlementType.FOP;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.model.ProcessExceptionDetails;
import com.intellecteu.onesource.integration.model.backoffice.Index;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.PositionSecurityDetail;
import com.intellecteu.onesource.integration.model.enums.FieldExceptionType;
import com.intellecteu.onesource.integration.model.enums.FieldSource;
import com.intellecteu.onesource.integration.model.onesource.Collateral;
import com.intellecteu.onesource.integration.model.onesource.CollateralType;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.FixedRate;
import com.intellecteu.onesource.integration.model.onesource.FloatingRate;
import com.intellecteu.onesource.integration.model.onesource.Instrument;
import com.intellecteu.onesource.integration.model.onesource.Price;
import com.intellecteu.onesource.integration.model.onesource.Rate;
import com.intellecteu.onesource.integration.model.onesource.SettlementType;
import com.intellecteu.onesource.integration.model.onesource.TermType;
import com.intellecteu.onesource.integration.model.onesource.TradeAgreement;
import com.intellecteu.onesource.integration.model.onesource.TransactingParty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class ContractReconcileService implements ReconcileService<Contract, Position> {

    public static final String RECONCILE_EXCEPTION = """
        The 1source contract id:%s is in discrepancies with the backoffice position id:%s""";

    public static final String RECONCILE_MISMATCH = "Reconciliation mismatch. Contract proposal %s:%s "
        + "is not matched with Backoffice position %s:%s";

    @Override
    public void reconcile(Contract contract, @NonNull Position backofficePosition) throws ReconcileException {
        var reconciliationExceptionDetails = new ArrayList<ProcessExceptionDetails>();
        var tradeAgreement = contract.getTrade();
        reconciliationExceptionDetails.addAll(validateReconcilableObject(contract, ONE_SOURCE_LOAN_CONTRACT));
        reconciliationExceptionDetails.addAll(validateReconcilableObject(backofficePosition, BACKOFFICE_POSITION));
        reconciliationExceptionDetails.addAll(reconcileTrade(tradeAgreement, backofficePosition));
        if (!reconciliationExceptionDetails.isEmpty()) {
            reconciliationExceptionDetails.forEach(msg -> log.trace(msg.getFieldValue()));
            throw new ReconcileException(
                format(RECONCILE_EXCEPTION, contract.getContractId(), backofficePosition.getPositionId()),
                reconciliationExceptionDetails);
        }
    }

    private List<ProcessExceptionDetails> validateReconcilableObject(Reconcilable reconcilable,
        FieldSource fieldSource) {
        return validateReconcilableObject().apply(reconcilable).stream().map(
            invalidField -> new ProcessExceptionDetails(fieldSource, invalidField, "null",
                FieldExceptionType.MISSING)).toList();
    }

    private Collection<? extends ProcessExceptionDetails> reconcileTrade(TradeAgreement trade,
        @NonNull Position position) {
        var failedList = new ArrayList<ProcessExceptionDetails>();
        reconcileVenue(trade, position).ifPresent(failedList::add);
        failedList.addAll(reconcileInstrument(trade.getInstrument(), position));
        failedList.addAll(reconcileRate(trade.getRate(), position));
        reconcileQuantity(trade, position).ifPresent(failedList::add);
        reconcileBillingCurrency(trade, position).ifPresent(failedList::add);
        reconcileDividendRate(trade, position).ifPresent(failedList::add);
        reconcileTradeDate(trade, position).ifPresent(failedList::add);
        reconcileSettlementDate(trade, position).ifPresent(failedList::add);
        reconcileTermType(trade.getTermType(), position.getTermId()).ifPresent(failedList::add);
        reconcileTermDate(trade, position).ifPresent(failedList::add);
        reconcileSettlementType(trade.getSettlementType(), position).ifPresent(failedList::add);
        failedList.addAll(reconcileCollateral(trade.getCollateral(), position));
        reconcileLei(trade.getTransactingParties(), position).ifPresent(failedList::add);
        return failedList;
    }

    private Optional<ProcessExceptionDetails> reconcileVenue(TradeAgreement trade, Position position) {
        if (position.getCustomValue2() == null || CollectionUtils.isEmpty(trade.getVenues())) {
            return Optional.empty();
        }
        if (trade.getVenues().get(0) == null || trade.getVenues().get(0).getVenueRefKey() == null) {
            return Optional.empty();
        }
        return checkEquality(trade.getVenues().get(0).getVenueRefKey(), VENUE_REF_KEY,
            position.getCustomValue2(), CUSTOM_VALUE_2);
    }

    private List<ProcessExceptionDetails> reconcileInstrument(Instrument instrument, Position position) {
        var failsList = checkEqualityOfSecurityIdentifiers(instrument, position);
        failsList.addAll(checkInstrumentUnit(instrument.getPrice(), position.getPositionSecurityDetail()));
        return failsList;
    }

    /*
     * Must reconcile if present. At least one security identifier must be present
     * ('At least one' presence is checked inside Position domain model logic)
     */
    private List<ProcessExceptionDetails> checkEqualityOfSecurityIdentifiers(Instrument instrument,
        Position position) {
        List<ProcessExceptionDetails> failsLog = new ArrayList<>();
        var securityDetail = position.getPositionSecurityDetail();
        if (securityDetail == null) {
            return failsLog;
        }
        if (securityDetail.getCusip() != null && instrument.getCusip() != null) {
            checkEquality(instrument.getCusip(), CUSIP, securityDetail.getCusip(), POSITION_CUSIP)
                .ifPresent(failsLog::add);
        }
        if (securityDetail.getIsin() != null && instrument.getIsin() != null) {
            checkEquality(instrument.getIsin(), ISIN, securityDetail.getIsin(), POSITION_ISIN)
                .ifPresent(failsLog::add);
        }
        if (securityDetail.getSedol() != null && instrument.getSedol() != null) {
            checkEquality(instrument.getSedol(), SEDOL, securityDetail.getSedol(), POSITION_SEDOL)
                .ifPresent(failsLog::add);
        }
        if (securityDetail.getQuickCode() != null && instrument.getQuickCode() != null) {
            checkEquality(instrument.getQuickCode(), QUICK, securityDetail.getQuickCode(), POSITION_QUICK)
                .ifPresent(failsLog::add);
        }
        return failsLog;
    }

    private List<ProcessExceptionDetails> checkInstrumentUnit(Price price, PositionSecurityDetail securityDetail) {
        List<ProcessExceptionDetails> failsLog = new ArrayList<>();
        if (price != null && securityDetail != null && securityDetail.getPriceFactor() != null) {
            final Integer priceFactor = securityDetail.getPriceFactor();
            if ((priceFactor.equals(1) && price.getUnit() != SHARE)
                || (!priceFactor.equals(1) && price.getUnit() != LOT)) {
                var exceptionDto = ProcessExceptionDetails.builder()
                    .source(ONE_SOURCE_LOAN_CONTRACT)
                    .fieldName(PRICE_UNIT)
                    .fieldValue(price.getUnit().name())
                    .fieldExceptionType(FieldExceptionType.DISCREPANCY)
                    .build();
                failsLog.add(exceptionDto);
            }
        }
        return failsLog;
    }

    private List<ProcessExceptionDetails> reconcileRate(Rate rate, @NonNull Position position) {
        List<ProcessExceptionDetails> failsLog = new ArrayList<>();
        var positionIndex = position.getIndex();
        if (rate != null && rate.getRebate() != null && positionIndex != null) {
            if (fixedPositionRate(positionIndex)) {
                if (rate.getRebate().getFixed() != null && position.getRate() != null) {
                    checkEquality(rate.getRebate().getFixed().getBaseRate(), REBATE_FIXED, position.getRate(), RATE)
                        .ifPresent(failsLog::add);
                    reconcileFixedDate(rate.getRebate().getFixed(), position)
                        .ifPresent(failsLog::add);
                }
            }
            if (floatingPositionRate(positionIndex)) {
                final FloatingRate floatingRate = rate.getRebate().getFloating();
                if (floatingRate != null) {
                    checkEquality(floatingRate.getBenchmark().name(),
                        REBATE_FLOATING_BENCHMARK, positionIndex.getIndexName(), POSITION_INDEX_NAME)
                        .ifPresent(failsLog::add);
                    reconcileFloatingRebateSpread(floatingRate, positionIndex)
                        .ifPresent(failsLog::add);
                    checkEquality(floatingRate.getEffectiveRate(),
                        REBATE_FLOATING_EFFECTIVE_RATE, position.getRate(), RATE)
                        .ifPresent(failsLog::add);
                    reconcileFloatingDate(floatingRate, position)
                        .ifPresent(failsLog::add);
                }
            }
        }
        return failsLog;
    }

    private Optional<ProcessExceptionDetails> reconcileFloatingDate(FloatingRate floatingRate, Position position) {
        final LocalDate effectiveDate = floatingRate.getEffectiveDate();
        final LocalDateTime settleDate = position.getSettleDate();
        if (effectiveDate != null && settleDate != null) {
            return checkEquality(effectiveDate, REBATE_FLOATING_EFFECTIVE_DATE,
                settleDate.toLocalDate(), SETTLE_DATE);
        }
        return Optional.empty();
    }

    private boolean floatingPositionRate(Index positionIndex) {
        return !"Fixed Rate".equals(positionIndex.getIndexName()) && 12 != positionIndex.getIndexId();
    }

    private Optional<ProcessExceptionDetails> reconcileFixedDate(FixedRate fixedRate, Position position) {
        final LocalDate effectiveDate = fixedRate.getEffectiveDate();
        final LocalDateTime accrualDate = position.getAccrualDate();
        if (effectiveDate != null && accrualDate != null) {
            return checkEquality(effectiveDate, REBATE_FIXED_EFFECTIVE_DATE,
                accrualDate.toLocalDate(), ACCRUAL_DATE);
        }
        return Optional.empty();
    }

    private Optional<ProcessExceptionDetails> reconcileFloatingRebateSpread(@NonNull FloatingRate floatingRate,
        @NonNull Index positionIndex) {
        final Double spread = floatingRate.getSpread();
        final Double positionSpread = positionIndex.getSpread();
        if (spread != null && positionSpread != null) {
            return checkEquality(spread, REBATE_FLOATING_SPREAD, positionSpread, POSITION_SPREAD);
        }
        return Optional.empty();
    }

    private boolean fixedPositionRate(Index positionIndex) {
        return "Fixed Rate".equals(positionIndex.getIndexName()) || 12 == positionIndex.getIndexId();
    }

    private Optional<ProcessExceptionDetails> reconcileQuantity(TradeAgreement trade, Position position) {
        if (trade.getQuantity() != null && position.getQuantity() != null) {
            return checkEquality(trade.getQuantity(), QUANTITY, position.getQuantity().intValue(), POSITION_QUANTITY);
        }
        return Optional.empty();
    }

    private Optional<ProcessExceptionDetails> reconcileBillingCurrency(TradeAgreement trade, Position position) {
        if (trade.getBillingCurrency() != null && position.getCurrency() != null) {
            final String tradeCurrency = trade.getBillingCurrency().name();
            final String positionCurrencyKy = position.getCurrency().getCurrencyKy();
            return checkEquality(tradeCurrency, BILLING_CURRENCY, positionCurrencyKy, POSITION_CURRENCY_KY);
        }
        return Optional.empty();
    }

    private Optional<ProcessExceptionDetails> reconcileDividendRate(TradeAgreement trade, Position position) {
        if (position.getLoanBorrow() != null && position.getLoanBorrow().getTaxWithholdingRate() != null
            && trade.getDividendRatePct() != null) {
            return checkEquality(trade.getDividendRatePct(), DIVIDENT_RATE_PCT,
                position.getLoanBorrow().getTaxWithholdingRate(), TAX_WITH_HOLDING_RATE);
        }
        return Optional.empty();
    }

    private Optional<ProcessExceptionDetails> reconcileTradeDate(TradeAgreement trade, Position positionDto) {
        if (trade.getTradeDate() != null && positionDto.getTradeDate() != null) {
            return checkEquality(trade.getTradeDate(), TRADE_DATE,
                positionDto.getTradeDate().toLocalDate(), POSITION_TRADE_DATE);
        }
        return Optional.empty();
    }

    private Optional<ProcessExceptionDetails> reconcileSettlementDate(TradeAgreement trade, Position positionDto) {
        if (trade.getSettlementDate() != null && positionDto.getSettleDate() != null) {
            return checkEquality(trade.getSettlementDate(), SETTLEMENT_DATE,
                positionDto.getSettleDate().toLocalDate(), SETTLE_DATE);
        }
        return Optional.empty();
    }

    private Optional<ProcessExceptionDetails> reconcileTermType(TermType termType, Integer termId) {
        if (termType != null && termId != null) {
            if ((termId.equals(1) && termType != TermType.OPEN)
                || (termId.equals(2) && termType != TermType.FIXED)) {
                var exceptionDto = ProcessExceptionDetails.builder()
                    .source(ONE_SOURCE_LOAN_CONTRACT)
                    .fieldName(TERM_TYPE)
                    .fieldValue(String.format(RECONCILE_MISMATCH, TERM_TYPE, termType,
                        POSITION_TERM_ID, termId))
                    .fieldExceptionType(FieldExceptionType.DISCREPANCY)
                    .build();
                return Optional.of(exceptionDto);
            }
        }
        return Optional.empty();
    }

    private Optional<ProcessExceptionDetails> reconcileTermDate(TradeAgreement trade, Position positionDto) {
        if (trade.getTermDate() != null && positionDto.getEndDate() != null) {
            return checkEquality(trade.getTermDate(), TERM_DATE,
                positionDto.getEndDate().toLocalDate(), POSITION_END_DATE);
        }
        return Optional.empty();
    }

    /*
     * If the SettlementType is DVP then the deliverFree must be FALSE
     */
    private Optional<ProcessExceptionDetails> reconcileSettlementType(SettlementType settlementType,
        Position position) {
        if (settlementType != null && position.getDeliverFree() != null) {
            if ((settlementType == DVP && position.getDeliverFree())
                || (settlementType == FOP && !position.getDeliverFree())) {
                var exceptionDto = ProcessExceptionDetails.builder()
                    .source(ONE_SOURCE_LOAN_CONTRACT)
                    .fieldName(SETTLEMENT_TYPE)
                    .fieldValue(String.format(RECONCILE_MISMATCH, SETTLEMENT_TYPE, settlementType,
                        DELIVER_FREE, position.getDeliverFree()))
                    .fieldExceptionType(FieldExceptionType.DISCREPANCY)
                    .build();
                return Optional.of(exceptionDto);
            }
        }
        return Optional.empty();
    }

    private List<ProcessExceptionDetails> reconcileCollateral(Collateral collateral, Position position) {
        List<ProcessExceptionDetails> failsLog = new ArrayList<>();
        if (collateral.getContractPrice() != null) {
            checkEquality(collateral.getContractPrice(), CONTRACT_PRICE, position.getPrice(), POSITION_PRICE)
                .ifPresent(failsLog::add);
        }
        if (collateral.getCollateralValue() != null) {
            checkEquality(collateral.getCollateralValue(), COLLATERAL_VALUE, position.getAmount(), POSITION_AMOUNT)
                .ifPresent(failsLog::add);
        }
        if (collateral.getCurrency() != null && position.getCurrency() != null
            && position.getCurrency().getCurrencyKy() != null) {
            final String currencyKy = position.getCurrency().getCurrencyKy();
            checkEquality(collateral.getCurrency(), CURRENCY, currencyKy, POSITION_CURRENCY_KY)
                .ifPresent(failsLog::add);
        }
        if (collateral.getType() != null && position.getPositionType() != null
            && position.getPositionType().getIsCash() != null) {
            if (position.getPositionType().getIsCash() && collateral.getType() != CollateralType.CASH) {
                failsLog.add(new ProcessExceptionDetails(
                    ONE_SOURCE_LOAN_CONTRACT,
                    COLLATERAL_TYPE,
                    String.valueOf(collateral.getType()),
                    FieldExceptionType.DISCREPANCY));
            }
        }
        if (collateral.getMargin() != null && position.getCpHaircut() != null) {
            // currently we have different value types for margin 1source and cpHaircut SPIRE 102 vs 1.02
            // the question is can we have values like: 102.0457 and 1.020457
            // temporary change data type to String and compare
            var oneSourceMargin = collateral.getMargin(); // expected value: 102.0
            var spireCpHaircut = position.getCpHaircut(); // expected value: 1.02
            checkEquality(String.valueOf(oneSourceMargin), COLLATERAL_MARGIN,
                String.valueOf(spireCpHaircut * 100.0), CP_HAIRCUT)
                .ifPresent(failsLog::add);
        }
        if (collateral.getRoundingRule() != null && position.getCpMarkRoundTo() != null) {
            checkEquality(collateral.getRoundingRule(), COLLATERAL_ROUNDING_RULE,
                position.getCpMarkRoundTo(), CP_MARKROUND_TO)
                .ifPresent(failsLog::add);
        }
        if (collateral.getRoundingMode() != null && collateral.getRoundingMode() != ALWAYSUP) {
            failsLog.add(new ProcessExceptionDetails(
                ONE_SOURCE_LOAN_CONTRACT,
                COLLATERAL_ROUNDING_MODE,
                String.valueOf(collateral.getRoundingMode()),
                FieldExceptionType.DISCREPANCY));
        }
        return failsLog;
    }

    private Optional<ProcessExceptionDetails> reconcileLei(List<TransactingParty> transactionParties,
        Position position) {
        var positionAccountLei = position.getAccountLei();
        var positionCpLei = position.getCpLei();

        for (var txParty : transactionParties) {
            var partyGleifLei = txParty.getParty().getGleifLei();
            if (partyGleifLei != null) {
                if (partyGleifLei.equals(positionAccountLei) || partyGleifLei.equals(positionCpLei)) {
                    return Optional.empty();
                }
            }
        }
        String valueMsg = "Reconciliation mismatch. OneSourceAccount Lei or CounterParty Lei "
            + "is not matched with Spire Position Lei";
        return Optional.of(new ProcessExceptionDetails(
            ONE_SOURCE_LOAN_CONTRACT,
            GLEIF_LEI,
            valueMsg,
            FieldExceptionType.DISCREPANCY));
    }

    Optional<ProcessExceptionDetails> checkEquality(@NonNull Object contractField, String contractFieldName,
        @NonNull Object positionField, String positionFieldName) {
        if (!Objects.equals(contractField, positionField)) {
            log.debug(format(RECONCILE_MISMATCH, contractField, contractFieldName, positionField, positionFieldName));
            return Optional.of(new ProcessExceptionDetails(
                ONE_SOURCE_LOAN_CONTRACT,
                contractFieldName,
                String.valueOf(contractField),
                FieldExceptionType.DISCREPANCY));
        }
        return Optional.empty();
    }
}
