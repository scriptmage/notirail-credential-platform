CREATE TABLE outbox_events
(
    event_id       UUID PRIMARY KEY,
    event_type     VARCHAR(128) NOT NULL,
    aggregate_type VARCHAR(128) NOT NULL,
    aggregate_id   UUID         NOT NULL,
    payload        JSONB        NOT NULL,
    status         VARCHAR(32)  NOT NULL DEFAULT 'PENDING',
    available_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    attempt_count  INTEGER      NOT NULL DEFAULT 0,
    claimed_at     TIMESTAMPTZ,
    delivered_at   TIMESTAMPTZ,
    last_error     TEXT,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT ck_outbox_status CHECK (
        status IN ('PENDING', 'CLAIMED', 'RETRY_WAITING', 'DELIVERED', 'DEAD')
    )
);

CREATE INDEX ix_outbox_polling
    ON outbox_events (status, available_at, created_at);

CREATE INDEX ix_outbox_claim_timeout
    ON outbox_events (claimed_at) WHERE status = 'CLAIMED';

CREATE INDEX ix_outbox_aggregate
    ON outbox_events (aggregate_id, created_at);
