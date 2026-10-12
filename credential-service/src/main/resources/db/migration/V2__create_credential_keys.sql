CREATE TABLE credential_keys
(
    key_id        VARCHAR(128) PRIMARY KEY,
    credential_id UUID        NOT NULL,
    secret_hash   TEXT        NOT NULL,
    status        VARCHAR(32) NOT NULL,
    expires_at    TIMESTAMPTZ,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at    TIMESTAMPTZ,

    CONSTRAINT fk_credential_keys_credential
        FOREIGN KEY (credential_id)
            REFERENCES credentials (credential_id),

    CONSTRAINT ck_credential_keys_status CHECK (
        status IN ('ACTIVE', 'REVOKED', 'EXPIRED')
    ),
    CONSTRAINT ck_credential_keys_revoked_at CHECK (
        (status = 'REVOKED' AND revoked_at IS NOT NULL)
            OR (status <> 'REVOKED' AND revoked_at IS NULL)
    )
);

CREATE INDEX ix_credential_keys_credential_id
    ON credential_keys (credential_id);

CREATE INDEX ix_credential_keys_active
    ON credential_keys (credential_id)
    WHERE status = 'ACTIVE';
