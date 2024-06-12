package com.intellecteu.onesource.integration.constant;

import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.RECONCILED;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AgreementConstant {

    public static final Set<ProcessingStatus> SKIP_RECONCILIATION_STATUSES = Set.of(RECONCILED, DISCREPANCIES);
    public static final String FIXED_RATE = "Fixed Rate";

    public static class Field {

        public static final String BILLING_CURRENCY = "trade.billingCurrency";
        public static final String COLLATERAL = "trade.collateral";
        public static final String COLLATERAL_MARGIN = "trade.collateral.margin";
        public static final String COLLATERAL_ROUNDING_MODE = "trade.collateral.roundingMode";
        public static final String COLLATERAL_ROUNDING_RULE = "trade.collateral.roundingRule";
        public static final String COLLATERAL_TYPE = "trade.collateral.collateralType";
        public static final String COLLATERAL_VALUE = "trade.collateral.collateralValue";
        public static final String CONTRACT_PRICE = "trade.collateral.contractPrice";
        public static final String CONTRACT_VALUE = "trade.collateral.contractValue";
        public static final String CURRENCY = "trade.collateral.currency";
        public static final String CUSIP = "trade.instrument.cusip";
        public static final String DIVIDENT_RATE_PCT = "trade.dividendRatePct";
        public static final String INSTRUMENT = "trade.instrument";
        public static final String INTERNAL_PARTY_ID = "trade.transactingParties.party.internalPartyId";
        public static final String ISIN = "trade.instrument.isin";
        public static final String FIGI = "trade.instrument.figi";
        public static final String GLEIF_LEI = "trade.transactingParties.party.gleifLei";
        public static final String PARTY_ID = "trade.transactingParties.party.partyId";
        public static final String PRICE_UNIT = "trade.instrument.price.unit";
        public static final String QUANTITY = "trade.quantity";
        public static final String QUICK = "trade.instrument.quick";
        public static final String RATE = "trade.rate";
        public static final String REBATE = "trade.rate.rebate";
        public static final String REBATE_FIXED = "trade.rate.rebate.fixed";
        public static final String REBATE_FIXED_BASE_RATE = "trade.rate.rebate.fixed.baseRate";
        public static final String REBATE_FIXED_EFFECTIVE_DATE = "trade.rate.rebate.fixed.effectiveDate";
        public static final String REBATE_FLOATING_BENCHMARK = "trade.rate.rebate.floating.benchmark";
        public static final String REBATE_FLOATING_EFFECTIVE_DATE = "trade.rate.rebate.floating.effectiveDate";
        public static final String REBATE_FLOATING_EFFECTIVE_RATE = "trade.rate.rebate.floating.effectiveRate";
        public static final String REBATE_FLOATING_SPREAD = "trade.rate.rebate.floating.spread";
        public static final String SEDOL = "trade.instrument.sedol";
        public static final String SETTLEMENT_DATE = "trade.settlementDate";
        public static final String SETTLEMENT_TYPE = "trade.settlementType";
        public static final String TERM_DATE = "trade.termDate";
        public static final String TERM_TYPE = "trade.termType";
        public static final String TICKER = "trade.instrument.ticker";
        public static final String TRADE = "trade";
        public static final String TRADE_DATE = "trade.tradeDate";
        public static final String TRANSACTING_PARTIES = "trade.transactingParties";
        public static final String VENUE_REF_KEY = "trade.executionVenue.venueRefKey";
    }

}
