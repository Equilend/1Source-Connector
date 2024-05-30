CREATE TABLE IF NOT EXISTS recall_spire
(
    recall_id                BIGINT NOT NULL,
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

CREATE TABLE IF NOT EXISTS recall_1source
(
    recall_id                 VARCHAR(255) PRIMARY KEY,
    contract_id               VARCHAR(255) NOT NULL UNIQUE,
    recall_status             VARCHAR(255) NULL,
    processing_status         VARCHAR(255) NULL,
    matching_spire_recall_id  VARCHAR(255) NULL,
    related_spire_position_id BIGINT NULL,
    create_update_date_time   TIMESTAMP NULL,
    last_update_date_time     TIMESTAMP NULL,
    venue_id                  BIGINT NULL,
    open_quantity             INT NULL,
    quantity                  INT NULL,
    recall_date               TIMESTAMP NULL,
    recall_due_date           TIMESTAMP NULL,
    CONSTRAINT fk_venue FOREIGN KEY (venue_id) REFERENCES venue
);

CREATE TABLE IF NOT EXISTS recall_spire_instruction
(
    instruction_id          VARCHAR(255) PRIMARY KEY,
    instruction_type        VARCHAR(255) NOT NULL,
    spire_recall_id         BIGINT NOT NULL,
    related_contract_id     VARCHAR(255) NOT NULL,
    related_position_id     BIGINT NOT NULL,
    creation_date_time      TIMESTAMP NOT NULL,
    quantity                INT NULL,
    recall_date             TIMESTAMP NULL,
    recall_due_date         TIMESTAMP NULL,
    processing_status       VARCHAR(255) NULL
);