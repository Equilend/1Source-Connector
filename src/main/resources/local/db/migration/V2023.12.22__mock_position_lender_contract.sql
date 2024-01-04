-- Template for required fields for adding position for local development
--
-- INSERT INTO position (spire_position_id, custom_value2, ticker, cusip, isin,
--                              sedol, quick_code, price_factor, base_rebate_rate, rate,
--                              quantity, currency, tax_with_holding_rate, trade_date, term_id,
--                              settle_date, deliver_free, amount, price, contract_value,
--                              collateral_type, cp_haircut, cp_mark_round_to, depo_id, position_type,
--                              account_lei, cp_lei)
-- VALUES
--     ('7777', '7777', '123', '023135107', 'US0231351068',
--      '2000010', '456', 0, 0, 0.05,
--      9999.0, 'USD', 85.0, '2023-10-25 13:50:41.633000', 0,
--      '2023-10-25 13:50:41.633000', false, 17935.5, 119.57, 17935.5,
--      'CASH', 1.02, 0, 0, 'CASH LOAN',
--      '7777', '8888');



-- INSERT MOCK DATA FOR LENDER

INSERT INTO internal_ref (id, broker, account_id, internal_id)
VALUES (7777, 'testLenderBroker', '7777', '7777');

INSERT INTO party (id, party_id, party_name, gleif_lei, internal_id)
VALUES (7777, 7777, 'LENDER', '7777', '7777');

INSERT INTO instrument (id, ticker, cusip, isin, sedol, quick)
VALUES (7777, '123', '023135107', 'US0231351068', '2000010', '456');

INSERT INTO fixed (id, base_rate, effective_rate, effective_date, cutoff_time)
values (7777, 0.05, 0, '2023-10-25 13:50:41.633000', '0');

INSERT INTO rebate (id, fixed)
VALUES (7777, 7777);

INSERT INTO rate (id, rebate)
VALUES (7777, 7777);

INSERT INTO collateral (id, contract_price, contract_value, collateral_value, currency,
                        type, margin, rounding_rule, rounding_mode)
VALUES (7777, 119.57, 17935.5, 17935.5, 'USD',
        'CASH', 102.0, 0, 'ALWAYSUP');

INSERT INTO venue (id, party_id, type, venue_name, venue_ref_key)
VALUES (7777, '7777', 'ONPLATFORM', 'testVenueName', '7777');


INSERT INTO trade (id, venue_id, instrument_id, rate_id, quantity, currency,
                   dividend_rate, trade_date, term_type, term_date, settlement_date,
                   settlement_type, collateral)
VALUES (7777, 7777, 7777, 7777, 9999.0, 'USD',
        85.0, '2023-10-25 13:50:41.633000', 'OPEN', '2023-10-25 13:50:41.633000', '2023-10-25 13:50:41.633000',
        'DVP', 7777);

INSERT INTO agreement (id, agreement_id, status, trade_id, matching_spire_position_id, processing_status)
VALUES (7777, '32b71278-9ad2-445a-bfb0-b5ada72f7777', 'PENDING', 7777, '7777', 'CREATED');

INSERT INTO transacting_party (id, party_role, party_id, internal_ref_id, transacting_party_id)
VALUES (7777, 'LENDER', 7777, 7777, 7777);



-- INSERT MOCK DATA FOR BORROWER

INSERT INTO internal_ref (id, broker, account_id, internal_id)
VALUES (8888, 'testBorrowerBroker', '8888', '8888');

INSERT INTO party (id, party_id, party_name, gleif_lei, internal_id)
VALUES (8888, 8888, 'BORROWER', '8888', '8888');

INSERT INTO instrument (id, ticker, cusip, isin, sedol, quick)
VALUES (8888, '234', '023135106', 'US0231351067', '2000019', '457');

INSERT INTO fixed (id, base_rate, effective_rate, effective_date, cutoff_time)
values (8888, 0.05, 0, '2023-10-25 13:50:41.633000', '0');

INSERT INTO rebate (id, fixed)
VALUES (8888, 8888);

INSERT INTO rate (id, rebate)
VALUES (8888, 8888);

INSERT INTO collateral (id, contract_price, contract_value, collateral_value, currency,
                        type, margin, rounding_rule, rounding_mode)
VALUES (8888, 119.57, 17935.5, 17935.5, 'USD',
        'CASH', 102.0, 0, 'ALWAYSUP');

INSERT INTO venue (id, party_id, type, venue_name, venue_ref_key)
VALUES (8888, '8888', 'ONPLATFORM', 'testBorrowerVenueName', '8888');


INSERT INTO trade (id, venue_id, instrument_id, rate_id, quantity, currency,
                   dividend_rate, trade_date, term_type, term_date, settlement_date,
                   settlement_type, collateral)
VALUES (8888, 8888, 8888, 8888, 15000, 'USD',
        85.0, '2023-10-25 13:50:41.633000', 'OPEN', '2023-10-25 13:50:41.633000', '2023-10-25 13:50:41.633000',
        'DVP', 8888);

INSERT INTO agreement (id, agreement_id, status, trade_id, matching_spire_position_id, processing_status)
VALUES (8888, '32b71278-9ad2-445a-bfb0-b5ada72f8888', 'PENDING', 8888, '8888', 'CREATED');

INSERT INTO transacting_party (id, party_role, party_id, internal_ref_id, transacting_party_id)
VALUES (8888, 'BORROWER', 8888, 8888, 8888);
