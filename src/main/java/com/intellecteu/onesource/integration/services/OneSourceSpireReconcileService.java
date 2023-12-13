package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_MARGIN;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_TYPE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.COLLATERAL_VALUE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CONTRACT_VALUE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CURRENCY;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CUSIP;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.DIVIDENT_RATE_PCT;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.GLEIF_LEI;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.ISIN;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.QUANTITY;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.QUICK;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.REBATE_BPS;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SEDOL;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SETTLEMENT_DATE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SETTLEMENT_TYPE;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.TICKER;
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
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_QUANTITY;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_QUICK;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_SEDOL;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_TICKER;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_TRADE_DATE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.RATE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.SETTLE_DATE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.TAX_WITH_HOLDING_RATE;
import static com.intellecteu.onesource.integration.exception.ReconcileException.RECONCILE_MISMATCH;
import static com.intellecteu.onesource.integration.model.SettlementType.DVP;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.dto.CollateralDto;
import com.intellecteu.onesource.integration.dto.ExceptionMessageDto;
import com.intellecteu.onesource.integration.dto.InstrumentDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.TransactingPartyDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.CollateralType;
import com.intellecteu.onesource.integration.model.SettlementType;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public abstract class OneSourceSpireReconcileService<T extends Reconcilable, R extends Reconcilable>
    implements ReconcileService<T, R> {

    /**
     * Reconcile the trade agreement retrieved from OneSource against the position retrieved from Spire. When the
     * reconciliation is successful, the loan contract can be initiated from OneSource.
     *
     * @param first Instance of Reconcilable
     * @param second Instance of Reconcilable
     * @throws ReconcileException if at least one reconciliation rule fails
     */
    public abstract void reconcile(T first, R second) throws ReconcileException;

    /*
     * Validates required fields presence and required data mismatches.
     */
    void validateReconcilableObjects(T first, R second, List<ExceptionMessageDto> fails) {
        var firstExceptionValidation = validateFunction().apply(first);
        var secondExceptionValidation = validateFunction().apply(second);
        CollectionUtils.addIgnoreNull(fails, firstExceptionValidation);
        CollectionUtils.addIgnoreNull(fails, secondExceptionValidation);
    }

    /*
     * Function to wrap try-catch and validate objects that implement Reconcilable interface.
     */
    Function<Reconcilable, ExceptionMessageDto> validateFunction() {
        return reconcilable -> {
            try {
                reconcilable.validateForReconciliation();
            } catch (Exception e) {
                if (e instanceof ValidationException exception) {
                    return exception.getDto();
                } else {
                    log.error("Reconciliation unexpected error: " + e.getMessage());
                }
            }
            return null;
        };
    }

    private void checkLei(List<TransactingPartyDto> transactionParties, PositionDto position,
        List<ExceptionMessageDto> failsLog) {
        var positionAccountLei = position.getAccountLei();
        var positionCpLei = position.getCpLei();

        for (var txParty : transactionParties) {
            var partyGleifLei = txParty.getParty().getGleifLei();
            if (partyGleifLei != null) {
                if (partyGleifLei.equals(positionAccountLei) || partyGleifLei.equals(positionCpLei)) {
                    return;
                }
            }
        }
        var exceptionDto = new ExceptionMessageDto();
        exceptionDto.setValue(GLEIF_LEI);
        exceptionDto.setExceptionMessage("Reconciliation mismatch. OneSourceAccount Lei or CounterParty Lei "
            + "is not matched with Spire Position Lei");
        failsLog.add(exceptionDto);
    }

    private void checkCollateralEquality(CollateralDto collateral, PositionDto position,
        List<ExceptionMessageDto> failsLog) {
//    Temporary remove price reconciliation checkin until requirements will be refined
//    if (collateral.getContractPrice() != null) {
//      checkEquality(collateral.getContractPrice(), CONTRACT_PRICE,
//          position.getPrice(), POSITION_PRICE, failsLog);
//    }
        // temporary parseInt solution until position data structure will be clarified
        // please take into account that mock data for Position.contractValue should be parseable to Integer
        if (collateral.getContractValue() != null && position.getContractValue() != null) {
            checkEquality(collateral.getContractValue(), CONTRACT_VALUE,
                position.getContractValue(), POSITION_CONTRACT_VALUE, failsLog);
        }
        if (collateral.getCollateralValue() != null) {
            checkEquality(collateral.getCollateralValue(), COLLATERAL_VALUE,
                position.getAmount(), POSITION_AMOUNT, failsLog);
        }
        if (collateral.getCurrency() != null && position.getCurrency() != null
            && position.getCurrency().getCurrencyKy() != null) {
            checkEquality(collateral.getCurrency(), CURRENCY, position.getCurrency().getCurrencyKy(),
                POSITION_CURRENCY, failsLog);
        }
        if (collateral.getType() != null) {
            if (position.getCollateralType() != null && !position.getCollateralType().isEmpty()) {
                checkEquality(collateral.getType().name(), COLLATERAL_TYPE,
                    position.getCollateralType(), POSITION_COLLATERAL_TYPE, failsLog);
            } else {
                if (collateral.getType() != CollateralType.CASH) {
                    var msg = format("Reconciliation mismatch. OneSource %s must be %s when %s is empty",
                        COLLATERAL_TYPE, CollateralType.CASH, POSITION_COLLATERAL_TYPE);
                    failsLog.add(new ExceptionMessageDto(COLLATERAL_TYPE, msg));
                }
            }
        }
        if (collateral.getMargin() != null && position.getCpHaircut() != null) {
            // currently we have different value types for margin 1source and cpHaircut SPIRE 102 vs 1.02
            // the question is can we have values like: 102.0457 and 1.020457
            // temporary change data type to String and compare
            var oneSourceMargin = collateral.getMargin(); // expected value: 102.0
            var spireCpHaircut = position.getCpHaircut(); // expected value: 1.02
            checkEquality(String.valueOf(oneSourceMargin), COLLATERAL_MARGIN,
                String.valueOf(spireCpHaircut * 100.0), CP_HAIRCUT, failsLog);
        }
    }

    /*
     * If the SettlementType is DVP then the deliverFree must be True
     */
    private void checkDeliverFree(SettlementType settlementType, PositionDto position,
        List<ExceptionMessageDto> failsLog) {
        if (settlementType != null && position.getDeliverFree() != null) {
            var exceptionDto = new ExceptionMessageDto();
            if (settlementType == DVP && position.getDeliverFree()) {
                exceptionDto.setValue(SETTLEMENT_TYPE);
                exceptionDto.setExceptionMessage(String.format(RECONCILE_MISMATCH, SETTLEMENT_TYPE, settlementType,
                    DELIVER_FREE, position.getDeliverFree()));
                failsLog.add(exceptionDto);
            }
            if (settlementType != DVP && !position.getDeliverFree()) {
                exceptionDto.setValue(SETTLEMENT_TYPE);
                exceptionDto.setExceptionMessage(String.format(RECONCILE_MISMATCH, SETTLEMENT_TYPE, settlementType,
                    DELIVER_FREE, position.getDeliverFree()));
                failsLog.add(exceptionDto);
            }
        }
    }

    /*
     * Must reconcile if present. At least one security identifier must be present
     * ('At least one' presence is checked inside PositionDto domain model logic)
     */
    private void checkEqualityOfSecurityIdentifiers(InstrumentDto instrument, PositionDto position,
        List<ExceptionMessageDto> failsLog) {
        if (position.getSecurityDetailDto() != null) {
            var securityDetailDto = position.getSecurityDetailDto();
            if (securityDetailDto.getTicker() != null) {
                checkEquality(instrument.getTicker(), TICKER, securityDetailDto.getTicker(), POSITION_TICKER, failsLog);
            }
            if (securityDetailDto.getCusip() != null) {
                checkEquality(instrument.getCusip(), CUSIP, securityDetailDto.getCusip(), POSITION_CUSIP, failsLog);
            }
            if (securityDetailDto.getIsin() != null) {
                checkEquality(instrument.getIsin(), ISIN, securityDetailDto.getIsin(), POSITION_ISIN, failsLog);
            }
            if (securityDetailDto.getSedol() != null) {
                checkEquality(instrument.getSedol(), SEDOL, securityDetailDto.getSedol(), POSITION_SEDOL, failsLog);
            }
            if (securityDetailDto.getQuickCode() != null) {
                checkEquality(instrument.getQuick(), QUICK, securityDetailDto.getQuickCode(), POSITION_QUICK, failsLog);
            }
        }
    }

    void reconcileTrade(TradeAgreementDto tradeDto, PositionDto positionDto,
        List<ExceptionMessageDto> reconciliationFailMessages) {
        checkEquality(tradeDto.getExecutionVenue().getVenueRefKey(), VENUE_REF_ID,
            positionDto.getCustomValue2(), CUSTOM_VALUE_2, reconciliationFailMessages);
        checkEquality(tradeDto.getRate().retrieveRateBps(), REBATE_BPS,
            positionDto.getRate(), RATE, reconciliationFailMessages);
        checkEqualityOfSecurityIdentifiers(tradeDto.getInstrument(), positionDto, reconciliationFailMessages);
        if (tradeDto.getQuantity() != null) {
            checkEquality(tradeDto.getQuantity().doubleValue(), QUANTITY,
                positionDto.getQuantity(), POSITION_QUANTITY, reconciliationFailMessages);
        }
        if (positionDto.getLoanBorrowDto() != null && positionDto.getLoanBorrowDto().getTaxWithholdingRate() != null
            && tradeDto.getDividendRatePct() != null) {
            checkEquality(tradeDto.getDividendRatePct().doubleValue(), DIVIDENT_RATE_PCT,
                positionDto.getLoanBorrowDto().getTaxWithholdingRate(), TAX_WITH_HOLDING_RATE,
                reconciliationFailMessages);
        }
        if (tradeDto.getTradeDate() != null && positionDto.getTradeDate() != null) {
            checkEquality(tradeDto.getTradeDate(), TRADE_DATE,
                positionDto.getTradeDate().toLocalDate(), POSITION_TRADE_DATE, reconciliationFailMessages);
        }
        if (tradeDto.getSettlementDate() != null && positionDto.getSettleDate() != null) {
            checkEquality(tradeDto.getSettlementDate(), SETTLEMENT_DATE,
                positionDto.getSettleDate().toLocalDate(), SETTLE_DATE, reconciliationFailMessages);
        }
        checkDeliverFree(tradeDto.getSettlementType(), positionDto, reconciliationFailMessages);
        checkCollateralEquality(tradeDto.getCollateral(), positionDto, reconciliationFailMessages);
        checkLei(tradeDto.getTransactingParties(), positionDto, reconciliationFailMessages);
    }

    void checkEquality(Object first, String firstName, Object second, String secondName,
        List<ExceptionMessageDto> exceptionList) {
        if (!Objects.equals(first, second)) {
            var msg = "Reconciliation mismatch. OneSource " + first + ":" + firstName
                + " is not matched with Spire " + second + ":" + secondName;
            exceptionList.add(new ExceptionMessageDto(firstName, msg));
        }
    }
}
