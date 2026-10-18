CREATE OR REPLACE FUNCTION maintain_outbox_event_state()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    NEW.updated_at := CURRENT_TIMESTAMP;

    IF NEW.status = 'CLAIMED' THEN
        NEW.claimed_at := CURRENT_TIMESTAMP;
    END IF;

    IF NEW.status = 'DELIVERED' AND OLD.status IS DISTINCT FROM 'DELIVERED' THEN
        NEW.delivered_at := CURRENT_TIMESTAMP;
        NEW.last_error := NULL;
    END IF;

    IF NEW.status = 'RETRY_WAITING' AND OLD.status IS DISTINCT FROM 'RETRY_WAITING' THEN
        NEW.attempt_count := OLD.attempt_count + 1;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_outbox_event_state
    BEFORE UPDATE
    ON outbox_events
    FOR EACH ROW
EXECUTE FUNCTION maintain_outbox_event_state();
