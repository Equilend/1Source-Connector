CREATE TABLE IF NOT EXISTS correction_instruction
(
    instruction_id     VARCHAR(255) NOT NULL,
    instruction_type   VARCHAR(255),
    amended_trade_id   BIGINT,
    creation_date_time TIMESTAMP    NOT NULL,
    old_trade_id       BIGINT,
    processing_status  VARCHAR(255),
    proposal_id        VARCHAR(255),
    proposal_type      VARCHAR(255),
    user_id            VARCHAR(255),
    CONSTRAINT correction_instruction_pkey PRIMARY KEY (instruction_id),
    CONSTRAINT correction_instruction_instruction_type_check
    CHECK (instruction_type in ('RERATE_AMEND', 'RERATE_CANCELLED')),
    CONSTRAINT correction_instruction_processing_status_check
    CHECK (processing_status in ('NEW', 'PROCESSED')),
    CONSTRAINT correction_instruction_proposal_type_check
    CHECK (proposal_type in ('CONTRACT', 'RERATE'))
    );