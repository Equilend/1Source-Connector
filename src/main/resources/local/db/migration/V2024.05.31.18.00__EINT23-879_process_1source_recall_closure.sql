-- INSERT INTO trade_event (event_id, event_type, event_datetime, resource_uri)
-- VALUES ('1892', 'RECALL_CLOSED', now(), '/ledger/contracts/5656-4767-7887-7455/recalls/2345');
--
--
-- INSERT INTO recall_1source (recall_id, contract_id, recall_status, matching_spire_recall_id,
--                             related_spire_position_id, quantity, recall_date, recall_due_date)
-- VALUES (2345, '2222-4444-5555-7777', 'OPEN', 6767,
--         20790, 100, '2024-05-30', '2024-05-31');
--
-- INSERT INTO recall_spire (recall_id, related_position_id, instruction_id, matching_1source_recall_id, related_contract_id,
--                           processing_status, status, creation_date_time, last_update_date_time, open_quantity, quantity,
--                           recall_date, recall_due_date)
-- VALUES (6767, 20790, 'instruction_test', '2345', '5656-4767-7887-7455',
--         'CONFIRMED_LENDER', 'OPEN', '2024-05-29 13:50:41.633000', '2024-05-29 13:50:41.633000', 40, 100,
--         '2024-05-29', '2024-05-29');
