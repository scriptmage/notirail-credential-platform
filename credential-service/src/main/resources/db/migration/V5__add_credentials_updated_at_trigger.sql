CREATE OR REPLACE FUNCTION set_credentials_updated_at()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    NEW.updated_at := CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_credentials_updated_at
    BEFORE UPDATE
    ON credentials
    FOR EACH ROW
EXECUTE FUNCTION set_credentials_updated_at();
