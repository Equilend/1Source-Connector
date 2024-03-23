-- Test GET_LOAN_CONTRACT_SETTLED success flow data

-- INSERT INTO trade_event (event_id, event_type, event_datetime, resource_uri)
-- VALUES (3003, 'CONTRACT_OPENED', now(), 'contracts/32b71278-9ad2-445a-bfb0-b5ada72f4789');
--
-- INSERT INTO internal_ref (id, broker, account_id, internal_id)
-- VALUES (7802, 'testLenderBroker', '7802', '7802'),
--        (4789, 'testBorrowerBroker', '4789', '4789');
--
-- INSERT INTO party (id, party_id, party_name, gleif_lei, internal_id)
-- VALUES (7802, 7802, 'LENDER', '7802', '7802'),
--        (4789, 4789, 'BORROWER', '4789', '47897');
--
-- INSERT INTO instrument (id, ticker, cusip, isin, sedol, quick_code)
-- VALUES  (7802, '234', '02317802106', 'US023178021067', '2000019', '457'),
--         (4789, '234', '02317802107', 'US023178021068', '2000010', '457');
--
-- INSERT INTO fixed (id, base_rate, effective_rate, effective_date, cutoff_time)
-- VALUES (7802, 0.05, 0, '2023-10-25 13:50:41.633000', '0'),
--        (4789, 0.05, 0, '2023-10-25 13:50:41.633000', '0');
--
-- INSERT INTO rebate (id, fixed)
-- VALUES (7802, 7802), (4789, 4789);
--
-- INSERT INTO rate (id, rebate)
-- VALUES  (7802, 7802), (4789, 4789);
--
-- INSERT INTO collateral (id, contract_price, collateral_value, currency,
--                         type, margin, rounding_rule, rounding_mode)
-- VALUES (7802, 119.57, 1797802.5, 'USD', 'CASH', 102.0, 0, 'ALWAYSUP'),
--        (4789, 119.57, 1797802.5, 'USD', 'CASH', 102.0, 0, 'ALWAYSUP');
--
-- INSERT INTO venue (id, party_id, type, venue_name, venue_ref_key)
-- VALUES (7802, '7802', 'ONPLATFORM', 'testLenderVenueName', '7802'),
--        (4789, '4789', 'ONPLATFORM', 'testBorrowerVenueName', '4789');
--
-- INSERT INTO trade (id, venue_id, instrument_id, rate_id, quantity, currency,
--                    dividend_rate, trade_date, term_type, term_date, settlement_date,
--                    settlement_type, collateral)
-- VALUES  (4789, 4789, 4789, 4789, 11000.0, 'USD',
--          85.0, '2024-02-21 13:50:41.633000', 'OPEN', '2024-02-21 13:50:41.633000', '2023-10-25 13:50:41.633000',
--          'DVP', 4789),
--         (7802, 7802, 7802, 7802, 15000, 'USD',
--          85.0, '2024-02-21 13:50:41.633000', 'OPEN', '2024-02-21 13:50:41.633000', '2023-10-25 13:50:41.633000',
--          'DVP', 7802);
--
-- INSERT INTO agreement (id, agreement_id, status, trade_id, matching_spire_position_id, processing_status)
-- VALUES (7802, '32b71278-9ad2-445a-bfb0-b5ada72f7802', 'PENDING', 7802, '7802', 'CREATED'),
--        (4789, '32b71278-9ad2-445a-bfb0-b5ada72f4789', 'PENDING', 4789, '4789', 'CREATED');
--
-- INSERT INTO transacting_party (id, party_role, party_id, internal_ref_id, transacting_party_id)
-- VALUES (7802, 'LENDER', 7802, 7802, 4789);
--
-- INSERT INTO contract (id, contract_id, contract_status, trade_id, processing_status,
--                       matching_spire_position_id, matching_spire_trade_id)
-- VALUES (4789, '32b71278-9ad2-445a-bfb0-b5ada72f4789', 'PROPOSED', 4789, 'CREATED', 4789, 4789);