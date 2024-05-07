ALTER TABLE agreement
DROP COLUMN IF EXISTS status,
DROP COLUMN IF EXISTS matching_1source_loan_contract_id,
DROP COLUMN IF EXISTS flow_status,
DROP COLUMN IF EXISTS event_type;

ALTER TABLE agreement
ADD COLUMN IF NOT EXISTS create_date_time TIMESTAMP;