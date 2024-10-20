-- Test GET_LOAN_CONTRACT_APPROVED success flow data

-- INSERT INTO trade_event (event_id, event_type, event_datetime, resource_uri)
-- VALUES ('789', 'CONTRACT_PENDING', now(), 'contracts/32b71278-9ad2-445a-bfb0-b5ada72f3456');
--
-- INSERT INTO internal_ref (id, broker, account_id, internal_id)
-- VALUES (890, 'testLenderBroker', '890', '890'),
--        (543, 'testBorrowerBroker', '543', '543');
--
-- INSERT INTO party (id, party_id, party_name, gleif_lei, internal_id)
-- VALUES (890, 890, 'LENDER', '890', '890'),
--        (543, 543, 'BORROWER', '543', '5437');
--
-- INSERT INTO instrument (id, ticker, cusip, isin, sedol, quick_code)
-- VALUES  (890, '234', '0231890106', 'US02318901067', '2000019', '457'),
--         (543, '234', '0231890107', 'US02318901068', '2000010', '457');
--
-- INSERT INTO fixed (id, base_rate, effective_rate, effective_date, cutoff_time)
-- VALUES (890, 0.05, 0, '5433-10-25 13:50:41.633000', '0'),
--        (543, 0.05, 0, '5433-10-25 13:50:41.633000', '0');
--
-- INSERT INTO rebate (id, fixed)
-- VALUES (890, 890), (543, 543);
--
-- INSERT INTO rate (id, rebate)
-- VALUES  (890, 890), (543, 543);
--
-- INSERT INTO collateral (id, contract_price, collateral_value, currency,
--                         type, margin, rounding_rule, rounding_mode)
-- VALUES (890, 119.57, 179890.5, 'USD', 'CASH', 102.0, 0, 'ALWAYSUP'),
--        (543, 119.57, 179890.5, 'USD', 'CASH', 102.0, 0, 'ALWAYSUP');
--
-- INSERT INTO trade (id, instrument_id, rate_id, quantity, currency,
--                    dividend_rate, trade_date, term_type, term_date, settlement_date,
--                    settlement_type, collateral)
-- VALUES  (543, 543, 543, 11000.0, 'USD',
--          85.0, '5434-02-21 13:50:41.633000', 'OPEN', '5434-02-21 13:50:41.633000', '5433-10-25 13:50:41.633000',
--          'DVP', 543),
--         (890, 890, 890, 15000, 'USD',
--          85.0, '5434-02-21 13:50:41.633000', 'OPEN', '5434-02-21 13:50:41.633000', '5433-10-25 13:50:41.633000',
--          'DVP', 890);
--
-- INSERT INTO venue (id, party_id, type, venue_name, venue_ref_key, trade_id)
-- VALUES (890, '890', 'ONPLATFORM', 'testLenderVenueName', '890', 890),
--        (543, '543', 'ONPLATFORM', 'testBorrowerVenueName', '543', 543);
--
-- INSERT INTO agreement (id, agreement_id,trade_id, matching_spire_position_id, processing_status)
-- VALUES (890, '32b71278-9ad2-445a-bfb0-b5ada72f890', 890, '890', 'CREATED'),
--        (543, '32b71278-9ad2-445a-bfb0-b5ada72f543', 543, '543', 'CREATED');
--
-- INSERT INTO transacting_party (id, party_role, party_id, internal_ref_id, transacting_party_id)
-- VALUES (890, 'LENDER', 890, 890, 543);
--
-- INSERT INTO contract (id, contract_id, contract_status, trade_id, processing_status,
--                       matching_spire_position_id, matching_spire_trade_id)
-- VALUES (543, '32b71278-9ad2-445a-bfb0-b5ada72f3456', 'PROPOSED', 543, 'DISCREPANCIES', 543, 543);
--
-- INSERT INTO account (id, account_id, short_name, lei, one_source_id, dtc)
-- VALUES (890, 890, 'lender_acc', '890', 890, 1),
--        (543, 543, 'borrower_acc', '543', 543, 2);
--
-- INSERT INTO position (position_id, custom_value2, ticker, cusip, isin,
--                       sedol, quick_code, price_factor, rate, end_date,
--                       quantity, currency, tax_with_holding_rate, trade_date, term_id,
--                       settle_date, deliver_free, amount, price,
--                       cp_haircut, cp_mark_round_to, depo_id, position_type, is_cash,
--                       account_id, cp_id, account_lei, cp_lei, processing_status, status,
--                       index_id, index_name, spread, accrual_date,
--                       matching_1source_loan_contract_id, trade_id)
-- VALUES ('543', '543', '543', '0231890107', 'US02318901068',
--         '2000010', '456', 0, 0.05, '5433-12-25 13:50:41.633000',
--         11000.0, 'USD', 85.0, '5434-02-21 13:50:41.633000', 0,
--         '5433-10-25 13:50:41.633000', false, 179890.5, 119.57,
--         1.02, 0, 0, 'CASH BORROW', true,
--         543, 890, '543', '890', 'MATCHED', null,
--         333, 'EFFR', 0.2, '5433-10-25 13:50:41.633000',
--         '32b71278-9ad2-445a-bfb0-b5ada72f3456', 543);