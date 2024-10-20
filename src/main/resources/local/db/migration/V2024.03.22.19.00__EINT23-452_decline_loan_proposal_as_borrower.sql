-- Test DECLINE_LOAN_CONTRACT_PROPOSAL Borrower success flow data

-- INSERT INTO decline_instruction
-- VALUES('34567', '1111', 6677, 'CONTRACT', now(), '1', '2', 'text');
--
-- INSERT INTO internal_ref (id, broker, account_id, internal_id)
-- VALUES (4455, 'testLenderBroker', '4455', '4455'),
--        (6677, 'testBorrowerBroker', '6677', '6677');
--
-- INSERT INTO party (id, party_id, party_name, gleif_lei, internal_id)
-- VALUES (4455, 4455, 'LENDER', '4455', '4455'),
--        (6677, 6677, 'BORROWER', '6677', '66777');
--
-- INSERT INTO instrument (id, ticker, cusip, isin, sedol, quick_code)
-- VALUES  (4455, '234', '02314455106', 'US023144551067', '2000019', '457'),
--         (6677, '234', '02314455107', 'US023144551068', '2000010', '457');
--
-- INSERT INTO fixed (id, base_rate, effective_rate, effective_date, cutoff_time)
-- VALUES (4455, 0.05, 0, '66773-10-25 13:50:41.633000', '0'),
--        (6677, 0.05, 0, '66773-10-25 13:50:41.633000', '0');
--
-- INSERT INTO rebate (id, fixed)
-- VALUES (4455, 4455), (6677, 6677);
--
-- INSERT INTO rate (id, rebate)
-- VALUES  (4455, 4455), (6677, 6677);
--
-- INSERT INTO collateral (id, contract_price, collateral_value, currency,
--                         type, margin, rounding_rule, rounding_mode)
-- VALUES (4455, 119.57, 1794455.5, 'USD', 'CASH', 102.0, 0, 'ALWAYSUP'),
--        (6677, 119.57, 1794455.5, 'USD', 'CASH', 102.0, 0, 'ALWAYSUP');
--
-- INSERT INTO trade (id, instrument_id, rate_id, quantity, currency,
--                    dividend_rate, trade_date, term_type, term_date, settlement_date,
--                    settlement_type, collateral)
-- VALUES  (6677, 6677, 6677, 11000.0, 'USD',
--          85.0, '66774-02-21 13:50:41.633000', 'OPEN', '66774-02-21 13:50:41.633000', '66773-10-25 13:50:41.633000',
--          'DVP', 6677),
--         (4455, 4455, 4455, 15000, 'USD',
--          85.0, '66774-02-21 13:50:41.633000', 'OPEN', '66774-02-21 13:50:41.633000', '66773-10-25 13:50:41.633000',
--          'DVP', 4455);
--
-- INSERT INTO venue (id, party_id, type, venue_name, venue_ref_key, trade_id)
-- VALUES (4455, '4455', 'ONPLATFORM', 'testLenderVenueName', '4455', 4455),
--        (6677, '6677', 'ONPLATFORM', 'testBorrowerVenueName', '6677', 6677);
--
-- INSERT INTO agreement (id, agreement_id, trade_id, matching_spire_position_id, processing_status)
-- VALUES (4455, '32b71278-9ad2-445a-bfb0-b5ada72f4455', 4455, '4455', 'CREATED'),
--        (6677, '32b71278-9ad2-445a-bfb0-b5ada72f6677', 6677, '6677', 'CREATED');
--
-- INSERT INTO transacting_party (id, party_role, party_id, internal_ref_id, transacting_party_id)
-- VALUES (4455, 'LENDER', 4455, 4455, 6677);
--
-- INSERT INTO contract (id, contract_id, contract_status, trade_id, processing_status,
--                       matching_spire_position_id, matching_spire_trade_id)
-- VALUES (6677, '6677', 'PROPOSED', 6677, 'DISCREPANCIES', 6677, 6677);