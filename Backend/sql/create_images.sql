CREATE TABLE IF NOT EXISTS images(
    image_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    image_uuid UUID UNIQUE NOT NULL,
    location_uuid UUID REFERENCES public.locations(location_uuid) UNIQUE NOT NULL,
    image BYTEA,
    created_on TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    modified_on TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

