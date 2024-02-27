-- ONLY for local development
-- Template for required fields for adding contract
--
-- INSERT CONTRACT MOCK DATA FOR BORROWER

INSERT INTO internal_ref (id, broker, account_id, internal_id)
VALUES (8888, 'testBorrowerBroker', '8888', '8888'),
       (7777, 'testLenderBroker', '7777', '7777');

INSERT INTO party (id, party_id, party_name, gleif_lei, internal_id)
VALUES (8888, 8888, 'BORROWER', '8888', '8888'),
       (7777, 7777, 'LENDER', '7777', '77777');

INSERT INTO instrument (id, ticker, cusip, isin, sedol, quick_code)
VALUES  (8888, '234', '023135106', 'US0231351067', '2000019', '457'),
        (7777, '234', '023135106', 'US0231351067', '2000019', '457');

INSERT INTO fixed (id, base_rate, effective_rate, effective_date, cutoff_time)
VALUES (8888, 0.05, 0, '2023-10-25 13:50:41.633000', '0'),
       (7777, 0.05, 0, '2023-10-25 13:50:41.633000', '0');


INSERT INTO rebate (id, fixed)
VALUES (8888, 8888),
       (7777, 7777);

INSERT INTO rate (id, rebate)
VALUES  (8888, 8888),
        (7777, 7777);

INSERT INTO collateral (id, contract_price, contract_value, collateral_value, currency,
                        type, margin, rounding_rule, rounding_mode)
VALUES (8888, 119.57, 17935.5, 17935.5, 'USD', 'CASH', 102.0, 0, 'ALWAYSUP'),
       (7777, 119.57, 17935.5, 17935.5, 'USD', 'CASH', 102.0, 0, 'ALWAYSUP');

INSERT INTO venue (id, party_id, type, venue_name, venue_ref_key)
VALUES (8888, '8888', 'ONPLATFORM', 'testBorrowerVenueName', '8888'),
       (7777, '7777', 'ONPLATFORM', 'testLenderVenueName', '7777');


INSERT INTO trade (id, venue_id, instrument_id, rate_id, quantity, currency,
                   dividend_rate, trade_date, term_type, term_date, settlement_date,
                   settlement_type, collateral)
VALUES  (8888, 8888, 8888, 8888, 15000, 'USD',
        85.0, '2024-02-21 13:50:41.633000', 'OPEN', '2024-02-21 13:50:41.633000', '2023-10-25 13:50:41.633000',
        'DVP', 8888),
        (7777, 7777, 7777, 7777, 15000, 'USD',
        85.0, '2024-02-21 13:50:41.633000', 'OPEN', '2024-02-21 13:50:41.633000', '2023-10-25 13:50:41.633000',
        'DVP', 7777);

INSERT INTO agreement (id, agreement_id, status, trade_id, matching_spire_position_id, processing_status)
VALUES (8888, '32b71278-9ad2-445a-bfb0-b5ada72f8888', 'PENDING', 8888, '8888', 'CREATED'),
       (7777, '32b71278-9ad2-445a-bfb0-b5ada72f7777', 'PENDING', 7777, '7777', 'CREATED');

INSERT INTO transacting_party (id, party_role, party_id, internal_ref_id, transacting_party_id)
VALUES (8888, 'BORROWER', 8888, 8888, 8888),
       (7777, 'LENDER', 7777, 7777, 7777);

INSERT INTO contract (id, contract_id, contract_status, trade_id, processing_status)
VALUES (8888, '8888', 'PROPOSED', 8888, 'UNMATCHED'),
       (7777, '7777', 'PROPOSED', 7777, 'UNMATCHED');
