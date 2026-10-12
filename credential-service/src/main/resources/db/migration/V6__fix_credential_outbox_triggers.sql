DROP TRIGGER IF EXISTS trg_credentials_outbox ON credentials;
DROP FUNCTION IF EXISTS credential_event();

CREATE
    OR REPLACE FUNCTION credential_key_event()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
DECLARE
    credential_record credentials%ROWTYPE;
    event_type TEXT;
    payload    JSONB;
BEGIN
    SELECT *
    INTO credential_record
    FROM credentials
    WHERE credential_id = NEW.credential_id;

    IF
        TG_OP = 'INSERT' THEN
        event_type := 'CredentialCreated';

        payload := jsonb_build_object(
            'credentialId', NEW.credential_id,
            'keyId', NEW.key_id,
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
    AFTER INSERT OR
        UPDATE
    ON credential_keys
    FOR EACH ROW
EXECUTE FUNCTION credential_key_event();

CREATE
    OR REPLACE FUNCTION credential_metadata_event()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
DECLARE
    event_type TEXT;
    payload    JSONB;
BEGIN
    IF
        OLD.scopes IS DISTINCT FROM NEW.scopes THEN
        event_type := 'CredentialScopesChanged';

        payload := jsonb_build_object(
            'credentialId', NEW.credential_id,
            'scopes', NEW.scopes
        );

    ELSIF
        OLD.partner_id IS DISTINCT FROM NEW.partner_id
            OR OLD.project_id IS DISTINCT FROM NEW.project_id
    THEN
        event_type := 'CredentialBindingChanged';

        payload := jsonb_build_object(
            'credentialId', NEW.credential_id,
            'partnerId', NEW.partner_id,
            'projectId', NEW.project_id
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

CREATE TRIGGER trg_credentials_metadata_outbox
    AFTER UPDATE
    ON credentials
    FOR EACH ROW
EXECUTE FUNCTION credential_metadata_event();
