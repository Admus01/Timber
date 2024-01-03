CREATE OR REPLACE FUNCTION update_locations_on_user_update_active()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.is_active = true THEN
        UPDATE locations
        SET is_active = true
        WHERE user_uuid = NEW.user_uuid;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trigger_update_locations_active
AFTER UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION update_locations_on_user_update_active();