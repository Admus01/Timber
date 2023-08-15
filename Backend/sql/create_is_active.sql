-- Create a trigger function
CREATE FUNCTION update_is_active() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.hashed_psw IS NOT NULL AND OLD.hashed_psw IS NULL THEN
        UPDATE users
        SET is_active = TRUE
        WHERE user_uuid = NEW.user_uuid;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Create a trigger
CREATE TRIGGER update_is_active_trigger
AFTER UPDATE ON login_data
FOR EACH ROW EXECUTE FUNCTION update_is_active();