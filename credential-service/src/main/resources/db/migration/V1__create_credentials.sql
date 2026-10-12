CREATE TABLE credentials
(
    credential_id UUID PRIMARY KEY,
    partner_id    VARCHAR(128) NOT NULL,
    project_id    VARCHAR(128),
    status        VARCHAR(32)  NOT NULL,
    scopes        JSONB        NOT NULL DEFAULT '[]'::jsonb,
    expires_at    TIMESTAMPTZ,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT ck_credentials_status CHECK (
        status IN ('ACTIVE', 'REVOKED', 'EXPIRED', 'SUSPENDED')
    )
);
