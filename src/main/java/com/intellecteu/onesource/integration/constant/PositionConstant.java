package com.intellecteu.onesource.integration.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PositionConstant {

    public final static String LENDER_POSITION_TYPE = "CASH LOAN";
    public final static String BORROWER_POSITION_TYPE = "CASH BORROW";

    public static class Field {

        public static final String COMMA_DELIMITER = ", ";
        public static final String ACCOUNT_LEI = "PositionOutDTO.accountDTO.accountLei";
        public static final String ACCRUAL_DATE = "PositionOutDTO.accrualDate";
        public static final String POSITION_AMOUNT = "PositionOutDTO.amount";
        public static final String POSITION_COLLATERAL_TYPE = "PositionOutDTO.collateralTypeDTO.collateralType";
        public static final String CP_LEI = "PositionOutDTO.counterPartyDTO.lei";
        public static final String POSITION_ACCOUNT = "PositionOutDTO.accountDTO";
        public static final String POSITION_ACCOUNT_LEI = "PositionOutDTO.accountDTO.lei";
        public static final String POSITION_CP_ACCOUNT = "PositionOutDTO.counterPartyDTO";
        public static final String POSITION_CP_ACCOUNT_LEI = "PositionOutDTO.counterPartyDTO.lei";
        public static final String POSITION_CURRENCY = "PositionOutDTO.currencyDTO";
        public static final String POSITION_CURRENCY_KY = "PositionOutDTO.currencyDTO.currencyKy";
        public static final String POSITION_TYPE = "PositionOutDTO.positionTypeDTO";
        public static final String POSITION_TYPE_IS_CASH = "PositionOutDTO.positionTypeDTO.isCash";
        public static final String CUSTOM_VALUE_2 = "PositionOutDTO.customValue2";
        public static final String DELIVER_FREE = "PositionOutDTO.deliverFree";
        public static final String CP_HAIRCUT = "PositionOutDTO.exposureDTO.cpHaircut";
        public static final String CP_MARKROUND_TO = "PositionOutDTO.exposureDTO.cpMarkRoundTo";
        public static final String POSITION_TERM_ID = "PositionOutDTO.termId";
        public static final String TAX_WITH_HOLDING_RATE = "PositionOutDTO.loanBorrowDTO.taxWithholdingRate";
        public static final String POSITION_PRICE = "PositionOutDTO.price";
        public static final String POSITION_PRICE_FACTOR = "PositionOutDTO.securityDetailDTO.priceFactor";
        public static final String RATE = "PositionOutDTO.rate";
        public static final String POSITION_SECURITY = "PositionOutDTO.securityDetailDTO";
        public static final String BLOOMBERG_ID = "PositionOutDTO.securityDetailDTO.bloombergId";
        public static final String POSITION_CUSIP = "PositionOutDTO.securityDetailDTO.cusip";
        public static final String POSITION_END_DATE = "PositionOutDTO.endDate";
        public static final String POSITION_INDEX = "PositionOutDTO.indexDTO";
        public static final String POSITION_INDEX_NAME = "PositionOutDTO.indexDTO.IndexName";
        public static final String POSITION_ISIN = "PositionOutDTO.securityDetailDTO.isin";
        public static final String POSITION_SEDOL = "PositionOutDTO.securityDetailDTO.sedol";
        public static final String POSITION_TICKER = "PositionOutDTO.securityDetailDTO.ticker";
        public static final String POSITION_QUICK = "PositionOutDTO.securityDetailDTO.quickCode";
        public static final String POSITION_SPREAD = "PositionOutDTO.indexDTO.spread";
        public static final String SETTLE_DATE = "PositionOutDTO.settleDate";
        public static final String POSITION_TRADE_DATE = "PositionOutDTO.tradeDate";
        public static final String POSITION_QUANTITY = "PositionOutDTO.quantity";
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
        public static final String STATUS = "Status";
        public static final String TRADE_ID = "tradeId";
        public static final String TRADE_STATUS = "status";
        public static final String TRADE_TYPE = "tradeType";
    }

    public static class Status {

        public final static String PENDING_ONESOURCE_CONFIRMATION = "PENDING ONESOURCE CONFIRMATION";
    }

}
