CREATE TABLE IF NOT EXISTS locations(
    location_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    location_uuid UUID NOT NULL UNIQUE,
    user_uuid UUID REFERENCES public.users(user_uuid) UNIQUE NOT NULL,
    name TEXT NOT NULL,
    beds INT NOT NULL,
    description TEXT NOT NULL,
    address_city TEXT NOT NULL,
    address_street TEXT NOT NULL,
    address_apartment_number TEXT,
    address_state TEXT NOT NULL,
    address_country TEXT NOT NULL,
    created_on TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    modified_on TIMESTAMPTZ NOT NULL DEFAULT NOW()
);