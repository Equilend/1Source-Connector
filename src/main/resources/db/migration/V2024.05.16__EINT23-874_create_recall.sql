CREATE TABLE IF NOT EXISTS recall
(
    recall_id                VARCHAR(255) NOT NULL,
    related_position_id       BIGINT NOT NULL,
    matching_1source_recall_id VARCHAR(255) NULL,
    related_contract_id       VARCHAR(255) NOT NULL,
    processing_status        VARCHAR(255) NULL,
    "status"                VARCHAR(255) NULL,
    creation_date_time        TIMESTAMP NOT NULL,
    last_update_date_time      TIMESTAMP NOT NULL,
    open_quantity            INT NULL,
    quantity                INT NULL,
    recall_date              TIMESTAMP NULL,
    recall_due_date           TIMESTAMP NULL,
    PRIMARY KEY (recall_id, related_position_id)
);