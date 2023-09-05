CREATE TABLE IF NOT EXISTS users(
  user_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  user_uuid UUID NOT NULL UNIQUE,
  berear TEXT UNIQUE,
  first_name TEXT NOT NULL,
  last_name TEXT NOT NULL,
  email TEXT NOT NULL UNIQUE,
  country_phone_code TEXT,
  phone_number TEXT,
  date_of_birth DATE NOT NULL,
  citizenship TEXT,
  is_master_admin BOOLEAN DEFAULT FALSE,
  is_active BOOLEAN DEFAULT TRUE,
  is_deleted BOOLEAN DEFAULT FALSE,
  created_on TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  modified_on TIMESTAMPTZ NOT NULL DEFAULT NOW()
);