CREATE TABLE IF NOT EXISTS decline_instruction
(
    instruction_id       VARCHAR(255) PRIMARY KEY,
    exception_event_id     VARCHAR(255) NULL,
    proposal_id          VARCHAR(255) NULL,
    proposal_type        VARCHAR(255) NULL CHECK (proposal_type in ('CONTRACT', 'RERATE')),
    creation_date_time   TIMESTAMP    NOT NULL,
    user_id              VARCHAR(255) NULL,
    reason_code          VARCHAR(255) NULL,
    reason_text          VARCHAR(255) NULL,
    processing_status    VARCHAR(255) NULL
);