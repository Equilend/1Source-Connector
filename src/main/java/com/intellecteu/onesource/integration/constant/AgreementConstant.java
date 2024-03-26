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

        public static final String BILLING_CURRENCY = "Trade.billingCurrency";
        public static final String COLLATERAL = "Trade.collateral";
        public static final String COLLATERAL_MARGIN = "Trade.collateral.margin";
        public static final String COLLATERAL_ROUNDING_MODE = "Trade.collateral.roundingMode";
        public static final String COLLATERAL_ROUNDING_RULE = "Trade.collateral.roundingRule";
        public static final String COLLATERAL_TYPE = "Trade.collateral.collateralType";
        public static final String COLLATERAL_VALUE = "Trade.collateral.collateralValue";
        public static final String CONTRACT_PRICE = "Trade.collateral.contractPrice";
        public static final String CONTRACT_VALUE = "Trade.collateral.contractValue";
        public static final String CURRENCY = "Trade.collateral.currency";
        public static final String CUSIP = "Trade.instrument.cusip";
        public static final String DIVIDENT_RATE_PCT = "Trade.dividendRatePct";
        public static final String INSTRUMENT = "Trade.instrument";
        public static final String INTERNAL_PARTY_ID = "Trade.transactingParties.party.internalPartyId";
        public static final String ISIN = "Trade.instrument.isin";
        public static final String FIGI = "Trade.instrument.figi";
        public static final String GLEIF_LEI = "Trade.transactingParties.party.gleifLei";
        public static final String PRICE_UNIT = "Trade.instrument.price.unit";
        public static final String QUANTITY = "Trade.quantity";
        public static final String QUICK = "Trade.instrument.quick";
        public static final String REBATE = "Trade.rate.rebate";
        public static final String REBATE_FIXED = "Trade.rate.rebate.fixed";
        public static final String REBATE_FIXED_BASE_RATE = "Trade.rate.rebate.fixed.baseRate";
        public static final String REBATE_FIXED_EFFECTIVE_DATE = "Trade.rate.rebate.fixed.effectiveDate";
        public static final String REBATE_FLOATING_BENCHMARK = "Trade.rate.rebate.floating.benchmark";
        public static final String REBATE_FLOATING_EFFECTIVE_DATE = "Trade.rate.rebate.floating.effectiveDate";
        public static final String REBATE_FLOATING_EFFECTIVE_RATE = "Trade.rate.rebate.floating.effectiveRate";
        public static final String REBATE_FLOATING_SPREAD = "Trade.rate.rebate.floating.spread";
        public static final String SEDOL = "Trade.instrument.sedol";
        public static final String SETTLEMENT_DATE = "Trade.settlementDate";
        public static final String SETTLEMENT_TYPE = "Trade.settlementType";
        public static final String TERM_DATE = "Trade.termDate";
        public static final String TERM_TYPE = "Trade.termType";
        public static final String TICKER = "Trade.instrument.ticker";
        public static final String TRADE = "Trade";
        public static final String TRADE_DATE = "TradeDate";
        public static final String VENUE_REF_KEY = "Trade.executionVenue.venueRefKey";
    }

}
