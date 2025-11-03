CREATE TABLE IF NOT EXISTS call_history
(
    id             UUID PRIMARY KEY,
    timestamp      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    endpoint       VARCHAR(255) NOT NULL,
    method         VARCHAR(10)  NOT NULL,
    request_params JSONB,
    response       JSONB,
    error_message  TEXT,
    success        BOOLEAN      NOT NULL DEFAULT false,
    version        BIGINT       NOT NULL DEFAULT 0
);
