package com.intellecteu.onesource.integration.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PositionConstant {

    public final static String LENDER_POSITION_TYPE = "CASH LOAN";
    public final static String BORROWER_POSITION_TYPE = "CASH BORROW";

    public static class Field {

        public static final String COMMA_DELIMITER = ", ";
        public static final String ACCOUNT_LEI = "Position.accountDTO.accountLei";
        public static final String POSITION_AMOUNT = "Position.amount";
        public static final String POSITION_COLLATERAL_TYPE = "Position.collateralTypeDTO.collateralType";
        public static final String CP_LEI = "Position.counterPartyDTO.lei";
        public static final String POSITION_CURRENCY = "Position.currencyDTO.currencyKy";
        public static final String CUSTOM_VALUE_2 = "Position.customValue2";
        public static final String DELIVER_FREE = "Position.deliverFree";
        public static final String CP_HAIRCUT = "Position.exposureDTO.cpHaircut";
        public static final String CP_MARKROUND_TO = "Position.exposureDTO.cpMarkRoundTo";
        public static final String POSITION_TERM_ID = "Position.termId";
        public static final String TAX_WITH_HOLDING_RATE = "Position.loanBorrowDTO.taxWithholdingRate";
        public static final String POSITION_PRICE = "Position.price";
        public static final String POSITION_PRICE_FACTOR = "Position.securityDetailDTO.priceFactor";
        public static final String RATE = "Position.rate";
        public static final String POSITION_SECURITY = "Position.securityDetailDTO";
        public static final String BLOOMBERG_ID = "Position.securityDetailDTO.bloombergId";
        public static final String POSITION_CUSIP = "Position.securityDetailDTO.cusip";
        public static final String POSITION_ISIN = "Position.securityDetailDTO.isin";
        public static final String POSITION_SEDOL = "Position.securityDetailDTO.sedol";
        public static final String POSITION_TICKER = "Position.securityDetailDTO.ticker";
        public static final String POSITION_QUICK = "Position.securityDetailDTO.quickCode";
        public static final String POSITION_SPREAD = "Position.indexDTO.spread";
        public static final String SETTLE_DATE = "Position.settleDate";
        public static final String POSITION_TRADE_DATE = "Position.tradeDate";
        public static final String POSITION_QUANTITY = "Position.quantity";
    }

    public static class Request {

        public static final String CANCEL_LOAN = "Cancel Loan";
        public static final String CANCEL_NEW_BORROW = "Cancel New Borrow";
        public static final String NEW_BORROW = "New Borrow";
        public static final String NEW_LOAN = "New Loan";
        public static final String POSITION_ID = "positionId";
        public static final String PENDING_ONESOURCE_CONFIRMATION = "PENDING ONESOURCE CONFIRMATION";
        public static final String RERATE = "Rerate";
        public static final String RERATE_BORROW = "Rerate Borrow";
        public static final String ROLL_BORROW = "Roll Borrow";
        public static final String ROLL_LOAN = "Roll Loan";
        public static final String TRADE_ID = "tradeId";
        public static final String TRADE_STATUS = "status";
        public static final String TRADE_TYPE = "tradeType";
    }

    public static class Status {

        public final static String PENDING_ONESOURCE_CONFIRMATION = "PENDING ONESOURCE CONFIRMATION";
    }

}
