-- INSERT INTO recall_spire (recall_id, related_position_id, instruction_id, matching_1source_recall_id, related_contract_id, processing_status, status, creation_date_time, last_update_date_time, open_quantity, quantity, recall_date, recall_due_date)
-- VALUES (123, 789, 'test_instruction', null, 'qwer-tyui-333', 'CREATED', 'OPEN', '2024-05-20 13:50:41.633000', '2024-05-20 13:50:41.633000', 40, 99, '2024-05-20', '2024-05-25');
--
-- INSERT INTO venue (id, type, venue_name, venue_ref_key)
-- VALUES (101, 'OFFPLATFORM', 'testLenderVenueName', '123-789');
--
-- INSERT INTO venue_party (id, party_role, venue_id, venue_party_id)
-- VALUES (101, 'LENDER', '123-789', '101');
--
-- INSERT INTO recall_1source (recall_id, contract_id, recall_status, processing_status, matching_spire_recall_id, related_spire_position_id, venue_id, quantity, recall_date, recall_due_date)
-- VALUES (101, 'testContractId', 'OPEN', 'CREATED', null, 789, 101, 99, '2024-05-20', '2024-05-25');