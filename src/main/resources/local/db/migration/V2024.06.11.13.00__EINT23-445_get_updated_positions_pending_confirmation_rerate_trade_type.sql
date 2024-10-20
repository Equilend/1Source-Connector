-- INSERT INTO account (id, account_id, short_name, lei, one_source_id, dtc)
-- VALUES (6710, 6710, 'lender_acc', '6710', 123, 1),
--        (7890, 7890, 'borrower_acc', '7890', 789, 2);
--
-- INSERT INTO position (position_id, custom_value2, ticker, cusip, isin,
--                       sedol, quick_code, price_factor, rate, end_date,
--                       quantity, currency, tax_with_holding_rate, trade_date, term_id,
--                       settle_date, deliver_free, amount, price,
--                       cp_haircut, cp_mark_round_to, depo_id, position_type, last_update_datetime,
--                       account_id, cp_id, account_lei, cp_lei, processing_status, status, is_cash,
--                       index_id, index_name, spread, accrual_date, matching_1source_loan_contract_id)
-- VALUES ('7890', '7890', '234', '02319411107', 'US023194111068',
--         '2000010', '458', 0, 0.05, '2024-02-21 13:50:41.633000',
--         7890.0, 'USD', 85.0, '2024-02-21 13:50:41.633000', 1,
--         '2023-10-25 13:50:41.633000', false, 1799411.5, 119.57,
--         1.02, 0, 0, 'CASH BORROW', '2024-03-24 13:50:41.633000',
--         7890, 6710, '4141', '1212', 'CREATED', 'PENDING LEDGER CONFIRMATION', true,
--         12, 'Fixed Rate', 11.0, '2023-10-25 13:50:41.633000', '32b71278-9ad2-445a-bfb0-b5ada72f7890');
--
-- INSERT INTO internal_ref (id, broker, account_id, internal_id)
-- VALUES (6710, 'testLenderBroker', '6710', '6710'),
--        (7890, 'testBorrowerBroker', '7890', '7890');
--
-- INSERT INTO party (id, party_id, party_name, gleif_lei, internal_id)
-- VALUES (6710, 6710, 'LENDER', '6710', '6710'),
--        (7890, 7890, 'BORROWER', '7890', '7890');
--
-- INSERT INTO instrument (id, ticker, cusip, isin, sedol, quick_code)
-- VALUES  (6710, '234', '02318091106', 'US023180911067', '2000019', '457'),
--         (7890, '234', '02318091107', 'US023180911068', '2000010', '457');
--
-- INSERT INTO fixed (id, base_rate, effective_rate, effective_date, cutoff_time)
-- VALUES (6710, 0.05, 0, '2023-10-25 13:50:41.633000', '0'),
--        (7890, 0.05, 0, '2023-10-25 13:50:41.633000', '0');
--
-- INSERT INTO rebate (id, fixed)
-- VALUES (6710, 6710), (7890, 7890);
--
-- INSERT INTO rate (id, rebate)
-- VALUES  (6710, 6710), (7890, 7890);
--
-- INSERT INTO collateral (id, contract_price, collateral_value, currency,
--                         type, margin, rounding_rule, rounding_mode)
-- VALUES (6710, 119.57, 1798091.5, 'USD', 'CASH', 102.0, 0, 'ALWAYSUP'),
--        (7890, 119.57, 1798091.5, 'USD', 'CASH', 102.0, 0, 'ALWAYSUP');
--
-- INSERT INTO trade (id, instrument_id, rate_id, quantity, currency,
--                    dividend_rate, trade_date, term_type, term_date, settlement_date,
--                    settlement_type, collateral)
-- VALUES  (7890, 7890, 7890, 11000.0, 'USD',
--          85.0, '2024-02-21 13:50:41.633000', 'OPEN', '2024-02-21 13:50:41.633000', '2023-10-25 13:50:41.633000',
--          'DVP', 7890),
--         (6710, 6710, 6710, 15000, 'USD',
--          85.0, '2024-02-21 13:50:41.633000', 'OPEN', '2024-02-21 13:50:41.633000', '2023-10-25 13:50:41.633000',
--          'DVP', 6710);
--
-- INSERT INTO venue (id, party_id, type, venue_name, venue_ref_key, trade_id)
-- VALUES (6710, '6710', 'ONPLATFORM', 'testLenderVenueName', '6710', 6710),
--        (7890, '7890', 'ONPLATFORM', 'testBorrowerVenueName', '7890', 7890);
--
-- INSERT INTO agreement (id, agreement_id, trade_id, matching_spire_position_id, processing_status)
-- VALUES (6710, '32b71278-9ad2-445a-bfb0-b5ada72f6710', 6710, '6710', 'CREATED'),
--        (7890, '32b71278-9ad2-445a-bfb0-b5ada72f7890', 7890, '7890', 'CREATED');
--
-- INSERT INTO transacting_party (id, party_role, party_id, internal_ref_id, transacting_party_id)
-- VALUES (6710, 'LENDER', 6710, 6710, 7890);
--
-- INSERT INTO contract (id, contract_id, contract_status, trade_id, processing_status,
--                       matching_spire_position_id, matching_spire_trade_id)
-- VALUES (7890, '32b71278-9ad2-445a-bfb0-b5ada72f7890', 'PENDING', 7890, 'DECLINED', 7890, 7890);