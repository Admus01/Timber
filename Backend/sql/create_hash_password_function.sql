CREATE OR REPLACE FUNCTION hash_password()
  RETURNS TRIGGER AS
$$
BEGIN
  NEW.hashed_psw := crypt(NEW.hashed_psw, NEW.salt);
  RETURN NEW;
END;
$$
LANGUAGE plpgsql;


CREATE TRIGGER trigger_hash_password
  BEFORE INSERT OR UPDATE ON login_data
  FOR EACH ROW
  EXECUTE FUNCTION hash_password();