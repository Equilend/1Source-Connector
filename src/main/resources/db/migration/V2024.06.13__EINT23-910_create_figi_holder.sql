CREATE TABLE IF NOT EXISTS figi_cache
(
    ticker                    VARCHAR(255) NOT NULL,
    figi                      VARCHAR(255) NOT NULL,
    PRIMARY KEY (ticker, figi)
);