-- Test MATCH_LOAN_CONTRACT_PROPOSAL Lender success flow data

-- INSERT INTO internal_ref (id, broker, account_id, internal_id)
-- VALUES (101, 'testLenderBroker', '101', '101'),
--        (202, 'testBorrowerBroker', '202', '202');
--
-- INSERT INTO party (id, party_id, party_name, gleif_lei, internal_id)
-- VALUES (101, 101, 'LENDER', '101', '101'),
--        (202, 202, 'BORROWER', '202', '2027');
--
-- INSERT INTO instrument (id, ticker, cusip, isin, sedol, quick_code)
-- VALUES  (101, '234', '0231101106', 'US02311011067', '2000019', '457'),
--         (202, '234', '0231101106', 'US02311011067', '2000019', '457');
--
-- INSERT INTO fixed (id, base_rate, effective_rate, effective_date, cutoff_time)
-- VALUES (101, 0.05, 0, '2023-10-25 13:50:41.633000', '0'),
--        (202, 0.05, 0, '2023-10-25 13:50:41.633000', '0');
--
--
-- INSERT INTO rebate (id, fixed)
-- VALUES (101, 101), (202, 202);
--
-- INSERT INTO rate (id, rebate)
-- VALUES  (101, 101), (202, 202);
--
-- INSERT INTO collateral (id, contract_price, contract_value, collateral_value, currency,
--                         type, margin, rounding_rule, rounding_mode)
-- VALUES (101, 119.57, 179101.5, 179101.5, 'USD', 'CASH', 102.0, 0, 'ALWAYSUP'),
--        (202, 119.57, 179101.5, 179101.5, 'USD', 'CASH', 102.0, 0, 'ALWAYSUP');
--
--
-- INSERT INTO trade (id, instrument_id, rate_id, quantity, currency,
--                    dividend_rate, trade_date, term_type, term_date, settlement_date,
--                    settlement_type, collateral)
-- VALUES  (101, 101, 101, 15000, 'USD',
--          85.0, '2024-02-21 13:50:41.633000', 'OPEN', '2024-02-21 13:50:41.633000', '2023-10-25 13:50:41.633000',
--          'DVP', 101),
--         (202, 202, 202, 15000, 'USD',
--          85.0, '2024-02-21 13:50:41.633000', 'OPEN', '2024-02-21 13:50:41.633000', '2023-10-25 13:50:41.633000',
--          'DVP', 202);
--
-- INSERT INTO venue (id, party_id, type, venue_name, venue_ref_key, trade_id)
-- VALUES (101, '101', 'ONPLATFORM', 'testLenderVenueName', '101', 101),
--        (202, '202', 'ONPLATFORM', 'testBorrowerVenueName', '202', 202);
--
-- INSERT INTO agreement (id, agreement_id, trade_id, matching_spire_position_id, processing_status)
-- VALUES (101, '32b71278-9ad2-445a-bfb0-b5ada72f101', 101, '101', 'CREATED'),
--        (202, '32b71278-9ad2-445a-bfb0-b5ada72f202', 202, '202', 'CREATED');
--
-- INSERT INTO transacting_party (id, party_role, party_id, internal_ref_id, transacting_party_id)
-- VALUES (101, 'LENDER', 101, 101, 101);
--
-- INSERT INTO contract (id, contract_id, contract_status, trade_id, processing_status,
--                       matching_spire_position_id, matching_spire_trade_id)
-- VALUES (101, '101', 'PROPOSED', 101, 'PROPOSED', 101, 101),
--        (202, '202', 'PROPOSED', 202, 'PROPOSED', 202, 202);
--
-- INSERT INTO account (id, account_id, short_name, lei, one_source_id, dtc)
-- VALUES (101, 101, 'lender_acc', '101', 101, 1),
--        (202, 202, 'borrower_acc', '202', 202, 2);
--
-- INSERT INTO position (position_id, custom_value2, ticker, cusip, isin,
--                       sedol, quick_code, price_factor, rate, end_date,
--                       quantity, currency, tax_with_holding_rate, trade_date, term_id,
--                       settle_date, deliver_free, amount, price,
--                       cp_haircut, cp_mark_round_to, depo_id, position_type, is_cash,
--                       account_id, cp_id, account_lei, cp_lei, processing_status, status,
--                       index_id, index_name, spread, accrual_date,
--                       matching_1source_loan_contract_id, trade_id)
-- VALUES ('101', '101', '101', '0231101107', 'US02311011068',
--         '2000010', '456', 0, 0.05, '2023-12-25 13:50:41.633000',
--         11000.0, 'USD', 85.0, '2023-10-25 13:50:41.633000', 0,
--         '2023-10-25 13:50:41.633000', false, 179101.5, 119.57,
--         1.02, 0, 0, 'CASH LOAN', true,
--         101, 202, '101', '202', 'SUBMITTED', null,
--         333, 'EFFR', 0.2, '2023-10-25 13:50:41.633000',
--         null, 101);