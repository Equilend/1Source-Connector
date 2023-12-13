package com.intellecteu.onesource.integration.constant;

import static com.intellecteu.onesource.integration.model.ProcessingStatus.RECONCILED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TO_CANCEL;

import com.intellecteu.onesource.integration.model.ProcessingStatus;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AgreementConstant {

    public static final Set<ProcessingStatus> SKIP_RECONCILIATION_STATUSES = Set.of(RECONCILED, TO_CANCEL);

    public static class Field {

        public static final String COLLATERAL = "Agreement.trade.collateral";
        public static final String COLLATERAL_MARGIN = "Agreement.trade.collateral.margin";
        public static final String COLLATERAL_ROUNDING_MODE = "Agreement.trade.collateral.roundingMode";
        public static final String COLLATERAL_ROUNDING_RULE = "Agreement.trade.collateral.roundingRule";
        public static final String COLLATERAL_TYPE = "Agreement.trade.collateral.collateralType";
        public static final String COLLATERAL_VALUE = "Agreement.trade.collateral.collateralValue";
        public static final String CONTRACT_PRICE = "Agreement.trade.collateral.contractPrice";
        public static final String CONTRACT_VALUE = "Agreement.trade.collateral.contractValue";
        public static final String CURRENCY = "Agreement.trade.collateral.currency";
        public static final String CUSIP = "Agreement.trade.instrument.cusip";
        public static final String DIVIDENT_RATE_PCT = "Agreement.trade.dividendRatePct";
        public static final String INSTRUMENT = "Agreement.trade.instrument";
        public static final String INTERNAL_PARTY_ID = "Agreement.trade.transactingParties.party.internalPartyId";
        public static final String ISIN = "Agreement.trade.instrument.isin";
        public static final String FIGI = "Agreement.trade.instrument.figi";
        public static final String GLEIF_LEI = "Agreement.trade.transactingParties.party.gleifLei";
        public static final String PRICE_UNIT = "Agreement.trade.instrument.price.unit";
        public static final String QUANTITY = "Agreement.trade.quantity";
        public static final String QUICK = "Agreement.trade.instrument.quick";
        public static final String REBATE = "Agreement.trade.rate.rebate";
        public static final String SEDOL = "Agreement.trade.instrument.sedol";
        public static final String SETTLEMENT_DATE = "Agreement.trade.settlementDate";
        public static final String SETTLEMENT_TYPE = "Agreement.trade.settlementType";
        public static final String TERM_TYPE = "Agreement.trade.termType";
        public static final String TICKER = "Agreement.trade.instrument.ticker";
        public static final String TRADE = "Agreement.trade";
        public static final String TRADE_DATE = "Agreement.tradeDate";
        public static final String VENUE_REF_ID = "Agreement.trade.executionVenue.venueRefKey";
    }

}
