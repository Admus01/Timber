CREATE OR REPLACE FUNCTION update_modified_on()
  RETURNS TRIGGER AS
$$
BEGIN
  NEW.modified_on := now();
  RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_modified_on
  BEFORE UPDATE ON login_data
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_on();