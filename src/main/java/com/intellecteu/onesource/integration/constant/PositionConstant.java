package com.intellecteu.onesource.integration.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PositionConstant {

    public final static String LENDER_POSITION_TYPE = "CASH LOAN";
    public final static String BORROWER_POSITION_TYPE = "CASH BORROW";
    public final static String SPIRE_RECALL_SPLIT = "-";

    public static class Field {

        public static final String COMMA_DELIMITER = ", ";
        public static final String ACCOUNT_LEI = "positionOutDTO.accountDTO.accountLei";
        public static final String ACCRUAL_DATE = "positionOutDTO.accrualDate";
        public static final String POSITION_AMOUNT = "positionOutDTO.amount";
        public static final String POSITION_COLLATERAL_TYPE = "positionOutDTO.collateralTypeDTO.collateralType";
        public static final String CP_LEI = "positionOutDTO.counterPartyDTO.lei";
        public static final String POSITION_ACCOUNT = "positionOutDTO.accountDTO";
        public static final String POSITION_ACCOUNT_LEI = "positionOutDTO.accountDTO.lei";
        public static final String POSITION_CP_ACCOUNT = "positionOutDTO.counterPartyDTO";
        public static final String POSITION_CP_ACCOUNT_LEI = "positionOutDTO.counterPartyDTO.lei";
        public static final String POSITION_CURRENCY = "positionOutDTO.currencyDTO";
        public static final String POSITION_CURRENCY_KY = "positionOutDTO.currencyDTO.currencyKy";
        public static final String POSITION_TYPE = "positionOutDTO.positionTypeDTO";
        public static final String POSITION_TYPE_IS_CASH = "positionOutDTO.positionTypeDTO.isCash";
        public static final String CUSTOM_VALUE_2 = "positionOutDTO.customValue2";
        public static final String DELIVER_FREE = "positionOutDTO.deliverFree";
        public static final String CP_HAIRCUT = "positionOutDTO.exposureDTO.cpHaircut";
        public static final String CP_MARKROUND_TO = "positionOutDTO.exposureDTO.cpMarkRoundTo";
        public static final String POSITION_TERM_ID = "positionOutDTO.termId";
        public static final String TAX_WITH_HOLDING_RATE = "positionOutDTO.loanBorrowDTO.taxWithholdingRate";
        public static final String POSITION_PRICE = "positionOutDTO.price";
        public static final String POSITION_PRICE_FACTOR = "positionOutDTO.securityDetailDTO.priceFactor";
        public static final String RATE = "positionOutDTO.rate";
        public static final String POSITION_SECURITY = "positionOutDTO.securityDetailDTO";
        public static final String BLOOMBERG_ID = "positionOutDTO.securityDetailDTO.bloombergId";
        public static final String POSITION_CUSIP = "positionOutDTO.securityDetailDTO.cusip";
        public static final String POSITION_END_DATE = "positionOutDTO.endDate";
        public static final String POSITION_INDEX = "positionOutDTO.indexDTO";
        public static final String POSITION_INDEX_NAME = "positionOutDTO.indexDTO.IndexName";
        public static final String POSITION_ISIN = "positionOutDTO.securityDetailDTO.isin";
        public static final String POSITION_SEDOL = "positionOutDTO.securityDetailDTO.sedol";
        public static final String POSITION_TICKER = "positionOutDTO.securityDetailDTO.ticker";
        public static final String POSITION_QUICK = "positionOutDTO.securityDetailDTO.quickCode";
        public static final String POSITION_SPREAD = "positionOutDTO.indexDTO.spread";
        public static final String SETTLE_DATE = "positionOutDTO.settleDate";
        public static final String POSITION_TRADE_DATE = "positionOutDTO.tradeDate";
        public static final String POSITION_QUANTITY = "positionOutDTO.quantity";
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
