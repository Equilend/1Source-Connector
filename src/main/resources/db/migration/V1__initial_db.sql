CREATE TABLE IF NOT EXISTS agreement
(
    id                   SERIAL NOT NULL,
    agreement_id         VARCHAR(255) NULL,
    status               VARCHAR(255) NULL,
    last_update_datetime timestamp    NULL,
    trade_id             BIGINT       NULL,
    event_type           VARCHAR(255) NULL,
    matching_spire_position_id          VARCHAR(255) NULL,
    matching_1source_loan_contract_id          VARCHAR(255) NULL,
    flow_status          VARCHAR(255) NULL,
    processing_status     VARCHAR(255) NULL,
    CONSTRAINT pk_agreement PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS collateral
(
    id                SERIAL NOT NULL,
    contract_price    DOUBLE PRECISION          NULL,
    contract_value    DOUBLE PRECISION       NULL,
    collateral_value  DOUBLE PRECISION    NULL,
    currency          VARCHAR(255) NULL,
    type            VARCHAR(255) NULL,
    description     VARCHAR(255) NULL,
    margin            DOUBLE PRECISION NULL,
    rounding_rule     INT NULL,
    rounding_mode     VARCHAR(255) NULL,
    CONSTRAINT pk_collateral PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS contract
(
    id                   SERIAL NOT NULL,
    contract_id          VARCHAR(255) NULL,
    last_event_id        BIGINT       NULL,
    contract_status      VARCHAR(255) NULL,
    settlement_status    VARCHAR(255) NULL,
    last_update_party_id VARCHAR(255) NULL,
    last_update_datetime timestamp    NULL,
    trade_id             BIGINT       NULL,
    processing_status    VARCHAR(255) NULL,
    event_type           VARCHAR(255) NULL,
    matching_spire_position_id          VARCHAR(255) NULL,
    flow_status          VARCHAR(255) NULL,
    CONSTRAINT pk_contract PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS fee
(
    id                        SERIAL NOT NULL,
    base_rate                 DOUBLE PRECISION  NULL,
    effective_rate            DOUBLE PRECISION  NULL,
    effective_date            DATE NULL,
    cutoff_time               VARCHAR(255)    NULL,
    CONSTRAINT pk_fee       PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS fixed
(
    id                        SERIAL NOT NULL,
    base_rate                 DOUBLE PRECISION  NULL,
    effective_rate            DOUBLE PRECISION  NULL,
    effective_date            DATE NULL,
    cutoff_time               VARCHAR(255)    NULL,
    CONSTRAINT pk_fixed       PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS floating
(
    id                        SERIAL NOT NULL,
    benchmark                 VARCHAR(255) NULL,
    base_rate                 DOUBLE PRECISION  NULL,
    spread                    DOUBLE PRECISION  NULL,
    effective_rate            DOUBLE PRECISION  NULL,
    is_auto_rerate            BOOLEAN      NULL,
    effective_date_delay      INT          NULL,
    effective_date            DATE NULL,
    cutoff_time               VARCHAR(255)    NULL,
    CONSTRAINT pk_floating    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS instrument
(
    id     SERIAL NOT NULL,
    ticker VARCHAR(255) NULL,
    cusip  VARCHAR(255) NULL,
    isin   VARCHAR(255) NULL,
    sedol  VARCHAR(255) NULL,
    quick  VARCHAR(255) NULL,
    figi   VARCHAR(255) NULL,
    description VARCHAR(255) NULL,
    price_id BIGINT NULL,
    CONSTRAINT pk_instrument PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS internal_ref
(
    id          SERIAL NOT NULL,
    broker      VARCHAR(255) NULL,
    account_id  VARCHAR(255) NULL,
    internal_id VARCHAR(255) NULL,
    CONSTRAINT pk_internal_ref PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS local_market_field
(
    id                    SERIAL NOT NULL,
    local_field_name      VARCHAR(255) NULL,
    local_field_value     VARCHAR(255) NULL,
    local_market_field_id BIGINT       NULL,
    CONSTRAINT pk_local_market_field PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS party
(
    id          SERIAL NOT NULL,
    party_id    VARCHAR(255) NULL,
    party_name  VARCHAR(255) NULL,
    gleif_lei   VARCHAR(255) NULL,
    internal_id VARCHAR(255) NULL,
    CONSTRAINT pk_party PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS platform
(
    id                   SERIAL NOT NULL,
    gleif_lei            VARCHAR(255) NULL,
    legal_name           VARCHAR(255) NULL,
    mic                  VARCHAR(255) NULL,
    venue_name           VARCHAR(255) NULL,
    venue_ref            VARCHAR(255) NULL,
    transaction_datetime timestamp    NULL,
    CONSTRAINT pk_platform PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS position
(
    id                    SERIAL NOT NULL,
    venue_ref_id          VARCHAR(255) NULL,
    spire_position_id     VARCHAR(255) NULL,
    custom_value2         VARCHAR(255) NULL,
    ticker                VARCHAR(255) NULL,
    cusip                 VARCHAR(255) NULL,
    isin                  VARCHAR(255) NULL,
    sedol                 VARCHAR(255) NULL,
    quick_code            VARCHAR(255) NULL,
    bloomberg_id          VARCHAR(255) NULL,
    rate                  DOUBLE PRECISION       NULL,
    quantity              DOUBLE PRECISION       NULL,
    currency              VARCHAR(255) NULL,
    tax_with_holding_rate DOUBLE PRECISION       NULL,
    trade_date            timestamp NULL,
    term_id               INT          NULL,
    end_date              timestamp NULL,
    settle_date           timestamp NULL,
    deliver_free          BOOLEAN      NULL,
    amount                DOUBLE PRECISION       NULL,
    price                 DOUBLE PRECISION       NULL,
    contract_value        DOUBLE PRECISION NULL,
    collateral_type       VARCHAR(255) NULL,
    cp_haircut            DOUBLE PRECISION       NULL,
    cp_mark_round_to      INT          NULL,
    depo_id               INT          NULL,
    currency_id           INT          NULL,
    security_id           BIGINT          NULL,
    position_type_id      INT          NULL,
    position_type         VARCHAR(255) NULL,
    account_lei           VARCHAR(255) NULL,
    short_name            VARCHAR(255) NULL,
    cp_lei                VARCHAR(255) NULL,
    status                VARCHAR(255) NULL,
    processing_status     VARCHAR(255) NULL,
    matching_1source_trade_agreement_id     VARCHAR(255) NULL,
    matching_1source_loan_contract_id     VARCHAR(255) NULL,
    applicable_instruction_id      INT          NULL,
    last_update_datetime   timestamp NULL,
    CONSTRAINT pk_position PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS price
(
    id       SERIAL NOT NULL,
    value    DOUBLE PRECISION       NULL,
    currency VARCHAR(255)       NULL,
    unit     VARCHAR(255) NULL,
    CONSTRAINT pk_price PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS rebate
(
    id            SERIAL NOT NULL,
    fixed         BIGINT       NULL,
    floating      BIGINT       NULL,
    CONSTRAINT pk_rebate PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS rate
(
    id       SERIAL NOT NULL,
    rebate   BIGINT       NULL,
    fee      BIGINT       NULL,
    CONSTRAINT pk_rate PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS settlement
(
    id            SERIAL NOT NULL,
    party_role    VARCHAR(255) NULL,
    instruction_id   BIGINT       NULL,
    instruction   BIGINT       NULL,
    settlement_id BIGINT       NULL,
    CONSTRAINT pk_settlement PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS settlement_instruction_update
(
    id            SERIAL NOT NULL,
    venue_ref_id  VARCHAR(255) NULL,
    party_role    VARCHAR(255) NULL,
    instruction_id   BIGINT       NULL,
    instruction   BIGINT       NULL,
    CONSTRAINT pk_settlement_instruction_update PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS settlement_instruction
(
    id               SERIAL NOT NULL,
    settlement_bic   VARCHAR(255) NULL,
    local_agent_bic  VARCHAR(255) NULL,
    local_agent_name VARCHAR(255) NULL,
    local_agent_acct VARCHAR(255) NULL,
    CONSTRAINT pk_settlement_instruction PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS timestamp
(
    id        SERIAL NOT NULL,
    timestamp timestamp NULL,
    CONSTRAINT pk_timestamp PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS trade
(
    id              SERIAL NOT NULL,
    venue_id        BIGINT       NULL,
    instrument_id   BIGINT       NULL,
    rate_id         BIGINT       NULL,
    quantity        INT          NULL,
    currency        VARCHAR(255) NULL,
    dividend_rate   INT          NULL,
    trade_date      DATE         NULL,
    term_type       VARCHAR(255) NULL,
    term_date       DATE         NULL,
    settlement_date DATE         NULL,
    settlement_type VARCHAR(255) NULL,
    collateral      BIGINT       NULL,
    processing_status VARCHAR(255) NULL,
    event_id             BIGINT       NULL,
    resource_uri         VARCHAR(255) NULL,
    CONSTRAINT pk_trade PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS trade_event
(
    id              SERIAL NOT NULL,
    event_id        BIGINT       NULL,
    event_type      VARCHAR(255) NULL,
    event_datetime  timestamp    NULL,
    resource_uri    VARCHAR(255) NULL,
    processing_status VARCHAR(255) NULL,
    CONSTRAINT pk_trade_event PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS transacting_party
(
    id                   SERIAL NOT NULL,
    party_role           VARCHAR(255) NULL,
    party_id             BIGINT       NULL,
    transacting_party_id BIGINT       NULL,
    CONSTRAINT pk_transacting_party PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS venue
(
    id          SERIAL NOT NULL,
    type        VARCHAR(255) NULL,
    platform_id BIGINT       NULL,
    CONSTRAINT pk_venue PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS venue_party
(
    id              SERIAL NOT NULL,
    party_role      VARCHAR(255) NULL,
    venue_id        VARCHAR(255) NULL,
    internal_ref_id BIGINT       NULL,
    venue_party_id  BIGINT       NULL,
    CONSTRAINT pk_venue_party PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS settlement_temp
(
    id            SERIAL NOT NULL,
    contract_id    VARCHAR(255) NULL,
    settlement_id    BIGINT NULL,
    CONSTRAINT pk_settlement_temp PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS participant
(
    id          SERIAL NOT NULL,
    party_id    VARCHAR(255) NULL,
    party_name  VARCHAR(255) NULL,
    gleif_lei   VARCHAR(255) NULL,
    internal_id VARCHAR(255) NULL,
    participant_start_date timestamp    NULL,
    participant_end_date timestamp    NULL,
    CONSTRAINT pk_participant PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS participant_holder
(
    id            SERIAL NOT NULL,
    participant_id    BIGINT NULL,
    CONSTRAINT pk_participant_holder PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS event_record
(
    id                    VARCHAR(255) PRIMARY KEY,
    specversion           VARCHAR(255) NOT NULL CHECK (specversion != ''),
    "type"                VARCHAR(255) NOT NULL CHECK ("type" != ''),
    source                VARCHAR(255) NOT NULL,
    "subject"             VARCHAR(255) NULL,
    "time"                timestamp NULL,
    relatedprocess        VARCHAR(255) NULL,
    relatedsubprocess     VARCHAR(255) NULL,
    dataContentType       VARCHAR(255) NULL,
    "data"                TEXT NULL,
    processingstatus      VARCHAR(255) NULL
);

ALTER TABLE agreement DROP CONSTRAINT IF EXISTS FK_AGREEMENT_ON_TRADE;
ALTER TABLE agreement
    ADD CONSTRAINT FK_AGREEMENT_ON_TRADE FOREIGN KEY (trade_id) REFERENCES trade (id);

ALTER TABLE contract DROP CONSTRAINT IF EXISTS FK_CONTRACT_ON_TRADE;
ALTER TABLE contract
    ADD CONSTRAINT FK_CONTRACT_ON_TRADE FOREIGN KEY (trade_id) REFERENCES trade (id);

ALTER TABLE contract DROP CONSTRAINT IF EXISTS FK_CONTRACT_ON_EVENT;
ALTER TABLE contract
    ADD CONSTRAINT FK_CONTRACT_ON_EVENT FOREIGN KEY (last_event_id) REFERENCES trade_event (id);

ALTER TABLE instrument DROP CONSTRAINT IF EXISTS FK_INSTRUMENT_ON_PRICE;
ALTER TABLE instrument
    ADD CONSTRAINT FK_INSTRUMENT_ON_PRICE FOREIGN KEY (price_id) REFERENCES price (id);

ALTER TABLE local_market_field DROP CONSTRAINT IF EXISTS FK_LOCAL_MARKET_FIELD_ON_LOCAL_MARKET_FIELD;
ALTER TABLE local_market_field
    ADD CONSTRAINT FK_LOCAL_MARKET_FIELD_ON_LOCAL_MARKET_FIELD FOREIGN KEY (local_market_field_id) REFERENCES settlement_instruction (id);

ALTER TABLE rate DROP CONSTRAINT IF EXISTS FK_RATE_ON_REBATE;
ALTER TABLE rate DROP CONSTRAINT IF EXISTS FK_RATE_ON_FEE;
ALTER TABLE rate
    ADD CONSTRAINT FK_RATE_ON_REBATE FOREIGN KEY (rebate) REFERENCES rebate (id);
ALTER TABLE rate
    ADD CONSTRAINT FK_RATE_ON_FEE FOREIGN KEY (fee) REFERENCES fee (id);

ALTER TABLE rebate DROP CONSTRAINT IF EXISTS FK_REBATE_ON_FIXED;
ALTER TABLE rebate DROP CONSTRAINT IF EXISTS FK_REBATE_ON_FLOATING;
ALTER TABLE rebate
    ADD CONSTRAINT FK_REBATE_ON_FIXED FOREIGN KEY (fixed) REFERENCES fixed (id);
ALTER TABLE rebate
    ADD CONSTRAINT FK_REBATE_ON_FLOATING FOREIGN KEY (floating) REFERENCES floating (id);

ALTER TABLE settlement DROP CONSTRAINT IF EXISTS FK_SETTLEMENT_ON_INSTRUCTION;
ALTER TABLE settlement DROP CONSTRAINT IF EXISTS FK_SETTLEMENT_ON_SETTLEMENT;
ALTER TABLE settlement
    ADD CONSTRAINT FK_SETTLEMENT_ON_INSTRUCTION FOREIGN KEY (instruction) REFERENCES settlement_instruction (id);
ALTER TABLE settlement
    ADD CONSTRAINT FK_SETTLEMENT_ON_SETTLEMENT FOREIGN KEY (settlement_id) REFERENCES contract (id);

ALTER TABLE settlement_instruction_update DROP CONSTRAINT IF EXISTS FK_SETTLEMENT_ON_INSTRUCTION;
ALTER TABLE settlement_instruction_update
    ADD CONSTRAINT FK_SETTLEMENT_ON_INSTRUCTION FOREIGN KEY (instruction) REFERENCES settlement_instruction (id);

ALTER TABLE trade DROP CONSTRAINT IF EXISTS FK_TRADE_ON_COLLATERAL;
ALTER TABLE trade DROP CONSTRAINT IF EXISTS FK_TRADE_ON_INSTRUMENT;
ALTER TABLE trade DROP CONSTRAINT IF EXISTS FK_TRADE_ON_RATE;
ALTER TABLE trade DROP CONSTRAINT IF EXISTS FK_TRADE_ON_VENUE;
ALTER TABLE trade
    ADD CONSTRAINT FK_TRADE_ON_COLLATERAL FOREIGN KEY (collateral) REFERENCES collateral (id);
ALTER TABLE trade
    ADD CONSTRAINT FK_TRADE_ON_INSTRUMENT FOREIGN KEY (instrument_id) REFERENCES instrument (id);
ALTER TABLE trade
    ADD CONSTRAINT FK_TRADE_ON_RATE FOREIGN KEY (rate_id) REFERENCES rate (id);
ALTER TABLE trade
    ADD CONSTRAINT FK_TRADE_ON_VENUE FOREIGN KEY (venue_id) REFERENCES venue (id);

ALTER TABLE transacting_party DROP CONSTRAINT IF EXISTS FK_TRANSACTING_PARTY_ON_PARTY;
ALTER TABLE transacting_party DROP CONSTRAINT IF EXISTS FK_TRANSACTING_PARTY_ON_TRANSACTING_PARTY;
ALTER TABLE transacting_party
    ADD CONSTRAINT FK_TRANSACTING_PARTY_ON_PARTY FOREIGN KEY (party_id) REFERENCES party (id);
ALTER TABLE transacting_party
    ADD CONSTRAINT FK_TRANSACTING_PARTY_ON_TRANSACTING_PARTY FOREIGN KEY (transacting_party_id) REFERENCES trade (id);

ALTER TABLE venue DROP CONSTRAINT IF EXISTS FK_VENUE_ON_PLATFORM;
ALTER TABLE venue
    ADD CONSTRAINT FK_VENUE_ON_PLATFORM FOREIGN KEY (platform_id) REFERENCES platform (id);

ALTER TABLE venue_party DROP CONSTRAINT IF EXISTS FK_VENUE_PARTY_ON_INTERNAL_REF;
ALTER TABLE venue_party DROP CONSTRAINT IF EXISTS FK_VENUE_PARTY_ON_VENUE_PARTY;
ALTER TABLE venue_party
    ADD CONSTRAINT FK_VENUE_PARTY_ON_INTERNAL_REF FOREIGN KEY (internal_ref_id) REFERENCES internal_ref (id);
ALTER TABLE venue_party
    ADD CONSTRAINT FK_VENUE_PARTY_ON_VENUE_PARTY FOREIGN KEY (venue_party_id) REFERENCES venue (id);

ALTER TABLE settlement_temp DROP CONSTRAINT IF EXISTS FK_SETTLEMENT_ON_SETTLEMENT_TEMP;
ALTER TABLE settlement_temp
    ADD CONSTRAINT FK_SETTLEMENT_ON_SETTLEMENT_TEMP FOREIGN KEY (settlement_id) REFERENCES settlement (id);

ALTER TABLE participant DROP CONSTRAINT IF EXISTS FK_PARTICIPANT_HOLDER;
ALTER TABLE participant add participant_holder_id BIGINT NULL;
ALTER TABLE participant ADD CONSTRAINT FK_PARTICIPANT_HOLDER FOREIGN KEY (participant_holder_id) REFERENCES participant_holder (id);

COMMENT ON COLUMN trade.event_id IS 'Link to the event that created this trade';
COMMENT ON COLUMN trade.resource_uri IS 'Resource URI for this trade';
COMMENT ON COLUMN position.status IS 'External Spire position status from statusDTO';
COMMENT ON COLUMN position.processing_status IS 'Integration toolkit internal status';