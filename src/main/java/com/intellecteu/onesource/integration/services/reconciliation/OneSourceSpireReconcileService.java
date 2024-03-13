package com.intellecteu.onesource.integration.services.reconciliation;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_MARGIN;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_TYPE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_VALUE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CONTRACT_PRICE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CONTRACT_VALUE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CURRENCY;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CUSIP;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.DIVIDENT_RATE_PCT;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.GLEIF_LEI;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.ISIN;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.PRICE_UNIT;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.QUANTITY;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.QUICK;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.REBATE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.REBATE_FIXED_EFFECTIVE_DATE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.REBATE_FLOATING_SPREAD;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SEDOL;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SETTLEMENT_DATE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SETTLEMENT_TYPE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.TERM_TYPE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.TRADE_DATE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.VENUE_REF_ID;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.CP_HAIRCUT;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.CUSTOM_VALUE_2;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.DELIVER_FREE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_AMOUNT;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_COLLATERAL_TYPE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_CONTRACT_VALUE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_CURRENCY;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_CUSIP;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_ISIN;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_PRICE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_PRICE_FACTOR;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_QUANTITY;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_QUICK;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_SEDOL;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_SPREAD;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_TERM_ID;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_TRADE_DATE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.RATE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.SETTLE_DATE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.TAX_WITH_HOLDING_RATE;
import static com.intellecteu.onesource.integration.exception.ReconcileException.RECONCILE_MISMATCH;
import static com.intellecteu.onesource.integration.model.onesource.PriceUnit.LOT;
import static com.intellecteu.onesource.integration.model.onesource.PriceUnit.SHARE;
import static com.intellecteu.onesource.integration.model.onesource.SettlementType.DVP;
import static com.intellecteu.onesource.integration.model.onesource.SettlementType.FOP;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.dto.spire.SecurityDetailDto;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.model.ProcessExceptionDetails;
import com.intellecteu.onesource.integration.model.enums.FieldExceptionType;
import com.intellecteu.onesource.integration.model.onesource.Collateral;
import com.intellecteu.onesource.integration.model.onesource.CollateralType;
import com.intellecteu.onesource.integration.model.onesource.Instrument;
import com.intellecteu.onesource.integration.model.onesource.Price;
import com.intellecteu.onesource.integration.model.onesource.Rate;
import com.intellecteu.onesource.integration.model.onesource.RebateRate;
import com.intellecteu.onesource.integration.model.onesource.SettlementType;
import com.intellecteu.onesource.integration.model.onesource.TermType;
import com.intellecteu.onesource.integration.model.onesource.TradeAgreement;
import com.intellecteu.onesource.integration.model.onesource.TransactingParty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public abstract class OneSourceSpireReconcileService<T extends Reconcilable, R extends Reconcilable>
    implements ReconcileService<T, R> {

    public static final String RECONCILE_EXCEPTION = "The trade agreement %s is in discrepancies "
        + "with the position %s in Spire";

    /**
     * Reconcile the trade agreement retrieved from OneSource against the position retrieved from Spire.
     *
     * @param first Instance of Reconcilable
     * @param second Instance of Reconcilable
     * @throws ReconcileException if at least one reconciliation rule fails
     */
    public abstract void reconcile(T first, R second) throws ReconcileException;

    /*
     * Validates required fields presence and required data mismatches.
     */
    List<ProcessExceptionDetails> validateReconcilableObjects(T first, R second) {
        var firstExceptionValidation = validateReconcilableObject().apply(first).stream()
            .map(invalidField -> new ProcessExceptionDetails(null, invalidField, "null", FieldExceptionType.MISSING))
            .toList();
        var secondExceptionValidation = validateReconcilableObject().apply(second).stream()
            .map(invalidField -> new ProcessExceptionDetails(null, invalidField, "null", FieldExceptionType.MISSING))
            .toList();
        var failsList = new ArrayList<ProcessExceptionDetails>();
        CollectionUtils.addAll(failsList, firstExceptionValidation);
        CollectionUtils.addAll(failsList, secondExceptionValidation);
        return failsList;
    }


    List<ProcessExceptionDetails> reconcileTrade(TradeAgreement trade, PositionDto positionDto) {
        var failedList = new ArrayList<ProcessExceptionDetails>();
        reconcileVenue(trade, positionDto).ifPresent(failedList::add);
        failedList.addAll(reconcileRate(trade.getRate(), positionDto));
        failedList.addAll(reconcileInstrument(trade.getInstrument(), positionDto));
        reconcileQuantity(trade, positionDto).ifPresent(failedList::add);
        reconcileDividendRate(trade, positionDto).ifPresent(failedList::add);
        reconcileTradeDate(trade, positionDto).ifPresent(failedList::add);
        reconcileSettlementDate(trade, positionDto).ifPresent(failedList::add);
        reconcileTermType(trade.getTermType(), positionDto.getTermId()).ifPresent(failedList::add);
        reconcileSettlementType(trade.getSettlementType(), positionDto).ifPresent(failedList::add);
        failedList.addAll(reconcileCollateral(trade.getCollateral(), positionDto));
        reconcileLei(trade.getTransactingParties(), positionDto).ifPresent(failedList::add);
        return failedList;
    }

    private Optional<ProcessExceptionDetails> reconcileLei(List<TransactingParty> transactionParties,
        PositionDto position) {
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
        var exceptionDto = new ProcessExceptionDetails();
        exceptionDto.setFieldName(GLEIF_LEI);
        exceptionDto.setFieldValue("Reconciliation mismatch. OneSourceAccount Lei or CounterParty Lei "
            + "is not matched with Spire Position Lei");
        return Optional.of(exceptionDto);
    }

    private List<ProcessExceptionDetails> reconcileCollateral(Collateral collateral, PositionDto position) {
        List<ProcessExceptionDetails> failsLog = new ArrayList<>();
        if (collateral.getContractPrice() != null) {
            checkEquality(collateral.getContractPrice(), CONTRACT_PRICE, position.getPrice(), POSITION_PRICE)
                .ifPresent(failsLog::add);
        }
        // temporary parseInt solution until position data structure will be clarified
        // please take into account that mock data for Position.contractValue should be parseable to Integer
        if (collateral.getContractValue() != null && position.getContractValue() != null) {
            checkEquality(collateral.getContractValue(), CONTRACT_VALUE,
                position.getContractValue(), POSITION_CONTRACT_VALUE)
                .ifPresent(failsLog::add);
        }
        if (collateral.getCollateralValue() != null) {
            checkEquality(collateral.getCollateralValue(), COLLATERAL_VALUE, position.getAmount(), POSITION_AMOUNT)
                .ifPresent(failsLog::add);
        }
        if (collateral.getCurrency() != null && position.getCurrency() != null
            && position.getCurrency().getCurrencyKy() != null) {
            final String currencyKy = position.getCurrency().getCurrencyKy();
            checkEquality(collateral.getCurrency(), CURRENCY, currencyKy, POSITION_CURRENCY)
                .ifPresent(failsLog::add);
        }
        if (collateral.getType() != null && position.getCollateralType() != null) {
            if (position.getCollateralType().isEmpty()) {
                if (collateral.getType() != CollateralType.CASH) {
                    var msg = format("Reconciliation mismatch. OneSource %s must be %s when %s is empty",
                        COLLATERAL_TYPE, CollateralType.CASH, POSITION_COLLATERAL_TYPE);
                    failsLog.add(new ProcessExceptionDetails(null, COLLATERAL_TYPE, String.valueOf(collateral.getType()), FieldExceptionType.DISCREPANCY));
                }
            } else {
                checkEquality(collateral.getType().name(), COLLATERAL_TYPE,
                    position.getCollateralType(), POSITION_COLLATERAL_TYPE)
                    .ifPresent(failsLog::add);
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
        return failsLog;
    }

    /*
     * If the SettlementType is DVP then the deliverFree must be True
     */
    private Optional<ProcessExceptionDetails> reconcileSettlementType(SettlementType settlementType,
        PositionDto position) {
        if (settlementType != null && position.getDeliverFree() != null) {
            if ((settlementType == DVP && position.getDeliverFree())
                || (settlementType == FOP && !position.getDeliverFree())) {
                var exceptionDto = new ProcessExceptionDetails();
                exceptionDto.setFieldName(SETTLEMENT_TYPE);
                exceptionDto.setFieldValue(String.format(RECONCILE_MISMATCH, SETTLEMENT_TYPE, settlementType,
                    DELIVER_FREE, position.getDeliverFree()));
                return Optional.of(exceptionDto);
            }
        }
        return Optional.empty();
    }

    /*
     * Must reconcile if present. At least one security identifier must be present
     * ('At least one' presence is checked inside PositionDto domain model logic)
     */
    private List<ProcessExceptionDetails> checkEqualityOfSecurityIdentifiers(Instrument instrument,
        PositionDto position) {
        List<ProcessExceptionDetails> failsLog = new ArrayList<>();
        var securityDetailDto = position.getSecurityDetailDto();
        if (securityDetailDto != null) {
            if (securityDetailDto.getCusip() != null) {
                checkEquality(instrument.getCusip(), CUSIP, securityDetailDto.getCusip(), POSITION_CUSIP)
                    .ifPresent(failsLog::add);
            }
            if (securityDetailDto.getIsin() != null) {
                checkEquality(instrument.getIsin(), ISIN, securityDetailDto.getIsin(), POSITION_ISIN)
                    .ifPresent(failsLog::add);
            }
            if (securityDetailDto.getSedol() != null) {
                checkEquality(instrument.getSedol(), SEDOL, securityDetailDto.getSedol(), POSITION_SEDOL)
                    .ifPresent(failsLog::add);
            }
            if (securityDetailDto.getQuickCode() != null) {
                checkEquality(instrument.getQuickCode(), QUICK, securityDetailDto.getQuickCode(), POSITION_QUICK)
                    .ifPresent(failsLog::add);
            }
        }
        return failsLog;
    }

    private Optional<ProcessExceptionDetails> reconcileSettlementDate(TradeAgreement trade, PositionDto positionDto) {
        if (trade.getSettlementDate() != null && positionDto.getSettleDate() != null) {
            return checkEquality(trade.getSettlementDate(), SETTLEMENT_DATE,
                positionDto.getSettleDate().toLocalDate(), SETTLE_DATE);
        }
        return Optional.empty();
    }

    private Optional<ProcessExceptionDetails> reconcileTradeDate(TradeAgreement trade, PositionDto positionDto) {
        if (trade.getTradeDate() != null && positionDto.getTradeDate() != null) {
            return checkEquality(trade.getTradeDate(), TRADE_DATE,
                positionDto.getTradeDate().toLocalDate(), POSITION_TRADE_DATE);
        }
        return Optional.empty();
    }

    private Optional<ProcessExceptionDetails> reconcileDividendRate(TradeAgreement trade, PositionDto positionDto) {
        if (positionDto.getLoanBorrowDto() != null && positionDto.getLoanBorrowDto().getTaxWithholdingRate() != null
            && trade.getDividendRatePct() != null) {
            return checkEquality(trade.getDividendRatePct().doubleValue(), DIVIDENT_RATE_PCT,
                positionDto.getLoanBorrowDto().getTaxWithholdingRate(), TAX_WITH_HOLDING_RATE);
        }
        return Optional.empty();
    }

    private Optional<ProcessExceptionDetails> reconcileQuantity(TradeAgreement trade, PositionDto positionDto) {
        if (trade.getQuantity() != null) {
            return checkEquality(trade.getQuantity().doubleValue(), QUANTITY,
                positionDto.getQuantity(), POSITION_QUANTITY);
        }
        return Optional.empty();
    }

    private Optional<ProcessExceptionDetails> reconcileVenue(TradeAgreement trade, PositionDto positionDto) {
        return checkEquality(trade.getVenue().getVenueRefKey(), VENUE_REF_ID,
            positionDto.getCustomValue2(), CUSTOM_VALUE_2);
    }

    private List<ProcessExceptionDetails> reconcileRate(Rate rate, PositionDto positionDto) {
        List<ProcessExceptionDetails> failsLog = new ArrayList<>();

        if (rate != null && positionDto != null) {
            var positionRate = positionDto.getRate();
            if (rate.getRebateRate() != null && positionRate != null) {
                checkEquality(rate.getRebateRate(), REBATE, positionRate, RATE).ifPresent(failsLog::add);
                reconcileFixedRebate(rate.getRebate(), positionDto).ifPresent(failsLog::add);
                reconcileFloatingRebate(rate.getRebate(), positionDto).ifPresent(failsLog::add);
            }
        }

        return failsLog;
    }

    private Optional<ProcessExceptionDetails> reconcileFixedRebate(RebateRate rebate, PositionDto positionDto) {
        if (rebate != null && rebate.getFixed() != null) {
            final LocalDate effectiveDate = rebate.getFixed().getEffectiveDate();
            final LocalDateTime settleDate = positionDto.getSettleDate();
            if (effectiveDate != null && settleDate != null) {
                return checkEquality(effectiveDate, REBATE_FIXED_EFFECTIVE_DATE,
                    settleDate.toLocalDate(), SETTLE_DATE);
            }
        }
        return Optional.empty();
    }

    private Optional<ProcessExceptionDetails> reconcileFloatingRebate(RebateRate rebate, PositionDto positionDto) {
        if (rebate != null && rebate.getFloating() != null && positionDto.getIndexDto() != null) {
            final Double spread = rebate.getFloating().getSpread();
            final Double positionSpread = positionDto.getIndexDto().getSpread();
            if (spread != null && positionSpread != null) {
                return checkEquality(spread, REBATE_FLOATING_SPREAD, positionSpread, POSITION_SPREAD);
            }
        }
        return Optional.empty();
    }

    private Optional<ProcessExceptionDetails> reconcileTermType(TermType termType, Integer termId) {
        if (termType != null && termId != null) {
            if ((termId.equals(1) && termType != TermType.OPEN)
                || (termId.equals(2) && termType != TermType.TERM)) {
                var exceptionDto = new ProcessExceptionDetails();
                exceptionDto.setFieldName(TERM_TYPE);
                exceptionDto.setFieldValue(String.format(RECONCILE_MISMATCH, TERM_TYPE, termType,
                    POSITION_TERM_ID, termId));
                return Optional.of(exceptionDto);
            }
        }
        return Optional.empty();
    }

    private List<ProcessExceptionDetails> reconcileInstrument(Instrument instrument, PositionDto positionDto) {
        var failsList = checkEqualityOfSecurityIdentifiers(instrument, positionDto);
        failsList.addAll(checkInstrumentUnit(instrument.getPrice(), positionDto.getSecurityDetailDto()));
        return failsList;
    }

    private List<ProcessExceptionDetails> checkInstrumentUnit(Price price, SecurityDetailDto securityDetailDto) {
        List<ProcessExceptionDetails> failsLog = new ArrayList<>();
        if (price != null && securityDetailDto != null && securityDetailDto.getPriceFactor() != null) {
            final Integer priceFactor = securityDetailDto.getPriceFactor();
            if ((priceFactor.equals(1) && price.getUnit() != SHARE)
                || (!priceFactor.equals(1) && price.getUnit() != LOT)) {
                var exceptionDto = new ProcessExceptionDetails();
                exceptionDto.setFieldName(PRICE_UNIT);
                exceptionDto.setFieldValue(String.format(RECONCILE_MISMATCH, PRICE_UNIT, price.getUnit(),
                    POSITION_PRICE_FACTOR, priceFactor));
                failsLog.add(exceptionDto);
            }
        }
        return failsLog;
    }

    Optional<ProcessExceptionDetails> checkEquality(@NonNull Object first, String firstName, @NonNull Object second,
        String secondName) {
        if (!Objects.equals(first, second)) {
            var msg = String.format(RECONCILE_MISMATCH, first, firstName, second, secondName);
            log.debug(msg);
            return Optional.of(new ProcessExceptionDetails(null, firstName, String.valueOf(first), FieldExceptionType.DISCREPANCY));
        }
        return Optional.empty();
    }
}
