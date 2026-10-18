DROP TRIGGER IF EXISTS trg_credential_keys_outbox ON credential_keys;

CREATE OR REPLACE FUNCTION credential_key_event()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
DECLARE
    credential_record credentials%ROWTYPE;
    event_type        TEXT;
    payload           JSONB;
BEGIN
    SELECT *
    INTO credential_record
    FROM credentials
    WHERE credential_id = NEW.credential_id;

    IF TG_OP = 'INSERT' THEN
        event_type := 'CredentialCreated';

        payload := jsonb_build_object(
            'credentialId', NEW.credential_id,
            'keyId', NEW.key_id,
            'secretHash', NEW.secret_hash,
            'partnerId', credential_record.partner_id,
            'projectId', credential_record.project_id,
            'scopes', credential_record.scopes,
            'expiresAt', NEW.expires_at
        );

    ELSIF
        TG_OP = 'UPDATE'
            AND OLD.status <> 'REVOKED'
            AND NEW.status = 'REVOKED' THEN
        event_type := 'CredentialRevoked';

        payload := jsonb_build_object(
            'credentialId', NEW.credential_id,
            'keyId', NEW.key_id,
            'revokedAt', NEW.revoked_at
        );

    ELSE
        RETURN NEW;
    END IF;

    INSERT INTO outbox_events (event_id,
                               event_type,
                               aggregate_type,
                               aggregate_id,
                               payload,
                               status,
                               available_at)
    VALUES (gen_random_uuid(),
            event_type,
            'Credential',
            NEW.credential_id,
            payload,
            'PENDING',
            CURRENT_TIMESTAMP);

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_credential_keys_outbox
    AFTER INSERT OR UPDATE
    ON credential_keys
    FOR EACH ROW
EXECUTE FUNCTION credential_key_event();
