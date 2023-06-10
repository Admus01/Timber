CREATE TABLE IF NOT EXISTS login_data (
  login_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  user_uuid UUID REFERENCES public.users(user_uuid) UNIQUE NOT NULL,
  email TEXT REFERENCES public.users(email) UNIQUE NOT NULL,
  username TEXT UNIQUE NOT NULL,
  salt TEXT UNIQUE,
  hashed_psw TEXT,
  created_on TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  modified_on TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE OR REPLACE FUNCTION update_user_salt_func() RETURNS trigger AS $$
BEGIN
  NEW.salt := gen_salt('md5');
  RETURN NEW;
END;
$$ language plpgsql;

CREATE TRIGGER update_user_salt_trigger
  BEFORE INSERT ON login_data
  FOR EACH ROW
  EXECUTE FUNCTION update_user_salt_func();
