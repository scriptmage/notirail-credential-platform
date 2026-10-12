CREATE OR REPLACE FUNCTION credential_event()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
DECLARE
    event_type TEXT;
    payload    JSONB;
BEGIN
    IF TG_OP = 'INSERT' THEN
        event_type := 'CredentialCreated';

        payload := jsonb_build_object(
            'credentialId', NEW.credential_id,
            'keyId', NEW.key_id,
            'partnerId', NEW.partner_id,
            'projectId', NEW.project_id,
            'scopes', NEW.scopes,
            'status', NEW.status,
            'environment', NEW.environment,
            'expiresAt', NEW.expires_at
                   );

    ELSIF TG_OP = 'UPDATE' THEN
        IF OLD.status <> 'REVOKED' AND NEW.status = 'REVOKED' THEN
            event_type := 'CredentialRevoked';

            payload := jsonb_build_object(
                'credentialId', NEW.credential_id,
                'keyId', NEW.key_id,
                'partnerId', NEW.partner_id,
                'projectId', NEW.project_id,
                'status', NEW.status,
                'revokedAt', NEW.revoked_at
                       );

        ELSIF OLD.scopes IS DISTINCT FROM NEW.scopes THEN
            event_type := 'CredentialScopesChanged';

            payload := jsonb_build_object(
                'credentialId', NEW.credential_id,
                'keyId', NEW.key_id,
                'scopes', NEW.scopes
                       );

        ELSIF
            OLD.partner_id IS DISTINCT FROM NEW.partner_id
                OR OLD.project_id IS DISTINCT FROM NEW.project_id
        THEN
            event_type := 'CredentialBindingChanged';

            payload := jsonb_build_object(
                'credentialId', NEW.credential_id,
                'keyId', NEW.key_id,
                'partnerId', NEW.partner_id,
                'projectId', NEW.project_id
                       );

        ELSE
            RETURN NEW;
        END IF;

    ELSE
        RETURN NEW;
    END IF;

    INSERT INTO outbox_events (event_id, event_type, aggregate_type, aggregate_id, payload, status, available_at)
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

CREATE TRIGGER trg_credentials_outbox
    AFTER INSERT OR UPDATE
    ON credentials
    FOR EACH ROW
EXECUTE FUNCTION credential_event();
