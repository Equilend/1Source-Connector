-- Test UPDATE_LOAN_CONTRACT_SETTL_STATUS success flow data

-- INSERT INTO account (id, account_id, short_name, lei, one_source_id, dtc)
-- VALUES (7373, 7373, 'lender_acc', '7373', 123, 1),
--        (2828, 2828, 'borrower_acc', '2828', 789, 2);
--
-- INSERT INTO position (position_id, custom_value2, ticker, cusip, isin,
--                       sedol, quick_code, price_factor, rate, end_date,
--                       quantity, currency, tax_with_holding_rate, trade_date, term_id,
--                       settle_date, deliver_free, amount, price,
--                       cp_haircut, cp_mark_round_to, depo_id, position_type, is_cash,
--                       account_id, cp_id, account_lei, cp_lei, processing_status, status,
--                       index_id, index_name, spread, accrual_date, matching_1source_loan_contract_id)
-- VALUES ('7373', '7373', '123', '023135107', 'US0231351068',
--         '2000010', '456', 0, 0.05, '2023-12-25 13:50:41.633000',
--         7373.0, 'USD', 85.0, '2023-10-25 13:50:41.633000', 0,
--         '2023-10-25 13:50:41.633000', false, 17935.5, 119.57,
--         1.02, 0, 0, 'CASH LOAN', true,
--         7373, 2828, '7373', '2828', 'CREATED', 'FUTURE',
--         123, 'EFFR', 11.0, '2023-10-25 13:50:41.633000', '7373');
--
-- INSERT INTO account (id, account_id, short_name, lei, one_source_id, dtc)
-- VALUES (4141, 4141, 'lender_acc', '4141', 123, 1),
--        (1212, 1212, 'borrower_acc', '1212', 789, 2);
--
-- INSERT INTO position (position_id, custom_value2, ticker, cusip, isin,
--                       sedol, quick_code, price_factor, rate, end_date,
--                       quantity, currency, tax_with_holding_rate, trade_date, term_id,
--                       settle_date, deliver_free, amount, price,
--                       cp_haircut, cp_mark_round_to, depo_id, position_type, is_cash,
--                       account_id, cp_id, account_lei, cp_lei, processing_status, status,
--                       index_id, index_name, spread, accrual_date, matching_1source_loan_contract_id)
-- VALUES ('4141', '4141', '123', '023135107', 'US0231351068',
--         '2000010', '456', 0, 0.05, '2023-12-25 13:50:41.633000',
--         4141.0, 'USD', 85.0, '2023-10-25 13:50:41.633000', 0,
--         '2023-10-25 13:50:41.633000', false, 17935.5, 119.57,
--         1.02, 0, 0, 'CASH LOAN', true,
--         4141, 1212, '4141', '1212', 'CREATED', 'FUTURE',
--         123, 'EFFR', 11.0, '2023-10-25 13:50:41.633000',  '4141');