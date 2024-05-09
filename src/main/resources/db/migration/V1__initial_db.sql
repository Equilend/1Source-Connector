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
    "type"            VARCHAR(255) NULL,
    "description"     VARCHAR(255) NULL,
    margin            DOUBLE PRECISION NULL,
    rounding_rule     INT NULL,
    rounding_mode     VARCHAR(255) NULL,
    CONSTRAINT pk_collateral PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS contract
(
    id                                      BIGSERIAL PRIMARY KEY,
    contract_id                             VARCHAR(255) NULL,
    processing_status                       VARCHAR(255) NULL,
    matching_1source_trade_agreement_id     VARCHAR(255) NULL,
    matching_spire_position_id              BIGINT NULL,
    matching_spire_trade_id                 BIGINT NULL,
    contract_status                         VARCHAR(255) NULL,
    last_update_party_id                    VARCHAR(255) NULL,
    create_datetime                         timestamp    NULL,
    last_update_datetime                    timestamp    NULL,
    trade_id                                BIGINT       NULL
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
    security_id BIGINT NULL,
    ticker VARCHAR(255) NULL,
    cusip  VARCHAR(255) NULL,
    isin   VARCHAR(255) NULL,
    sedol  VARCHAR(255) NULL,
    quick_code  VARCHAR(255) NULL,
    figi   VARCHAR(255) NULL,
    "description" VARCHAR(255) NULL,
    price_id BIGINT NULL,
    CONSTRAINT pk_instrument PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS internal_ref
(
    id          SERIAL NOT NULL,
    "broker"      VARCHAR(255) NULL,
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

CREATE TABLE IF NOT EXISTS account
(
    id              BIGSERIAL PRIMARY KEY,
    account_id      BIGINT NOT NULL,
    short_name      VARCHAR(255) NULL,
    lei             VARCHAR(255) NULL,
    one_source_id   VARCHAR(255) NULL,
    dtc             BIGINT NULL
);

CREATE TABLE IF NOT EXISTS position
(
    position_id           BIGINT PRIMARY KEY,
    trade_id              BIGINT NULL,
    creation_datetime     TIMESTAMP NULL,
    last_update_datetime  TIMESTAMP NULL,
    processing_status     VARCHAR(255) NULL,
    matching_1source_trade_agreement_id     VARCHAR(255) NULL,
    matching_1source_loan_contract_id     VARCHAR(255) NULL,
    position_type_id      INT          NULL,
    position_type         VARCHAR(255) NULL,
    is_cash               BOOLEAN NULL,
    status_id             INT NULL,
    "status"              VARCHAR(255) NULL,
    custom_value2         VARCHAR(255) NULL,
    venue_ref_id          VARCHAR(255) NULL,
    position_ref          VARCHAR(255) NULL,
    position_security_id  BIGINT NULL,
    security_id           BIGINT NULL,
    ticker                VARCHAR(255) NULL,
    cusip                 VARCHAR(255) NULL,
    isin                  VARCHAR(255) NULL,
    sedol                 VARCHAR(255) NULL,
    quick_code            VARCHAR(255) NULL,
    bloomberg_id          VARCHAR(255) NULL,
    "description"         VARCHAR(255) NULL,
    security_price        DOUBLE PRECISION NULL,
    price_factor          INT          NULL,
    currency_id           INT          NULL,
    currency              VARCHAR(255) NULL,
    rate                  DOUBLE PRECISION NULL,
    quantity              DOUBLE PRECISION NULL,
    trade_date            TIMESTAMP    NULL,
    settle_date           TIMESTAMP    NULL,
    accrual_date          TIMESTAMP    NULL,
    deliver_free          BOOLEAN      NULL,
    price                 DOUBLE PRECISION NULL,
    amount                DOUBLE PRECISION NULL,
    term_id               INT          NULL,
    end_date              TIMESTAMP    NULL,
    index_id              INT          NULL,
    index_name            VARCHAR(255) NULL,
    spread                DOUBLE PRECISION NULL,
    tax_with_holding_rate DOUBLE PRECISION NULL,
    exposure_id           INT          NULL,
    cp_haircut            DOUBLE PRECISION NULL,
    cp_mark_round_to      INT          NULL,
    account_lei           VARCHAR(255) NULL,
    cp_account_id         BIGINT       NULL,
    cp_lei                VARCHAR(255) NULL,
    depo_id               INT          NULL,
    depo_ky               VARCHAR(255) NULL,
    account_id            BIGINT NULL,
    cp_id                 BIGINT NULL
);

CREATE TABLE IF NOT EXISTS price
(
    id          BIGSERIAL PRIMARY KEY,
    "value"     DOUBLE PRECISION       NULL,
    currency    VARCHAR(255)       NULL,
    unit        VARCHAR(255) NULL
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
    id                  BIGSERIAL PRIMARY KEY,
    instruction_id      BIGINT       NULL,
    party_role          VARCHAR(255) NULL,
    settlement_status   VARCHAR(255) NULL,
    instruction         BIGINT       NULL,
    contract_id         BIGINT       NULL,
    internal_acct_cd   VARCHAR(255)  NULL
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
    "type"        VARCHAR(255) NOT NULL,
    "timestamp"   TIMESTAMP NULL,
    CONSTRAINT pk_timestamp PRIMARY KEY (type)
);

CREATE TABLE IF NOT EXISTS trade
(
    id              BIGSERIAL NOT NULL,
    instrument_id   BIGINT       NULL,
    rate_id         BIGINT       NULL,
    quantity        INT    NULL,
    currency        VARCHAR(255) NULL,
    dividend_rate   DOUBLE PRECISION NULL,
    trade_date      DATE         NULL,
    term_type       VARCHAR(255) NULL,
    term_date       DATE         NULL,
    settlement_date DATE         NULL,
    settlement_type VARCHAR(255) NULL,
    collateral      BIGINT       NULL,
    processing_status VARCHAR(255) NULL,
    event_id          VARCHAR(255)       NULL,
    resource_uri         VARCHAR(255) NULL,
    CONSTRAINT pk_trade PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS trade_event
(
    id              SERIAL NOT NULL,
    event_id        VARCHAR(255)      NULL,
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
    internal_ref_id BIGINT       NULL,
    transacting_party_id BIGINT       NULL,
    CONSTRAINT pk_transacting_party PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS venue
(
    id          SERIAL NOT NULL,
    party_id    VARCHAR(255) NULL,
    "type"        VARCHAR(255) NULL,
    venue_name           VARCHAR(255) NULL,
    venue_ref_key            VARCHAR(255) NULL,
    transaction_datetime timestamp    NULL,
    trade_id             BIGINT       NULL,
    CONSTRAINT pk_venue PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS venue_party
(
    id              SERIAL NOT NULL,
    party_role      VARCHAR(255) NULL,
    venue_id        VARCHAR(255) NULL,
    venue_party_id  BIGINT       NULL,
    CONSTRAINT pk_venue_party PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS local_venue_field
(
    id              SERIAL NOT NULL,
    local_field_name      VARCHAR(255) NULL,
    local_field_value     VARCHAR(255) NULL,
    local_venue_field_id  BIGINT       NULL,
    CONSTRAINT pk_local_venue_field PRIMARY KEY (id)
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

CREATE TABLE IF NOT EXISTS trade_out
(
    trade_id                 BIGSERIAL NOT NULL,
    post_date                TIMESTAMP,
    settle_date              TIMESTAMP,
    accrual_date             TIMESTAMP,
    trade_date               TIMESTAMP,
    rate_or_spread           DOUBLE PRECISION NULL,
    trade_type               VARCHAR(255),
    trade_type_id            INTEGER,
    index_id                 INTEGER,
    index_name               VARCHAR(255),
    index_spread             DOUBLE PRECISION NULL,
    "status"                 VARCHAR(255),
    status_id                INTEGER,
    position_id              BIGINT NULL,
    quantity                 DOUBLE PRECISION NULL,
    amount                   DOUBLE PRECISION NULL,
    account_id               BIGINT NULL,
    counter_party_id         BIGINT NULL,
    depo_id                  INTEGER,
    depo_ky                  VARCHAR(255),
    CONSTRAINT pk_trade_out PRIMARY KEY (trade_id),
    CONSTRAINT fk_position_out FOREIGN KEY (position_id) REFERENCES position (position_id),
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account (id),
    CONSTRAINT fk_counter_party FOREIGN KEY (counter_party_id) REFERENCES account (id)
    );

CREATE TABLE IF NOT EXISTS rerate_trade
(
    trade_id             BIGSERIAL NOT NULL,
    creation_datetime TIMESTAMP,
    last_update_datetime TIMESTAMP,
    matching_rerate_id   VARCHAR(255),
    processing_status    VARCHAR(255),
    related_contract_id  VARCHAR(255),
    related_position_id  BIGINT,
    trade_out_trade_id   BIGINT,
    CONSTRAINT pk_rerate_trade PRIMARY KEY (trade_id),
    CONSTRAINT fk_trade_out FOREIGN KEY (trade_out_trade_id) REFERENCES trade_out (trade_id)
);

CREATE TABLE IF NOT EXISTS rerate
(
    rerate_id                 VARCHAR(255) NOT NULL,
    contract_id               VARCHAR(255),
    create_update_datetime    TIMESTAMP,
    last_update_datetime      TIMESTAMP,
    matching_spire_trade_id   BIGINT,
    processing_status         VARCHAR(255),
    related_spire_position_id BIGINT,
    "status"                    VARCHAR(255),
    rerate_status             VARCHAR(255),
    venue_id                  BIGINT,
    rate_rate_id              BIGINT,
    rerate_rate_id            BIGINT,
    CONSTRAINT pk_rerate PRIMARY KEY (rerate_id),
    CONSTRAINT fk_venue FOREIGN KEY (venue_id) REFERENCES venue (id),
    CONSTRAINT fk_rate_rate_id FOREIGN KEY (rate_rate_id) REFERENCES rate (id),
    CONSTRAINT fk_rerate_rate_id FOREIGN KEY (rerate_rate_id) REFERENCES rate (id)
    );

CREATE TABLE IF NOT EXISTS return_trade
(
    trade_id                  bigint not null,
    canceling_trade_id        bigint,
    creation_datetime         timestamp,
    last_update_datetime      timestamp,
    related_position_id       bigint,
    trade_out_trade_id        bigint,
    matching1source_return_id varchar(255),
    processing_status         varchar(255),
    related_contract_id       varchar(255),
    CONSTRAINT return_trade_pkey PRIMARY KEY (trade_id),
    CONSTRAINT fk_return_trade_out FOREIGN KEY (trade_out_trade_id) REFERENCES trade_out
    );

ALTER TABLE agreement DROP CONSTRAINT IF EXISTS FK_AGREEMENT_ON_TRADE;
ALTER TABLE agreement
    ADD CONSTRAINT FK_AGREEMENT_ON_TRADE FOREIGN KEY (trade_id) REFERENCES trade (id);

ALTER TABLE contract DROP CONSTRAINT IF EXISTS FK_CONTRACT_ON_TRADE;
ALTER TABLE contract
    ADD CONSTRAINT FK_CONTRACT_ON_TRADE FOREIGN KEY (trade_id) REFERENCES trade (id);

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
ALTER TABLE settlement DROP CONSTRAINT IF EXISTS FK_SETTLEMENT_ON_CONTRACT;
ALTER TABLE settlement
    ADD CONSTRAINT FK_SETTLEMENT_ON_INSTRUCTION FOREIGN KEY (instruction) REFERENCES settlement_instruction (id);
ALTER TABLE settlement
    ADD CONSTRAINT FK_SETTLEMENT_ON_CONTRACT FOREIGN KEY (contract_id) REFERENCES contract (id);

ALTER TABLE settlement_instruction_update DROP CONSTRAINT IF EXISTS FK_SETTLEMENT_ON_INSTRUCTION;
ALTER TABLE settlement_instruction_update
    ADD CONSTRAINT FK_SETTLEMENT_ON_INSTRUCTION FOREIGN KEY (instruction) REFERENCES settlement_instruction (id);

ALTER TABLE trade DROP CONSTRAINT IF EXISTS FK_TRADE_ON_COLLATERAL;
ALTER TABLE trade DROP CONSTRAINT IF EXISTS FK_TRADE_ON_INSTRUMENT;
ALTER TABLE trade DROP CONSTRAINT IF EXISTS FK_TRADE_ON_RATE;
ALTER TABLE trade
    ADD CONSTRAINT FK_TRADE_ON_COLLATERAL FOREIGN KEY (collateral) REFERENCES collateral (id);
ALTER TABLE trade
    ADD CONSTRAINT FK_TRADE_ON_INSTRUMENT FOREIGN KEY (instrument_id) REFERENCES instrument (id);
ALTER TABLE trade
    ADD CONSTRAINT FK_TRADE_ON_RATE FOREIGN KEY (rate_id) REFERENCES rate (id);

ALTER TABLE transacting_party DROP CONSTRAINT IF EXISTS FK_TRANSACTING_PARTY_ON_PARTY;
ALTER TABLE transacting_party DROP CONSTRAINT IF EXISTS FK_TRANSACTING_PARTY_ON_TRANSACTING_PARTY;
ALTER TABLE transacting_party DROP CONSTRAINT IF EXISTS FK_TRANSACTING_PARTY_ON_INTERNAL_REF;
ALTER TABLE transacting_party
    ADD CONSTRAINT FK_TRANSACTING_PARTY_ON_PARTY FOREIGN KEY (party_id) REFERENCES party (id);
ALTER TABLE transacting_party
    ADD CONSTRAINT FK_TRANSACTING_PARTY_ON_TRANSACTING_PARTY FOREIGN KEY (transacting_party_id) REFERENCES trade (id);
ALTER TABLE transacting_party
    ADD CONSTRAINT FK_TRANSACTING_PARTY_ON_INTERNAL_REF FOREIGN KEY (internal_ref_id) REFERENCES internal_ref (id);

ALTER TABLE venue_party
    ADD CONSTRAINT FK_VENUE_PARTY_ON_VENUE_PARTY FOREIGN KEY (venue_party_id) REFERENCES venue (id);
ALTER TABLE venue_party DROP CONSTRAINT IF EXISTS FK_VENUE_PARTY_ON_VENUE_PARTY;
ALTER TABLE settlement_temp DROP CONSTRAINT IF EXISTS FK_SETTLEMENT_ON_SETTLEMENT_TEMP;

ALTER TABLE local_venue_field DROP CONSTRAINT IF EXISTS FK_LOCAL_VENUE_FIELD_ON_VENUE;
ALTER TABLE local_venue_field
ADD CONSTRAINT FK_LOCAL_VENUE_FIELD_ON_VENUE FOREIGN KEY (local_venue_field_id) REFERENCES venue (id);

ALTER TABLE position DROP CONSTRAINT IF EXISTS FK_POSITION_ACCOUNT;
ALTER TABLE position DROP CONSTRAINT IF EXISTS FK_POSITION_CP;
ALTER TABLE position
ADD CONSTRAINT FK_POSITION_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE position
ADD CONSTRAINT FK_POSITION_CP FOREIGN KEY (cp_id) REFERENCES account (id);

ALTER TABLE settlement_temp
    ADD CONSTRAINT FK_SETTLEMENT_ON_SETTLEMENT_TEMP FOREIGN KEY (settlement_id) REFERENCES settlement (id);

ALTER TABLE participant DROP CONSTRAINT IF EXISTS FK_PARTICIPANT_HOLDER;
ALTER TABLE participant add participant_holder_id BIGINT NULL;
ALTER TABLE participant ADD CONSTRAINT FK_PARTICIPANT_HOLDER FOREIGN KEY (participant_holder_id) REFERENCES participant_holder (id);

ALTER TABLE venue DROP CONSTRAINT IF EXISTS FK_TRADE;
ALTER TABLE venue ADD CONSTRAINT FK_TRADE FOREIGN KEY (trade_id) REFERENCES trade (id);

COMMENT ON COLUMN trade.event_id IS 'Link to the event that created this trade';
COMMENT ON COLUMN trade.resource_uri IS 'Resource URI for this trade';
COMMENT ON COLUMN position.status IS 'External Spire position status from statusDTO';
COMMENT ON COLUMN position.processing_status IS 'Integration toolkit internal status';